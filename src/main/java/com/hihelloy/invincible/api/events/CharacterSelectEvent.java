package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.characters.CharacterType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CharacterSelectEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final CharacterType oldCharacter;
    private final CharacterType newCharacter;

    public CharacterSelectEvent(Player player, CharacterType oldCharacter,
    CharacterType newCharacter) {
        super(player);
        this.oldCharacter = oldCharacter;
        this.newCharacter = newCharacter;
    }


    public CharacterType getOldCharacter(){
        return oldCharacter;
    }


    public CharacterType getNewCharacter(){
        return newCharacter;
    }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList(){
        return HANDLERS;
    }
}