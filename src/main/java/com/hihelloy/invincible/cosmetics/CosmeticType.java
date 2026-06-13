package com.hihelloy.invincible.cosmetics;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum CosmeticType {

    TECH_JACKET(
    "Tech Jacket", NamedTextColor.AQUA, CosmeticSlot.CHEST,
    new String[]{"DRONE_SWARM","FORCE_FIELD","LASER_ARRAY","NANITE_REPAIR","ROCKET_BARRAGE","SYSTEM_BOOST"},
    "A sentient alien combat jacket granting flight, force fields, and advanced weaponry.",
    250000.0, "tech_jacket", Material.NETHERITE_CHESTPLATE, 1
    ),

    VILTRUMITE_CAPE(
    "Viltrumite Cape", NamedTextColor.DARK_RED, CosmeticSlot.HEAD,
    new String[]{"INTIMIDATION_AURA","WARCRY","ENDURANCE","TERROR_STRIKE","IMPERIAL_RAGE","VILTRUMITE_ENDURANCE"},
    "The ceremonial cape of Viltrum's elite warriors. Radiates dominance that weakens nearby enemies.",
    300000.0, "viltrumite_cape", Material.NETHERITE_HELMET, 1
    ),

    BATTLE_BEAST_ARMOR(
    "Battle Beast's Armor", NamedTextColor.DARK_RED, CosmeticSlot.CHEST,
    new String[]{"UNYIELDING","MANE_SHIELD","WARRIOR_BLOOD","BEAST_ENDURANCE","PRIMAL_FRENZY","KILLING_BLOW"},
    "Crude but nearly indestructible armor fashioned from the hides of creatures Battle Beast has slain.",
    175000.0, "battle_beast_armor", Material.NETHERITE_CHESTPLATE, 2
    ),

    CONQUEST_GAUNTLET(
    "Conquest's Gauntlet", NamedTextColor.RED, CosmeticSlot.LEGS,
    new String[]{"CONQUEROR_STRIKE","SCAR_POWER","BLADE_SPIN","OPPRESSOR_GRIP","DEVASTATION","CONQUEST_RAGE"},
    "Battle-scarred gauntlet of the Viltrumite conqueror Conquest, forged through a thousand victories.",
    200000.0, "conquest_gauntlet", Material.NETHERITE_LEGGINGS, 1
    ),

    GUARDIANS_BELT(
    "Guardian's Belt", NamedTextColor.GOLD, CosmeticSlot.FEET,
    new String[]{"ENERGY_SHIELD","TACTICAL_COMMAND","SHIELD_DEPLOY","GOVERNMENT_BACKUP","COUNTER_INTEL","TACTICAL_RETREAT"},
    "Utility belt worn by senior Guardians of the Globe members, packed with GDA technology.",
    150000.0, "guardians_belt", Material.NETHERITE_BOOTS, 1
    ),

    ATOM_EVE_BRACERS(
    "Atom Eve's Bracers", NamedTextColor.LIGHT_PURPLE, CosmeticSlot.LEGS,
    new String[]{"MATTER_BLAST","ATOMIC_CAGE","PINK_BEAM","MATTER_WALL","TRANSMUTE_GROUND","ATOMIC_BURST"},
    "Slender bracers designed by Eve to focus matter-manipulation powers.",
    225000.0, "atom_eve_bracers", Material.NETHERITE_LEGGINGS, 2
    ),

    IMMORTAL_PLATE(
    "Immortal's Plate", NamedTextColor.GOLD, CosmeticSlot.CHEST,
    new String[]{"TIME_HARDENED","UNDYING_WILL","ANCIENT_FURY","RESURRECTION_SURGE","TIMELESS_STRIKE","RELENTLESS"},
    "Battle-worn golden armor worn by The Immortal across millennia of conflict.",
    280000.0, "immortal_plate", Material.NETHERITE_CHESTPLATE, 3
    ),

    DARKWING_BELT(
    "Darkwing's Utility Belt", NamedTextColor.DARK_GRAY, CosmeticSlot.FEET,
    new String[]{"SMOKE_BOMB","GRAPPLE_LINE","BATARANG_VOLLEY","DARK_SILENCE","TACTICAL_RETREAT","STEALTH_MODE"},
    "High-tech utility belt packed with Darkwing's custom shadow-operative gadgets.",
    185000.0, "darkwing_belt", Material.NETHERITE_BOOTS, 2
    ),

    GDA_HELMET(
    "GDA Combat Helmet", NamedTextColor.DARK_AQUA, CosmeticSlot.HEAD,
    new String[]{"INTEL_SCAN","TACTICAL_SCAN","COUNTER_INTEL","ASSET_EXTRACTION","SUPPRESSION_FIRE","GOVERNMENT_BACKUP"},
    "Standard-issue GDA tactical helmet with integrated HUD and intelligence systems.",
    120000.0, "gda_helmet", Material.NETHERITE_HELMET, 2
    );

    private final String displayName;
    private final NamedTextColor color;
    private final CosmeticSlot slot;
    private final String[] abilityKeys;
    private final String description;
    private final double price;
    private final String modelId;
    private final Material armorMaterial;
    private final int customModelData;

    CosmeticType(String displayName, NamedTextColor color, CosmeticSlot slot,
    String[] abilityKeys, String description, double price,
    String modelId, Material armorMaterial, int customModelData) {
        this.displayName = displayName;
        this.color = color;
        this.slot = slot;
        this.abilityKeys = abilityKeys;
        this.description = description;
        this.price = price;
        this.modelId = modelId;
        this.armorMaterial = armorMaterial;
        this.customModelData = customModelData;
    }

    public String getDisplayName(){
        return displayName;
    }
    public NamedTextColor getColor(){
        return color;
    }
    public CosmeticSlot getSlot(){
        return slot;
    }
    public String[] getAbilityKeys(){
        return abilityKeys;
    }
    public String getDescription(){
        return description;
    }
    public double getPrice(){
        return price;
    }
    public String getModelId(){
        return modelId;
    }
    public Material getModelMaterial(){
        return armorMaterial;
    }
    public int getCustomModelData(){
        return customModelData;
    }

    public static CosmeticType fromName(String name) {
        for (CosmeticType type : values()) {
            if (type.name().equalsIgnoreCase(name)) return type;
            if (type.displayName.equalsIgnoreCase(name)) return type;
        }
        return null;
    }
}