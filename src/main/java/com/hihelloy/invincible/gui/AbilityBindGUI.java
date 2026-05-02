package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.cosmetics.CosmeticType;
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
import java.util.Set;

public class AbilityBindGUI {

    public static final String TITLE_PREFIX = "&3&l✦ Abilities: ";
    private static final int SIZE = 54;
    private static final int[] ABILITY_SLOTS = {
            10, 11, 12, 13, 14, 15,
            19, 20, 21, 22, 23, 24, 25
    };

    public static void open(Player player, InvinciblePlugin plugin) {
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null) {
            player.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&cYou must select a character first! Use /invincible select"));
            return;
        }

        Inventory inv = Bukkit.createInventory(null, SIZE,
                legacy(TITLE_PREFIX + CharacterType.ampCode(character.getColor()) + character.getDisplayName()));

        String[] keys = character.getAbilityKeys();
        for (int i = 0; i < keys.length && i < ABILITY_SLOTS.length; i++) {
            AbilityType ability = AbilityType.fromName(keys[i]);
            if (ability != null) {
                inv.setItem(ABILITY_SLOTS[i], createAbilityItem(ability, character, plugin, player));
            }
        }

        Set<String> cosmeticKeys = plugin.getAbilityManager().getCosmeticAbilityKeys(player);
        if (!cosmeticKeys.isEmpty()) {
            int[] cosmeticSlots = {28, 29, 30, 31, 32, 33};
            int idx = 0;
            for (String key : cosmeticKeys) {
                if (idx >= cosmeticSlots.length) break;
                AbilityType ability = AbilityType.fromName(key);
                if (ability != null) {
                    inv.setItem(cosmeticSlots[idx], createCosmeticAbilityItem(ability, plugin, player));
                    idx++;
                }
            }
        }

        addBoundSlotsDisplay(inv, player, plugin, character);
        addInstructions(inv);
        fillRemaining(inv);
        player.openInventory(inv);
    }

    private static ItemStack createAbilityItem(AbilityType ability, CharacterType character,
                                               InvinciblePlugin plugin, Player player) {
        ItemStack item = new ItemStack(getMaterialForCategory(ability));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(legacy(CharacterType.ampCode(ability.getColor()) + "&l" + ability.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&7" + ability.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eCategory: " + CharacterType.ampCode(ability.getCategory().getColor()) + ability.getCategory().getDisplayName()));
        if (ability.getCooldownMs() > 0) {
            lore.add(legacy("&eCooldown: &f" + (ability.getCooldownMs() / 1000) + "s"));
        } else {
            lore.add(legacy("&eCooldown: &aPassive"));
        }
        lore.add(Component.empty());

        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        for (int i = 0; i < bound.length; i++) {
            if (bound[i] == ability) {
                lore.add(legacy("&a✔ Bound to slot " + (i + 1)));
                break;
            }
        }

        lore.add(legacy("&ePress &f1&e, &f2&e, &f3&e, &f4 &eover this item to bind to that slot"));
        lore.add(legacy("&eLeft-click: &fBind to slot 1"));
        lore.add(legacy("&eF (swap hands): &fBind to slot 2"));
        lore.add(legacy("&eShift-click: &fBind to slot 3"));
        lore.add(legacy("&eQ (drop): &fBind to slot 4"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static void addBoundSlotsDisplay(Inventory inv, Player player, InvinciblePlugin plugin,
                                             CharacterType character) {
        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        int[] slots = {46, 47, 51, 52};
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(bound[i] != null ? getMaterialForCategory(bound[i]) : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (bound[i] != null) {
                    meta.displayName(legacy(CharacterType.ampCode(character.getColor()) + "[" + (i + 1) + "] " + bound[i].getDisplayName()));
                } else {
                    meta.displayName(legacy("&8[" + (i + 1) + "] Empty"));
                }
                item.setItemMeta(meta);
            }
            inv.setItem(slots[i], item);
        }
    }

    private static void addInstructions(Inventory inv) {
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            meta.displayName(legacy("&bAbility Binding"));
            List<Component> lore = new ArrayList<>();
            lore.add(legacy("&7Click an ability to bind it."));
            lore.add(legacy("&7You may have 4 bound at once."));
            lore.add(legacy("&7Hold hotbar slots 1-4 and"));
            lore.add(legacy("&7left-click or F (swap) or Q (drop) to use."));
            lore.add(Component.empty());
            lore.add(legacy("&e/invincible info <ability>"));
            lore.add(legacy("&7to read about any ability."));
            meta.lore(lore);
            info.setItemMeta(meta);
        }
        inv.setItem(49, info);
    }

    private static ItemStack createCosmeticAbilityItem(AbilityType ability,
                                                       InvinciblePlugin plugin, Player player) {
        ItemStack item = new ItemStack(getMaterialForCategory(ability));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.displayName(legacy("&5&l✦ " + ability.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&5[Cosmetic Ability]"));
        lore.add(legacy("&7" + ability.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eCategory: " + CharacterType.ampCode(ability.getCategory().getColor()) + ability.getCategory().getDisplayName()));
        if (ability.getCooldownMs() > 0) {
            lore.add(legacy("&eCooldown: &f" + (ability.getCooldownMs() / 1000) + "s"));
        } else {
            lore.add(legacy("&eCooldown: &aPassive"));
        }
        lore.add(Component.empty());

        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        for (int i = 0; i < bound.length; i++) {
            if (bound[i] == ability) {
                lore.add(legacy("&a✔ Bound to slot " + (i + 1)));
                break;
            }
        }

        lore.add(legacy("&ePress &f1&e, &f2&e, &f3&e, &f4 &eover this item to bind to that slot"));
        lore.add(legacy("&eLeft-click: &fBind to slot 1"));
        lore.add(legacy("&eF (swap hands): &fBind to slot 2"));
        lore.add(legacy("&eShift-click: &fBind to slot 3"));
        lore.add(legacy("&eQ (drop): &fBind to slot 4"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static Material getMaterialForCategory(AbilityType ability) {
        return switch (ability.getCategory()) {
            case OFFENSE -> Material.IRON_SWORD;
            case DEFENSE -> Material.SHIELD;
            case MOBILITY -> Material.FEATHER;
            case UTILITY -> Material.COMPASS;
        };
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

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}