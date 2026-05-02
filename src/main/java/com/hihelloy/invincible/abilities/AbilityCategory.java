package com.hihelloy.invincible.abilities;

import net.kyori.adventure.text.format.NamedTextColor;

public enum AbilityCategory {
    OFFENSE ("Offense", NamedTextColor.RED),
    DEFENSE ("Defense", NamedTextColor.AQUA),
    MOBILITY("Mobility", NamedTextColor.YELLOW),
    UTILITY ("Utility", NamedTextColor.GREEN);

    private final String displayName;
    private final NamedTextColor color;

    AbilityCategory(String displayName, NamedTextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public NamedTextColor getColor() { return color; }
}