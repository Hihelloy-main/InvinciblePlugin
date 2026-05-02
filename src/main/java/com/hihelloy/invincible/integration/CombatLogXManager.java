package com.hihelloy.invincible.integration;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CombatLogXManager {

    private final InvinciblePlugin plugin;
    private ICombatLogX api = null;
    private boolean hooked = false;

    public CombatLogXManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        PluginManager pm = plugin.getServer().getPluginManager();
        if (pm.isPluginEnabled("CombatLogX")) {
            Plugin clxPlugin = pm.getPlugin("CombatLogX");
            if (clxPlugin instanceof ICombatLogX combatLogX) {
                this.api = combatLogX;
                this.hooked = true;
                plugin.getLogger().info("CombatLogX hooked successfully.");
            } else {
                plugin.getLogger().warning("CombatLogX found but API cast failed.");
            }
        } else {
            plugin.getLogger().info("CombatLogX not found — combat tagging disabled.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    public void tag(Player attacker, Entity victim) {
        if (!hooked || api == null) return;
        ICombatManager mgr = api.getCombatManager();

        TagType tagType = (victim instanceof Player) ? TagType.PLAYER : TagType.MOB;

        mgr.tag(attacker, victim, tagType, TagReason.ATTACKER);

        if (victim instanceof Player defender) {
            mgr.tag(defender, attacker, TagType.PLAYER, TagReason.ATTACKED);
        }
    }
}