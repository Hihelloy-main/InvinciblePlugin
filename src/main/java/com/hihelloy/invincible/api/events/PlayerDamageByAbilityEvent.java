package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.abilities.AbilityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDamageByAbilityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private final LivingEntity victim;
    private final AbilityType ability;
    private double damage;

    public PlayerDamageByAbilityEvent(Player attacker, LivingEntity victim, AbilityType ability, double damage) {
        super(attacker);
        this.victim = victim;
        this.ability = ability;
        this.damage = damage;
    }

    public LivingEntity getVictim() {
        return victim;
    }

    public AbilityType getAbility() {
        return ability;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
