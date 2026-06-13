package com.hihelloy.invincible.shop;

import org.bukkit.Material;

public enum ForgeRecipe {

    OMNI_IRON_CORE(
    "MATERIAL", "OMNI_IRON_CORE",
    "Iron Core", Material.IRON_INGOT, 20,
    "§7Basic forge ingredient.\nUsed in most weapon recipes."
    ),
    OMNI_ARCANE_CRYSTAL(
    "MATERIAL", "OMNI_ARCANE_CRYSTAL",
    "Arcane Crystal", Material.AMETHYST_SHARD, 30,
    "§5Magic ingredient.\nRequired for arcane weapons."
    ),
    OMNI_TECH_CHIP(
    "MATERIAL", "OMNI_TECH_CHIP",
    "Tech Chip", Material.ECHO_SHARD, 30,
    "§bGDA nano-circuitry.\nFor GDA & Robot weapons."
    ),
    OMNI_SHADOW_METAL(
    "MATERIAL", "OMNI_SHADOW_METAL",
    "Shadow Metal", Material.NETHERITE_SCRAP, 35,
    "§8Dark alloy for Darkwing\nweapons & batarangs."
    ),
    OMNI_BEAST_ESSENCE(
    "MATERIAL", "OMNI_BEAST_ESSENCE",
    "Beast Essence", Material.MAGMA_CREAM, 35,
    "§6Primal energy.\nFor Battle Beast & Monster Girl."
    ),
    OMNI_VOID_FRAGMENT(
    "MATERIAL", "OMNI_VOID_FRAGMENT",
    "Void Fragment", Material.CHORUS_FRUIT, 50,
    "§dDimensional energy.\nFor Atom Eve & Angstrom."
    ),
    OMNI_VILTRUMITE_ALLOY(
    "MATERIAL", "OMNI_VILTRUMITE_ALLOY",
    "Viltrumite Alloy", Material.NETHERITE_INGOT, 60,
    "§4Near-indestructible metal.\nFor Invincible & Omni-Man."
    );

    public final String mmoType;
    public final String mmoId;
    public final String displayName;
    public final Material icon;
    public final int cost;
    public final String description;

    ForgeRecipe(String mmoType, String mmoId, String displayName, Material icon,
    int cost, String description) {
        this.mmoType = mmoType;
        this.mmoId = mmoId;
        this.displayName = displayName;
        this.icon = icon;
        this.cost = cost;
        this.description = description;
    }
}