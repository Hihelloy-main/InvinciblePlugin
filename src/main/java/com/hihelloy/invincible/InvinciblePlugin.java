package com.hihelloy.invincible;

import com.hihelloy.invincible.abilities.AbilityManager;
import com.hihelloy.invincible.characters.CustomNameInputManager;
import com.hihelloy.invincible.combat.CombatManager;
import com.hihelloy.invincible.commands.InvincibleAdminCommand;
import com.hihelloy.invincible.commands.InvincibleCommand;
import com.hihelloy.invincible.commands.PresetCommand;
import com.hihelloy.invincible.config.AbilityConfig;
import com.hihelloy.invincible.cosmetics.CosmeticManager;
import com.hihelloy.invincible.data.DataManager;
import com.hihelloy.invincible.flight.FlightManager;
import com.hihelloy.invincible.integration.CombatLogXManager;
import com.hihelloy.invincible.listeners.AbilityListener;
import com.hihelloy.invincible.listeners.CombatListener;
import com.hihelloy.invincible.listeners.CosmeticListener;
import com.hihelloy.invincible.listeners.DoubleJumpDetector;
import com.hihelloy.invincible.listeners.FlightListener;
import com.hihelloy.invincible.listeners.GUIListener;
import com.hihelloy.invincible.listeners.PlayerJoinListener;
import com.hihelloy.invincible.listeners.PlayerQuitListener;
import com.hihelloy.invincible.scoreboard.ScoreboardManager;
import com.hihelloy.invincible.stats.StatManager;
import com.hihelloy.invincible.util.WorldGuard;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class InvinciblePlugin extends JavaPlugin {

    private static InvinciblePlugin instance;

    private DataManager dataManager;
    private FlightManager flightManager;
    private StatManager statManager;
    private ScoreboardManager scoreboardManager;
    private AbilityManager abilityManager;
    private AbilityConfig abilityConfig;
    private CosmeticManager cosmeticManager;
    private CombatManager combatManager;
    private WorldGuard worldGuard;
    private CombatLogXManager combatLogX;
    private PresetCommand presetCommand;
    private CustomNameInputManager customNameInputManager;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().warning("Vault economy not found — cosmetic purchases disabled.");
        }

        dataManager = new DataManager(this);
        worldGuard = new WorldGuard(this);
        combatLogX = new CombatLogXManager(this);
        presetCommand = new PresetCommand(this);
        customNameInputManager = new CustomNameInputManager(this);
        statManager = new StatManager(this);
        flightManager = new FlightManager(this);
        abilityConfig = new AbilityConfig(this);
        abilityManager = new AbilityManager(this);
        cosmeticManager = new CosmeticManager(this);
        combatManager = new CombatManager(
                getConfig().getLong("combat.combo-window-ms", 2000L),
                getConfig().getLong("combat.iframe-ticks", 8L)
        );
        scoreboardManager = new ScoreboardManager(this);

        InvincibleCommand mainCommand = new InvincibleCommand(this);
        getCommand("invincible").setExecutor(mainCommand);
        getCommand("invincible").setTabCompleter(mainCommand);

        InvincibleAdminCommand adminCommand = new InvincibleAdminCommand(this);
        getCommand("invincibleadmin").setExecutor(adminCommand);
        getCommand("invincibleadmin").setTabCompleter(adminCommand);

        getCommand("invpreset").setExecutor(presetCommand);
        getCommand("invpreset").setTabCompleter(presetCommand);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new FlightListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpDetector(this), this);
        getServer().getPluginManager().registerEvents(new CosmeticListener(this), this);

        getLogger().info("InvinciblePlugin enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveAll();
        if (scoreboardManager != null) scoreboardManager.cleanup();
        if (flightManager != null) flightManager.cleanup();
        getLogger().info("InvinciblePlugin disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        abilityConfig.reload();
        getLogger().info("Config reloaded.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    public static InvinciblePlugin getInstance() { return instance; }
    public DataManager getDataManager() { return dataManager; }
    public FlightManager getFlightManager() { return flightManager; }
    public StatManager getStatManager() { return statManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public AbilityConfig getAbilityConfig() { return abilityConfig; }
    public CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public CombatManager getCombatManager() { return combatManager; }
    public WorldGuard getWorldGuard() { return worldGuard; }
    public CombatLogXManager getCombatLogX() { return combatLogX; }
    public PresetCommand getPresetCommand() { return presetCommand; }
    public CustomNameInputManager getCustomNameInputManager() { return customNameInputManager; }
    public boolean hasEconomy() { return economy != null; }
    public Economy getEconomy() { return economy; }
}