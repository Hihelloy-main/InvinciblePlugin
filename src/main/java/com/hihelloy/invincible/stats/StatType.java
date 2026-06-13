package com.hihelloy.invincible.stats;

import net.kyori.adventure.text.format.NamedTextColor;

public enum StatType {
    DAMAGE ("Damage", "Increases ability damage output", NamedTextColor.RED),
    SPEED ("Speed", "Increases walk speed fully; no effect on flight", NamedTextColor.YELLOW),
    DURATION ("Duration", "Increases ability active duration", NamedTextColor.GREEN),
    DEFENSE ("Defense", "Increases damage resistance", NamedTextColor.BLUE),
    REGENERATION("Regeneration", "Passively regenerates health", NamedTextColor.DARK_GREEN);

    private final String displayName;
    private final String description;
    private final NamedTextColor color;

    StatType(String displayName, String description, NamedTextColor color) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    public String getDisplayName(){
        return displayName;
    }
    public String getDescription(){
        return description;
    }
    public NamedTextColor getColor(){
        return color;
    }
}