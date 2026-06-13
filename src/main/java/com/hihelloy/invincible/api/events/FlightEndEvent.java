package com.hihelloy.invincible.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class FlightEndEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public FlightEndEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
