package com.hihelloy.invincible.api.events;

import com.hihelloy.invincible.stats.StatType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Fired when a player upgrades a stat.
 * Cancel to prevent the upgrade and refund the point.
 */
public class StatUpgradeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled = false;

    private final StatType stat;
    private final int oldLevel;
    private final int newLevel;

    public StatUpgradeEvent(Player player, StatType stat, int oldLevel, int newLevel) {
        super(player);
        this.stat = stat;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    /** The stat being upgraded. */
    public StatType getStat() { return stat; }

    /** The level before the upgrade. */
    public int getOldLevel() { return oldLevel; }

    /** The level after the upgrade. */
    public int getNewLevel() { return newLevel; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean c) { this.cancelled = c; }
    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}