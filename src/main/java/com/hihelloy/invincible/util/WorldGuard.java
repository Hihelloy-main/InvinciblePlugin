package com.hihelloy.invincible.util;

import com.hihelloy.invincible.InvinciblePlugin;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldGuard {

    private final InvinciblePlugin plugin;
    private boolean griefPreventionEnabled = false;

    public WorldGuard(InvinciblePlugin plugin) {
        this.plugin = plugin;
        Plugin gp = plugin.getServer().getPluginManager().getPlugin("GriefPrevention");
        if (gp != null && gp.isEnabled()) {
            griefPreventionEnabled = true;
            plugin.getLogger().info("GriefPrevention detected — claim protection active.");
        } else {
            plugin.getLogger().info("GriefPrevention not found — running without claim protection.");
        }
    }

    public boolean isGriefPreventionEnabled() {
        return griefPreventionEnabled;
    }

    public boolean isDisabled(World world) {
        if (world == null) return false;
        List<String> disabled = plugin.getConfig().getStringList("settings.disabled-worlds");
        return disabled.stream().anyMatch(w -> w.equalsIgnoreCase(world.getName()));
    }

    public boolean isDisabled(Player player) {
        return isDisabled(player.getWorld());
    }

    public boolean canUseAbility(Player player) {
        if (isDisabled(player)) return false;
        if (!griefPreventionEnabled) return true;
        if (!plugin.getConfig().getBoolean("settings.griefprevention-respect-claims", true)) return true;

        Claim claim = getClaim(player.getLocation());
        if (claim == null) return true;
        if (claim.isAdminClaim()) return true;

        String noBuildReason = claim.allowBuild(player, org.bukkit.Material.AIR);
        return noBuildReason == null;
    }

    public boolean canUseAbilityAt(Player player, Location location) {
        if (isDisabled(player)) return false;
        if (!griefPreventionEnabled) return true;
        if (!plugin.getConfig().getBoolean("settings.griefprevention-respect-claims", true)) return true;

        Claim claim = getClaim(location);
        if (claim == null) return true;
        if (claim.isAdminClaim()) return true;

        String noBuildReason = claim.allowBuild(player, org.bukkit.Material.AIR);
        return noBuildReason == null;
    }

    public boolean isPvPAllowed(Player attacker, Player victim) {
        if (!griefPreventionEnabled) return true;
        if (!plugin.getConfig().getBoolean("settings.griefprevention-respect-pvp", true)) return true;

        if (GriefPrevention.instance.config_pvp_noCombatInPlayerLandClaims) {
            Claim attackerClaim = getClaim(attacker.getLocation());
            if (attackerClaim != null && !attackerClaim.isAdminClaim()) {
                return false;
            }
            Claim victimClaim = getClaim(victim.getLocation());
            if (victimClaim != null && !victimClaim.isAdminClaim()) {
                return false;
            }
        }

        if (GriefPrevention.instance.config_pvp_noCombatInAdminLandClaims) {
            Claim attackerClaim = getClaim(attacker.getLocation());
            if (attackerClaim != null && attackerClaim.isAdminClaim()) {
                return false;
            }
            Claim victimClaim = getClaim(victim.getLocation());
            if (victimClaim != null && victimClaim.isAdminClaim()) {
                return false;
            }
        }

        return true;
    }

    public boolean isInClaim(Player player) {
        if (!griefPreventionEnabled) return false;
        return getClaim(player.getLocation()) != null;
    }

    public boolean isInOwnClaim(Player player) {
        if (!griefPreventionEnabled) return false;
        Claim claim = getClaim(player.getLocation());
        if (claim == null) return false;
        if (claim.isAdminClaim()) return false;
        return player.getUniqueId().equals(claim.getOwnerID());
    }

    public Claim getClaim(Location location) {
        if (!griefPreventionEnabled) return null;
        try {
            return GriefPrevention.instance.dataStore.getClaimAt(location, false, null);
        } catch (Exception e) {
            return null;
        }
    }
}