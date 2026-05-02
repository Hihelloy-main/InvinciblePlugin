package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.characters.CharacterType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player selects or changes their character.
 * Cancel to prevent the character change.
 */
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

    /** The character the player had before (may be null on first selection). */
    public CharacterType getOldCharacter() { return oldCharacter; }

    /** The character the player is switching to. */
    public CharacterType getNewCharacter() { return newCharacter; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}