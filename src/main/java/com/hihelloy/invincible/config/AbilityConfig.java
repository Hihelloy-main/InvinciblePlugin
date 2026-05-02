package com.hihelloy.invincible.config;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.abilities.ActivationTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class AbilityConfig {

    private final InvinciblePlugin plugin;

    public AbilityConfig(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.getLogger().info("AbilityConfig reloaded from config.yml.");
    }

    public boolean isEnabled(AbilityType ability) {
        return getBool(ability, "enabled", true);
    }


    public long getCooldown(AbilityType ability) {
        return (long) getInt(ability, "cooldown-ms", (int) ability.getCooldownMs());
    }


    public double getDamage(AbilityType ability, double def) {
        return getDouble(ability, "damage", def);
    }


    public double getTickDamage(AbilityType ability, double def) {
        return getDouble(ability, "tick-damage", def);
    }


    public double getKnockback(AbilityType ability, double def) {
        return getDouble(ability, "knockback", def);
    }


    public double getReturnMultiplier(AbilityType ability, double def) {
        return getDouble(ability, "return-multiplier", def);
    }


    public double getDamageReduction(AbilityType ability, double def) {
        return getDouble(ability, "damage-reduction", def);
    }


    public double getRadius(AbilityType ability, double def) {
        return getDouble(ability, "radius", def);
    }


    public double getRange(AbilityType ability, double def) {
        return getDouble(ability, "range", def);
    }


    public double getTargetRadius(AbilityType ability, double def) {
        return getDouble(ability, "target-radius", def);
    }


    public double getStrikeRadius(AbilityType ability, double def) {
        return getDouble(ability, "strike-radius", def);
    }


    public double getAoeRadius(AbilityType ability, double def) {
        return getDouble(ability, "aoe-radius", def);
    }


    public double getExplosionPower(AbilityType ability, double def) {
        return getDouble(ability, "explosion-power", def);
    }


    public int getDurationTicks(AbilityType ability, int def) {
        return getInt(ability, "duration-ticks", def);
    }


    public int getDelayTicks(AbilityType ability, int def) {
        return getInt(ability, "delay-ticks", def);
    }


    public int getChargeTicks(AbilityType ability, int def) {
        return getInt(ability, "charge-ticks", def);
    }


    public int getStrikeTicks(AbilityType ability, int def) {
        return getInt(ability, "strike-ticks", def);
    }


    public int getHits(AbilityType ability, int def) {
        return getInt(ability, "hits", def);
    }


    public int getHitDelay(AbilityType ability, int def) {
        return getInt(ability, "hit-delay-ticks", def);
    }


    public int getTargetCount(AbilityType ability, int def) {
        return getInt(ability, "target-count", def);
    }


    public int getCount(AbilityType ability, String key, int def) {
        return getInt(ability, key, def);
    }


    public double getVelocity(AbilityType ability, double def) {
        return getDouble(ability, "velocity", def);
    }


    public double getTeleportHeight(AbilityType ability, double def) {
        return getDouble(ability, "teleport-height", def);
    }


    public double getHeal(AbilityType ability, double def) {
        return getDouble(ability, "heal", def);
    }


    public double getHealPercent(AbilityType ability, double def) {
        return getDouble(ability, "heal-percent", def);
    }


    public double getMinHeal(AbilityType ability, double def) {
        return getDouble(ability, "min-heal", def);
    }


    public double getStealPercent(AbilityType ability, double def) {
        return getDouble(ability, "steal-percent", def) / 100.0;
    }


    public double getMaxSteal(AbilityType ability, double def) {
        return getDouble(ability, "max-steal", def);
    }


    public int getResistanceLevel(AbilityType ability, int def) {
        return getInt(ability, "resistance-level", def);
    }


    public int getRegenLevel(AbilityType ability, int def) {
        return getInt(ability, "regen-level", def);
    }


    public int getRegenTicks(AbilityType ability, int def) {
        return getInt(ability, "regen-ticks", def);
    }


    public int getStrengthLevel(AbilityType ability, int def) {
        return getInt(ability, "strength-level", def);
    }


    public int getStrengthTicks(AbilityType ability, int def) {
        return getInt(ability, "strength-ticks", def);
    }


    public int getSpeedLevel(AbilityType ability, int def) {
        return getInt(ability, "speed-level", def);
    }


    public int getSpeedTicks(AbilityType ability, int def) {
        return getInt(ability, "speed-ticks", def);
    }


    public int getSlownessLevel(AbilityType ability, int def) {
        return getInt(ability, "slowness-level", def);
    }


    public int getSlownessTicks(AbilityType ability, int def) {
        return getInt(ability, "slowness-ticks", def);
    }


    public int getWeaknessTicks(AbilityType ability, int def) {
        return getInt(ability, "weakness-ticks", def);
    }


    public int getWeaknessLevel(AbilityType ability, int def) {
        return getInt(ability, "weakness-level", def);
    }


    public int getFireTicks(AbilityType ability, int def) {
        return getInt(ability, "fire-ticks", def);
    }


    public int getHasteLevel(AbilityType ability, int def) {
        return getInt(ability, "haste-level", def);
    }


    public int getAbsorptionLevel(AbilityType ability, int def) {
        return getInt(ability, "absorption-level", def);
    }


    public int getPoisonTicks(AbilityType ability, int def) {
        return getInt(ability, "poison-ticks", def);
    }


    public int getBlindnessTicks(AbilityType ability, int def) {
        return getInt(ability, "blindness-ticks", def);
    }


    public int getExecuteThreshold(AbilityType ability, int def) {
        return getInt(ability, "execute-threshold-percent", def);
    }

    public ActivationTrigger getActivationTrigger(AbilityType ability) {
        String raw = getString(ability, "activation-trigger", "LEFT_CLICK");
        try {
            return ActivationTrigger.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ActivationTrigger.LEFT_CLICK;
        }
    }

    private ConfigurationSection getSection(AbilityType ability) {
        FileConfiguration cfg = plugin.getConfig();
        if (ability == AbilityType.GENERIC_DASH) {
            return cfg.getConfigurationSection("universal-slot-5");
        }
        return cfg.getConfigurationSection("abilities." + ability.name());
    }

    private double getDouble(AbilityType ability, String key, double def) {
        ConfigurationSection sec = getSection(ability);
        return sec != null ? sec.getDouble(key, def) : def;
    }

    private int getInt(AbilityType ability, String key, int def) {
        ConfigurationSection sec = getSection(ability);
        return sec != null ? sec.getInt(key, def) : def;
    }

    private String getString(AbilityType ability, String key, String def) {
        ConfigurationSection sec = getSection(ability);
        return sec != null ? sec.getString(key, def) : def;
    }

    private boolean getBool(AbilityType ability, String key, boolean def) {
        ConfigurationSection sec = getSection(ability);
        return sec != null ? sec.getBoolean(key, def) : def;
    }
}