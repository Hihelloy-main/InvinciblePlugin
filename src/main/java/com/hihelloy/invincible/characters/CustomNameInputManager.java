package com.hihelloy.invincible.characters;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.stats.StatType;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CustomNameInputManager implements Listener {

    private final InvinciblePlugin plugin;
    private final Set<UUID> awaitingInput = new HashSet<>();

    private static final String[] CUSTOM_ABILITIES = {
            "BERSERKER_RAGE", "ATOMIC_PUNCH", "SONIC_CLAP", "COMBO_STRIKE"
    };

    public CustomNameInputManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void startNameInput(Player player) {
        awaitingInput.add(player.getUniqueId());
        player.sendMessage(Component.text(
                "Type your custom hero name in chat. Type 'cancel' to cancel.",
                NamedTextColor.GOLD));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!awaitingInput.remove(player.getUniqueId())) return;

        event.setCancelled(true);

        String name = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        if (name.equalsIgnoreCase("cancel")) {
            player.sendMessage(Component.text("Custom hero creation cancelled.", NamedTextColor.GRAY));
            return;
        }
        if (name.length() < 2 || name.length() > 24) {
            player.sendMessage(Component.text("Name must be 2-24 characters. Try again:", NamedTextColor.RED));
            awaitingInput.add(player.getUniqueId());
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getDataManager().getCharacterManager().setCharacter(player, CharacterType.CUSTOM);
                plugin.getDataManager().saveCustomHeroName(player.getUniqueId(), name);

                for (StatType stat : StatType.values()) {
                    plugin.getStatManager().setStatLevel(player, stat,
                            plugin.getConfig().getInt("settings.max-stat-level", 30));
                }

                AbilityType[] startingBinds = new AbilityType[4];
                for (int i = 0; i < Math.min(CUSTOM_ABILITIES.length, 4); i++) {
                    try {
                        startingBinds[i] = AbilityType.valueOf(CUSTOM_ABILITIES[i]);
                    } catch (IllegalArgumentException ignored) {}
                }
                plugin.getAbilityManager().setBoundAbilities(player, startingBinds);
                plugin.getDataManager().saveBoundAbilities(player.getUniqueId(), startingBinds);
                plugin.getScoreboardManager().updateScoreboard(player);

                player.sendMessage(Component.text("You are now ", NamedTextColor.GOLD)
                        .append(Component.text(name, NamedTextColor.YELLOW))
                        .append(Component.text("! All stats maxed.", NamedTextColor.GOLD)));
                player.sendMessage(Component.text(
                        "You have flight (double-jump) and full ability access.",
                        NamedTextColor.GRAY));
                player.sendMessage(Component.text(
                        "Use /inv abilities to bind any moves you want.",
                        NamedTextColor.GRAY));
            }
        }.runTask(plugin);
    }
}