package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AbilityActivateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final AbilityType ability;
    private final CharacterType character;
    private final int slot;

    private double damage;
    private int duration;
    private double radius;
    private int cooldown;

    public AbilityActivateEvent(Player player, AbilityType ability,
    CharacterType character, int slot) {
        super(player);
        this.ability = ability;
        this.character = character;
        this.slot = slot;
        this.damage = 0.0;
        this.duration = 0;
        this.radius = 0.0;
        this.cooldown = 0;
    }

    public AbilityType getAbility(){
        return ability;
    }
    public CharacterType getCharacter(){
        return character;
    }
    public int getSlot(){
        return slot;
    }

    public double getDamage(){
        return damage;
    }
    public void setDamage(double d){
        this.damage = d;
    }

    public int getDuration(){
        return duration;
    }
    public void setDuration(int d){
        this.duration = d;
    }

    public double getRadius(){
        return radius;
    }
    public void setRadius(double r){
        this.radius = r;
    }

    public int getCooldownMs(){
        return cooldown;
    }
    public void setCooldownMs(int c){
        this.cooldown = Math.max(0, c);
    }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList(){
        return HANDLERS;
    }
}