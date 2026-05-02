package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final InvinciblePlugin plugin;

    public PlayerQuitListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        com.hihelloy.invincible.listeners.CosmeticListener.markQuitting(player);
        plugin.getFlightManager().onPlayerQuit(player);

        com.hihelloy.invincible.abilities.AbilityType[] bound =
                plugin.getAbilityManager().getBoundAbilities(player);
        plugin.getDataManager().saveBoundAbilities(player.getUniqueId(), bound);
        plugin.getAbilityManager().clearPlayer(player);
        plugin.getCosmeticManager().unloadPlayer(player);
        plugin.getDataManager().getCharacterManager().unloadPlayer(player);
        plugin.getDataManager().unloadPlayer(player.getUniqueId());
        plugin.getScoreboardManager().removeScoreboard(player);
        com.hihelloy.invincible.listeners.CosmeticListener.unmarkQuitting(player);
    }
}