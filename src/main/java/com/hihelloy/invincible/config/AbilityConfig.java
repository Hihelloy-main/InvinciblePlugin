package com.hihelloy.invincible.config;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.abilities.ActivationTrigger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class AbilityConfig {

    private final InvinciblePlugin plugin;
    private FileConfiguration diskConfig;

    public static final ThreadLocal<String> activeConfigKey = new ThreadLocal<>();

    public AbilityConfig(InvinciblePlugin plugin) {
        this.plugin = plugin;
        load();
        loadConfigAbilities();
    }

    private void load() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        diskConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
        loadConfigAbilities();
        plugin.getLogger().info("AbilityConfig reloaded from disk.");
    }

    public void loadConfigAbilities() {
        AbilityType.clearConfigRegistry();
        ConfigurationSection abilitiesSec = diskConfig.getConfigurationSection("abilities");
        if (abilitiesSec == null) return;
        for (String key : abilitiesSec.getKeys(false)) {
            boolean isEnum = false;
            for (AbilityType t : AbilityType.values()) {
                if (t.name().equalsIgnoreCase(key)) {
                    isEnum = true;
                    break;
                }
            }
            if (isEnum) continue;
            ConfigurationSection sec = abilitiesSec.getConfigurationSection(key);
            if (sec == null) continue;
            String displayName = sec.getString("display-name", key);
            String description = sec.getString("description", "");
            String categoryStr = sec.getString("category", "OFFENSE");
            long cooldownMs = sec.getLong("cooldown-ms", 5000L);
            List<String> effects = sec.getStringList("effects");
            List<String> allowedChars = sec.getStringList("allowed-characters");
            com.hihelloy.invincible.abilities.AbilityCategory category;
            try {
                category = com.hihelloy.invincible.abilities.AbilityCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                category = com.hihelloy.invincible.abilities.AbilityCategory.OFFENSE;
            }
            AbilityType.registerConfigAbility(new AbilityType.ConfigAbilityMeta(
            key.toUpperCase(), displayName, category, cooldownMs,
            net.kyori.adventure.text.format.NamedTextColor.YELLOW,
            description, effects, allowedChars));
        }
        plugin.getLogger().info("[AbilityConfig] Loaded " + AbilityType.getConfigAbilities().size() + " config abilities.");
    }

    public boolean isEnabled(AbilityType ability) {
        return getBool(ability, "enabled", true);
    }

    public long getCooldown(AbilityType ability) {
        if (ability == AbilityType.CONFIG_ABILITY) {
            String key = activeConfigKey.get();
            if (key != null) {
                AbilityType.ConfigAbilityMeta meta = AbilityType.getConfigMeta(key);
                if (meta != null) return meta.cooldownMs;
            }
            return 5000L;
        }
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

    public String getDeathMessage(AbilityType ability) {
        String def = "&c%victim% &7was slain by &c%killer% &7using &e%ability%&7.";
        ConfigurationSection sec = getSection(ability);
        if (sec == null) return def;
        return sec.getString("death-message", def);
    }

    public List<String> getEffectsList(AbilityType ability) {
        ConfigurationSection sec = getSection(ability);
        if (sec == null) return Collections.emptyList();
        List<String> list = sec.getStringList("effects");
        return list != null ? list : Collections.emptyList();
    }

    public List<String> getAllowedCharacters(AbilityType ability) {
        ConfigurationSection sec = getSection(ability);
        if (sec == null) return Collections.emptyList();
        List<String> list = sec.getStringList("allowed-characters");
        return list != null ? list : Collections.emptyList();
    }

    private ConfigurationSection getSection(AbilityType ability) {
        if (ability == AbilityType.GENERIC_DASH) {
            return diskConfig.getConfigurationSection("universal-slot-5");
        }
        if (ability == AbilityType.CONFIG_ABILITY) {
            String key = activeConfigKey.get();
            if (key == null) return null;
            return diskConfig.getConfigurationSection("abilities." + key.toUpperCase());
        }
        return diskConfig.getConfigurationSection("abilities." + ability.name());
    }

    public ConfigurationSection getSectionByKey(String key) {
        return diskConfig.getConfigurationSection("abilities." + key.toUpperCase());
    }

    public FileConfiguration getDiskConfig() {
        return diskConfig;
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
