package com.hihelloy.invincible.stats;

import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StatManager {

    private final InvinciblePlugin plugin;
    private static final int MAX_LEVEL = 30;

    public StatManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        startPassiveRegenTask();
    }

    public boolean upgradeStat(Player player, StatType stat) {
        int points = plugin.getDataManager().getStatPoints(player.getUniqueId());
        int currentLevel = plugin.getDataManager().getStatLevel(player.getUniqueId(), stat);

        if (currentLevel >= MAX_LEVEL) {
            player.sendMessage(net.kyori.adventure.text.Component.text(stat.getDisplayName() + " is already at maximum level!", net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }
        if (points < 1) {
            player.sendMessage(net.kyori.adventure.text.Component.text("You don't have enough stat points!", net.kyori.adventure.text.format.NamedTextColor.RED));
            return false;
        }

        var event = new com.hihelloy.invincible.api.events.StatUpgradeEvent(
                player, stat, currentLevel, currentLevel + 1);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        plugin.getDataManager().setStatPoints(player.getUniqueId(), points - 1);
        plugin.getDataManager().setStatLevel(player.getUniqueId(), stat, currentLevel + 1);

        if (stat == StatType.SPEED) {
            applyWalkSpeed(player);
        }

        plugin.getScoreboardManager().updateScoreboard(player);
        return true;
    }

    public int getStatLevel(Player player, StatType stat) {
        return plugin.getDataManager().getStatLevel(player.getUniqueId(), stat);
    }

    public void setStatLevel(Player player, StatType stat, int level) {
        int max = plugin.getConfig().getInt("settings.max-stat-level", 30);
        int clamped = Math.max(0, Math.min(level, max));
        plugin.getDataManager().setStatLevel(player.getUniqueId(), stat, clamped);
        if (stat == StatType.SPEED) applyWalkSpeed(player);
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    public int getStatPoints(Player player) {
        return plugin.getDataManager().getStatPoints(player.getUniqueId());
    }

    public void giveStatPoints(Player player, int amount) {
        int current = plugin.getDataManager().getStatPoints(player.getUniqueId());
        plugin.getDataManager().setStatPoints(player.getUniqueId(), current + amount);
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    public double getDamageFlatBonus(Player player) {
        int level = getStatLevel(player, StatType.DAMAGE);
        double perLevel = plugin.getConfig().getDouble("stats.damage-stat-per-level", 0.2);
        return level * perLevel;
    }

    @Deprecated
    public double getDamageMultiplier(Player player) {
        return 1.0 + getDamageFlatBonus(player) / 10.0;
    }

    public double getSpeedMultiplier(Player player) {
        int level = getStatLevel(player, StatType.SPEED);
        double perLevel = plugin.getConfig().getDouble("stats.speed-stat-per-level", 0.010);
        return 1.0 + (level * perLevel);
    }

    public double getDurationMultiplier(Player player) {
        int level = getStatLevel(player, StatType.DURATION);
        double perLevel = plugin.getConfig().getDouble("stats.duration-stat-per-level", 0.04);
        return 1.0 + (level * perLevel);
    }

    public double getDefenseMultiplier(Player player) {
        int level = getStatLevel(player, StatType.DEFENSE);
        double perLevel = plugin.getConfig().getDouble("stats.defense-stat-per-level", 0.033);
        return 1.0 + (level * perLevel);
    }

    private void startPassiveRegenTask() {
        int maxLevel = plugin.getConfig().getInt("settings.max-stat-level", 30);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) continue;
                    int regenLevel = plugin.getDataManager().getStatLevel(player.getUniqueId(), StatType.REGENERATION);
                    if (regenLevel <= 0) continue;

                    int amplifier = Math.min(3, (regenLevel - 1) / (Math.max(1, maxLevel / 4)));
                    player.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION, 30, amplifier, true, false, false));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void applyWalkSpeed(Player player) {
        int level = getStatLevel(player, StatType.SPEED);
        float base = (float) plugin.getConfig().getDouble("settings.base-walk-speed", 0.2);
        float max = (float) plugin.getConfig().getDouble("settings.max-walk-speed", 0.4);
        double perLevel = plugin.getConfig().getDouble("stats.speed-stat-per-level", 0.010);
        float speed = (float) Math.min(base + level * perLevel, max);
        var ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (ch != null) player.setWalkSpeed(speed);
    }

    public void onKill(Player killer) {
        int points = plugin.getConfig().getInt("settings.stat-points-per-kill", 3);
        giveStatPoints(killer, points);
        killer.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize("&a+" + points + " stat points for defeating an enemy!"));
    }
}