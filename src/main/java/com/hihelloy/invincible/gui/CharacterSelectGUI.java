package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CharacterSelectGUI {

    public static final String TITLE = "&5&l✦ Select Your Character ✦";
    private static final int SIZE = 54;

    public static void open(Player player, InvinciblePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy(TITLE));

        int slot = 0;
        for (CharacterType ch : CharacterType.values()) {
            if (ch == CharacterType.CUSTOM) continue;
            if (slot >= SIZE - 1) break;
            inv.setItem(slot++, createCharacterItem(ch, plugin, player));
        }

        fillBorder(inv, slot);

        ItemStack customItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta customMeta = customItem.getItemMeta();
        if (customMeta != null) {
            customMeta.displayName(Component.text("Create Custom Hero",
                    NamedTextColor.GOLD, TextDecoration.BOLD));
            List<Component> cl = new ArrayList<>();
            cl.add(Component.text("Design your own character.", NamedTextColor.GRAY));
            cl.add(Component.text("All stats maxed. Flight enabled.", NamedTextColor.GRAY));
            cl.add(Component.text("Full ability access. Custom name.", NamedTextColor.GRAY));
            cl.add(Component.empty());
            cl.add(Component.text("Click to name your hero.", NamedTextColor.YELLOW));
            customMeta.lore(cl);
            customItem.setItemMeta(customMeta);
        }
        inv.setItem(53, customItem);

        player.openInventory(inv);
    }

    private static ItemStack createCharacterItem(CharacterType character,
                                                 InvinciblePlugin plugin, Player player) {
        ItemStack item = new ItemStack(character.getGuiItem());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        CharacterType current = plugin.getDataManager().getCharacterManager().getCharacter(player);
        boolean isSelected = current == character;

        meta.displayName(legacy(CharacterType.ampCode(character.getColor())
                + (isSelected ? "▶ " : "") + character.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));

        String fly = character.canFly() ? "&aYes &7(double-jump)" : "&cNo";
        String str = character.hasSuperStrength() ? "&aYes &7(passive)" : "&cNo";
        String spd = character.hasSuperSpeed() ? "&aYes &7(passive)" : "&cNo";
        lore.add(legacy("&eFlight: " + fly));
        lore.add(legacy("&eSuper Strength: " + str));
        lore.add(legacy("&eSuper Speed: " + spd));
        lore.add(Component.empty());
        lore.add(legacy("&7Abilities:"));

        String[] keys = character.getAbilityKeys();
        for (int i = 0; i < keys.length; i++) {
            lore.add(legacy(CharacterType.ampCode(character.getColor())
                    + "  " + (i + 1) + ". &f" + formatName(keys[i])));
        }

        lore.add(Component.empty());
        lore.add(legacy(isSelected ? "&a&l★ CURRENTLY SELECTED ★" : "&eClick to select!"));

        meta.lore(lore);
        if (isSelected) {
            meta.addEnchant(Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static void fillBorder(Inventory inv, int usedSlots) {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            border.setItemMeta(meta);
        }
        for (int i = usedSlots; i < SIZE - 1; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
    }

    private static String formatName(String key) {
        String[] parts = key.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}