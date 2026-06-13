package com.hihelloy.invincible.flight;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.world.TempBlockManager;
import com.hihelloy.invincible.characters.CharacterType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class FlightManager {

    public enum FlightMode{
        HOVER, GLIDE
    }

    private final InvinciblePlugin plugin;
    private final Set<UUID> flyingPlayers = new HashSet<>();
    private final TempBlockManager tempBlockManager;
    private final Map<UUID, FlightMode> modes = new HashMap<>();
    private final Map<UUID, BukkitTask> progressTasks = new HashMap<>();
    private final Map<UUID, Float> originalSpeeds = new HashMap<>();

    public FlightManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        this.tempBlockManager = new TempBlockManager(plugin);
    }

    public TempBlockManager getTempBlockManager(){
        return tempBlockManager;
    }

    public boolean canFly(Player player) {
        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        return ch != null && ch.canFly();
    }

    public void startFlight(Player player) {
        if (flyingPlayers.contains(player.getUniqueId())) return;
        if (plugin.getWorldGuard().isDisabled(player)) return;
        if (!plugin.getWorldGuard().canUseAbility(player)) {
            player.sendMessage(net.kyori.adventure.text.Component.text(
                    "You cannot fly inside a claim you don't own.", net.kyori.adventure.text.format.NamedTextColor.RED));
            return;
        }
        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (ch == null || !ch.canFly()) return;

        com.hihelloy.invincible.api.events.FlightStartEvent startEvent =
                new com.hihelloy.invincible.api.events.FlightStartEvent(player);
        org.bukkit.Bukkit.getPluginManager().callEvent(startEvent);
        if (startEvent.isCancelled()) return;

        flyingPlayers.add(player.getUniqueId());
        originalSpeeds.put(player.getUniqueId(), player.getFlySpeed());

        if (plugin.getConfig().getBoolean("flight.phase-through-blocks", true)) {
            player.setNoPhysics(true);
        }

        FlightMode mode = FlightMode.HOVER;
        modes.put(player.getUniqueId(), mode);

        if (plugin.getConfig().getBoolean("flight.pose-enabled", true)) {
            player.setSwimming(true);
        }

        applyMode(player, mode);
        startProgressTask(player);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.3f, 1.5f);
    }

    public void stopFlight(Player player) {
        flyingPlayers.remove(player.getUniqueId());
        modes.remove(player.getUniqueId());
        org.bukkit.Bukkit.getPluginManager().callEvent(
                new com.hihelloy.invincible.api.events.FlightEndEvent(player));
        player.setNoPhysics(false);

        BukkitTask task = progressTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();

        player.setSwimming(false);
        player.setGliding(false);
        player.setFlying(false);
        player.setAllowFlight(canFly(player));
        player.setFallDistance(0);

        Float orig = originalSpeeds.remove(player.getUniqueId());
        player.setFlySpeed(orig != null ? orig : (float) plugin.getConfig().getDouble("settings.base-fly-speed", 0.025));
    }

    public void toggleMode(Player player) {
        if (!flyingPlayers.contains(player.getUniqueId())) return;
        FlightMode current = modes.getOrDefault(player.getUniqueId(), FlightMode.HOVER);
        FlightMode next = (current == FlightMode.HOVER) ? FlightMode.GLIDE : FlightMode.HOVER;
        modes.put(player.getUniqueId(), next);
        applyMode(player, next);
        player.getWorld().playSound(player.getLocation(),
                next == FlightMode.GLIDE ? Sound.ENTITY_FIREWORK_ROCKET_LAUNCH : Sound.ITEM_ELYTRA_FLYING,
                0.5f, next == FlightMode.GLIDE ? 1.8f : 0.8f);
    }

    private void applyMode(Player player, FlightMode mode) {
        float hoverSpeed = (float) plugin.getConfig().getDouble("settings.base-fly-speed", 0.025);
        if (mode == FlightMode.HOVER) {
            player.setGliding(false);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(hoverSpeed);
        } else {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGliding(true);
        }
    }

    private void startProgressTask(Player player) {
        BukkitTask old = progressTasks.remove(player.getUniqueId());
        if (old != null) old.cancel();

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !flyingPlayers.contains(player.getUniqueId())) {
                    cancel();
                    return;
                }

                FlightMode mode = modes.getOrDefault(player.getUniqueId(), FlightMode.HOVER);

                if (mode == FlightMode.GLIDE) {
                    double flySpeed = plugin.getConfig().getDouble("flight.glide-speed", 0.8);
                    Vector velocity = player.getEyeLocation().getDirection().normalize().multiply(flySpeed);
                    if (player.getVelocity().getY() < 0) {
                        velocity.add(player.getVelocity().multiply(0.15));
                    }
                    player.setVelocity(velocity);
                    player.setGliding(true);

                    if (plugin.getConfig().getBoolean("flight.break-blocks", true)) {
                        com.hihelloy.invincible.characters.CharacterType ch =
                                plugin.getDataManager().getCharacterManager().getCharacter(player);
                        if (ch != null && ch.canBreakBlocks()) {
                            breakBlocksAhead(player);
                        }
                    }
                } else {
                    player.setFlying(true);
                    player.setVelocity(player.getVelocity().add(new Vector(0, -0.015, 0)));
                }

                if (plugin.getConfig().getBoolean("flight.foot-particles-enabled", true)) {
                    spawnTrailParticles(player, mode);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        progressTasks.put(player.getUniqueId(), task);
    }

    private void breakBlocksAhead(Player player) {
        long restoreTicks = plugin.getConfig().getLong("settings.block-restore-ticks", 600L);
        double sideRadius = plugin.getConfig().getDouble("flight.break-side-radius", 1.5);
        Vector dir = player.getEyeLocation().getDirection().normalize();
        Location eye = player.getEyeLocation();
        Location feet = player.getLocation();

        java.util.Set<String> broken = new java.util.LinkedHashSet<>();

        int[] ahead = {3, 2, 1};
        aheadLoop:
        for (int step = 0; step < 3; step++) {
            double dist = step + 1.0;
            Location stepLoc = eye.clone().add(dir.clone().multiply(dist));
            int found = 0;
            for (int ox = -1; ox <= 1 && found < ahead[step]; ox++) {
                for (int oy = -1; oy <= 1 && found < ahead[step]; oy++) {
                    for (int oz = -1; oz <= 1 && found < ahead[step]; oz++) {
                        Block b = stepLoc.getWorld().getBlockAt(
                                stepLoc.getBlockX() + ox,
                                stepLoc.getBlockY() + oy,
                                stepLoc.getBlockZ() + oz);
                        if (!tempBlockManager.isBreakable(b)) continue;
                        String k = key(b);
                        if (broken.add(k)) {
                            found++; breakBlock(b, restoreTicks); }
                    }
                }
            }
        }

        int ir = (int) Math.ceil(sideRadius);
        for (int ox = -ir; ox <= ir; ox++) {
            for (int oy = -1; oy <= 2; oy++) {
                for (int oz = -ir; oz <= ir; oz++) {
                    if (ox * ox + oz * oz > sideRadius * sideRadius) continue;
                    Block b = feet.getWorld().getBlockAt(
                            feet.getBlockX() + ox,
                            feet.getBlockY() + oy,
                            feet.getBlockZ() + oz);
                    if (!tempBlockManager.isBreakable(b)) continue;
                    String k = key(b);
                    if (broken.add(k)) breakBlock(b, restoreTicks);
                }
            }
        }
    }

    private static String key(Block b) {
        return b.getWorld().getName() + ":" + b.getX() + ":" + b.getY() + ":" + b.getZ();
    }

    private void breakBlock(Block b, long restoreTicks) {

        tempBlockManager.explodeBlocks(b.getLocation(), 0.4, restoreTicks);
    }

    private void spawnTrailParticles(Player player, FlightMode mode) {
        String typeName = plugin.getConfig().getString("flight.foot-particle-type", "CLOUD");
        Particle particleType;
        try {
            particleType = Particle.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            particleType = Particle.CLOUD;
        }

        Vector trailDir = (mode == FlightMode.HOVER)
                ? new Vector(0, -0.4, 0)
                : player.getEyeLocation().getDirection().normalize().multiply(-0.4);

        int count = plugin.getConfig().getInt("flight.particle-count", 18);
        double spread = plugin.getConfig().getDouble("flight.particle-spread", 0.35);
        double speed = plugin.getConfig().getDouble("flight.particle-speed", 0.04);

        for (int i = 0; i < 4; i++) {
            Location pos = player.getLocation().clone().add(trailDir.clone().multiply(i));
            int particlesAtPos = Math.max(1, count - (i * 4));
            double spreadAtPos = Math.max(0.05, spread - (i * 0.07));
            player.getWorld().spawnParticle(
                    particleType, pos,
                    particlesAtPos, spreadAtPos, spreadAtPos * 0.4, spreadAtPos, speed
            );
        }
    }

    public void applyFlightSpeed(Player player) {
        int speedLevel = plugin.getStatManager().getStatLevel(player, com.hihelloy.invincible.stats.StatType.SPEED);
        float baseWalk = (float) plugin.getConfig().getDouble("settings.base-walk-speed", 0.2);
        float maxWalk = (float) plugin.getConfig().getDouble("settings.max-walk-speed", 0.4);
        float walkSpeed = (float) Math.min(baseWalk + speedLevel * 0.010f, maxWalk);

        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (ch != null && ch.hasSuperSpeed()) {
            player.setWalkSpeed(walkSpeed);
        }

        float baseFly = (float) plugin.getConfig().getDouble("settings.base-fly-speed", 0.025);
        if (isFlying(player) && modes.getOrDefault(player.getUniqueId(), FlightMode.HOVER) == FlightMode.HOVER) {
            player.setFlySpeed(baseFly);
        }
    }

    public boolean isFlying(Player player) {
        return flyingPlayers.contains(player.getUniqueId());
    }

    public FlightMode getMode(Player player) {
        return modes.getOrDefault(player.getUniqueId(), FlightMode.HOVER);
    }

    public void onPlayerQuit(Player player) {
        stopFlight(player);
    }

    public void cleanup() {
        tempBlockManager.cleanup();
    }
}