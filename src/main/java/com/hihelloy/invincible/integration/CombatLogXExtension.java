package com.hihelloy.invincible.integration;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CombatLogXExtension {

    private final ICombatLogX api;

    public CombatLogXExtension(ICombatLogX api) {
        this.api = api;
    }

    public ICombatManager getCombatManager() {
        return api.getCombatManager();
    }

    public void tag(Player attacker, Entity victim) {
        ICombatManager mgr = api.getCombatManager();
        TagType tagType = (victim instanceof Player) ? TagType.PLAYER : TagType.MOB;
        mgr.tag(attacker, victim, tagType, TagReason.ATTACKER);
        if (victim instanceof Player defender) {
            mgr.tag(defender, attacker, TagType.PLAYER, TagReason.ATTACKED);
        }
    }
}