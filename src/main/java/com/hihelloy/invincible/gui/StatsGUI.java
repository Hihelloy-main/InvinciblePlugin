package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.stats.StatType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StatsGUI {

    public static final String TITLE = "&6&l✦ Character Stats ✦";
    private static final int SIZE = 27;

    private static final Material[] STAT_MATERIALS = {
            Material.IRON_SWORD,
            Material.FEATHER,
            Material.CLOCK,
            Material.LIME_DYE,
            Material.SHIELD,
            Material.GOLDEN_APPLE
    };

    private static final int[] STAT_SLOTS = {10, 11, 12, 13, 14, 15};

    public static void open(Player player, InvinciblePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy(TITLE));

        StatType[] stats = StatType.values();
        for (int i = 0; i < stats.length && i < STAT_SLOTS.length; i++) {
            inv.setItem(STAT_SLOTS[i], createStatItem(stats[i], i, plugin, player));
        }

        inv.setItem(4, createPointsDisplay(player, plugin));
        fillRemaining(inv);
        player.openInventory(inv);
    }

    private static ItemStack createStatItem(StatType stat, int matIndex, InvinciblePlugin plugin, Player player) {
        ItemStack item = new ItemStack(STAT_MATERIALS[matIndex]);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        int level = plugin.getStatManager().getStatLevel(player, stat);
        int points = plugin.getStatManager().getStatPoints(player);

        meta.displayName(legacy(CharacterType.ampCode(stat.getColor()) + "&l" + stat.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        org.bukkit.configuration.file.FileConfiguration cfg = plugin.getConfig();
        double damPct = cfg.getDouble("stats.damage-stat-per-level", 0.05) * 100;
        double spdPct = cfg.getDouble("stats.speed-stat-per-level", 0.010) * 100;
        double durPct = cfg.getDouble("stats.duration-stat-per-level", 0.04) * 100;
        double defPct = cfg.getDouble("stats.defense-stat-per-level", 0.033) * 100;
        int maxLevel = cfg.getInt("settings.max-stat-level", 30);

        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&7" + stat.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eLevel: &f" + level + "/" + maxLevel + " " + buildProgressBar(level, maxLevel, stat)));
        lore.add(Component.empty());
        String bonusLine = switch (stat) {
            case DAMAGE -> String.format("&cBonus: +&f%.1f &chearts flat &8(max +%.0f hearts)",
                    (level * damPct) / 2.0, (maxLevel * damPct) / 2.0);
            case SPEED -> String.format("&eBonus: +&f%.1f&e%% walk speed &8(max +%.0f%%)",
                    level * spdPct, maxLevel * spdPct);
            case DURATION -> String.format("&aBonus: +&f%.0f&a%% duration &8(max +%.0f%%)",
                    level * durPct, maxLevel * durPct);
            case DEFENSE -> String.format("&9Bonus: +&f%.0f&9%% resistance &8(max +%.0f%%)",
                    level * defPct, maxLevel * defPct);
            case REGENERATION -> level > 0 ? "&2Effect: &fPassive Regen Lv." + level : "&2Effect: &8Inactive";
        };
        lore.add(legacy(bonusLine));
        lore.add(Component.empty());

        if (level >= maxLevel) {
            lore.add(legacy("&6&l★ MAX LEVEL ★"));
        } else if (points > 0) {
            lore.add(legacy("&a▶ Click to upgrade! &7(1 point)"));
        } else {
            lore.add(legacy("&c✗ No stat points available"));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPointsDisplay(Player player, InvinciblePlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        int points = plugin.getStatManager().getStatPoints(player);
        meta.displayName(legacy("&6&lStat Points: &f" + points));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&7Use these to upgrade your stats!"));
        lore.add(legacy("&7Earn more by winning PvP fights."));
        lore.add(Component.empty());
        lore.add(legacy("&eAvailable: &6" + points));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static String buildProgressBar(int level, int max, StatType stat) {
        String color = CharacterType.ampCode(stat.getColor());
        StringBuilder sb = new StringBuilder("&8[");
        for (int i = 0; i < max; i++) {
            sb.append(i < level ? color + "█" : "&8█");
        }
        sb.append("&8]");
        return sb.toString();
    }

    private static void fillRemaining(Inventory inv) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < SIZE; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}