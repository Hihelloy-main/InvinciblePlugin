package com.hihelloy.invincible.util;

import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Checks whether the plugin should be active in a given world.
 * Worlds listed under settings.disabled-worlds in config.yml will
 * suppress abilities, flight, and scoreboards entirely.
 */
public class WorldGuard {

    private final InvinciblePlugin plugin;

    public WorldGuard(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    /** Returns true if the plugin should be DISABLED in this world. */
    public boolean isDisabled(World world) {
        if (world == null) return false;
        List<String> disabled = plugin.getConfig().getStringList("settings.disabled-worlds");
        return disabled.stream().anyMatch(w -> w.equalsIgnoreCase(world.getName()));
    }

    /** Convenience overload for a player's current world. */
    public boolean isDisabled(Player player) {
        return isDisabled(player.getWorld());
    }
}