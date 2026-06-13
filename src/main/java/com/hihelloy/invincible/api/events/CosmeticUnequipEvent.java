package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.cosmetics.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CosmeticUnequipEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final CosmeticType cosmetic;

    public CosmeticUnequipEvent(Player player, CosmeticType cosmetic) {
        super(player);
        this.cosmetic = cosmetic;
    }


    public CosmeticType getCosmetic(){
        return cosmetic;
    }

    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList(){
        return HANDLERS;
    }
}