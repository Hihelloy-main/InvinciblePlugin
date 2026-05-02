package com.hihelloy.invincible.scoreboard;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import com.hihelloy.invincible.stats.StatType;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ScoreboardManager implements Listener {

    private static final int MAX_LINES = 15;

    private final InvinciblePlugin plugin;
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public ScoreboardManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    public void setupScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(
                Component.text("✦ INVINCIBLE ✦", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
        );
        boards.put(player.getUniqueId(), board);
        updateScoreboardContent(player, board);
    }

    public void updateScoreboard(Player player) {
        if (plugin.getWorldGuard().isDisabled(player)) {
            removeScoreboard(player);
            return;
        }
        FastBoard board = boards.get(player.getUniqueId());
        if (board == null) {
            setupScoreboard(player);
            return;
        }
        updateScoreboardContent(player, board);
    }

    private void updateScoreboardContent(Player player, FastBoard board) {
        List<Component> lines = new ArrayList<>();
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);

        if (character != null) {
            lines.add(legacy(CharacterType.ampCode(character.getColor()) + "&l" + character.getDisplayName()));

            if (character.canFly() || character.hasSuperStrength() || character.hasSuperSpeed()) {
                StringBuilder passives = new StringBuilder("&8");
                if (character.canFly()) passives.append("✈ ");
                if (character.hasSuperStrength()) passives.append("⚔ ");
                if (character.hasSuperSpeed()) passives.append("⚡");
                lines.add(legacy(passives.toString().trim()));
            }

            lines.add(Component.empty());
            lines.add(legacy("&eAbilities"));

            AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
            for (int i = 0; i < 6; i++) {
                AbilityType ability;
                if (i == 4) ability = AbilityType.GENERIC_DASH;
                else if (i == 5) ability = AbilityType.GRAPPLE;
                else ability = bound[i];

                if (ability != null) {
                    String status;
                    if (plugin.getAbilityManager().isOnCooldown(player, ability)) {
                        long rem = plugin.getAbilityManager().getRemainingCooldown(player, ability) / 1000;
                        status = " &c" + rem + "s";
                    } else if (plugin.getAbilityManager().isAbilityActive(player, ability)) {
                        status = " &a●";
                    } else {
                        status = " &a✔";
                    }
                    lines.add(legacy(CharacterType.ampCode(ability.getColor()) + "[" + (i + 1) + "] &f"
                            + ability.getDisplayName() + status));
                } else {
                    lines.add(legacy("&8[" + (i + 1) + "] Empty"));
                }
            }

            int statPoints = plugin.getStatManager().getStatPoints(player);
            lines.add(Component.empty());
            lines.add(legacy("&6Pts: &f" + statPoints + buildActiveStatsInline(player)));

            Set<CosmeticType> equipped = plugin.getCosmeticManager().getEquipped(player);
            if (!equipped.isEmpty()) {
                StringBuilder cosLine = new StringBuilder("&d");
                for (CosmeticType c : equipped) {
                    cosLine.append(CharacterType.ampCode(c.getColor())).append(c.getDisplayName()).append("&d ");
                }
                lines.add(legacy(cosLine.toString().trim()));
            }

            if (plugin.getFlightManager().isFlying(player)) {
                lines.add(legacy("&b✈ Flying"));
            }

        } else {
            lines.add(legacy("&7No character selected"));
            lines.add(legacy("&e/invincible select"));
        }

        lines.add(Component.empty());
        lines.add(legacy("&8invincible.hihelloy"));

        if (lines.size() > MAX_LINES) {
            lines = lines.subList(0, MAX_LINES);
        }

        board.updateLines(lines);
    }

    private String buildActiveStatsInline(Player player) {
        StringBuilder sb = new StringBuilder();
        for (StatType stat : StatType.values()) {
            int level = plugin.getStatManager().getStatLevel(player, stat);
            if (level > 0) {
                sb.append(" &8| ").append(CharacterType.ampCode(stat.getColor()))
                        .append(stat.getDisplayName().charAt(0)).append("&f").append(level);
            }
        }
        return sb.toString();
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public void removeScoreboard(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null && !board.isDeleted()) {
            board.delete();
        }
    }

    public void cleanup() {
        for (FastBoard board : boards.values()) {
            if (!board.isDeleted()) {
                board.delete();
            }
        }
        boards.clear();
    }

    private void startUpdateTask() {
        int interval = plugin.getConfig().getInt("settings.scoreboard-update-interval", 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, FastBoard> entry : new HashMap<>(boards).entrySet()) {
                    Player player = plugin.getServer().getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        updateScoreboardContent(player, entry.getValue());
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    @EventHandler
    public void onWorldChange(org.bukkit.event.player.PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWorldGuard().isDisabled(player)) {
            removeScoreboard(player);
        } else {
            updateScoreboard(player);
        }
    }

}