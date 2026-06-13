package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.abilities.AbilityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AbilityEndEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final AbilityType ability;
    private final EndReason reason;

    public enum EndReason {
        DURATION_EXPIRED,
        PLAYER_CANCELLED,
        PLUGIN_CANCELLED,
        PLAYER_DEATH,
        ABILITY_INTERRUPTED
    }

    public AbilityEndEvent(Player player, AbilityType ability, EndReason reason) {
        super(player);
        this.ability = ability;
        this.reason = reason;
    }

    public AbilityType getAbility() {
        return ability;
    }

    public EndReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
