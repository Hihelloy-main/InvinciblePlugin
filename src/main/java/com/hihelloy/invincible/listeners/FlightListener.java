package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.events.PlayerDoubleJumpEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlightListener implements Listener {

    private final InvinciblePlugin plugin;
    private final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    public FlightListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDoubleJump(PlayerDoubleJumpEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null || !character.canFly()) return;

        if (plugin.getFlightManager().isFlying(player)) {
            plugin.getFlightManager().stopFlight(player);
        } else {
            event.setCancelled(true);
            plugin.getFlightManager().startFlight(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!plugin.getFlightManager().isFlying(player)) return;
        if (player.isSneaking()) return;

        int held = player.getInventory().getHeldItemSlot();
        if (held >= 0 && held <= 3) {
            AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
            AbilityType slotAbility = bound[held];
            if (slotAbility == AbilityType.AERIAL_GRAB
                    || slotAbility == AbilityType.AERIAL_SLAM) {
                return;
            }
        }

        event.setCancelled(true);
        plugin.getFlightManager().toggleMode(player);
    }

    private final java.util.Map<String, Long> collisionCooldowns = new java.util.HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getFlightManager().isFlying(player)) return;

        handleFlightCollision(player);

        UUID id = player.getUniqueId();
        Block blockBelow = player.getLocation().clone().subtract(0, 0.2, 0).getBlock();
        boolean onGround = blockBelow.getType().isSolid();
        boolean previouslyOnGround = wasOnGround.getOrDefault(id, false);

        if (!previouslyOnGround && onGround) {
            plugin.getFlightManager().stopFlight(player);

            if (plugin.getConfig().getBoolean("flight.landing-shockwave-enabled", true)) {
                spawnLandingShockwave(player);
            }
        }

        wasOnGround.put(id, onGround);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (plugin.getFlightManager().isFlying(player)) {
            plugin.getFlightManager().stopFlight(player);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null) return;

        if (character.canFly()) {
            event.setCancelled(true);
            return;
        }

        for (String key : character.getAbilityKeys()) {
            if (key.equals("INVULNERABILITY") || key.equals("ENDURANCE") ||
                    key.equals("VILTRUMITE_ENDURANCE") || key.equals("BATTLE_HARDENED") ||
                    key.equals("THICK_HIDE") || key.equals("UNYIELDING")) {
                event.setDamage(event.getDamage() * 0.3);
                return;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        wasOnGround.remove(event.getPlayer().getUniqueId());
    }

    private void spawnLandingShockwave(Player player) {
        Location centre = player.getLocation().clone();
        double maxRadius = plugin.getConfig().getDouble("flight.landing-shockwave-radius", 6.0);
        int rings = plugin.getConfig().getInt("flight.landing-shockwave-rings", 5);
        double ringStep = maxRadius / rings;

        String soundName = plugin.getConfig().getString(
                "flight.landing-shockwave-sound", "ENTITY_GENERIC_EXPLODE");
        Sound shockSound;
        try {
            shockSound = Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            shockSound = Sound.ENTITY_GENERIC_EXPLODE;
        }
        float shockVolume = (float) plugin.getConfig().getDouble("flight.landing-shockwave-sound-volume", 1.2);
        float shockPitch = (float) plugin.getConfig().getDouble("flight.landing-shockwave-sound-pitch", 0.6);

        String particleName = plugin.getConfig().getString("flight.landing-shockwave-particle", "BLOCK");
        Particle parsed;
        try {
            parsed = Particle.valueOf(particleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            parsed = Particle.BLOCK;
        }
        final Particle shockParticle = parsed;
        final boolean useBlock = shockParticle == Particle.BLOCK;

        player.getWorld().playSound(centre, shockSound, shockVolume, shockPitch);
        player.getWorld().spawnParticle(Particle.EXPLOSION, centre, 3, 0.5, 0.2, 0.5, 0);

        Block groundBlock = centre.clone().subtract(0, 1, 0).getBlock();
        final Material blockMat = groundBlock.getType().isAir() ? Material.STONE : groundBlock.getType();

        for (int i = 1; i <= rings; i++) {
            scheduleRing(player, centre, ringStep * i, i * 2L, shockParticle, useBlock, blockMat);
        }
    }

    private void scheduleRing(Player player, Location centre, double radius,
                              long delayTicks, Particle shockParticle,
                              boolean useBlock, Material blockMat) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                int points = Math.max(12, (int) (radius * 8));
                for (int p = 0; p < points; p++) {
                    double angle = 2 * Math.PI * p / points;
                    double x = centre.getX() + radius * Math.cos(angle);
                    double z = centre.getZ() + radius * Math.sin(angle);
                    Location ringLoc = new Location(centre.getWorld(), x, centre.getY(), z);

                    if (useBlock) {
                        centre.getWorld().spawnParticle(
                                Particle.BLOCK, ringLoc, 2, 0, 0.1, 0, 0, blockMat.createBlockData()
                        );
                    } else {
                        centre.getWorld().spawnParticle(shockParticle, ringLoc, 2, 0, 0.1, 0, 0);
                    }
                }
                centre.getWorld().spawnParticle(
                        Particle.CLOUD, centre,
                        (int) (radius * 3), radius * 0.4, 0.05, radius * 0.4, 0.02
                );
            }
        }.runTaskLater(plugin, delayTicks);
    }

    private void handleFlightCollision(Player player) {
        double speed = player.getVelocity().length();
        if (speed < 0.4) return;

        double collisionDamage = plugin.getConfig().getDouble("flight.collision-damage", 4.0);
        String particleName = plugin.getConfig().getString("flight.collision-particle", "CLOUD");
        org.bukkit.Particle collisionParticle;
        try {
            collisionParticle = org.bukkit.Particle.valueOf(particleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            collisionParticle = org.bukkit.Particle.CLOUD;
        }
        final org.bukkit.Particle finalParticle = collisionParticle;

        for (org.bukkit.entity.Entity nearby : player.getWorld().getNearbyEntities(
                player.getLocation(), 1.2, 1.2, 1.2)) {
            if (!(nearby instanceof Player other)) continue;
            if (other == player) continue;

            String key = player.getUniqueId() + "<>" + other.getUniqueId();
            long lastHit = collisionCooldowns.getOrDefault(key, 0L);
            if (System.currentTimeMillis() - lastHit < 800) continue;
            collisionCooldowns.put(key, System.currentTimeMillis());

            double speedFactor = Math.min(speed / 1.5, 2.0);
            double totalDamage = collisionDamage * speedFactor;

            other.damage(totalDamage, player);
            player.damage(totalDamage * 0.5);

            org.bukkit.Location mid = player.getLocation().clone().add(
                    other.getLocation().subtract(player.getLocation()).toVector().multiply(0.5));
            player.getWorld().spawnParticle(finalParticle, mid, 20, 0.5, 0.5, 0.5, 0.1);
            player.getWorld().playSound(mid, org.bukkit.Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.6f);
        }
    }
}