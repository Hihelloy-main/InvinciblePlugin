package com.hihelloy.invincible.scoreboard;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.commands.InvincibleCommand;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ScoreboardManager implements Listener {

    private static final int MAX_LINES = 15;

    private final InvinciblePlugin plugin;
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private BukkitTask updateTask;

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
        if (!InvincibleCommand.boardtoggled) {
            removeScoreboard(player);
            return;
        }
        FastBoard board = boards.get(player.getUniqueId());
        if (board == null || board.isDeleted()) {
            setupScoreboard(player);
            return;
        }
        updateScoreboardContent(player, board);
    }

    private void updateScoreboardContent(Player player, FastBoard board) {
        List<Component> lines = new ArrayList<>();
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);

        if (character != null) {
            String heroName = character == CharacterType.CUSTOM
            ? plugin.getDataManager().getCustomHeroName(player.getUniqueId())
            : character.getDisplayName();

            lines.add(legacy(CharacterType.ampCode(character.getColor()) + "&l" + heroName));

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

            Set<CosmeticType> equipped = plugin.getCosmeticManager().getEquipped(player);
            if (!equipped.isEmpty()) {
                lines.add(Component.empty());
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

    public void removeScoreboard(Player player) {
        FastBoard board = boards.remove(player.getUniqueId());
        if (board != null && !board.isDeleted()) {
            board.delete();
        }
    }

    public void cleanup() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        for (FastBoard board : boards.values()) {
            if (!board.isDeleted()) {
                board.delete();
            }
        }
        boards.clear();
    }

    public void restartUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
        startUpdateTask();
    }

    private void startUpdateTask() {
        int interval = plugin.getConfig().getInt("settings.scoreboard-update-interval", 10);
        updateTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!player.isOnline()) continue;
                if (plugin.getWorldGuard().isDisabled(player)) {
                    if (boards.containsKey(player.getUniqueId())) removeScoreboard(player);
                    continue;
                }
                if (!InvincibleCommand.boardtoggled) {
                    if (boards.containsKey(player.getUniqueId())) removeScoreboard(player);
                    continue;
                }
                FastBoard board = boards.get(player.getUniqueId());
                if (board == null || board.isDeleted()) {
                    setupScoreboard(player);
                } else {
                    updateScoreboardContent(player, board);
                }
            }
        }, 0L, interval);
    }

    @EventHandler
    public void onWorldChange(org.bukkit.event.player.PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWorldGuard().isDisabled(player)) {
            removeScoreboard(player);
            player.sendActionBar(Component.text(""));
        } else {
            updateScoreboard(player);
        }
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
