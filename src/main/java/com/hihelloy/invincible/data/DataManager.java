package com.hihelloy.invincible.data;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterManager;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import com.hihelloy.invincible.stats.StatType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {

    private final InvinciblePlugin plugin;
    private final File dataFolder;
    private final Map<UUID, FileConfiguration> playerConfigs = new HashMap<>();
    private final CharacterManager characterManager;

    public DataManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        this.characterManager = new CharacterManager(plugin);
    }

    public CharacterManager getCharacterManager() {
        return characterManager;
    }

    private FileConfiguration getPlayerConfig(UUID uuid) {
        if (playerConfigs.containsKey(uuid)) return playerConfigs.get(uuid);
        File file = new File(dataFolder, uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        playerConfigs.put(uuid, config);
        return config;
    }

    private void savePlayerConfig(UUID uuid) {
        FileConfiguration config = playerConfigs.get(uuid);
        if (config == null) return;
        File file = new File(dataFolder, uuid + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save data for " + uuid + ": " + e.getMessage());
        }
    }

    public void setCharacter(UUID uuid, CharacterType type) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("character", type.name());
        savePlayerConfig(uuid);
    }

    public CharacterType getCharacter(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        String name = config.getString("character");
        if (name == null) return null;
        try {
            return CharacterType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void saveBoundAbilities(UUID uuid, AbilityType[] abilities) {
        FileConfiguration config = getPlayerConfig(uuid);
        for (int i = 0; i < 4; i++) {
            config.set("abilities.slot" + (i + 1), abilities[i] != null ? abilities[i].name() : null);
        }
        savePlayerConfig(uuid);
    }

    public AbilityType[] loadBoundAbilities(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        AbilityType[] abilities = new AbilityType[4];
        for (int i = 0; i < 4; i++) {
            String name = config.getString("abilities.slot" + (i + 1));
            if (name != null) {
                try {
                    abilities[i] = AbilityType.valueOf(name);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return abilities;
    }

    public void saveEquippedCosmetics(UUID uuid, Set<CosmeticType> cosmetics) {
        FileConfiguration config = getPlayerConfig(uuid);
        List<String> names = new ArrayList<>();
        for (CosmeticType cosmetic : cosmetics) names.add(cosmetic.name());
        config.set("cosmetics.equipped", names);
        savePlayerConfig(uuid);
    }

    public Set<CosmeticType> loadEquippedCosmetics(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        Set<CosmeticType> result = new HashSet<>();
        for (String name : config.getStringList("cosmetics.equipped")) {
            try {
                result.add(CosmeticType.valueOf(name)); } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    public void saveOwnedCosmetics(UUID uuid, Set<CosmeticType> cosmetics) {
        FileConfiguration config = getPlayerConfig(uuid);
        List<String> names = new ArrayList<>();
        for (CosmeticType cosmetic : cosmetics) names.add(cosmetic.name());
        config.set("cosmetics.owned", names);
        savePlayerConfig(uuid);
    }

    public Set<CosmeticType> loadOwnedCosmetics(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        Set<CosmeticType> result = new HashSet<>();
        for (String name : config.getStringList("cosmetics.owned")) {
            try {
                result.add(CosmeticType.valueOf(name)); } catch (IllegalArgumentException ignored) {}
        }
        return result;
    }

    public int getStatPoints(UUID uuid) {
        return getPlayerConfig(uuid).getInt("stats.points", 0);
    }

    public void setStatPoints(UUID uuid, int points) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("stats.points", points);
        savePlayerConfig(uuid);
    }

    public int getStatLevel(UUID uuid, StatType stat) {
        return getPlayerConfig(uuid).getInt("stats." + stat.name().toLowerCase(), 0);
    }

    public void setStatLevel(UUID uuid, StatType stat, int level) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("stats." + stat.name().toLowerCase(), level);
        savePlayerConfig(uuid);
    }

    public void loadPlayer(UUID uuid) {
        getPlayerConfig(uuid);
    }

    private final Map<UUID, String> customHeroNames = new HashMap<>();

    public boolean hasJoinedBefore(UUID uuid) {
        FileConfiguration cfg = getPlayerConfig(uuid);
        if (cfg.getBoolean("has-joined", false)) return true;
        cfg.set("has-joined", true);
        savePlayerConfig(uuid);
        return false;
    }

    public void saveCustomHeroName(UUID uuid, String name) {
        customHeroNames.put(uuid, name);
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("custom-hero-name", name);
        savePlayerConfig(uuid);
    }

    public String getCustomHeroName(UUID uuid) {
        if (customHeroNames.containsKey(uuid)) return customHeroNames.get(uuid);
        return getPlayerConfig(uuid).getString("custom-hero-name", "Custom Hero");
    }

    public void saveCustomHeroFly(java.util.UUID uuid, boolean fly) {
        org.bukkit.configuration.file.FileConfiguration config = getPlayerConfig(uuid);
        config.set("custom-hero-fly", fly);
        savePlayerConfig(uuid);
    }

    public boolean getCustomHeroFly(java.util.UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("custom-hero-fly", true);
    }

    public void saveCustomHeroStrength(java.util.UUID uuid, boolean strength) {
        org.bukkit.configuration.file.FileConfiguration config = getPlayerConfig(uuid);
        config.set("custom-hero-strength", strength);
        savePlayerConfig(uuid);
    }

    public boolean getCustomHeroStrength(java.util.UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("custom-hero-strength", true);
    }

    public void saveCustomHeroSpeed(java.util.UUID uuid, boolean speed) {
        org.bukkit.configuration.file.FileConfiguration config = getPlayerConfig(uuid);
        config.set("custom-hero-speed", speed);
        savePlayerConfig(uuid);
    }

    public boolean getCustomHeroSpeed(java.util.UUID uuid) {
        return getPlayerConfig(uuid).getBoolean("custom-hero-speed", true);
    }

    public void saveCustomHeroAbilities(java.util.UUID uuid, java.util.List<String> abilities) {
        org.bukkit.configuration.file.FileConfiguration config = getPlayerConfig(uuid);
        config.set("custom-hero-abilities", abilities);
        savePlayerConfig(uuid);
    }

    public java.util.List<String> getAllAbilityKeys() {
        java.util.List<String> all = new java.util.ArrayList<>();
        for (com.hihelloy.invincible.abilities.AbilityType t : com.hihelloy.invincible.abilities.AbilityType.values()) {
            if (t == com.hihelloy.invincible.abilities.AbilityType.CONFIG_ABILITY) continue;
            all.add(t.name());
        }
        for (String key : com.hihelloy.invincible.abilities.AbilityType.getConfigAbilities().keySet()) {
            all.add(key);
        }
        return all;
    }

    public java.util.List<String> getCustomHeroAbilities(java.util.UUID uuid) {
        java.util.List<String> saved = getPlayerConfig(uuid).getStringList("custom-hero-abilities");
        if (saved == null || saved.isEmpty()) {
            return getAllAbilityKeys();
        }
        return saved;
    }

    public void unloadPlayer(UUID uuid) {
        savePlayerConfig(uuid);
        playerConfigs.remove(uuid);
    }

    public void saveAll() {
        for (UUID uuid : playerConfigs.keySet()) {
            savePlayerConfig(uuid);
        }
    }
}