package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CosmeticsGUI {

    public static final String TITLE = "&5&l✦ Cosmetics ✦";
    private static final int SIZE = 54;
    private static final int[] COSMETIC_SLOTS = {10, 12, 14, 16, 28, 30};

    public static void open(Player player, InvinciblePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy(TITLE));

        CosmeticType[] cosmetics = CosmeticType.values();
        for (int i = 0; i < cosmetics.length && i < COSMETIC_SLOTS.length; i++) {
            inv.setItem(COSMETIC_SLOTS[i], createCosmeticItem(cosmetics[i], player, plugin));
        }

        addEquippedDisplay(inv, player, plugin);
        fillRemaining(inv);
        player.openInventory(inv);
    }

    private static ItemStack createCosmeticItem(CosmeticType cosmetic, Player player,
    InvinciblePlugin plugin) {
        boolean equipped = plugin.getCosmeticManager().isEquipped(player, cosmetic);
        boolean owned = plugin.getCosmeticManager().isOwned(player, cosmetic);

        boolean canAfford = false;
        double balance = 0;
        if (plugin.hasEconomy()) {
            balance = plugin.getEconomy().getBalance(player);
            canAfford = balance >= cosmetic.getPrice();
        }

        ItemStack item = new ItemStack(cosmetic.getModelMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setCustomModelData(cosmetic.getCustomModelData());

        String prefix = equipped ? "&a▶ " : "";
        meta.displayName(legacy(prefix + CharacterType.ampCode(cosmetic.getColor()) + "&l" + cosmetic.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&7" + cosmetic.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eSlot: &f" + cosmetic.getSlot().name()));
        lore.add(Component.empty());
        lore.add(legacy("&eGranted Abilities:"));
        for (String key : cosmetic.getAbilityKeys()) {
            AbilityType ability = AbilityType.fromName(key);
            if (ability != null) {
                lore.add(legacy(CharacterType.ampCode(ability.getColor()) + "  ◆ &f" + ability.getDisplayName()
                + " &8[" + ability.getCategory().getDisplayName() + "]"));
            }
        }
        lore.add(Component.empty());

        if (plugin.hasEconomy()) {
            if (owned) {
                lore.add(legacy("&a&lPURCHASED"));
            } else {
                String priceColor = canAfford ? "&a" : "&c";
                lore.add(legacy("&ePrice: " + priceColor + "$" + formatMoney(cosmetic.getPrice())));
                lore.add(legacy("&eYour balance: &f$" + formatMoney(balance)));
            }
        } else {
            lore.add(legacy("&7(No economy plugin found)"));
        }

        lore.add(Component.empty());
        if (equipped) {
            lore.add(legacy("&a&lEQUIPPED — Click to unequip"));
        } else if (owned) {
            lore.add(legacy("&eClick to equip"));
        } else if (plugin.hasEconomy() && canAfford) {
            lore.add(legacy("&eClick to purchase & equip"));
        } else if (plugin.hasEconomy()) {
            lore.add(legacy("&c✗ Cannot afford"));
        } else {
            lore.add(legacy("&eClick to equip"));
        }

        meta.lore(lore);
        if (equipped) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS,
            org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        } else {
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static void addEquippedDisplay(Inventory inv, Player player, InvinciblePlugin plugin) {
        ItemStack info = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            meta.displayName(legacy("&b&lCurrently Equipped"));
            List<Component> lore = new ArrayList<>();
            var equipped = plugin.getCosmeticManager().getEquipped(player);
            if (equipped.isEmpty()) {
                lore.add(legacy("&8None equipped."));
            } else {
                for (CosmeticType c : equipped) {
                    lore.add(legacy(CharacterType.ampCode(c.getColor()) + "  ◆ " + c.getDisplayName()
                    + " &8[" + c.getSlot().name() + "]"));
                }
            }
            lore.add(Component.empty());
            lore.add(legacy("&7One cosmetic per slot. Unequipping is free."));
            meta.lore(lore);
            info.setItemMeta(meta);
        }
        inv.setItem(49, info);
    }

    private static void fillRemaining(Inventory inv) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < SIZE; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
    }

    private static String formatMoney(double amount) {
        return NumberFormat.getNumberInstance(Locale.US).format((long) amount);
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}