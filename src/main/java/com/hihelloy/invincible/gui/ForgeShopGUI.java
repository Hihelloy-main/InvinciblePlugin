package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.shop.ForgeRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ForgeShopGUI {

    public static final String TITLE = "⚒ OmniMC Ingredient Shop";

    public static void open(Player player, InvinciblePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 54,
        Component.text(TITLE, NamedTextColor.DARK_PURPLE).decoration(TextDecoration.BOLD, false));

        fillBorder(inv);

        int points = plugin.getStatManager().getStatPoints(player);
        inv.setItem(4, makePointsDisplay(points));
        inv.setItem(49, makeGuideButton());
        inv.setItem(50, makeForgeInfoButton());

        int[] slots = {10, 11, 12, 13, 14, 19, 20};
        ForgeRecipe[] recipes = ForgeRecipe.values();
        for (int i = 0; i < recipes.length && i < slots.length; i++) {
            inv.setItem(slots[i], makeIngredientItem(recipes[i], points));
        }

        player.openInventory(inv);
    }

    private static ItemStack makeIngredientItem(ForgeRecipe recipe, int playerPoints) {
        ItemStack item = new ItemStack(recipe.icon);
        ItemMeta meta = item.getItemMeta();

        boolean canAfford = playerPoints >= recipe.cost;
        NamedTextColor nameColor = canAfford ? NamedTextColor.GOLD : NamedTextColor.DARK_GRAY;

        meta.displayName(Component.text(recipe.displayName, nameColor, TextDecoration.BOLD)
        .decoration(TextDecoration.ITALIC, false));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        for (String line : recipe.description.split("\n")) {
            lore.add(LegacyComponentSerializer.legacySection().deserialize(line)
            .decoration(TextDecoration.ITALIC, false));
        }
        lore.add(Component.empty());
        lore.add(Component.text("Cost: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        .append(Component.text(recipe.cost + " stat points each",
        canAfford ? NamedTextColor.GREEN : NamedTextColor.RED)));
        lore.add(Component.empty());
        if (canAfford) {
            lore.add(Component.text("Left-click: Buy 1  (+", NamedTextColor.GREEN)
            .append(Component.text(recipe.cost + " pts)", NamedTextColor.DARK_GREEN))
            .decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text("Right-click: Buy 5  (+" + (recipe.cost * 5) + " pts)",
            NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
        } else {
            lore.add(Component.text("Not enough stat points.", NamedTextColor.RED)
            .decoration(TextDecoration.ITALIC, false));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makePointsDisplay(int points) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Stat Points", NamedTextColor.AQUA, TextDecoration.BOLD)
        .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
        Component.text(points + " available", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false),
        Component.empty(),
        Component.text("Earn by defeating enemies.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makeForgeInfoButton() {
        ItemStack item = new ItemStack(Material.BLAST_FURNACE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("⚒ OmniMC Forge", NamedTextColor.YELLOW, TextDecoration.BOLD)
        .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
        Component.text("Find the Forge in-world", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
        Component.text("and bring your ingredients", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
        Component.text("to craft OmniMC weapons!", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makeGuideButton() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("⬡ Weapon Guide", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
        .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
        Component.text("Click to receive your", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
        Component.text("OmniMC Weapon Guide.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static void fillBorder(Inventory inv) {
        ItemStack pane = makePaneOf(Material.PURPLE_STAINED_GLASS_PANE);
        ItemStack black = makePaneOf(Material.BLACK_STAINED_GLASS_PANE);

        for (int i = 0; i < 9; i++) inv.setItem(i, pane);
        for (int i = 45; i < 54; i++) inv.setItem(i, pane);
        for (int i = 9; i < 45; i += 9) inv.setItem(i, pane);
        for (int i = 17; i < 45; i += 9) inv.setItem(i, pane);

        for (int i = 9; i < 45; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, black);
        }
    }

    private static ItemStack makePaneOf(Material mat) {
        ItemStack pane = new ItemStack(mat);
        ItemMeta meta = pane.getItemMeta();
        meta.displayName(Component.empty());
        pane.setItemMeta(meta);
        return pane;
    }

    public static ForgeRecipe getRecipeAtSlot(int slot) {
        int[] slots = {10, 11, 12, 13, 14, 19, 20};
        ForgeRecipe[] recipes = ForgeRecipe.values();
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot && i < recipes.length) return recipes[i];
        }
        return null;
    }
}
