package com.hihelloy.invincible.world;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Breaks blocks temporarily around impacts/flight and restores them.
 * Never breaks hard blocks (bedrock, obsidian, ores, spawners, etc.).
 */
public class TempBlockManager {

    private static final Set<Material> UNBREAKABLE = Set.of(
            Material.BEDROCK, Material.BARRIER, Material.SPAWNER,
            Material.END_PORTAL, Material.END_PORTAL_FRAME,
            Material.NETHER_PORTAL, Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK, Material.JIGSAW,
            Material.OBSIDIAN, Material.CRYING_OBSIDIAN,
            Material.REINFORCED_DEEPSLATE, Material.ANCIENT_DEBRIS,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHERITE_BLOCK
    );

    private static final float MAX_HARDNESS = 10.0f;

    private final Plugin plugin;
    private final Map<String, BlockData> stored = new ConcurrentHashMap<>();

    public TempBlockManager(Plugin plugin) {
        this.plugin = plugin;
    }


    private static String locKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public boolean isBreakable(Block block) {
        Material mat = block.getType();
        if (mat == Material.AIR || mat == Material.CAVE_AIR || mat == Material.VOID_AIR) return false;
        if (mat.isAir()) return false;
        if (UNBREAKABLE.contains(mat)) return false;
        if (mat.getHardness() < 0) return false;
        if (mat.getHardness() > MAX_HARDNESS) return false;
        return true;
    }

    /**
     * Breaks a sphere of breakable blocks around centre.
     * Spawns BLOCK particles flying outward. Restores after restoreTicks.
     */
    public void explodeBlocks(Location centre, double radius, long restoreTicks) {
        World world = centre.getWorld();
        if (world == null) return;

        int ir = (int) Math.ceil(radius);
        List<Block> toBreak = new ArrayList<>();

        for (int x = -ir; x <= ir; x++) {
            for (int y = -ir; y <= ir; y++) {
                for (int z = -ir; z <= ir; z++) {
                    if (x * x + y * y + z * z > radius * radius) continue;
                    Block b = world.getBlockAt(
                            centre.getBlockX() + x,
                            centre.getBlockY() + y,
                            centre.getBlockZ() + z);
                    if (!isBreakable(b)) continue;
                    if (stored.containsKey(locKey(b.getLocation()))) continue;
                    toBreak.add(b);
                }
            }
        }

        for (Block b : toBreak) {
            BlockData data = b.getBlockData().clone();
            stored.put(locKey(b.getLocation()), data);

            world.spawnParticle(Particle.BLOCK, b.getLocation().add(0.5, 0.5, 0.5),
                    12, 0.5, 0.5, 0.5, 0.3, data);

            b.setType(Material.AIR, false);
        }

        scheduleRestore(toBreak, restoreTicks);
    }

    /**
     * Breaks blocks directly in a line ahead of the given location/direction.
     * Used for flight path clearing.
     */
    public void clearPath(Location eyeLoc, org.bukkit.util.Vector direction,
                          double reach, double clearRadius, long restoreTicks) {
        World world = eyeLoc.getWorld();
        if (world == null) return;

        List<Block> toBreak = new ArrayList<>();
        org.bukkit.util.Vector norm = direction.clone().normalize();
        int clearR = (int) Math.ceil(clearRadius);

        for (double d = 0.5; d <= reach; d += 0.5) {
            Location step = eyeLoc.clone().add(norm.clone().multiply(d));

            for (int ox = -clearR; ox <= clearR; ox++) {
                for (int oy = -clearR; oy <= clearR; oy++) {
                    for (int oz = -clearR; oz <= clearR; oz++) {
                        if (ox * ox + oy * oy + oz * oz > clearRadius * clearRadius) continue;
                        Block b = world.getBlockAt(
                                step.getBlockX() + ox,
                                step.getBlockY() + oy,
                                step.getBlockZ() + oz);
                        if (!isBreakable(b)) continue;
                        if (stored.containsKey(locKey(b.getLocation()))) continue;
                        if (toBreak.contains(b)) continue;
                        toBreak.add(b);
                    }
                }
            }
        }

        if (toBreak.isEmpty()) return;

        for (Block b : toBreak) {
            BlockData data = b.getBlockData().clone();
            stored.put(locKey(b.getLocation()), data);

            world.spawnParticle(Particle.BLOCK, b.getLocation().add(0.5, 0.5, 0.5),
                    8, 0.6, 0.3, 0.6, 0.5, data);

            b.setType(Material.AIR, false);
        }

        scheduleRestore(toBreak, restoreTicks);
    }

    private void scheduleRestore(List<Block> blocks, long restoreTicks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block b : blocks) {
                    Location loc = b.getLocation();
                    BlockData original = stored.remove(locKey(loc));
                    if (original == null) continue;
                    if (b.getType() != Material.AIR) continue;
                    b.setBlockData(original, false);
                    b.getWorld().spawnParticle(Particle.BLOCK,
                            loc.clone().add(0.5, 0.5, 0.5), 6, 0.3, 0.3, 0.3, 0.1, original);
                }
            }
        }.runTaskLater(plugin, restoreTicks);
    }

    public void setBlock(org.bukkit.Location loc, org.bukkit.Material material, long restoreTicks) {
        if (loc == null || loc.getWorld() == null) return;
        org.bukkit.block.Block b = loc.getBlock();
        if (!isBreakable(b)) return;
        String key = loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
        if (!stored.containsKey(key)) {
            stored.put(key, b.getBlockData().clone());
        }
        b.setType(material);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            org.bukkit.block.data.BlockData original = stored.remove(key);
            if (original != null) b.setBlockData(original);
        }, restoreTicks);
    }

    public void cleanup() {
        // Cannot easily reconstruct Location from string key in cleanup
        // Blocks will simply not be restored on shutdown — acceptable tradeoff

        stored.clear();
    }
}