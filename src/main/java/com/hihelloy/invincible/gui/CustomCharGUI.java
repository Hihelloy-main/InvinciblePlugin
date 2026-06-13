package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomCharGUI {

    public static final String TITLE = "✦ Custom Hero Editor";
    public static final String ABILITY_TITLE_PREFIX = "✦ Custom Abilities: p";

    private static final int SIZE = 54;

    private static final int[] ABILITY_SLOTS = {
        10, 11, 12, 13, 14, 15,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    };

    public static void openEditor(Player player, InvinciblePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy("&d&l" + TITLE));

        String customName = plugin.getDataManager().getCustomHeroName(player.getUniqueId());
        boolean fly = plugin.getDataManager().getCustomHeroFly(player.getUniqueId());
        boolean strength = plugin.getDataManager().getCustomHeroStrength(player.getUniqueId());
        boolean speed = plugin.getDataManager().getCustomHeroSpeed(player.getUniqueId());

        inv.setItem(4, makeInfo(customName, fly, strength, speed));

        inv.setItem(20, makeToggle(Material.ELYTRA, "&bFlight", fly,
        "Toggle whether your custom hero can fly."));
        inv.setItem(22, makeToggle(Material.IRON_SWORD, "&cSuper Strength", strength,
        "Toggle passive super strength."));
        inv.setItem(24, makeToggle(Material.FEATHER, "&eSuper Speed", speed,
        "Toggle passive super speed."));

        ItemStack rename = new ItemStack(Material.NAME_TAG);
        ItemMeta rm = rename.getItemMeta();
        if (rm != null) {
            rm.displayName(legacy("&6Rename Hero"));
            rm.lore(List.of(legacy("&7Click to type a new name in chat.")));
            rename.setItemMeta(rm);
        }
        inv.setItem(13, rename);

        ItemStack editAbilities = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta em = editAbilities.getItemMeta();
        if (em != null) {
            em.displayName(legacy("&aEdit Abilities"));
            em.lore(List.of(
            legacy("&7Open the ability picker to choose"),
            legacy("&7which abilities your custom hero has."),
            Component.empty(),
            legacy("&eClick to open.")
            ));
            editAbilities.setItemMeta(em);
        }
        inv.setItem(31, editAbilities);

        fillRemaining(inv);
        player.openInventory(inv);
    }

    public static void openAbilityPicker(Player player, InvinciblePlugin plugin, int page) {
        AbilityType[] all = AbilityType.values();
        List<AbilityType> pickable = new ArrayList<>();
        for (AbilityType a : all) {
            if (a == AbilityType.GENERIC_DASH || a == AbilityType.GRAPPLE) continue;
            pickable.add(a);
        }

        int perPage = ABILITY_SLOTS.length;
        int totalPages = Math.max(1, (int) Math.ceil((double) pickable.size() / perPage));
        int clampedPage = Math.min(Math.max(page, 0), totalPages - 1);

        Inventory inv = Bukkit.createInventory(null, SIZE,
        legacy("&d&l" + ABILITY_TITLE_PREFIX + (clampedPage + 1)));

        List<String> currentAbilities = plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId());

        int start = clampedPage * perPage;
        int end = Math.min(start + perPage, pickable.size());
        for (int i = start; i < end; i++) {
            AbilityType ability = pickable.get(i);
            boolean selected = currentAbilities.contains(ability.name());
            inv.setItem(ABILITY_SLOTS[i - start], makeAbilityPickerItem(ability, selected));
        }

        if (clampedPage > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta pm = prev.getItemMeta();
            if (pm != null) {
                pm.displayName(legacy("&ePrevious Page"));
                pm.lore(List.of(legacy("&7Page " + clampedPage + " of " + totalPages)));
                prev.setItemMeta(pm);
            }
            inv.setItem(45, prev);
        }

        if (clampedPage < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nm = next.getItemMeta();
            if (nm != null) {
                nm.displayName(legacy("&eNext Page"));
                nm.lore(List.of(legacy("&7Page " + (clampedPage + 2) + " of " + totalPages)));
                next.setItemMeta(nm);
            }
            inv.setItem(53, next);
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta bm = back.getItemMeta();
        if (bm != null) {
            bm.displayName(legacy("&cBack to Editor"));
            back.setItemMeta(bm);
        }
        inv.setItem(49, back);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta im = info.getItemMeta();
        if (im != null) {
            im.displayName(legacy("&bAbility Selection"));
            im.lore(List.of(
            legacy("&7Click any ability to toggle it"),
            legacy("&7on or off for your custom hero."),
            Component.empty(),
            legacy("&aGreen glow &7= currently selected.")
            ));
            info.setItemMeta(im);
        }
        inv.setItem(48, info);

        fillRemaining(inv);
        player.openInventory(inv);
    }

    private static ItemStack makeAbilityPickerItem(AbilityType ability, boolean selected) {
        Material mat = switch (ability.getCategory()) {
            case OFFENSE -> Material.IRON_SWORD;
            case DEFENSE -> Material.SHIELD;
            case MOBILITY -> Material.FEATHER;
            case UTILITY -> Material.COMPASS;
        };
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String color = CharacterType.ampCode(ability.getColor());
        meta.displayName(legacy(color + (selected ? "&l✔ " : "") + ability.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&7" + ability.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eCategory: " + CharacterType.ampCode(ability.getCategory().getColor())
        + ability.getCategory().getDisplayName()));
        if (ability.getCooldownMs() > 0) {
            lore.add(legacy("&eCooldown: &f" + (ability.getCooldownMs() / 1000) + "s"));
        } else {
            lore.add(legacy("&eCooldown: &aPassive"));
        }
        lore.add(Component.empty());
        lore.add(selected
        ? legacy("&a&lSELECTED &7— click to remove")
        : legacy("&7Not selected — click to add"));
        meta.lore(lore);

        if (selected) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makeInfo(String name, boolean fly, boolean strength, boolean speed) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(legacy("&6&l" + name));
        meta.lore(List.of(
        legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"),
        legacy("&eFlight: " + (fly ? "&aEnabled" : "&cDisabled")),
        legacy("&eSuper Strength: " + (strength ? "&aEnabled" : "&cDisabled")),
        legacy("&eSuper Speed: " + (speed ? "&aEnabled" : "&cDisabled")),
        Component.empty(),
        legacy("&7Use the buttons below to edit your hero.")
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makeToggle(Material mat, String label, boolean enabled, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(legacy(label + ": " + (enabled ? "&a&lON" : "&c&lOFF")));
        meta.lore(List.of(
        legacy("&7" + desc),
        Component.empty(),
        legacy("&eClick to toggle.")
        ));
        if (enabled) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.SHARPNESS, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static void fillRemaining(Inventory inv) {
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

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
