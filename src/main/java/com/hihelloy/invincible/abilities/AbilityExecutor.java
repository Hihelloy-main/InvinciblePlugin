package com.hihelloy.invincible.abilities;

import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class AbilityExecutor {

    public static void execute(Player player, AbilityType ability, InvinciblePlugin plugin) {
        execute(player, ability, plugin, 0.0, 0, 0.0);
    }

    public static void execute(Player player, AbilityType ability, InvinciblePlugin plugin,
                               double bonusDamage, int bonusDuration, double bonusRadius) {
        double dmg = plugin.getStatManager().getDamageFlatBonus(player);
        double dur = plugin.getStatManager().getDurationMultiplier(player);
        double spd = plugin.getStatManager().getSpeedMultiplier(player);

        String _abilityKey = ability.name();
        try {
            Class<?> bridge = Class.forName("com.hihelloy.invinciblemmo.manager.AbilityBoostManager");
            java.lang.reflect.Method getDmg = bridge.getMethod("getDamageBonus", org.bukkit.entity.Player.class, String.class);
            java.lang.reflect.Method getDur = bridge.getMethod("getDurationBonus", org.bukkit.entity.Player.class, String.class);
            java.lang.reflect.Method getRad = bridge.getMethod("getRadiusBonus", org.bukkit.entity.Player.class, String.class);
            dmg += (double) getDmg.invoke(null, player, _abilityKey);
            dur += (double) getDur.invoke(null, player, _abilityKey);
        } catch (ClassNotFoundException ignored) {
        } catch (Exception e) {
            plugin.getLogger().warning("InvincibleMMO bridge error: " + e.getMessage());
        }

        switch (ability) {
            case INVULNERABILITY -> invulnerability(player, dur, plugin);
            case RELENTLESS, IRON_DYNASTY, VILTRUMITE_ENDURANCE,
                 ENDURANCE, BATTLE_HARDENED, UNYIELDING -> endurance(player, dur, plugin);
            case COMBO_STRIKE -> comboStrike(player, dmg, plugin);
            case SONIC_CLAP -> sonicClap(player, dmg, plugin);
            case ATOMIC_PUNCH, COSMIC_PUNCH -> atomicPunch(player, dmg, plugin);
            case REGENERATION, REGENERATE, NANITE_REPAIR -> regeneration(player, dur);
            case TERROR_STRIKE, CONQUEROR_STRIKE, SAVAGE_STRIKE -> terrorStrike(player, dmg, plugin);
            case WARCRY, WAR_CRY, MONSTER_ROAR, BATTLE_ROAR,
                 BERSERK_ROAR, SUPREME_AUTHORITY -> warCry(player, plugin);
            case SPIN_DASH, FERAL_CHARGE -> spinDash(player, spd, plugin);
            case INTIMIDATION_AURA, GRAND_REGENT_AURA -> intimidationAura(player, dur, plugin);
            case MATTER_RESHAPE, TRANSMUTE_GROUND, MATTER_WALL -> matterReshape(player);
            case ENERGY_SHIELD, FORCE_FIELD, KINETIC_SHIELD,
                 BODY_DOUBLE, REACTIVE_SHELL -> energyShield(player, dur, plugin);
            case HEALING_TOUCH, FIELD_MEDIC -> healingTouch(player);
            case MATTER_BLAST, INTELLECT_BLAST, PINK_BEAM -> matterBlast(player, dmg, plugin);
            case CONSTRUCT_ARMOR, STEEL_SKIN, THICK_HIDE,
                 SKIN_FORTRESS, POWER_ARMOR -> constructArmor(player, ability, dur, plugin);
            case ATOMIC_BURST, GALACTIC_BURST, ENERGY_BURST,
                 VILTRUMITE_MASTERY, DEVASTATION -> atomicBurst(player, dmg, plugin);
            case EXPLOSIVE_CHARGE, GRENADE_VOLLEY, CLUSTER_BLAST -> explosiveCharge(player, dmg, plugin);
            case KINETIC_INFUSE -> kineticInfuse(player, dmg, plugin);
            case SPLODE_DASH, SHRINK_DASH, ORBIT_DASH -> splodeDash(player, spd);
            case CHAIN_EXPLOSION -> chainExplosion(player, dmg, plugin);
            case DETONATION_FIELD -> detonationField(player, dmg, plugin);
            case IMPACT_WAVE -> impactWave(player, dmg, plugin);
            case REX_BURST, DUPLICATE_BURST, MICRO_BURST,
                 ALIEN_OVERDRIVE, WARLORD_WRATH -> rexBurst(player, dmg, plugin);
            case DRONE_SWARM, ROCKET_BARRAGE -> droneSwarm(player, dmg, plugin);
            case LASER_ARRAY, ENERGY_LANCE -> laserArray(player, dmg, plugin);
            case EMP_PULSE, SUPPRESSION_FIRE -> empPulse(player, plugin);
            case TACTICAL_SCAN, INTEL_SCAN -> tacticalScan(player, plugin);
            case MECH_OVERLOAD, IMPERIAL_RAGE, CONQUEST_RAGE,
                 PRIMAL_FRENZY -> mechOverload(player, dmg, dur, plugin);
            case BERSERKER_RAGE -> berserkerRage(player, dmg, dur, plugin);
            case DUPLICATE_SELF, INFINITE_COPIES -> duplicateSelf(player, plugin);
            case SWARM_STRIKE, PINCER_ATTACK, CLONE_AMBUSH,
                 PARALLEL_STRIKE, BLADE_SPIN -> swarmStrike(player, dmg, plugin);
            case CLONE_SHIELD, BULLET_IMMUNITY, ARMOR_SURGE -> cloneShield(player, dur, plugin);
            case MASS_ASSAULT, COORDINATED_ASSAULT,
                 RUTHLESS_ASSAULT, EARTH_SHATTER -> massAssault(player, dmg, plugin);
            case DIVERSION_CLONE, COPY_FADE -> diversionClone(player);
            case CLONE_SURGE, DIMENSION_PORTAL, MULTIVERSE_STEP,
                 ASSET_EXTRACTION -> dimensionPortal(player);
            case MONSTER_TRANSFORM -> monsterTransform(player, dur, plugin);
            case RAMPAGE -> rampage(player, dmg, dur, plugin);
            case TREMOR_SLAM, POWER_SLAM, TREMOR_SLAM_MG,
                 GIANT_STOMP, TITAN_SLAM, SOVEREIGN_SLAM -> tremorSlam(player, dmg, plugin);
            case TAIL_SWEEP -> tailSweep(player, dmg, plugin);
            case ENERGY_DISCHARGE, LIGHTNING_STRIKE,
                 CHAIN_LIGHTNING -> energyDischarge(player, dmg, plugin);
            case SAMSON_CHARGE, SCAR_POWER,
                 BONE_CRUSH, CRUSHING_GRIP -> samsonCharge(player, dmg, plugin);
            case OPPRESSOR_GRIP, SUBJUGATION_GRIP -> opponentGrip(player, dmg, plugin);
            case ELECTROMAGNETIC_FIELD, ENERGY_VORTEX -> electromagneticField(player, dmg, dur, plugin);
            case OVERCHARGE, WARRIOR_BLOOD, GOVERNMENT_BACKUP,
                 SYSTEM_BOOST, BATTLE_HARNESS -> overcharge(player, dur, plugin);
            case DEFLECT, COUNTER_STRIKE, FORCE_RETURN, RICOCHET,
                 CONCUSSIVE_RETURN -> deflect(player, plugin);
            case IMPACT_ABSORB, PHASE_DODGE -> impactAbsorb(player, dur, plugin);
            case RAPID_ADAPT, ADAPTATION_SHIELD, ALIEN_RESILIENCE,
                 PAIN_THRESHOLD, COUNTER_INTEL -> rapidAdapt(player, ability, dur, plugin);
            case MULTIVERSE_DRAIN, KNOWLEDGE_SURGE -> multiverseDrain(player, plugin);
            case PORTAL_TRAP, DIMENSIONAL_ANCHOR, VOID_PULL -> portalTrap(player, plugin);
            case DIMENSIONAL_SHIFT -> dimensionalShift(player, dur, plugin);
            case CROSS_DIMENSION -> crossDimension(player, dmg, dur, plugin);
            case REALITY_TEAR -> realityTear(player, dmg, plugin);
            case MINIATURIZE, SIZE_SHIFT -> miniaturize(player, dur, plugin);
            case MICRO_STRIKE, INTERNAL_ATTACK -> microStrike(player, dmg, plugin);
            case GIANT_FORM -> giantForm(player, dur, plugin);
            case SIZE_WAVE, SHRINK_BOMB -> sizeWave(player, plugin);
            case TACTICAL_COMMAND -> tacticalCommand(player, dur, plugin);
            case WEAPON_MASTERY -> weaponMastery(player, dmg, dur, plugin);
            case STRATEGIC_STRIKE -> strategicStrike(player, dmg, plugin);
            case PREDATOR_LEAP, DEATH_CHARGE, ANCIENT_LEAP -> predatorLeap(player, spd, plugin);
            case MANE_SHIELD, BEAST_ENDURANCE, IRON_WILL -> maneShield(player, dur, plugin);
            case VELOCITY_DIVE, LETHAL_DESCENT, GROUND_POUND,
                 SHOCKWAVE_LANDING -> velocityDive(player, dmg, plugin);
            case SUPERSONIC_STRIKE, VILTRUMITE_CHARGE,
                 CONQUEROR_CHARGE, SONIC_BOOM_PASS,
                 REGENT_CHARGE, CHARGE_SPRINT -> supersonicStrike(player, dmg, spd, plugin);
            case IRON_RUSH -> ironRush(player, dmg, spd, plugin);
            case KILLING_BLOW, CULLING_STRIKE, TIMELESS_STRIKE,
                 JUGULAR_STRIKE -> killingBlow(player, dmg, plugin);
            case ATOMIC_CAGE -> atomicCage(player, dur, plugin);
            case GRAVITIC_SLAM -> graviticSlam(player, dmg, plugin);
            case STEALTH_MODE -> stealthMode(player, dur, plugin);
            case DARK_SILENCE -> shadowStrike(player, dmg, plugin);
            case SHIELD_DEPLOY -> shieldDeploy(player, dur, plugin);
            case TACTICAL_RETREAT, CAPE_GLIDE -> tacticalRetreat(player, ability, spd, plugin);
            case MONSTER_LEAP -> monsterLeap(player, spd, plugin);
            case PROXIMITY_MINE -> proximityMine(player, dmg, plugin);
            case EDICT -> edict(player, dur, plugin);
            case ANCIENT_FURY, FULL_POWER -> ancientFury(player, dmg, plugin);
            case TIME_HARDENED, UNDYING_WILL -> undyingWill(player, dur, plugin);
            case RESURRECTION_SURGE -> resurrectionSurge(player, plugin);
            case GRAPPLE_LINE -> grappelLine(player, spd, plugin);
            case SMOKE_BOMB -> smokeBomb(player, dur, plugin);
            case WING_STRIKE -> wingStrike(player, dmg, plugin);
            case BATARANG_VOLLEY -> batarangVolley(player, dmg, plugin);
            case BLUE_FLAME -> blueFlame(player, dmg, plugin);
            case HALF_BREED_SURGE -> halfBreedSurge(player, dmg, dur, plugin);
            case HEAT_VISION -> heatVision(player, dmg, plugin);
            case SUPER_BREATH -> superBreath(player, plugin);
            case TECH_JACKET_BOOST -> techJacketBoost(player, spd, plugin);

            case GDA_AIRSTRIKE -> gdaAirstrike(player, dmg, plugin);
            case DRONE_BARRAGE -> droneBarrage(player, dmg, plugin);
            case FREEZE_ASSETS -> freezeAssets(player, dur, plugin);
            case RED_TAPE -> redTape(player, dur, plugin);
            case NANITE_INJECTION -> naniteInjection(player, dur, plugin);
            case LOCKDOWN -> lockdown(player, dur, plugin);
            case FIELD_COMMANDER -> fieldCommander(player, dur, plugin);
            case BLACK_SITE -> blackSite(player, plugin);
            case CLASSIFIED -> classified(player, dur, plugin);
            case AUTHORITY_OVERRIDE -> authorityOverride(player, dmg, dur, plugin);

            case KINETIC_RELEASE -> kineticRelease(player, dmg, plugin);
            case SHOCKWAVE_FIST -> shockwaveFist(player, dmg, plugin);
            case IMPENETRABLE -> impenetrable(player, plugin);
            case BULLET_CATCH -> bulletCatch(player, dmg, plugin);
            case MOMENTUM_TRANSFER -> momentumTransfer(player, dmg, plugin);
            case UNSTOPPABLE -> unstoppable(player, dmg, spd, plugin);
            case IRON_DENSITY -> ironDensity(player, dur, plugin);
            case KINETIC_FEEDBACK -> kineticFeedback(player, dur, plugin);
            case TITANIUM_RUSH -> titaniumRush(player, dmg, spd, plugin);
            case DENSITY_SLAM -> densitySlam(player, dmg, plugin);
            case ATOMIC_FLIGHT -> atomicFlightToggle(player, plugin);
            case ELECTRIC_DASH -> electricDash(player, dmg, spd, plugin);
            case STELLAR_SLAM -> stellarSlam(player, dmg, plugin);
            case INTERSTELLAR_DASH -> interstellarDash(player, spd, plugin);
            case COSMIC_ROAR -> cosmicRoar(player, dur, plugin);
            case REGENERATIVE_SURGE -> regenerativeSurge(player, plugin);
            case RIFT_SHIELD -> riftShield(player, dur, plugin);

            case GENERIC_DASH -> genericDash(player, spd, plugin);
            case GRAPPLE -> grapple(player, dmg, spd, plugin);
            case VACUUM_PUNCH -> vacuumPunch(player, dmg, plugin);
            default -> player.sendMessage(Component.text("Ability not implemented: " + ability.getDisplayName(), net.kyori.adventure.text.format.NamedTextColor.RED));
        }
    }

    private static void ancientFury(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.5f, 0.6f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 12, 3, 3, 3, 0.4);
        strikeNearbyEntities(player, 9.0, 10.0 + dmg, plugin);
        knockbackNearbyEntities(player, 9.0, 0, 3.0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, (int)(3 + dmg)));
    }

    private static void undyingWill(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(60 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 2));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 1, 1, 1, 0.8);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.UNDYING_WILL, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.UNDYING_WILL, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void resurrectionSurge(Player player, InvinciblePlugin plugin) {
        double missing = player.getMaxHealth() - player.getHealth();
        double heal = Math.max(missing * 0.6, 10.0);
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 2));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 20, 0.5, 1, 0.5, 0.3);
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 1, 1, 1, 0.6);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5f, 1.2f);
        player.sendMessage(Component.text("RESURRECTION SURGE — You refuse to fall!", net.kyori.adventure.text.format.NamedTextColor.GOLD, net.kyori.adventure.text.format.TextDecoration.BOLD));
    }

    private static void grappelLine(Player player, double spd, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 20.0);
        Vector dir;
        if (target != null) {
            dir = target.getLocation().subtract(player.getLocation()).toVector().normalize();
        } else {
            dir = player.getLocation().getDirection().normalize();
        }
        dir.setY(Math.max(dir.getY(), 0.2));
        player.setVelocity(dir.multiply(4.0 * spd));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 8, 0.3, 0.3, 0.3, 0.2);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1.0f, 1.5f);
    }

    private static void smokeBomb(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(60 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 0));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 40, 2, 1, 2, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.8f, 2.0f);
        nearbyEntities(player, 5.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ticks / 2, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks / 2, 2));
            }
        });
    }

    private static void wingStrike(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.5f, 0.7f);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 8, 2, 0.5, 2, 0.1);
        Entity target = getClosestEntity(player, 5.0);
        if (target instanceof LivingEntity le) {
            le.damage(6.0 + dmg, player);
            Vector dir = target.getLocation().subtract(player.getLocation()).toVector().normalize();
            le.setVelocity(dir.multiply(2.5).add(new Vector(0, 0.8, 0)));
        } else {
            knockbackNearbyEntities(player, 4.0, 5.0 + dmg, 2.0);
        }
    }

    private static void batarangVolley(Player player, double dmg, InvinciblePlugin plugin) {
        Vector base = player.getLocation().getDirection().normalize();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.8f);
        double[] angles = {-20, -10, 0, 10, 20};
        for (double angle : angles) {
            double rad = Math.toRadians(angle);
            Vector spread = base.clone().rotateAroundY(rad);
            player.getWorld().spawnParticle(Particle.CRIT, player.getEyeLocation(),
                    3, spread.getX() * 4, spread.getY() * 4, spread.getZ() * 4, 0.05);
            nearbyEntitiesInDirection(player, spread, 16.0).forEach(e -> {
                if (e instanceof LivingEntity le) {
                    le.damage(5.0 + dmg, player);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                }
            });
        }
    }

    private static void blueFlame(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.6f);
        Vector dir = player.getLocation().getDirection().normalize();
        player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(),
                25, dir.getX() * 5, dir.getY() * 5, dir.getZ() * 5, 0.05);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getEyeLocation(),
                15, dir.getX() * 4, dir.getY() * 4, dir.getZ() * 4, 0.05);
        nearbyEntitiesInDirection(player, dir, 14.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.damage(7.0 + dmg, player);
                le.setFireTicks(80);
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
            }
        });
    }

    private static void halfBreedSurge(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, (int)(6 + dmg)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,ticks, 1));
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 50, 1, 1, 1, 0.6);
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 1, 1, 1, 0.4);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.6f, 1.8f);
        player.sendMessage(Component.text("HALF-BREED SURGE!", net.kyori.adventure.text.format.NamedTextColor.AQUA, net.kyori.adventure.text.format.TextDecoration.BOLD));
    }

    private static void heatVision(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 2.0f);
        Vector dir = player.getLocation().getDirection().normalize();
        Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize().multiply(0.15);
        player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation().add(right),
                15, dir.getX() * 6, dir.getY() * 6, dir.getZ() * 6, 0.02);
        player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation().subtract(right),
                15, dir.getX() * 6, dir.getY() * 6, dir.getZ() * 6, 0.02);
        nearbyEntitiesInDirection(player, dir, 20.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.damage(8.0 + dmg, player);
                le.setFireTicks(100);
            }
        });
    }

    private static void superBreath(Player player, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1.5f, 1.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getEyeLocation(), 30,
                player.getLocation().getDirection().getX() * 5,
                player.getLocation().getDirection().getY() * 5,
                player.getLocation().getDirection().getZ() * 5, 0.1);
        knockbackNearbyEntities(player, 12.0, 0, 3.5);
        nearbyEntities(player, 12.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 3));
            }
        });
    }

    private static void techJacketBoost(Player player, double spd, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.8f);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.3);
        Vector dir = player.getLocation().getDirection().normalize().multiply(5.0 * spd);
        dir.setY(Math.max(dir.getY(), 0.5));
        player.setVelocity(dir);
        plugin.getFlightManager().applyFlightSpeed(player);
        new BukkitRunnable() {
            @Override public void run() {
                if (player.isOnline()) plugin.getFlightManager().applyFlightSpeed(player);
            }
        }.runTaskLater(plugin, 40L);
    }

    private static void invulnerability(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 4));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 30, 0.5, 1, 0.5, 0.3);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.INVULNERABILITY, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.INVULNERABILITY, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void endurance(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(plugin.getAbilityConfig().getDurationTicks(AbilityType.ENDURANCE, 80) * dur);
        int resistance = plugin.getAbilityConfig().getResistanceLevel(AbilityType.ENDURANCE, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, resistance));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, ticks, 0));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 20, 0.5, 1, 0.5, 0.5);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 0.9f);
        for (double a = 0; a < Math.PI * 2; a += Math.PI / 12) {
            player.getWorld().spawnParticle(Particle.END_ROD,
                    player.getLocation().add(Math.cos(a)*1.6, 1.0, Math.sin(a)*1.6),
                    3, 0.05, 0.35, 0.05, 0.04);
        }
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0,1,0), 20, 0.4, 0.6, 0.4, 0.08);
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 0.6, 1.2, 0.6, 0.9);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 2.0f);
    }

    private static void comboStrike(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 1.2f);
        new BukkitRunnable() {
            int hits = 0;
            @Override public void run() {
                if (hits >= 5 || !player.isOnline()) { cancel(); return; }
                strikeNearbyEntities(player, 3.5, 2.5 + dmg, plugin);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 3, 0.5, 0.5, 0.5, 0);
                hits++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private static void sonicClap(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.8f, 0.6f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.8f);
        // Expanding shockwave rings at 3 radii
        for (int ring = 1; ring <= 3; ring++) {
            final double rad = ring * 3.5;
            final int delay = ring * 2;
            new BukkitRunnable() { @Override public void run() {
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 20) {
                    double rx = Math.cos(a) * rad, rz = Math.sin(a) * rad;
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                            player.getLocation().add(rx, 0.7, rz), 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.CLOUD,
                            player.getLocation().add(rx, 0.5, rz), 2, 0.1, 0.1, 0.1, 0.05);
                }
                player.getWorld().spawnParticle(Particle.EXPLOSION,
                        player.getLocation(), 3, rad * 0.15, 0.2, rad * 0.15, 0.0);
            }}.runTaskLater(plugin, delay);
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 8, 1.5, 0.3, 1.5, 0.05);
        knockbackNearbyEntities(player, 7.0, 0, 1.8);
        strikeNearbyEntities(player, 7.0, 4.0 + dmg, plugin);
    }

    private static void atomicPunch(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        org.bukkit.util.Vector punchDir = player.getLocation().getDirection().normalize();
        player.setVelocity(punchDir.clone().multiply(3.0));
        final org.bukkit.Location startLoc = player.getLocation().clone();

        new BukkitRunnable() {
            int t = 0;
            @Override public void run() {
                if (!player.isOnline()) { cancel(); return; }

                if (t % 2 == 0) {
                    plugin.getFlightManager().getTempBlockManager()
                            .clearPath(player.getEyeLocation(), punchDir, 3.0, 1.2,
                                    plugin.getConfig().getLong("settings.block-restore-ticks", 600L));
                }

                if (t >= 8) {
                    cancel();
                    strikeNearbyEntities(player, 5.0, 17.0 + dmg, plugin);
                    player.getWorld().createExplosion(player.getLocation(), 0f, false, false);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 15, 3, 2, 3, 0.4);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 60, 4, 2, 4, 0.5);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0,0.5,0), 10, 3, 0.2, 3, 0.0);
                    for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                        player.getWorld().spawnParticle(Particle.CRIT,
                                player.getLocation().add(Math.cos(a)*3, 0.5, Math.sin(a)*3),
                                4, 0.2, 0.2, 0.2, 0.1);
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.4f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.8f, 0.5f);
                    destroyBlocks(player.getLocation(), 4.0, plugin);
                    return;
                }
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 8, 0.4, 0.2, 0.4, 0.2);
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 5, 0.3, 0.3, 0.3, 0.1);
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 2, 0.5, 0.2, 0.5, 0.0);
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void regeneration(Player player, double dur) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 1));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 10, 0.5, 1, 0.5, 0.2);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.8f);
    }

    private static void terrorStrike(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1.5f, 0.7f);
        strikeNearbyEntities(player, 5.0, 6.0 + dmg, plugin);
        nearbyEntities(player, 6.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
            }
        });
    }

    private static void warCry(Player player, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 2.5f, 0.4f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 1.8f);
        for (int _r = 2; _r <= 10; _r += 2) {
            final double _fr = _r;
            new BukkitRunnable() { @Override public void run() {
                for (double _a = 0; _a < Math.PI * 2; _a += Math.PI / 18) {
                    player.getWorld().spawnParticle(Particle.CLOUD,
                            player.getLocation().add(Math.cos(_a)*_fr, 0.6, Math.sin(_a)*_fr),
                            2, 0.1, 0.1, 0.1, 0.03);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                            player.getLocation().add(Math.cos(_a)*_fr, 0.5, Math.sin(_a)*_fr),
                            1, 0, 0, 0, 0);
                }
            }}.runTaskLater(plugin, (long)(_fr / 2));
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 5, 0.5, 0.2, 0.5, 0.0);
        nearbyEntities(player, 12.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
            }
        });
        knockbackNearbyEntities(player, 12.0, 0, 2.5);
    }

    private static void spinDash(Player player, double spd, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 1.5f);
        Vector dir = player.getLocation().getDirection().normalize().multiply(3.0 * spd);
        dir.setY(0.3);
        player.setVelocity(dir);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 3));
        for (double _a = 0; _a < Math.PI * 4; _a += 0.3) {
            double _r = 1.5;
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                    player.getLocation().add(Math.cos(_a)*_r, _a*0.1, Math.sin(_a)*_r), 1, 0, 0, 0, 0);
            player.getWorld().spawnParticle(Particle.CLOUD,
                    player.getLocation().add(Math.cos(_a)*_r, _a*0.1, Math.sin(_a)*_r), 1, 0.1, 0.1, 0.1, 0.02);
        }
    }

    private static void intimidationAura(Player player, double dur, InvinciblePlugin plugin) {
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.INTIMIDATION_AURA, true);
        int ticks = (int)(200 * dur);
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (elapsed >= ticks || !player.isOnline()) {
                    cancel();
                    plugin.getAbilityManager().setActiveAbility(player, AbilityType.INTIMIDATION_AURA, false);
                    return;
                }
                nearbyEntities(player, 8.0).forEach(e -> {
                    if (e instanceof LivingEntity le && !(le instanceof Player)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 25, 1));
                        le.damage(1.0, player);
                    }
                });
                // Grand Regent aura — dark orbit ring every tick
                double _angle = elapsed * 0.15;
                for (int _i = 0; _i < 6; _i++) {
                    double _a = _angle + (_i * Math.PI / 3.0);
                    double _r = 5.0;
                    player.getWorld().spawnParticle(Particle.SQUID_INK,
                            player.getLocation().add(Math.cos(_a)*_r, 1.2, Math.sin(_a)*_r),
                            2, 0.05, 0.1, 0.05, 0.02);
                    player.getWorld().spawnParticle(Particle.PORTAL,
                            player.getLocation().add(Math.cos(_a)*_r, 1.0, Math.sin(_a)*_r),
                            1, 0.05, 0.05, 0.05, 0.1);
                }
                player.getWorld().spawnParticle(Particle.CRIMSON_SPORE, player.getLocation(), 8, 5, 1.5, 5, 0.05);
                elapsed += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private static void matterReshape(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.5f);
        Location target = player.getTargetBlockExact(8) != null
                ? player.getTargetBlockExact(8).getLocation().add(0, 1, 0)
                : player.getLocation().add(player.getLocation().getDirection().multiply(5));
        if (target != null) {
            player.getWorld().spawnParticle(Particle.WITCH, target, 20, 1, 1, 1, 0.2);
        }
        player.sendMessage(Component.text("Matter reshaped!", net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
    }

    private static void energyShield(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 3));
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 30, 1, 1, 1, 0.3);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.ENERGY_SHIELD, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.ENERGY_SHIELD, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void healingTouch(Player player) {
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 10.0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 15, 0.5, 1, 0.5, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);
    }

    private static void matterBlast(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.2f);
        Vector dir = player.getLocation().getDirection().normalize();
        player.getWorld().spawnParticle(Particle.WITCH, player.getEyeLocation(), 20,
                dir.getX() * 3, dir.getY() * 3, dir.getZ() * 3, 0.1);
        nearbyEntitiesInDirection(player, dir, 12.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.damage(8.0 + dmg, player);
                le.setVelocity(dir.clone().multiply(2.0));
            }
        });
    }

    private static void constructArmor(Player player, AbilityType ability, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, ticks, 0));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 1, 1, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1.5f, 0.8f);
    }

    private static void atomicBurst(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.3f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.5f, 0.5f);
        // Pink matter shockwave — expanding firework rings
        for (int r = 1; r <= 5; r++) {
            final double rad = r * 2.2;
            new BukkitRunnable() { @Override public void run() {
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 18) {
                    org.bukkit.Location fl = player.getLocation().clone()
                            .add(Math.cos(a) * rad, 0.6, Math.sin(a) * rad);
                    player.getWorld().spawnParticle(Particle.FIREWORK, fl, 3, 0.1, 0.2, 0.1, 0.08);
                    player.getWorld().spawnParticle(Particle.END_ROD, fl, 1, 0.05, 0.3, 0.05, 0.04);
                }
            }}.runTaskLater(plugin, (long) r);
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10, 3, 2, 3, 0.1);
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation().add(0,1,0), 80, 4, 2, 4, 0.5);
        strikeNearbyEntities(player, 10.0, 10.0 + dmg, plugin);
        knockbackNearbyEntities(player, 10.0, 0, 3.0);
        destroyBlocks(player.getLocation(), 5.0, plugin);
    }

    private static void explosiveCharge(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
        Location target = player.getTargetBlockExact(20) != null
                ? player.getTargetBlockExact(20).getLocation()
                : player.getLocation().add(player.getLocation().getDirection().multiply(10));
        if (target != null) {
            player.getWorld().createExplosion(target, 0f, false, false);
            player.getWorld().spawnParticle(Particle.EXPLOSION, target, 5, 1, 1, 1, 0.3);
            nearbyEntities(target, 5.0, player.getWorld()).forEach(e -> {
                if (e instanceof LivingEntity le) le.damage(6.0 + dmg, player);
            });
        }
    }

    private static void kineticInfuse(Player player, double dmg, InvinciblePlugin plugin) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, (int)(3 + dmg)));
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.2);
        player.sendMessage(Component.text("Next strike is kinetically charged!", net.kyori.adventure.text.format.NamedTextColor.GOLD));
    }

    private static void splodeDash(Player player, double spd) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(4.0 * spd);
        dir.setY(0.5);
        player.setVelocity(dir);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 3, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.5f);
    }

    private static void chainExplosion(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.7f);
        new BukkitRunnable() {
            int count = 0;
            final Location center = player.getLocation().clone();
            @Override public void run() {
                if (count >= 5) { cancel(); return; }
                double angle = count * (2 * Math.PI / 5);
                Location loc = center.clone().add(Math.cos(angle) * 4, 0, Math.sin(angle) * 4);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.getWorld().createExplosion(loc, 0f, false, false);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, loc, 3, 1, 1, 1, 0.2);
                    nearbyEntities(loc, 4.0, player.getWorld()).forEach(e -> {
                        if (e instanceof LivingEntity le) le.damage(8.0 + dmg, player);
                    });
                });
                count++;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private static void detonationField(Player player, double dmg, InvinciblePlugin plugin) {
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.DETONATION_FIELD, true);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    cancel();
                    plugin.getAbilityManager().setActiveAbility(player, AbilityType.DETONATION_FIELD, false);
                    return;
                }
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 5, 4, 0.5, 4, 0);
                nearbyEntities(player, 5.0).forEach(e -> {
                    if (e instanceof LivingEntity le && !(le instanceof Player)) le.damage(1.5 + dmg, player);
                });
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private static void impactWave(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.5f, 0.5f);
        Vector dir = player.getLocation().getDirection().normalize();
        dir.setY(0);
        for (double d = 2; d <= 12; d += 2) {
            final double dist = d;
            new BukkitRunnable() {
                @Override public void run() {
                    Location waveLoc = player.getLocation().clone().add(dir.clone().multiply(dist));
                    player.getWorld().spawnParticle(Particle.EXPLOSION, waveLoc, 2, 0.5, 0.5, 0.5, 0);
                    nearbyEntities(waveLoc, 2.0, player.getWorld()).forEach(e -> {
                        if (e instanceof LivingEntity le) {
                            le.damage(5.0 + dmg, player);
                            le.setVelocity(dir.clone().multiply(2.0).add(new Vector(0, 0.5, 0)));
                        }
                    });
                }
            }.runTaskLater(plugin, (long)(dist / 2));
        }
    }

    private static void rexBurst(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.5f, 0.3f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.8f, 1.2f);
        // REX BURST — chain explosions at increasing radii
        for (int r = 1; r <= 4; r++) {
            final int fr = r;
            new BukkitRunnable() { @Override public void run() {
                double rad = fr * 4.0;
                for (double a = 0; a < Math.PI * 2; a += Math.PI / (fr * 4)) {
                    org.bukkit.Location el = player.getLocation().clone()
                            .add(Math.cos(a) * rad, 0.5, Math.sin(a) * rad);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, el, 1, 0.2, 0.2, 0.2, 0.0);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, el, 3, 0.3, 0.1, 0.3, 0.0);
                    player.getWorld().spawnParticle(Particle.CLOUD, el, 3, 0.3, 0.5, 0.3, 0.1);
                }
                if (fr == 4) player.getWorld().playSound(player.getLocation(),
                        Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.4f);
            }}.runTaskLater(plugin, r * 3L);
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 15, 3, 3, 3, 0.2);
        strikeNearbyEntities(player, 16.0, 12.0 + dmg, plugin);
        knockbackNearbyEntities(player, 16.0, 0, 3.5);
    }

    private static void droneSwarm(Player player, double dmg, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int durationTicks = ac.getDurationTicks(AbilityType.DRONE_SWARM, 80);
        int droneCount = 4;
        double tickDamage = ac.getTickDamage(AbilityType.DRONE_SWARM, 1.0);
        double radius = ac.getRadius(AbilityType.DRONE_SWARM, 7.0);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1.5f, 1.8f);

        java.util.List<org.bukkit.entity.Entity> drones = new java.util.ArrayList<>();
        for (int i = 0; i < droneCount; i++) {
            double angle = (Math.PI * 2 / droneCount) * i;
            org.bukkit.Location spawnLoc = player.getLocation().clone().add(
                    Math.cos(angle) * 2, 1.5, Math.sin(angle) * 2);
            org.bukkit.entity.Phantom phantom = (org.bukkit.entity.Phantom)
                    player.getWorld().spawnEntity(spawnLoc, org.bukkit.entity.EntityType.PHANTOM);
            phantom.setSize(0);
            phantom.setAI(true);
            phantom.setInvulnerable(true);
            phantom.setCustomName("§8[Drone]");
            phantom.setCustomNameVisible(false);
            phantom.setSilent(true);
            drones.add(phantom);
        }

        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            @Override public void run() {
                if (ticks >= durationTicks || !player.isOnline()) {
                    drones.forEach(d -> { if (d.isValid()) d.remove(); });
                    cancel(); return;
                }
                angle += 0.18;
                for (int i = 0; i < drones.size(); i++) {
                    org.bukkit.entity.Entity drone = drones.get(i);
                    if (!drone.isValid()) continue;
                    double a = angle + (Math.PI * 2 / drones.size()) * i;
                    org.bukkit.Location orbitTarget = player.getLocation().clone().add(
                            Math.cos(a) * 2.5, 1.8, Math.sin(a) * 2.5);
                    drone.teleport(orbitTarget);
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                            orbitTarget, 2, 0.1, 0.1, 0.1, 0.05);
                }
                if (ticks % 10 == 0) {
                    nearbyEntities(player, radius).forEach(e -> {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(tickDamage + dmg, player);
                            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                                    le.getLocation().add(0,1,0), 6, 0.3, 0.5, 0.3, 0.1);
                        }
                    });
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 0.4f, 2.0f);
                }
                ticks += 2;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private static void laserArray(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 1.5f);
        Vector dir = player.getLocation().getDirection().normalize();
        player.getWorld().spawnParticle(Particle.END_ROD, player.getEyeLocation(), 20,
                dir.getX() * 5, dir.getY() * 5, dir.getZ() * 5, 0.05);
        nearbyEntitiesInDirection(player, dir, 20.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.damage(8.0 + dmg, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
            }
        });
    }

    private static void forceFieldAbility(Player player, double dur, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int ticks = ac.getDurationTicks(AbilityType.FORCE_FIELD, 100);
        int resistance = ac.getResistanceLevel(AbilityType.FORCE_FIELD, 3);
        int radius = 3;
        long restoreTicks = plugin.getConfig().getLong("settings.block-restore-ticks", 600L);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 1.8f);

        com.hihelloy.invincible.world.TempBlockManager tbm =
                plugin.getFlightManager().getTempBlockManager();

        org.bukkit.Location centre = player.getLocation().clone();
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= radius + 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x*x + y*y + z*z);
                    if (dist < radius - 0.5 || dist > radius + 0.5) continue;
                    if (y == 0 && dist > radius * 0.7) continue;
                    org.bukkit.block.Block b = centre.getWorld().getBlockAt(
                            centre.getBlockX()+x, centre.getBlockY()+y, centre.getBlockZ()+z);
                    if (!b.getType().isAir()) continue;
                    if (y < 2) tbm.setBlock(b.getLocation(), org.bukkit.Material.IRON_BLOCK, restoreTicks);
                    else tbm.setBlock(b.getLocation(), org.bukkit.Material.CYAN_STAINED_GLASS, restoreTicks);
                }
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, (int)(ticks * dur), resistance));
        player.getWorld().spawnParticle(Particle.END_ROD, centre.add(0,2,0), 40, 2.0, 1.5, 2.0, 0.1);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, centre, 30, 2.0, 1.5, 2.0, 0.2);
        player.sendActionBar(Component.text("FORCE FIELD ACTIVE",
                net.kyori.adventure.text.format.NamedTextColor.AQUA,
                net.kyori.adventure.text.format.TextDecoration.BOLD));
    }

    private static void empPulse(Player player, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        double radius = ac.getRadius(AbilityType.EMP_PULSE, 10.0);
        int slowTicks = ac.getSlownessTicks(AbilityType.EMP_PULSE, 80);
        int weakTicks = ac.getWeaknessTicks(AbilityType.EMP_PULSE, 80);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2.0f, 1.8f);
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(0,1,0),
                100, radius * 0.4, 0.8, radius * 0.4, 0.3);

        nearbyEntities(player, radius).forEach(e -> {
            if (!(e instanceof LivingEntity le) || e.equals(player)) return;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowTicks, 3));
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weakTicks, 2));
            le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, slowTicks, 1));
            player.getWorld().strikeLightningEffect(le.getLocation());
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                    le.getLocation().add(0,1,0), 20, 0.3, 0.5, 0.3, 0.2);
        });
        player.sendActionBar(Component.text("EMP PULSE!",
                net.kyori.adventure.text.format.NamedTextColor.AQUA,
                net.kyori.adventure.text.format.TextDecoration.BOLD));
    }

    private static void tacticalScan(Player player, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.5f);
        StringBuilder sb = new StringBuilder(ChatColor.AQUA + "=== Scan ===\n");
        nearbyEntities(player, 20.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                sb.append(ChatColor.YELLOW).append(le.getName())
                        .append(ChatColor.WHITE).append(" HP: ")
                        .append(String.format("%.1f", le.getHealth())).append("/")
                        .append(String.format("%.1f", le.getMaxHealth())).append("\n");
                player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0);
            }
        });
        player.sendMessage(sb.toString());
    }

    private static void mechOverload(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(60 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, (int)(5 + dmg)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, (int)(3 + dmg)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 50, 1, 1, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 1.5f);
        new BukkitRunnable() {
            @Override public void run() {
                if (player.isOnline()) player.damage(5.0);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void duplicateSelf(Player player, InvinciblePlugin plugin) {
        for (int i = 0; i < 3; i++) {
            double angle = i * (2 * Math.PI / 3);
            Location loc = player.getLocation().clone().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);
            player.getWorld().spawnParticle(Particle.ENCHANT, loc, 20, 0.5, 1, 0.5, 0.5);
            player.getWorld().playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
        }
    }

    private static void swarmStrike(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.8f);
        nearbyEntities(player, 8.0).forEach(e -> {
            if (e instanceof LivingEntity le && e != player) {
                le.damage(6.0 + dmg, player);
                player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 8, 0.3, 0.5, 0.3, 0);
            }
        });
    }

    private static void cloneShield(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(120 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 3));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 1, 1, 1, 0.8);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.CLONE_SHIELD, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.CLONE_SHIELD, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void massAssault(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.5f, 0.8f);
        nearbyEntities(player, 12.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.damage(6.0 + dmg, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
            }
        });
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 20, 6, 0.5, 6, 0.1);
    }

    private static void diversionClone(Player player) {
        Location loc = player.getTargetBlock(null, 10).getLocation().add(0.5, 0, 0.5);
        player.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.5, 1, 0.5, 0.5);
        player.sendMessage(Component.text("Clone placed!", net.kyori.adventure.text.format.NamedTextColor.YELLOW));
    }

    private static void dimensionPortal(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.5f, 1.2f);
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0,1,0), 40, 0.5, 1, 0.5, 0.5);
        player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0,1,0), 20, 0.4, 0.8, 0.4, 0.3);

        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(30);
        Location dest;
        if (targetBlock != null) {
            dest = targetBlock.getLocation().add(0, 1, 0);
            dest.setYaw(player.getLocation().getYaw());
            dest.setPitch(player.getLocation().getPitch());
        } else {
            dest = player.getLocation().add(player.getLocation().getDirection().multiply(25));
        }
        dest.setY(Math.max(dest.getY(), player.getWorld().getMinHeight() + 1));
        player.teleport(dest);
        player.setFallDistance(0);
        dest.getWorld().playSound(dest, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.5f, 0.8f);
        dest.getWorld().spawnParticle(Particle.PORTAL, dest.clone().add(0,1,0), 40, 0.5, 1, 0.5, 0.5);
        dest.getWorld().spawnParticle(Particle.REVERSE_PORTAL, dest.clone().add(0,1,0), 20, 0.4, 0.8, 0.4, 0.3);
    }

    private static void monsterTransform(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(200 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 2.0f, 0.4f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.5f, 0.3f);
        for (int _mi = 0; _mi < 3; _mi++) {
            final int _mfi = _mi;
            new BukkitRunnable() { @Override public void run() {
                double _mr = 1.5 + _mfi * 1.5;
                for (double _ma = 0; _ma < Math.PI * 2; _ma += Math.PI / 10) {
                    player.getWorld().spawnParticle(Particle.EXPLOSION,
                            player.getLocation().add(Math.cos(_ma)*_mr, 0.5, Math.sin(_ma)*_mr),
                            1, 0.1, 0.1, 0.1, 0.0);
                    player.getWorld().spawnParticle(Particle.LAVA,
                            player.getLocation().add(Math.cos(_ma)*_mr, 0.3, Math.sin(_ma)*_mr),
                            2, 0.05, 0.1, 0.05, 0.0);
                }
            }}.runTaskLater(plugin, _mfi * 3L);
        }
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.MONSTER_TRANSFORM, true);
        player.sendMessage(Component.text("MONSTER TRANSFORM!", net.kyori.adventure.text.format.NamedTextColor.GREEN, net.kyori.adventure.text.format.TextDecoration.BOLD));
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.MONSTER_TRANSFORM, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void rampage(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (elapsed >= ticks || !player.isOnline()) { cancel(); return; }
                strikeNearbyEntities(player, 5.0, 4.0 + dmg, plugin);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 3, 2, 0.5, 2, 0);
                elapsed += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private static void tremorSlam(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 2.0f, 0.3f);
        player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation(),
                50, 2, 0.5, 2, 0.5, Material.STONE.createBlockData());
        knockbackNearbyEntities(player, 6.0, 0, 2.0);
        strikeNearbyEntities(player, 6.0, 6.0 + dmg, plugin);
    }

    private static void tailSweep(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2.0f, 0.7f);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 15, 3, 0.5, 3, 0.1);
        knockbackNearbyEntities(player, 6.0, 4.0 + dmg, 1.5);
    }

    private static void energyDischarge(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.2f);
        strikeNearbyEntities(player, 5.0, 6.0 + dmg, plugin);
        nearbyEntities(player, 5.0).forEach(e -> {
            if (e instanceof LivingEntity le) le.setFireTicks(60);
        });
    }

    private static void samsonCharge(Player player, double dmg, InvinciblePlugin plugin) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, (int)(4 + dmg)));
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 30, 0.5, 1, 0.5, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.8f);
        new BukkitRunnable() {
            @Override public void run() {
                if (player.isOnline()) {
                    strikeNearbyEntities(player, 5.0, 8.0 + dmg, plugin);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 5, 1, 1, 1, 0.2);
                }
            }
        }.runTaskLater(plugin, 20L);
    }

    private static void electromagneticField(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(120 * dur);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.ELECTROMAGNETIC_FIELD, true);
        new BukkitRunnable() {
            int elapsed = 0;
            @Override public void run() {
                if (elapsed >= ticks || !player.isOnline()) {
                    cancel();
                    plugin.getAbilityManager().setActiveAbility(player, AbilityType.ELECTROMAGNETIC_FIELD, false);
                    return;
                }
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 8, 4, 1, 4, 0.1);
                nearbyEntities(player, 5.0).forEach(e -> {
                    if (e instanceof LivingEntity le && !(le instanceof Player)) {
                        le.damage(2.0 + dmg, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 15, 2));
                    }
                });
                elapsed += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private static void overcharge(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 4));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 1));
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 40, 1, 1, 1, 0.4);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.5f, 2.0f);
    }

    private static void deflect(Player player, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int ticks = ac.getDurationTicks(AbilityType.DEFLECT, 40);
        int resistance = ac.getResistanceLevel(AbilityType.DEFLECT, 3);

        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, resistance));
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.DEFLECT, true);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.COUNTER_STRIKE, true);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.BULLET_CATCH, true);

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.5f, 0.8f);
        for (double a = 0; a < Math.PI * 2; a += Math.PI / 8) {
            double rx = Math.cos(a) * 1.2, rz = Math.sin(a) * 1.2;
            player.getWorld().spawnParticle(Particle.END_ROD,
                    player.getLocation().add(rx, 1, rz), 3, 0.05, 0.4, 0.05, 0.05);
        }
        player.sendActionBar(Component.text("DEFLECT ACTIVE — damage absorbed!",
                net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN,
                net.kyori.adventure.text.format.TextDecoration.BOLD));
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.DEFLECT, false);
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.COUNTER_STRIKE, false);
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.BULLET_CATCH, false);
            }
        }.runTaskLater(plugin, ticks);
    }
    private static void impactAbsorb(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, 2));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 25, 1, 1, 1, 0.5);
    }

    private static void rapidAdapt(Player player, AbilityType ability, double dur, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int ticks = (int)(ac.getDurationTicks(ability, 80) * dur);
        int resistance = ac.getResistanceLevel(ability, 2);
        int regenLevel = ac.getRegenLevel(ability, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, resistance));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, ticks, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, regenLevel));
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 20, 0.5, 1, 0.5, 0.3);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN, 1.0f, 1.2f);
    }

    private static void multiverseDrain(Player player, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        double radius = ac.getRadius(AbilityType.MULTIVERSE_DRAIN, 8.0);
        double stealPct = ac.getStealPercent(AbilityType.MULTIVERSE_DRAIN, 0.15);
        double maxSteal = ac.getMaxSteal(AbilityType.MULTIVERSE_DRAIN, 5.0);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.5f, 0.8f);
        player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation().add(0,1,0),
                60, radius * 0.3, 1.5, radius * 0.3, 0.4);

        final double[] totalStolen = {0};
        nearbyEntities(player, radius).forEach(e -> {
            if (!(e instanceof LivingEntity le) || e.equals(player)) return;
            double steal = Math.min(le.getHealth() * stealPct, maxSteal - totalStolen[0]);
            if (steal <= 0) return;
            le.setHealth(Math.max(0.5, le.getHealth() - steal));
            player.setHealth(Math.min(player.getAttribute(
                            Attribute.GENERIC_MAX_HEALTH).getValue(),
                    player.getHealth() + steal));
            totalStolen[0] += steal;
            le.getWorld().spawnParticle(Particle.REVERSE_PORTAL,
                    le.getLocation().add(0,1,0), 15, 0.3, 0.5, 0.3, 0.3);
            le.getWorld().playSound(le.getLocation(), Sound.ENTITY_ENDERMAN_HURT, 1.0f, 1.5f);
        });

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,
                (int)(plugin.getAbilityConfig().getStrengthTicks(AbilityType.MULTIVERSE_DRAIN, 80)),
                2));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0,2,0),
                (int)(totalStolen[0] * 2 + 3), 0.5, 0.3, 0.5, 0.1);
        player.sendActionBar(Component.text(String.format("Drained %.1f HP!", totalStolen[0]),
                net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE));
    }
    private static void portalTrap(Player player, InvinciblePlugin plugin) {
        org.bukkit.block.Block tb = player.getTargetBlockExact(20);
        Location trapLoc = tb != null
                ? tb.getLocation().add(0, 1, 0)
                : player.getLocation().add(player.getLocation().getDirection().multiply(10));
        if (trapLoc == null) return;

        player.getWorld().playSound(trapLoc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.5f);
        player.getWorld().spawnParticle(Particle.PORTAL, trapLoc.clone().add(0,0.5,0), 30, 0.4, 0.5, 0.4, 0.4);
        player.sendMessage(Component.text("Portal trap placed!", net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE));

        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (ticks >= 300 || !player.isOnline()) { cancel(); return; }
                trapLoc.getWorld().spawnParticle(Particle.PORTAL, trapLoc.clone().add(0,0.5,0),
                        2, 0.2, 0.3, 0.2, 0.3);
                for (org.bukkit.entity.Entity e : trapLoc.getWorld()
                        .getNearbyEntities(trapLoc, 2, 2, 2)) {
                    if (e.equals(player)) continue;
                    if (!(e instanceof LivingEntity le)) continue;
                    Location highLoc = trapLoc.clone().add(0, 12, 0);
                    le.teleport(highLoc);
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4));
                    le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 2));
                    trapLoc.getWorld().spawnParticle(Particle.REVERSE_PORTAL,
                            le.getLocation().add(0,1,0), 20, 0.3, 0.5, 0.3, 0.3);
                    trapLoc.getWorld().playSound(trapLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.6f);
                    cancel(); return;
                }
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private static void dimensionalShift(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(40 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 4));
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 1, 0.5, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.5f);
    }

    private static void crossDimension(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int durationTicks = (int)(ac.getDurationTicks(AbilityType.CROSS_DIMENSION, 100) * dur);
        double cloneDamage = 3.5 + dmg;
        int cloneCount = 3;

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 2.0f, 0.6f);
        // Spiral vortex windup
        for (double a = 0; a < Math.PI * 4; a += 0.25) {
            double r = 3.0 * (1 - a / (Math.PI * 4));
            org.bukkit.Location pl = player.getLocation().clone().add(
                    Math.cos(a) * r, a * 0.2, Math.sin(a) * r);
            player.getWorld().spawnParticle(Particle.PORTAL, pl, 3, 0.1, 0.1, 0.1, 0.2);
            player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, pl, 2, 0.1, 0.1, 0.1, 0.1);
        }
        player.sendActionBar(net.kyori.adventure.text.Component.text("Cross-Dimensional Clones!",
                net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE,
                net.kyori.adventure.text.format.TextDecoration.BOLD));

        // Build a player skull item with the player's skin
        org.bukkit.inventory.ItemStack skull = new org.bukkit.inventory.ItemStack(org.bukkit.Material.PLAYER_HEAD);
        org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) skull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skull.setItemMeta(skullMeta);
        }

        java.util.List<org.bukkit.entity.ArmorStand> clones = new java.util.ArrayList<>();
        for (int i = 0; i < cloneCount; i++) {
            double angle = (Math.PI * 2.0 / cloneCount) * i;
            org.bukkit.Location spawnLoc = player.getLocation().clone().add(
                    Math.cos(angle) * 2.5, 0, Math.sin(angle) * 2.5);
            spawnLoc.setYaw((float)(Math.toDegrees(-angle) + 180));

            org.bukkit.entity.ArmorStand stand = player.getWorld().spawn(spawnLoc, org.bukkit.entity.ArmorStand.class, s -> {
                s.setGravity(true);
                s.setVisible(true);
                s.setCustomName("§5§l" + player.getName());
                s.setCustomNameVisible(true);
                s.setArms(true);
                s.setBasePlate(true);
                // Equip player's skin and armor
                org.bukkit.inventory.EntityEquipment eq = s.getEquipment();
                if (eq != null) {
                    eq.setHelmet(skull);
                    eq.setChestplate(player.getInventory().getChestplate() != null
                            ? player.getInventory().getChestplate().clone() : null);
                    eq.setLeggings(player.getInventory().getLeggings() != null
                            ? player.getInventory().getLeggings().clone() : null);
                    eq.setBoots(player.getInventory().getBoots() != null
                            ? player.getInventory().getBoots().clone() : null);
                }
            });
            clones.add(stand);
            // Spawn-in burst
            spawnLoc.getWorld().spawnParticle(Particle.PORTAL, spawnLoc.clone().add(0,1,0), 30, 0.3, 0.8, 0.3, 0.5);
            spawnLoc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, spawnLoc.clone().add(0,1,0), 15, 0.2, 0.6, 0.2, 0.3);
            spawnLoc.getWorld().playSound(spawnLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.7f);
        }

        new BukkitRunnable() {
            int t = 0;
            double orbit = 0;
            @Override public void run() {
                if (t >= durationTicks || !player.isOnline()) {
                    clones.forEach(s -> { if (s.isValid()) {
                        s.getWorld().spawnParticle(Particle.REVERSE_PORTAL,
                                s.getLocation().add(0,1,0), 20, 0.3, 0.6, 0.3, 0.4);
                        s.remove();
                    }});
                    cancel(); return;
                }
                orbit += 0.05;
                for (int i = 0; i < clones.size(); i++) {
                    org.bukkit.entity.ArmorStand stand = clones.get(i);
                    if (!stand.isValid()) continue;
                    double a = orbit + (Math.PI * 2.0 / clones.size()) * i;
                    org.bukkit.Location target = player.getLocation().clone().add(
                            Math.cos(a) * 2.5, 0, Math.sin(a) * 2.5);
                    target.setYaw((float)(Math.toDegrees(-a) + 180));
                    stand.teleport(target);
                    // Constant damage aura — radiates 2.5 block damage every 10 ticks
                    if (t % 10 == 0) {
                        for (org.bukkit.entity.Entity e : stand.getWorld()
                                .getNearbyEntities(stand.getLocation(), 2.5, 2.5, 2.5)) {
                            if (e.equals(player) || clones.contains(e)) continue;
                            if (!(e instanceof LivingEntity le)) continue;
                            le.damage(cloneDamage * 0.3, player);
                            stand.getWorld().spawnParticle(Particle.CRIT,
                                    le.getLocation().add(0,1,0), 8, 0.3, 0.4, 0.3, 0.15);
                            stand.getWorld().spawnParticle(Particle.PORTAL,
                                    le.getLocation().add(0,1,0), 5, 0.2, 0.3, 0.2, 0.2);
                        }
                    }
                    // Portal trail
                    stand.getWorld().spawnParticle(Particle.PORTAL,
                            stand.getLocation().add(0, 0.8, 0), 4, 0.1, 0.3, 0.1, 0.2);
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    private static void realityTear(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.5f, 0.5f);
        // Reality tear — dimensional shatter
        for (int _wave = 1; _wave <= 3; _wave++) {
            final int _w = _wave;
            new BukkitRunnable() { @Override public void run() {
                double _r = _w * 5;
                for (double _a = 0; _a < Math.PI * 2; _a += Math.PI / 16) {
                    player.getWorld().spawnParticle(Particle.PORTAL,
                            player.getLocation().add(Math.cos(_a)*_r, 1, Math.sin(_a)*_r),
                            3, 0.2, 0.5, 0.2, 0.3);
                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL,
                            player.getLocation().add(Math.cos(_a)*_r, 1, Math.sin(_a)*_r),
                            2, 0.1, 0.3, 0.1, 0.2);
                }
                player.getWorld().spawnParticle(Particle.EXPLOSION,
                        player.getLocation(), 4, _r*0.2, 0.5, _r*0.2, 0.0);
            }}.runTaskLater(plugin, _w * 4L);
        }
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 100, 10, 5, 10, 1.0);
        strikeNearbyEntities(player, 18.0, 13.0 + dmg, plugin);
        knockbackNearbyEntities(player, 18.0, 0, 4.5);
        player.getWorld().createExplosion(player.getLocation(), 0f, false, false);
    }

    private static void miniaturize(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 3));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.5, 0.5, 0.5, 0.1);
    }

    private static void microStrike(Player player, double dmg, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 4.0);
        if (target instanceof LivingEntity le) {
            le.damage(8.0 + dmg, player);
            le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
            player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.3);
            player.getWorld().playSound(le.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.5f, 1.5f);
        }
    }

    private static void giantForm(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(120 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 5));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 1));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 2.0f, 0.3f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10, 2, 2, 2, 0.3);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.GIANT_FORM, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.GIANT_FORM, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void sizeWave(Player player, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.8f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, 8, 0.5, 8, 0.1);
        nearbyEntities(player, 10.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 2));
            }
        });
    }

    private static void tacticalCommand(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 1));
        nearbyEntities(player, 15.0).forEach(e -> {
            if (e instanceof Player ally && ally != player) {
                ally.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 1));
            }
        });
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 20, 3, 1, 3, 0.3);
    }

    private static void weaponMastery(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, (int)(3 + dmg)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, ticks, 2));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 20, 0.5, 1, 0.5, 0.8);
    }

    private static void strategicStrike(Player player, double dmg, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 6.0);
        if (target instanceof LivingEntity le) {
            le.damage(8.0 + dmg, player);
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0.5);
        }
    }

    private static void predatorLeap(Player player, double spd, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 20.0);
        if (target != null) {
            Vector dir = target.getLocation().subtract(player.getLocation()).toVector().normalize();
            dir.setY(0.6);
            player.setVelocity(dir.multiply(3.0 * spd));
        } else {
            Vector dir = player.getLocation().getDirection().normalize().multiply(3.0 * spd);
            dir.setY(0.8);
            player.setVelocity(dir);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_STEP, 1.5f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.6, 0.4, 0.6, 0.25);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 15, 0.5, 0.3, 0.5, 0.2);
        for (double a = 0; a < Math.PI * 2; a += Math.PI / 8) {
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                    player.getLocation().add(Math.cos(a)*1.2, 0.3, Math.sin(a)*1.2), 1, 0, 0, 0, 0);
        }
    }

    private static void maneShield(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 4));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.7f);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.0f, 0.9f);
        for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
            player.getWorld().spawnParticle(Particle.ENCHANT,
                    player.getLocation().add(Math.cos(a)*1.8, 1.0, Math.sin(a)*1.8), 5, 0.1, 0.4, 0.1, 0.5);
            player.getWorld().spawnParticle(Particle.END_ROD,
                    player.getLocation().add(Math.cos(a)*1.8, 1.0, Math.sin(a)*1.8), 2, 0.05, 0.2, 0.05, 0.03);
        }
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0,1,0), 20, 0.6, 0.8, 0.6, 0.1);
    }

    private static void berserkerRage(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, (int)(5 + dmg)));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 1));
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 4.0));
        // Berserker rage — ring of fire pillars + WITHER darkness
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 2.0f, 0.4f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.8f, 0.5f);
        for (int ring = 0; ring < 3; ring++) {
            final int fr = ring;
            new BukkitRunnable() { @Override public void run() {
                double r = 1.5 + fr * 1.2;
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                    org.bukkit.Location fl = player.getLocation().clone()
                            .add(Math.cos(a) * r, 0.2, Math.sin(a) * r);
                    player.getWorld().spawnParticle(Particle.FLAME, fl, 6, 0.1, 0.5, 0.1, 0.04);
                    player.getWorld().spawnParticle(Particle.LAVA, fl, 2, 0.05, 0.1, 0.05, 0.0);
                }
                player.getWorld().spawnParticle(Particle.EXPLOSION,
                        player.getLocation(), 2, r * 0.1, 0.1, r * 0.1, 0.0);
            }}.runTaskLater(plugin, ring * 4L);
        }
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0,1,0), 40, 0.6, 0.8, 0.6, 0.08);
    }

    private static void velocityDive(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 0.5f);
        Vector dir = new Vector(0, -3.0, 0).add(player.getLocation().getDirection().normalize().multiply(1.5));
        player.setVelocity(dir);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline()) { cancel(); return; }
                if (player.isOnGround() || ticks > 40) {
                    cancel();
                    strikeNearbyEntities(player, 6.0, 14.0 + dmg, plugin);
                    knockbackNearbyEntities(player, 6.0, 0, 2.5);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 8, 2, 0.5, 2, 0.3);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
                    return;
                }
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 5, 0.2, 0.2, 0.2, 0.06);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 2, 0.3, 0.1, 0.3, 0.0);
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 3, 0.2, 0.1, 0.2, 0.08);
                ticks++;
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private static void supersonicStrike(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 1.8f);
        Entity target = getClosestEntity(player, 20.0);
        Vector dir = target != null
                ? target.getLocation().subtract(player.getLocation()).toVector().normalize()
                : player.getLocation().getDirection().normalize();
        dir.setY(0.1);
        player.setVelocity(dir.multiply(6.0 * spd));
        // Sonic boom trail
        for (int i = 0; i < 8; i++) {
            final int fi = i;
            new BukkitRunnable() { @Override public void run() {
                org.bukkit.Location tl = player.getLocation().clone().subtract(
                        player.getLocation().getDirection().multiply(fi * 0.6));
                player.getWorld().spawnParticle(Particle.CRIT, tl, 6, 0.15, 0.15, 0.15, 0.06);
                player.getWorld().spawnParticle(Particle.CLOUD, tl, 4, 0.1, 0.1, 0.1, 0.03);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, tl, 2, 0.3, 0.1, 0.3, 0.0);
            }}.runTaskLater(plugin, fi);
        }
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 12) { cancel(); return; }
                strikeNearbyEntities(player, 3.0, 10.0 + dmg, plugin);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void ironRush(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(5.0 * spd);
        dir.setY(0.3);
        player.setVelocity(dir);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 2));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.2);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 10) { cancel(); return; }
                strikeNearbyEntities(player, 2.5, 8.0 + dmg, plugin);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void killingBlow(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 30, 1, 1, 1, 0.5);
        nearbyEntities(player, 5.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                double extraDmg = le.getHealth() < le.getMaxHealth() * 0.30 ? le.getHealth() + 1 : 10.0 + dmg;
                le.damage(extraDmg, player);
            }
        });
    }

    private static void atomicCage(Player player, double dur, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.5f, 0.5f);
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation(), 40, 2, 2, 2, 0.3);
        int ticks = (int)(60 * dur);
        nearbyEntities(player, 6.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 10));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 2));
            }
        });
    }

    private static void graviticSlam(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1.5f, 0.5f);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 30, 3, 2, 3, 0.3);
        nearbyEntities(player, 8.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.damage(6.0 + dmg, player);
                le.setVelocity(new Vector(0, -3.0, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 3));
            }
        });
    }

    private static void stealthMode(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticks, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 1, 0.5, 0.1);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.STEALTH_MODE, true);
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.STEALTH_MODE, false);
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void shieldDeploy(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        nearbyEntities(player, 8.0).forEach(e -> {
            if (e instanceof Player ally && ally != player) {
                ally.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 1));
            }
        });
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 30, 3, 1, 3, 0.2);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
    }

    private static void tacticalRetreat(Player player, AbilityType ability, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(-4.0 * spd);
        dir.setY(0.4);
        player.setVelocity(dir);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_STEP, 1.5f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.6, 0.4, 0.6, 0.25);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 15, 0.5, 0.3, 0.5, 0.2);
        for (double _pa = 0; _pa < Math.PI * 2; _pa += Math.PI / 8) {
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                    player.getLocation().add(Math.cos(_pa)*1.2, 0.3, Math.sin(_pa)*1.2), 1, 0, 0, 0, 0);
        }
    }

    private static void monsterLeap(Player player, double spd, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 30.0);
        Vector dir = target != null
                ? target.getLocation().subtract(player.getLocation()).toVector().normalize()
                : player.getLocation().getDirection().normalize();
        dir.setY(0.9);
        player.setVelocity(dir.multiply(4.0 * spd));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ATTACK, 1.5f, 0.6f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 5, 1, 1, 1, 0.2);
    }

    private static void proximityMine(Player player, double dmg, InvinciblePlugin plugin) {
        Location mineLoc = player.getLocation().clone();
        player.getWorld().spawnParticle(Particle.FLAME, mineLoc, 5, 0.2, 0.1, 0.2, 0);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (ticks >= 300) { cancel(); return; }
                nearbyEntities(mineLoc, 2.5, player.getWorld()).forEach(e -> {
                    if (e instanceof Player target && target != player) {
                        player.getWorld().createExplosion(mineLoc, 0f, false, false);
                        player.getWorld().spawnParticle(Particle.EXPLOSION, mineLoc, 5, 1, 1, 1, 0.3);
                        nearbyEntities(mineLoc, 4.0, player.getWorld()).forEach(en -> {
                            if (en instanceof LivingEntity le) le.damage(12.0 + dmg, player);
                        });
                        cancel();
                    }
                });
                ticks += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private static void edict(Player player, double dur, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.5f, 0.7f);
        player.getWorld().spawnParticle(Particle.CRIMSON_SPORE, player.getLocation(), 50, 5, 2, 5, 0.3);
        int ticks = (int)(40 * dur);
        nearbyEntities(player, 10.0).forEach(e -> {
            if (e instanceof LivingEntity le && !(le instanceof Player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 10));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 4));
                le.setVelocity(new Vector(0, 0, 0));
            }
        });
    }

    private static void genericDash(Player player, double spd, InvinciblePlugin plugin) {
        double velocity = 3.8 * spd;
        org.bukkit.util.Vector dir = player.getLocation().getDirection().normalize();
        dir.setY(0.15);
        player.setVelocity(dir.multiply(Math.min(velocity, 5.0)));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 12, 0.3, 0.2, 0.3, 0.15);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0,0.8,0), 3, 0.4, 0.1, 0.4, 0.0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.8f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.4f, 2.0f);
    }

    private static void shadowStrike(Player player, double dmg, InvinciblePlugin plugin) {
        double range = 12.0;
        double damage = 11.0 + dmg;

        org.bukkit.entity.Entity target = getClosestEntity(player, range);
        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(net.kyori.adventure.text.Component.text("No target in range.",
                    net.kyori.adventure.text.format.NamedTextColor.GRAY));
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20, 0));
        // Shadow strike — black smoke + crit ring
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 25, 0.4, 0.6, 0.4, 0.3);
        player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation().add(0,1,0), 20, 0.4, 0.5, 0.4, 0.1);
        for (double _a = 0; _a < Math.PI * 2; _a += Math.PI / 8) {
            player.getWorld().spawnParticle(Particle.CRIT,
                    player.getLocation().add(Math.cos(_a)*1.5, 1, Math.sin(_a)*1.5), 2, 0.05, 0.2, 0.05, 0.05);
        }

        org.bukkit.util.Vector behind = target.getLocation().getDirection().normalize().multiply(-1.5);
        org.bukkit.Location strikePos = target.getLocation().clone().add(behind).add(0, 0.1, 0);
        strikePos.setYaw(target.getLocation().getYaw() + 180);

        new BukkitRunnable() {
            @Override public void run() {
                if (!player.isOnline() || !le.isValid()) return;
                player.teleport(strikePos);
                le.damage(damage, player);
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                org.bukkit.util.Vector kb = le.getLocation().subtract(player.getLocation()).toVector().normalize()
                        .multiply(3.0).add(new org.bukkit.util.Vector(0, 0.5, 0));
                le.setVelocity(kb);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, le.getLocation().add(0,1,0), 12, 0.5, 0.5, 0.5, 0.1);
                player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.0f, 0.7f);
                player.sendActionBar(net.kyori.adventure.text.Component.text("SHADOW STRIKE!",
                        net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY,
                        net.kyori.adventure.text.format.TextDecoration.BOLD));
            }
        }.runTaskLater(plugin, 8L);
    }

    private static void opponentGrip(Player player, double dmg, InvinciblePlugin plugin) {
        double range = 4.0;
        double damage = 5.0 + dmg;
        int holdTicks = 30;

        org.bukkit.entity.Entity target = getClosestEntity(player, range);
        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(net.kyori.adventure.text.Component.text("No target in range.",
                    net.kyori.adventure.text.format.NamedTextColor.GRAY));
            return;
        }

        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.5f);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, holdTicks + 20, 10));
        le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, holdTicks + 20, 2));

        new BukkitRunnable() {
            int t = 0;
            @Override public void run() {
                if (!player.isOnline() || !le.isValid() || t >= holdTicks) { cancel(); return; }
                org.bukkit.util.Vector pull = player.getLocation().subtract(le.getLocation()).toVector();
                if (pull.length() > 2.0) le.setVelocity(pull.normalize().multiply(1.5));
                if (t % 6 == 0) {
                    le.damage(damage * 0.3, player);
                    player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0,1,0), 4, 0.2, 0.2, 0.2, 0.1);
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void grapple(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        double range = 5.0;
        double pullVel = 1.8 * spd;
        double damage = 8.0 + dmg;
        int holdTicks = 40;

        Entity target = getClosestEntity(player, range);
        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(Component.text("No target in range.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
            return;
        }

        if (target instanceof Player targetPlayer && plugin.getFlightManager().isFlying(targetPlayer)) {
            plugin.getFlightManager().stopFlight(targetPlayer);
            targetPlayer.sendActionBar(Component.text("Flight disrupted by grapple!", net.kyori.adventure.text.format.NamedTextColor.RED));
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.6f);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, le.getLocation().add(0, 1, 0), 6, 0.5, 0.5, 0.5, 0);
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.GRAPPLE, true);
        player.sendActionBar(Component.text("GRAPPLE — Sneak to break", net.kyori.adventure.text.format.NamedTextColor.YELLOW));

        new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                if (!player.isOnline() || !le.isValid()) { cancel(); return; }
                if (!plugin.getAbilityManager().isAbilityActive(player, AbilityType.GRAPPLE)) { cancel(); return; }
                if (t >= holdTicks) {

                    org.bukkit.util.Vector dir = le.getLocation().subtract(player.getLocation()).toVector().normalize();
                    le.setVelocity(dir.multiply(3.5).add(new org.bukkit.util.Vector(0, 0.6, 0)));
                    le.damage(Math.min(damage, 14.0), player);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, le.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.3);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.0f, 0.6f);
                    player.sendActionBar(Component.text("SLAMMED!", net.kyori.adventure.text.format.NamedTextColor.GOLD));
                    plugin.getAbilityManager().setActiveAbility(player, AbilityType.GRAPPLE, false);
                    cancel();
                    return;
                }

                org.bukkit.util.Vector toPlayer = player.getLocation().subtract(le.getLocation()).toVector();
                double dist = toPlayer.length();
                if (dist > 2.5) le.setVelocity(toPlayer.normalize().multiply(pullVel));
                else le.setVelocity(new org.bukkit.util.Vector(0, -0.1, 0));

                if (t % 8 == 0 && t > 0) {
                    le.damage(damage * 0.4, player);
                    player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 4, 0.2, 0.2, 0.2, 0);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 0.7f, 1.2f);
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void vacuumPunch(Player player, double dmg, InvinciblePlugin plugin) {
        double pullRadius = 14.0;
        double damage = 28.0 + dmg;
        int windupTicks = 40;
        double pullForce = 2.8;

        player.sendActionBar(Component.text("VACUUM PUNCH — pulling...",
                net.kyori.adventure.text.format.NamedTextColor.RED,
                net.kyori.adventure.text.format.TextDecoration.BOLD));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, 1.5f, 0.5f);

        new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }

                for (int p = 0; p < 12; p++) {
                    double angle = 2 * Math.PI * p / 12 + (t * 0.3);
                    double spiralR = pullRadius * (1.0 - (double) t / windupTicks);
                    double x = player.getLocation().getX() + spiralR * Math.cos(angle);
                    double z = player.getLocation().getZ() + spiralR * Math.sin(angle);
                    org.bukkit.Location pLoc = new org.bukkit.Location(player.getWorld(), x, player.getLocation().getY() + 1, z);
                    player.getWorld().spawnParticle(Particle.CLOUD, pLoc, 1, 0.3, 0.3, 0.3, 0.01);
                    player.getWorld().spawnParticle(Particle.PORTAL, pLoc, 3, 0.2, 0.2, 0.2, 0.4);
                    player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, pLoc, 2, 0.15, 0.15, 0.15, 0.2);
                }

                nearbyEntities(player, pullRadius).forEach(e -> {
                    if (e instanceof LivingEntity le && !e.equals(player)) {
                        org.bukkit.util.Vector pull = player.getLocation().subtract(le.getLocation()).toVector();
                        double dist = pull.length();
                        if (dist > 0.5) {
                            double force = pullForce * (1.0 - (dist / pullRadius));
                            le.setVelocity(pull.normalize().multiply(Math.max(force, 0.3)));
                        }
                    }
                });
                if (t >= windupTicks) {
                    cancel();
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 2.0f, 0.4f);
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10, 3, 1, 3, 0.3);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 60, 5, 2, 5, 0.5);
                    destroyBlocks(player.getLocation(), 5.0, plugin);
                    nearbyEntities(player, 6.0).forEach(e -> {
                        if (e instanceof LivingEntity le && !e.equals(player)) {
                            le.damage(damage, player);
                            org.bukkit.util.Vector kb = le.getLocation().subtract(player.getLocation())
                                    .toVector().normalize().multiply(4.0).add(new org.bukkit.util.Vector(0, 0.8, 0));
                            le.setVelocity(kb);
                        }
                    });
                    player.sendActionBar(Component.text("VACUUM PUNCH!",
                            net.kyori.adventure.text.format.NamedTextColor.RED,
                            net.kyori.adventure.text.format.TextDecoration.BOLD));
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void destroyBlocks(org.bukkit.Location centre, double radius, InvinciblePlugin plugin) {
        long ticks = plugin.getConfig().getLong("settings.block-restore-ticks", 600L);
        plugin.getFlightManager().getTempBlockManager().explodeBlocks(centre, radius, ticks);
    }

    private static void strikeNearbyEntities(Player player, double radius, double damage, InvinciblePlugin plugin) {
        nearbyEntities(player, radius).forEach(e -> {
            if (e instanceof LivingEntity le && e != player) {
                le.damage(damage, player);
                player.getWorld().spawnParticle(Particle.CRIT, le.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0);
            }
        });
    }

    private static void knockbackNearbyEntities(Player player, double radius, double damage, double force) {
        nearbyEntities(player, radius).forEach(e -> {
            if (e instanceof LivingEntity le && e != player) {
                Vector dir = e.getLocation().subtract(player.getLocation()).toVector().normalize();
                dir.setY(0.4);
                le.setVelocity(dir.multiply(force));
                if (damage > 0) le.damage(damage, player);
            }
        });
    }

    private static List<Entity> nearbyEntities(Player player, double radius) {
        return player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)
                .stream().filter(e -> e != player).toList();
    }

    private static List<Entity> nearbyEntities(Location loc, double radius, World world) {
        return world.getNearbyEntities(loc, radius, radius, radius).stream().toList();
    }

    private static List<Entity> nearbyEntitiesInDirection(Player player, Vector dir, double maxDist) {
        return player.getWorld().getNearbyEntities(player.getEyeLocation(), maxDist, maxDist, maxDist)
                .stream()
                .filter(e -> e != player)
                .filter(e -> e.getLocation().subtract(player.getLocation()).toVector().normalize().dot(dir) > 0.7)
                .toList();
    }

    private static Entity getClosestEntity(Player player, double radius) {
        return player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)
                .stream()
                .filter(e -> e != player && e instanceof LivingEntity)
                .min((a, b) -> Double.compare(
                        a.getLocation().distanceSquared(player.getLocation()),
                        b.getLocation().distanceSquared(player.getLocation())))
                .orElse(null);
    }

    private static void gdaAirstrike(Player player, double dmg, InvinciblePlugin plugin) {

        Location target = player.getTargetBlockExact(30) != null
                ? player.getTargetBlockExact(30).getLocation().add(0.5, 1, 0.5)
                : player.getLocation().add(player.getLocation().getDirection().multiply(15));
        player.getWorld().spawnParticle(Particle.FLAME, target, 20, 1, 1, 1, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5f, 0.5f);
        player.sendMessage(Component.text("GDA Airstrike inbound...", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
        Location strikeTarget = target.clone();
        new BukkitRunnable() {
            @Override public void run() {
                strikeTarget.getWorld().createExplosion(strikeTarget, (float)(8.0 + dmg), false, false);
                strikeTarget.getWorld().spawnParticle(Particle.EXPLOSION, strikeTarget, 6, 3, 3, 3, 0.3);
                strikeTarget.getWorld().playSound(strikeTarget, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.6f);
                nearbyEntities(player, strikeTarget, 8.0).forEach(e -> {
                    if (e instanceof LivingEntity le && !le.equals(player)) le.damage(10.0 + dmg, player);
                });
            }
        }.runTaskLater(plugin, 40L);
    }

    private static void droneBarrage(Player player, double dmg, InvinciblePlugin plugin) {

        List<Entity> targets = nearbyEntities(player, player.getLocation(), 20.0).stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(player))
                .sorted(java.util.Comparator.comparingDouble(e -> ((org.bukkit.entity.Entity) e).getLocation().distanceSquared(player.getLocation())))
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        if (targets.isEmpty()) { player.sendMessage(Component.text("No targets in drone range.", net.kyori.adventure.text.format.NamedTextColor.GRAY)); return; }
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 2.0f);
        for (int i = 0; i < targets.size(); i++) {
            final Entity t = targets.get(i);
            new BukkitRunnable() {
                @Override public void run() {
                    if (!t.isValid()) return;
                    t.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, t.getLocation().add(0,1,0), 15, 0.3, 0.3, 0.3, 0.2);
                    t.getWorld().playSound(t.getLocation(), Sound.ENTITY_ARROW_HIT, 1.0f, 1.5f);
                    if (t instanceof LivingEntity le) le.damage(14.0 + dmg, player);
                }
            }.runTaskLater(plugin, (i + 1) * 10L);
        }
    }

    private static void freezeAssets(Player player, double dur, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 15.0);
        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(Component.text("No target in range.", net.kyori.adventure.text.format.NamedTextColor.GRAY)); return;
        }
        int ticks = (int)(100 * dur);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 10));
        le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 10));
        le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, ticks, 10));
        target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation().add(0,1,0), 25, 0.5, 1, 0.5, 0.1);
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
        player.sendMessage(Component.text("Target frozen by GDA authority.", net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA));
    }

    private static void redTape(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        nearbyEntities(player, player.getLocation(), 10.0).forEach(e -> {
            if (e instanceof LivingEntity le && !le.equals(player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 6));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 4));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, ticks, 0));
            }
        });
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 30, 3, 1, 3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.7f);
        player.sendMessage(Component.text("Enemies buried in red tape!", net.kyori.adventure.text.format.NamedTextColor.RED));
    }

    private static void naniteInjection(Player player, double dur, InvinciblePlugin plugin) {
        com.hihelloy.invincible.config.AbilityConfig ac = plugin.getAbilityConfig();
        int ticks = (int)(ac.getDurationTicks(AbilityType.NANITE_INJECTION, 100) * dur);
        int regenLvl = ac.getRegenLevel(AbilityType.NANITE_INJECTION, 2);
        int absLvl = 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, ticks, regenLvl));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, ticks, absLvl));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.5f);

        for (int i = 0; i < 3; i++) {
            final int fi = i;
            new BukkitRunnable() { @Override public void run() {
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,
                        player.getLocation().add(0, 0.5 + fi * 0.4, 0),
                        12, 0.4, 0.3, 0.4, 0.05);
                player.getWorld().spawnParticle(Particle.END_ROD,
                        player.getLocation().add(0, 1, 0),
                        6, 0.3, 0.4, 0.3, 0.08);
            }}.runTaskLater(plugin, fi * 5L);
        }
        player.sendActionBar(Component.text("Nanite Repair Active — Regenerating",
                net.kyori.adventure.text.format.NamedTextColor.GREEN));
    }
    private static void lockdown(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);
        nearbyEntities(player, player.getLocation(), 15.0).forEach(e -> {
            if (e instanceof LivingEntity le && !le.equals(player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 4));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 2));
            }
        });
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 40, 5, 1, 5, 0.2);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 2.0f, 0.5f);
        player.sendMessage(Component.text("GDA LOCKDOWN ACTIVE.", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
    }

    private static void fieldCommander(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);

        player.getWorld().getNearbyEntities(player.getLocation(), 15, 15, 15).forEach(e -> {
            if (e instanceof Player ally && !ally.equals(player)) {
                ally.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 1));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ticks, 1));
                ally.sendMessage(Component.text("Field Commander Stedman has boosted your combat effectiveness!", net.kyori.adventure.text.format.NamedTextColor.GOLD));
            }
        });
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, ticks, 1));
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 30, 3, 1, 3, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 1.8f);
        player.sendMessage(Component.text("Field command assumed — allies boosted!", net.kyori.adventure.text.format.NamedTextColor.GOLD));
    }

    private static void blackSite(Player player, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 15.0);
        if (!(target instanceof LivingEntity)) {
            player.sendMessage(Component.text("No target in range.", net.kyori.adventure.text.format.NamedTextColor.GRAY)); return;
        }
        Location drop = target.getLocation().add(0, 15, 0);
        target.teleport(drop);
        target.getWorld().spawnParticle(Particle.PORTAL, drop, 30, 1, 1, 1, 0.5);
        target.getWorld().playSound(drop, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.5f);
        player.sendMessage(Component.text("Target sent to black site.", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
    }

    private static void classified(Player player, double dur, InvinciblePlugin plugin) {

        player.getActivePotionEffects().stream()
                .map(PotionEffect::getType)
                .filter(t -> t == PotionEffectType.SLOWNESS || t == PotionEffectType.WEAKNESS
                        || t == PotionEffectType.POISON || t == PotionEffectType.WITHER
                        || t == PotionEffectType.BLINDNESS || t == PotionEffectType.NAUSEA
                        || t == PotionEffectType.NAUSEA || t == PotionEffectType.MINING_FATIGUE)
                .forEach(player::removePotionEffect);
        int ticks = (int)(40 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 4));
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 40, 1, 1, 1, 0.8);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.5f);
        player.sendMessage(Component.text("CLASSIFIED protocols activated.", net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY));
    }

    private static void authorityOverride(Player player, double dmg, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(60 * dur);
        nearbyEntities(player, player.getLocation(), 12.0).forEach(e -> {
            if (e instanceof LivingEntity le && !le.equals(player)) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, ticks, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 2));
                le.damage(8.0 + dmg, player);
            }
        });
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 25, 3, 1, 3, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 1.5f);
        player.sendMessage(Component.text("Authority override — enemy combat protocols disrupted!", net.kyori.adventure.text.format.NamedTextColor.GRAY));
    }

    private static void kineticRelease(Player player, double dmg, InvinciblePlugin plugin) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.5f, 0.7f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.8f);
        // Stored kinetic energy burst — cylindrical rings outward
        for (int ring = 0; ring < 3; ring++) {
            final double r = 2.0 + ring * 2.0;
            final int del = ring * 2;
            new BukkitRunnable() { @Override public void run() {
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 14) {
                    player.getWorld().spawnParticle(Particle.END_ROD,
                            player.getLocation().add(Math.cos(a)*r, 0.8, Math.sin(a)*r),
                            2, 0.05, 0.25, 0.05, 0.04);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                            player.getLocation().add(Math.cos(a)*r, 0.5, Math.sin(a)*r),
                            1, 0, 0, 0, 0);
                }
            }}.runTaskLater(plugin, del);
        }
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 12, 2.5, 1.0, 2.5, 0.1);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0,1,0), 50, 3, 1.5, 3, 0.4);
        player.getWorld().createExplosion(player.getLocation(), 0f, false, false);
        strikeNearbyEntities(player, 6.0, 8.0 + dmg, plugin);
        knockbackNearbyEntities(player, 8.0, 0, 3.0);
    }

    private static void shockwaveFist(Player player, double dmg, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.6f);

        for (int i = 1; i <= 12; i++) {
            Location pos = player.getLocation().add(dir.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, pos, 4, 0.5, 0.5, 0.5, 0.05);
            player.getWorld().spawnParticle(Particle.EXPLOSION, pos, 1, 0, 0, 0, 0);
        }
        nearbyEntitiesInDirection(player, dir, 14.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.damage(14.0 + dmg, player);
                le.setVelocity(dir.clone().multiply(3.0).add(new Vector(0, 0.5, 0)));
            }
        });
    }

    private static void impenetrable(Player player, InvinciblePlugin plugin) {

        plugin.getAbilityManager().setActiveAbility(player, AbilityType.IMPENETRABLE, true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255));
        // Impenetrable — 3-ring END_ROD fortress rising upward
        for (int y = 0; y <= 2; y++) {
            final int fy = y;
            new BukkitRunnable() { @Override public void run() {
                for (double a = 0; a < Math.PI * 2; a += Math.PI / 12) {
                    player.getWorld().spawnParticle(Particle.END_ROD,
                            player.getLocation().add(Math.cos(a)*1.8, fy * 0.9, Math.sin(a)*1.8),
                            3, 0.05, 0.05, 0.05, 0.03);
                }
            }}.runTaskLater(plugin, fy * 3L);
        }
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0,1,0), 80, 1.5, 1.5, 1.5, 1.2);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0,1,0), 30, 1, 1, 1, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.5f, 0.5f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 2.0f);
        player.sendMessage(Component.text("IMPENETRABLE — you cannot be moved!", net.kyori.adventure.text.format.NamedTextColor.WHITE, net.kyori.adventure.text.format.TextDecoration.BOLD));
        new BukkitRunnable() {
            @Override public void run() {
                player.removePotionEffect(PotionEffectType.RESISTANCE);
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.IMPENETRABLE, false);
                player.sendMessage(Component.text("Impenetrable ended.", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
            }
        }.runTaskLater(plugin, 100L);
    }

    private static void bulletCatch(Player player, double dmg, InvinciblePlugin plugin) {
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.BULLET_CATCH, true);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 20, 0.5, 1, 0.5, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.5f);
        player.sendMessage(Component.text("Bullet Catch ready — next hit will be returned!", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));

        new BukkitRunnable() {
            @Override public void run() {
                if (plugin.getAbilityManager().isAbilityActive(player, AbilityType.BULLET_CATCH)) {
                    plugin.getAbilityManager().setActiveAbility(player, AbilityType.BULLET_CATCH, false);
                    player.sendMessage(Component.text("Bullet Catch expired.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    private static void momentumTransfer(Player player, double dmg, InvinciblePlugin plugin) {
        Entity target = getClosestEntity(player, 8.0);
        if (!(target instanceof LivingEntity le)) {
            player.sendMessage(Component.text("No target close enough.", net.kyori.adventure.text.format.NamedTextColor.GRAY)); return;
        }
        Vector dir = target.getLocation().subtract(player.getLocation()).toVector().normalize();
        le.damage(12.0 + dmg, player);
        le.setVelocity(dir.multiply(6.0).add(new Vector(0, 1.0, 0)));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.4f);
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation(), 8, 1, 1, 1, 0.2);
        player.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 3, 0.5, 0.5, 0.5, 0.1);
    }

    private static void unstoppable(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(5.5 * spd);
        dir.setY(0.15);
        player.setVelocity(dir);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 4));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 2.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.3);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 8) { cancel(); return; }

                player.removePotionEffect(PotionEffectType.SLOWNESS);
                strikeNearbyEntities(player, 2.5, 10.0 + dmg, plugin);
                knockbackNearbyEntities(player, 2.5, 0, 2.0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void ironDensity(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(100 * dur);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 3));
        player.setWalkSpeed(0.05f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 25, 0.5, 1, 0.5, 0.2);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.5f, 0.3f);
        player.sendMessage(Component.text("Iron Density — you are immovable!", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
        new BukkitRunnable() {
            @Override public void run() {
                player.setWalkSpeed(0.2f);
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                player.sendMessage(Component.text("Density returned to normal.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void kineticFeedback(Player player, double dur, InvinciblePlugin plugin) {
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.KINETIC_FEEDBACK, true);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 0.5, 1, 0.5, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.8f);
        player.sendMessage(Component.text("Kinetic Feedback active — absorbing damage...", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
        int ticks = (int)(120 * dur);
        new BukkitRunnable() {
            @Override public void run() {
                if (!plugin.getAbilityManager().isAbilityActive(player, AbilityType.KINETIC_FEEDBACK)) return;
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.KINETIC_FEEDBACK, false);

                double stored = plugin.getAbilityManager().getStoredKineticDamage(player);
                plugin.getAbilityManager().clearStoredKineticDamage(player);
                if (stored > 0) {
                    strikeNearbyEntities(player, 8.0, stored, plugin);
                    player.getWorld().createExplosion(player.getLocation(), (float)(stored / 10), false, false);
                    player.sendMessage(Component.text("Kinetic Feedback released: " + (int)stored + " damage!", net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN));
                } else {
                    player.sendMessage(Component.text("No damage absorbed.", net.kyori.adventure.text.format.NamedTextColor.GRAY));
                }
            }
        }.runTaskLater(plugin, ticks);
    }

    private static void titaniumRush(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize();
        dir.setY(0);
        player.setVelocity(dir.multiply(7.0 * spd));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.7f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 15, 1, 0.3, 1, 0.4);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 10) { cancel(); return; }
                Entity hit = getClosestEntity(player, 2.0);
                if (hit instanceof LivingEntity le) {
                    le.damage(20.0 + dmg, player);
                    le.setVelocity(dir.clone().multiply(4.0).add(new Vector(0, 1.5, 0)));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 2.0f, 0.4f);
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void densitySlam(Player player, double dmg, InvinciblePlugin plugin) {
        player.setVelocity(new Vector(0, -3.0, 0));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.3f);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 20) { cancel(); return; }
                if (player.isOnGround()) {
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10, 4, 0.5, 4, 0.3);
                    player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation(), 40,
                            4, 0.5, 4, 0.3, org.bukkit.Material.STONE.createBlockData());
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.5f, 0.4f);
                    strikeNearbyEntities(player, 10.0, 22.0 + dmg, plugin);
                    knockbackNearbyEntities(player, 10.0, 0, 4.0);
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private static List<Entity> nearbyEntities(Player player, Location loc, double radius) {
        return loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream()
                .filter(e -> !e.equals(player))
                .collect(java.util.stream.Collectors.toList());
    }

    private static void atomicFlightToggle(Player player, InvinciblePlugin plugin) {
        if (plugin.getFlightManager().isFlying(player)) {
            plugin.getFlightManager().stopFlight(player);
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            player.sendMessage(Component.text("Atomic Flight — off.", net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
        } else {
            plugin.getFlightManager().startFlight(player);
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 0.5, 1, 0.5, 0.2);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8f, 1.8f);
            player.sendMessage(Component.text("Atomic Flight — on.", net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE));
        }
    }

    private static void electricDash(Player player, double dmg, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize();
        dir.setY(0.1);
        player.setVelocity(dir.multiply(6.0 * spd));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 2.0f);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 10) { cancel(); return; }
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 8, 0.3, 0.3, 0.3, 0.2);
                nearbyEntities(player, 2.5).forEach(e -> {
                    if (e instanceof LivingEntity le) {
                        le.damage(6.0 + dmg, player);
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
                        le.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, le.getLocation(), 6, 0.3, 0.5, 0.3, 0.1);
                    }
                });
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private static void stellarSlam(Player player, double dmg, InvinciblePlugin plugin) {
        player.setVelocity(new Vector(0, -4.0, 0));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.3f);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 1, 1, 1, 0.4);
        new BukkitRunnable() {
            int ticks = 0;
            @Override public void run() {
                if (!player.isOnline() || ticks > 25) { cancel(); return; }
                if (player.isOnGround()) {
                    // Impact crater — expanding rings on landing
                    for (int r = 1; r <= 5; r++) {
                        final double rad = r * 2.5;
                        final int del = r * 2;
                        new BukkitRunnable() { @Override public void run() {
                            for (double a = 0; a < Math.PI * 2; a += Math.PI / 18) {
                                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK,
                                        player.getLocation().add(Math.cos(a)*rad, 0.3, Math.sin(a)*rad),
                                        3, 0.1, 0.1, 0.1, 0.15);
                            }
                        }}.runTaskLater(plugin, del);
                    }
                    player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 20, 5, 0.5, 5, 0.3);
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 60, 6, 1.5, 6, 0.6);
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0,1,0), 40, 5, 2, 5, 0.4);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.3f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.5f, 0.5f);
                    strikeNearbyEntities(player, 12.0, 24.0 + dmg, plugin);
                    knockbackNearbyEntities(player, 12.0, 0, 4.5);
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 2L, 1L);
    }

    private static void interstellarDash(Player player, double spd, InvinciblePlugin plugin) {
        Vector dir = player.getLocation().getDirection().normalize().multiply(20.0 * spd);
        Location dest = player.getLocation().add(dir);

        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 1, 0.5, 1, 0.4);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        player.teleport(dest);
        player.getWorld().spawnParticle(Particle.EXPLOSION, dest, 5, 0.5, 0.5, 0.5, 0.1);
        strikeNearbyEntities(player, 4.0, 8.0, plugin);
    }

    private static void cosmicRoar(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(40 * dur);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 2.0f, 0.5f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 50, 4, 1, 4, 0.4);
        nearbyEntities(player, 12.0).forEach(e -> {
            if (e instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 5));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, ticks, 0));
                le.setVelocity(le.getLocation().subtract(player.getLocation())
                        .toVector().normalize().multiply(2.5).add(new Vector(0, 0.5, 0)));
            }
        });
        player.sendMessage(Component.text("COSMIC ROAR!", net.kyori.adventure.text.format.NamedTextColor.AQUA));
    }

    private static void regenerativeSurge(Player player, InvinciblePlugin plugin) {
        double missing = player.getMaxHealth() - player.getHealth();
        double heal = Math.max(missing * 0.55, 8.0);
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 2));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 15, 0.5, 1, 0.5, 0.3);
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 20, 0.5, 1, 0.5, 0.3);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.sendMessage(Component.text("Regenerative Surge — healed " + (int)heal + " health!", net.kyori.adventure.text.format.NamedTextColor.AQUA));
    }

    private static void riftShield(Player player, double dur, InvinciblePlugin plugin) {
        int ticks = (int)(80 * dur);
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, ticks, 2));
        plugin.getAbilityManager().setActiveAbility(player, AbilityType.RIFT_SHIELD, true);
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 40, 1, 1.5, 1, 0.5);
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation(), 30, 1, 1.5, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 1.5f);
        player.sendMessage(Component.text("Rift Shield active — dimensional absorption ready.", net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE));
        new BukkitRunnable() {
            @Override public void run() {
                plugin.getAbilityManager().setActiveAbility(player, AbilityType.RIFT_SHIELD, false);
            }
        }.runTaskLater(plugin, ticks);
    }
}