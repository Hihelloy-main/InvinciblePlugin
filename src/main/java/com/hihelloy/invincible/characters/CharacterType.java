package com.hihelloy.invincible.characters;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum CharacterType {

    INVINCIBLE(
            "Mark Grayson / Invincible",
            NamedTextColor.BLUE, Material.BLUE_WOOL,
            true, true, true, "steve",
            new String[]{
                    "INVULNERABILITY", "COMBO_STRIKE", "SONIC_CLAP", "ATOMIC_PUNCH",
                    "REGENERATION", "TREMOR_SLAM", "WARCRY", "BERSERKER_RAGE",
                    "VELOCITY_DIVE", "SHOCKWAVE_LANDING", "RELENTLESS", "SUPERSONIC_STRIKE"
            }
    ),
    OMNI_MAN(
            "Nolan Grayson / Omni-Man",
            NamedTextColor.RED, Material.RED_WOOL,
            true, true, true, "steve",
            new String[]{
                    "ENDURANCE", "TERROR_STRIKE", "WARCRY", "SPIN_DASH",
                    "INTIMIDATION_AURA", "CONQUEROR_STRIKE", "RUTHLESS_ASSAULT", "IMPERIAL_RAGE",
                    "GROUND_POUND", "VILTRUMITE_CHARGE", "SUBJUGATION_GRIP", "SONIC_BOOM_PASS"
            }
    ),
    ATOM_EVE(
            "Samantha Eve Wilkins / Atom Eve",
            NamedTextColor.LIGHT_PURPLE, Material.PINK_WOOL,
            true, false, false, "steve",
            new String[]{
                    "MATTER_RESHAPE", "ENERGY_SHIELD", "HEALING_TOUCH", "MATTER_BLAST",
                    "CONSTRUCT_ARMOR", "TRANSMUTE_GROUND", "ATOMIC_BURST", "DIMENSIONAL_SHIFT",
                    "ATOMIC_CAGE", "PINK_BEAM", "MATTER_WALL", "REGENERATION"
            }
    ),
    REX_SPLODE(
            "Rex Sloan / Rex Splode",
            NamedTextColor.GOLD, Material.ORANGE_WOOL,
            false, false, false, "steve",
            new String[]{
                    "EXPLOSIVE_CHARGE", "KINETIC_INFUSE", "SPLODE_DASH", "CHAIN_EXPLOSION",
                    "GRENADE_VOLLEY", "DETONATION_FIELD", "IMPACT_WAVE", "REX_BURST",
                    "PROXIMITY_MINE", "KINETIC_SHIELD", "CLUSTER_BLAST", "CHARGE_SPRINT"
            }
    ),
    ROBOT(
            "Rudy Connors / Robot",
            NamedTextColor.AQUA, Material.CYAN_WOOL,
            true, false, false, "steve",
            new String[]{
                    "DRONE_SWARM", "FORCE_FIELD", "LASER_ARRAY", "NANITE_REPAIR",
                    "EMP_PULSE", "TACTICAL_SCAN", "MECH_OVERLOAD", "ELECTROMAGNETIC_FIELD",
                    "ROCKET_BARRAGE", "STEALTH_MODE", "GRAVITIC_SLAM", "SYSTEM_BOOST"
            }
    ),
    DUPLI_KATE(
            "Kate Cha / Dupli-Kate",
            NamedTextColor.YELLOW, Material.YELLOW_WOOL,
            false, false, false, "steve",
            new String[]{
                    "DUPLICATE_SELF", "SWARM_STRIKE", "CLONE_SHIELD", "MASS_ASSAULT",
                    "DIVERSION_CLONE", "PINCER_ATTACK", "CLONE_SURGE", "DUPLICATE_BURST",
                    "CLONE_AMBUSH", "INFINITE_COPIES", "COPY_FADE", "BODY_DOUBLE"
            }
    ),
    MONSTER_GIRL(
            "Amanda / Monster Girl",
            NamedTextColor.GREEN, Material.GREEN_WOOL,
            false, true, false, "steve",
            new String[]{
                    "MONSTER_TRANSFORM", "RAMPAGE", "REGENERATE", "TREMOR_SLAM_MG",
                    "MONSTER_ROAR", "TAIL_SWEEP", "THICK_HIDE", "FERAL_CHARGE",
                    "MONSTER_LEAP", "CRUSHING_GRIP", "EARTH_SHATTER", "BERSERK_ROAR",
                    "WARRIOR_BLOOD", "KILLING_BLOW", "PRIMAL_FRENZY"
            }
    ),
    BLACK_SAMSON(
            "Markus Robinson / Black Samson",
            NamedTextColor.DARK_GRAY, Material.BLACK_WOOL,
            false, true, false, "steve",
            new String[]{
                    "ENERGY_DISCHARGE", "SAMSON_CHARGE", "ENERGY_SHIELD", "POWER_SLAM",
                    "LIGHTNING_STRIKE", "ENERGY_BURST", "OVERCHARGE", "ELECTROMAGNETIC_FIELD",
                    "ENERGY_LANCE", "CHAIN_LIGHTNING", "POWER_ARMOR", "ENERGY_VORTEX"
            }
    ),
    BULLETPROOF(
            "Zandale Randolph / Bulletproof",
            NamedTextColor.DARK_GREEN, Material.LIME_WOOL,
            true, true, false, "steve",
            new String[]{
                    "BULLET_IMMUNITY", "DEFLECT", "COUNTER_STRIKE", "STEEL_SKIN",
                    "FORCE_RETURN", "IMPACT_ABSORB", "ARMOR_SURGE", "RICOCHET",
                    "IRON_RUSH", "SKIN_FORTRESS", "REACTIVE_SHELL", "CONCUSSIVE_RETURN",
                    "KINETIC_RELEASE", "SHOCKWAVE_FIST", "IMPENETRABLE", "BULLET_CATCH",
                    "MOMENTUM_TRANSFER", "UNSTOPPABLE", "IRON_DENSITY", "KINETIC_FEEDBACK",
                    "TITANIUM_RUSH", "DENSITY_SLAM",
                    "STELLAR_SLAM", "SHOCKWAVE_FIST"
            }
    ),
    ALLEN_THE_ALIEN(
            "Allen the Alien",
            NamedTextColor.DARK_AQUA, Material.LIGHT_BLUE_WOOL,
            true, true, true, "steve",
            new String[]{
                    "RAPID_ADAPT", "REGENERATION", "COSMIC_PUNCH", "ADAPTATION_SHIELD",
                    "GALACTIC_BURST", "ENDURANCE", "BERSERKER_RAGE", "WARCRY",
                    "ALIEN_RESILIENCE", "ORBIT_DASH", "PAIN_THRESHOLD", "ALIEN_OVERDRIVE",
                    "SAVAGE_STRIKE", "SUPERSONIC_STRIKE", "RELENTLESS"
            }
    ),
    BATTLE_BEAST(
            "Battle Beast",
            NamedTextColor.DARK_RED, Material.BROWN_WOOL,
            true, true, false, "steve",
            new String[]{
                    "BERSERKER_RAGE", "WAR_CRY", "SAVAGE_STRIKE", "UNYIELDING",
                    "MANE_SHIELD", "PREDATOR_LEAP", "WARRIOR_BLOOD", "BATTLE_ROAR",
                    "PRIMAL_FRENZY", "DEATH_CHARGE", "BONE_CRUSH", "KILLING_BLOW",
                    "IRON_WILL", "BEAST_ENDURANCE", "BERSERK_ROAR"
            }
    ),
    CONQUEST(
            "Conquest",
            NamedTextColor.RED, Material.NETHER_BRICK,
            true, true, true, "steve",
            new String[]{
                    "ENDURANCE", "CONQUEROR_STRIKE", "BATTLE_HARDENED", "SCAR_POWER",
                    "RUTHLESS_ASSAULT", "CONQUEST_RAGE", "TERROR_STRIKE", "INTIMIDATION_AURA",
                    "CONQUEROR_CHARGE", "BLADE_SPIN", "DEVASTATION", "OPPRESSOR_GRIP"
            }
    ),
    THRAGG(
            "Grand Regent Thragg",
            NamedTextColor.DARK_RED, Material.CRIMSON_STEM,
            true, true, true, "steve",
            new String[]{
                    "VILTRUMITE_ENDURANCE", "JUGULAR_STRIKE", "IMPERIAL_RAGE", "GRAND_REGENT_AURA",
                    "VILTRUMITE_MASTERY", "INTIMIDATION_AURA", "WARCRY", "CONQUEST_RAGE",
                    "SUPREME_AUTHORITY", "LETHAL_DESCENT", "IRON_DYNASTY", "CULLING_STRIKE", "VACUUM_PUNCH"
            }
    ),
    ANGSTROM_LEVY(
            "Angstrom Levy",
            NamedTextColor.DARK_PURPLE, Material.PURPLE_WOOL,
            true, false, false, "steve",
            new String[]{
                    "DIMENSION_PORTAL", "MULTIVERSE_DRAIN", "KNOWLEDGE_SURGE", "PORTAL_TRAP",
                    "INTELLECT_BLAST", "CROSS_DIMENSION", "REALITY_TEAR", "DIMENSIONAL_SHIFT",
                    "MULTIVERSE_STEP", "PARALLEL_STRIKE", "DIMENSIONAL_ANCHOR", "VOID_PULL"
            }
    ),
    SHRINKING_RAY(
            "Shrinking Ray",
            NamedTextColor.WHITE, Material.WHITE_WOOL,
            false, false, false, "steve",
            new String[]{
                    "MINIATURIZE", "MICRO_STRIKE", "SIZE_SHIFT", "INTERNAL_ATTACK",
                    "SHRINK_DASH", "GIANT_FORM", "SIZE_WAVE", "MICRO_BURST",
                    "SHRINK_BOMB", "GIANT_STOMP", "PHASE_DODGE", "TITAN_SLAM",
                    "RAPID_ADAPT", "DEFLECT", "ADAPTATION_SHIELD"
            }
    ),
    CECILIA_STEDMAN(
            "Cecil Stedman",
            NamedTextColor.GRAY, Material.GRAY_WOOL,
            false, false, false, "steve",
            new String[]{
                    "TACTICAL_COMMAND", "SHIELD_DEPLOY", "WEAPON_MASTERY", "STRATEGIC_STRIKE",
                    "COORDINATED_ASSAULT", "FIELD_MEDIC", "INTEL_SCAN", "GOVERNMENT_BACKUP",
                    "TACTICAL_RETREAT", "SUPPRESSION_FIRE", "COUNTER_INTEL", "ASSET_EXTRACTION",
                    "GDA_AIRSTRIKE", "DRONE_BARRAGE", "FREEZE_ASSETS", "RED_TAPE",
                    "NANITE_INJECTION", "LOCKDOWN", "FIELD_COMMANDER", "BLACK_SITE",
                    "CLASSIFIED", "AUTHORITY_OVERRIDE"
            }
    ),
    IMMORTAL(
            "The Immortal",
            NamedTextColor.YELLOW, Material.GOLDEN_SWORD,
            true, true, false, "steve",
            new String[]{
                    "ANCIENT_FURY", "TIME_HARDENED", "RESURRECTION_SURGE", "TIMELESS_STRIKE",
                    "UNDYING_WILL", "ANCIENT_LEAP", "INVULNERABILITY", "TREMOR_SLAM",
                    "WARCRY", "BERSERKER_RAGE", "RELENTLESS", "SUPERSONIC_STRIKE"
            }
    ),
    DARKWING(
            "Darkwing",
            NamedTextColor.DARK_GRAY, Material.BLACK_STAINED_GLASS,
            true, false, false, "steve",
            new String[]{
                    "GRAPPLE_LINE", "SMOKE_BOMB", "WING_STRIKE", "BATARANG_VOLLEY",
                    "DARK_SILENCE", "CAPE_GLIDE", "TACTICAL_SCAN", "STEALTH_MODE",
                    "DEFLECT", "COUNTER_STRIKE", "STRATEGIC_STRIKE", "TACTICAL_RETREAT"
            }
    ),
    OLIVER_GRAYSON(
            "Oliver Grayson / Kid Omni-Man",
            NamedTextColor.BLUE, Material.CYAN_CONCRETE,
            true, true, true, "steve",
            new String[]{
                    "BLUE_FLAME", "HALF_BREED_SURGE", "HEAT_VISION", "SUPER_BREATH",
                    "TECH_JACKET_BOOST", "FULL_POWER", "VELOCITY_DIVE", "ATOMIC_PUNCH",
                    "SONIC_CLAP", "REGENERATION", "SUPERSONIC_STRIKE", "RELENTLESS"
            }
    ),

    CUSTOM(
            "Custom Hero",
            NamedTextColor.GOLD, Material.NETHER_STAR,
            true, true, true, "steve",
            new String[]{
                    "BERSERKER_RAGE", "ATOMIC_PUNCH", "SONIC_CLAP", "COMBO_STRIKE",
                    "ENERGY_SHIELD", "HEALING_TOUCH", "VELOCITY_DIVE", "SUPERSONIC_STRIKE",
                    "DEVASTATION", "REALITY_TEAR", "VACUUM_PUNCH", "REGENERATION",
                    "TREMOR_SLAM", "SPIN_DASH"
            }
    );

    private final String displayName;
    private final NamedTextColor color;
    private final Material guiItem;
    private final boolean canFly;
    private final boolean hasSuperStrength;
    private final boolean hasSuperSpeed;
    private final String skinName;
    private final String[] abilityKeys;

    CharacterType(String displayName, NamedTextColor color, Material guiItem,
                  boolean canFly, boolean hasSuperStrength, boolean hasSuperSpeed,
                  String skinName, String[] abilityKeys) {
        this.displayName = displayName;
        this.color = color;
        this.guiItem = guiItem;
        this.canFly = canFly;
        this.hasSuperStrength = hasSuperStrength;
        this.hasSuperSpeed = hasSuperSpeed;
        this.skinName = skinName;
        this.abilityKeys = abilityKeys;
    }

    public String getDisplayName() { return displayName; }
    public NamedTextColor getColor() { return color; }
    public Material getGuiItem() { return guiItem; }
    public boolean canFly() { return canFly; }
    public boolean hasSuperStrength() { return hasSuperStrength; }
    public boolean canBreakBlocks() { return canFly && hasSuperStrength; }
    public boolean hasSuperSpeed() { return hasSuperSpeed; }
    public String getSkinName() { return skinName; }
    public String[] getAbilityKeys() { return abilityKeys; }

    public static String ampCode(NamedTextColor c) {
        if (c == NamedTextColor.RED) return "&c";
        if (c == NamedTextColor.DARK_RED) return "&4";
        if (c == NamedTextColor.GREEN) return "&a";
        if (c == NamedTextColor.DARK_GREEN) return "&2";
        if (c == NamedTextColor.YELLOW) return "&e";
        if (c == NamedTextColor.GOLD) return "&6";
        if (c == NamedTextColor.AQUA) return "&b";
        if (c == NamedTextColor.DARK_AQUA) return "&3";
        if (c == NamedTextColor.BLUE) return "&9";
        if (c == NamedTextColor.DARK_BLUE) return "&1";
        if (c == NamedTextColor.WHITE) return "&f";
        if (c == NamedTextColor.GRAY) return "&7";
        if (c == NamedTextColor.DARK_GRAY) return "&8";
        if (c == NamedTextColor.LIGHT_PURPLE) return "&d";
        if (c == NamedTextColor.DARK_PURPLE) return "&5";
        return "&f";
    }
}