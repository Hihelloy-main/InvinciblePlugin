package com.hihelloy.invincible;

import com.hihelloy.invincible.abilities.AbilityManager;
import com.hihelloy.invincible.characters.CharacterManager;
import com.hihelloy.invincible.characters.CustomNameInputManager;
import com.hihelloy.invincible.combat.CombatManager;
import com.hihelloy.invincible.commands.InvincibleAdminCommand;
import com.hihelloy.invincible.commands.InvincibleCommand;
import com.hihelloy.invincible.commands.PresetCommand;
import com.hihelloy.invincible.config.AbilityConfig;
import com.hihelloy.invincible.cosmetics.CosmeticManager;
import com.hihelloy.invincible.data.DataManager;
import com.hihelloy.invincible.flight.FlightManager;
import com.hihelloy.invincible.gui.AbilityCreateGUI;
import com.hihelloy.invincible.gui.AbilityListGUI;
import com.hihelloy.invincible.listeners.AbilityCreateListener;
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
    private Economy economy;
    private PresetCommand presetCommand;
    private CustomNameInputManager customNameInputManager;
    private AbilityCreateGUI abilityCreateGUI;
    private AbilityListGUI abilityListGUI;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);

        worldGuard = new WorldGuard(this);
        dataManager = new DataManager(this);
        abilityConfig = new AbilityConfig(this);
        combatManager = new CombatManager(this,
        getConfig().getLong("combat.combo-window-ms", 2000L),
        getConfig().getLong("combat.iframe-ticks", 8L));
        flightManager = new FlightManager(this);
        statManager = new StatManager(this);
        abilityManager = new AbilityManager(this);
        scoreboardManager = new ScoreboardManager(this);
        cosmeticManager = new CosmeticManager(this);
        customNameInputManager = new CustomNameInputManager(this);
        abilityCreateGUI = new AbilityCreateGUI(this);
        abilityListGUI = new AbilityListGUI(this);

        setupEconomy();

        presetCommand = new PresetCommand(this);

        InvincibleCommand invCmd = new InvincibleCommand(this);
        getCommand("invincible").setExecutor(invCmd);
        getCommand("invincible").setTabCompleter(invCmd);

        InvincibleAdminCommand adminCmd = new InvincibleAdminCommand(this);
        getCommand("invincibleadmin").setExecutor(adminCmd);
        getCommand("invincibleadmin").setTabCompleter(adminCmd);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new FlightListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new DoubleJumpDetector(this), this);
        getServer().getPluginManager().registerEvents(new CosmeticListener(this), this);
        getServer().getPluginManager().registerEvents(scoreboardManager, this);
        getServer().getPluginManager().registerEvents(new AbilityCreateListener(this, abilityCreateGUI), this);
        getServer().getPluginManager().registerEvents(abilityListGUI, this);

        getLogger().info("InvinciblePlugin v2.0 enabled.");
    }

    @Override
    public void onDisable() {
        if (scoreboardManager != null) scoreboardManager.cleanup();
        if (dataManager != null) dataManager.saveAll();
        getLogger().info("InvinciblePlugin disabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        getConfig().options().copyDefaults(false);
        abilityConfig.reload();
        scoreboardManager.restartUpdateTask();
        for (org.bukkit.entity.Player p : getServer().getOnlinePlayers()) {
            scoreboardManager.updateScoreboard(p);
        }
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

    public static InvinciblePlugin getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public FlightManager getFlightManager() {
        return flightManager;
    }

    public StatManager getStatManager() {
        return statManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public AbilityConfig getAbilityConfig() {
        return abilityConfig;
    }

    public CosmeticManager getCosmeticManager() {
        return cosmeticManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public WorldGuard getWorldGuard() {
        return worldGuard;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean hasEconomy() {
        return economy != null;
    }

    public PresetCommand getPresetCommand() {
        return presetCommand;
    }

    public CustomNameInputManager getCustomNameInputManager() {
        return customNameInputManager;
    }

    public AbilityCreateGUI getAbilityCreateGUI() {
        return abilityCreateGUI;
    }

    public AbilityListGUI getAbilityListGUI() {
        return abilityListGUI;
    }
}
