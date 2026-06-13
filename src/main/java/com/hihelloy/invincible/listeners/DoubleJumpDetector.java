package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.events.PlayerDoubleJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DoubleJumpDetector implements Listener {

    private final InvinciblePlugin plugin;
    private final Set<UUID> inDoubleJumpWindow = new HashSet<>();

    public DoubleJumpDetector(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFirstJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE
        || player.getGameMode() == GameMode.SPECTATOR) return;

        UUID uuid = player.getUniqueId();
        if (inDoubleJumpWindow.contains(uuid)) return;

        if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) return;

        if (!plugin.getFlightManager().canFly(player)) return;

        if (plugin.getFlightManager().isFlying(player)) return;

        boolean movingUpward = event.getTo().getY() > event.getFrom().getY();
        boolean nowAirborne = !player.isOnGround();

        if (movingUpward && nowAirborne) {
            inDoubleJumpWindow.add(uuid);
            player.setAllowFlight(true);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (inDoubleJumpWindow.remove(uuid)) {
                    if (!plugin.getFlightManager().isFlying(player)
                    && player.getGameMode() != GameMode.CREATIVE) {
                        player.setAllowFlight(false);
                    }
                }
            }, 20L);
        }

        if (player.isOnGround() && inDoubleJumpWindow.contains(uuid)) {
            inDoubleJumpWindow.remove(uuid);
            if (!plugin.getFlightManager().isFlying(player)
            && player.getGameMode() != GameMode.CREATIVE) {
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (player.getGameMode() == GameMode.CREATIVE
        || player.getGameMode() == GameMode.SPECTATOR) return;

        if (!inDoubleJumpWindow.remove(uuid)) return;

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        Bukkit.getPluginManager().callEvent(new PlayerDoubleJumpEvent(player));
    }

    public void cleanup(Player player) {
        UUID uuid = player.getUniqueId();
        inDoubleJumpWindow.remove(uuid);
    }
}