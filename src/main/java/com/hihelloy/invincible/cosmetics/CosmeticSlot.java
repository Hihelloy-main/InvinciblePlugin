package com.hihelloy.invincible.cosmetics;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.inventory.EquipmentSlot;

public enum CosmeticSlot {
    HEAD (EquipmentSlot.HEAD, PlayerArmorChangeEvent.SlotType.HEAD, 5, 36),
    CHEST (EquipmentSlot.CHEST, PlayerArmorChangeEvent.SlotType.CHEST, 6, 37),
    LEGS (EquipmentSlot.LEGS, PlayerArmorChangeEvent.SlotType.LEGS, 7, 38),
    FEET (EquipmentSlot.FEET, PlayerArmorChangeEvent.SlotType.FEET, 8, 39),
    OFFHAND(EquipmentSlot.OFF_HAND, null, 40, 40);

    private final EquipmentSlot equipmentSlot;
    private final PlayerArmorChangeEvent.SlotType armorSlotType;
    private final int displaySlot;
    private final int inventorySlot;

    CosmeticSlot(EquipmentSlot equipmentSlot, PlayerArmorChangeEvent.SlotType armorSlotType,
                 int displaySlot, int inventorySlot) {
        this.equipmentSlot = equipmentSlot;
        this.armorSlotType = armorSlotType;
        this.displaySlot = displaySlot;
        this.inventorySlot = inventorySlot;
    }

    public EquipmentSlot getEquipmentSlot() { return equipmentSlot; }
    public PlayerArmorChangeEvent.SlotType getArmorSlotType() { return armorSlotType; }
    public int getRawInventorySlot() { return displaySlot; }
    public int getInventorySlot() { return inventorySlot; }

    public static CosmeticSlot fromArmorSlotType(PlayerArmorChangeEvent.SlotType type) {
        for (CosmeticSlot slot : values()) {
            if (slot.armorSlotType == type) return slot;
        }
        return null;
    }
}