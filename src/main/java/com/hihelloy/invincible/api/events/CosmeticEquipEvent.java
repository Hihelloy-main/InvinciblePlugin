package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.cosmetics.CosmeticType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player equips a cosmetic item.
 */
public class CosmeticEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final CosmeticType cosmetic;
    private final boolean purchased;

    public CosmeticEquipEvent(Player player, CosmeticType cosmetic, boolean purchased) {
        super(player);
        this.cosmetic = cosmetic;
        this.purchased = purchased;
    }

    /** The cosmetic being equipped. */
    public CosmeticType getCosmetic() { return cosmetic; }

    /** True if this equip was triggered by a new purchase (not just re-equipping owned item). */
    public boolean wasPurchased() { return purchased; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}