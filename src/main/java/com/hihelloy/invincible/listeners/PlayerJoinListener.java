package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final InvinciblePlugin plugin;

    public PlayerJoinListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getDataManager().loadPlayer(player.getUniqueId());
        plugin.getDataManager().getCharacterManager().loadPlayer(player);
        plugin.getCosmeticManager().loadPlayer(player);
        plugin.getAbilityManager().loadPlayer(player);
        plugin.getScoreboardManager().setupScoreboard(player);

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character != null) {
            AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.setBaseValue(plugin.getConfig().getDouble("settings.player-max-health", 60.0));
            }
            player.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(CharacterType.ampCode(character.getColor()) + "Welcome back, " + character.getDisplayName() + "!"));
        } else {
            player.sendMessage(Component.text("Select a character with /inv select", NamedTextColor.YELLOW));
        }
    }
}