package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityCategory;
import com.hihelloy.invincible.abilities.ActivationTrigger;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AbilityCreateGUI {

    public static final String TITLE = "✦ Ability Creator";
    public static final String FIELDS_TITLE = "✦ Ability Fields";
    public static final String CHARS_TITLE = "✦ Assign Characters";
    public static final String TRIGGER_TITLE = "✦ Activation Trigger";
    public static final String EFFECTS_TITLE = "✦ Effects & Behaviour";
    public static final String CATEGORY_TITLE = "✦ Category";

    private static final int SIZE = 54;

    private final InvinciblePlugin plugin;
    private final Map<UUID, AbilityDraft> drafts = new HashMap<>();
    private final Map<UUID, String> awaitingInput = new HashMap<>();

    public AbilityCreateGUI(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, AbilityDraft> getDrafts() {
        return drafts;
    }

    public Map<UUID, String> getAwaitingInput() {
        return awaitingInput;
    }

    public void open(Player player) {
        AbilityDraft draft = drafts.computeIfAbsent(player.getUniqueId(), k -> new AbilityDraft());
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy("&5&l" + TITLE));

        inv.setItem(4, makeStack(Material.NETHER_STAR, "&e&lAbility Draft",
        "&7ID (key): &f" + (draft.key.isEmpty() ? "&cnot set" : draft.key),
        "&7Display Name: &f" + (draft.displayName.isEmpty() ? "&cnot set" : draft.displayName),
        "&7Category: &f" + draft.category.getDisplayName(),
        "&7Trigger: &f" + draft.trigger.name(),
        "&7Cooldown: &f" + draft.cooldownMs + "ms",
        "&7Permission: &f" + (draft.permission.isEmpty() ? "&cnone" : draft.permission),
        "&7Characters: &f" + draft.characters.size() + " assigned",
        "",
        "&7Custom heroes can always use any ability."));

        inv.setItem(10, makeStack(Material.NAME_TAG, "&aSet Ability Key",
        "&7The internal ID used in config.",
        "&7Example: &fMY_ABILITY",
        "",
        "&eCurrent: &f" + (draft.key.isEmpty() ? "&cnot set" : draft.key),
        "",
        "&eClick to type in chat."));

        inv.setItem(11, makeStack(Material.BOOK, "&aSet Display Name",
        "&7The name shown to players.",
        "",
        "&eCurrent: &f" + (draft.displayName.isEmpty() ? "&cnot set" : draft.displayName),
        "",
        "&eClick to type in chat."));

        inv.setItem(12, makeStack(Material.CLOCK, "&aSet Cooldown",
        "&7Milliseconds between uses.",
        "&7Example: 8000 = 8 seconds",
        "",
        "&eCurrent: &f" + draft.cooldownMs + "ms",
        "",
        "&eClick to type in chat."));

        inv.setItem(13, makeStack(Material.FEATHER, "&aSet Description",
        "&7Short description shown in GUIs.",
        "",
        "&eCurrent: &f" + (draft.description.isEmpty() ? "&cnot set" : draft.description),
        "",
        "&eClick to type in chat."));

        inv.setItem(14, makeStack(Material.IRON_NUGGET, "&aSet Permission Node",
        "&7Optional. Leave blank for no restriction.",
        "&7Example: &finvincible.ability.myability",
        "",
        "&eCurrent: &f" + (draft.permission.isEmpty() ? "&7none" : draft.permission),
        "",
        "&eClick to type in chat."));

        inv.setItem(19, makeStack(Material.COMPASS, "&bSet Activation Trigger",
        "&7How this ability is activated.",
        "",
        "&eCurrent: &f" + draft.trigger.name(),
        "",
        "&eClick to choose."));

        inv.setItem(20, makeStack(Material.ORANGE_DYE, "&bSet Category",
        "&7Offense / Defense / Mobility / Utility",
        "",
        "&eCurrent: &f" + draft.category.getDisplayName(),
        "",
        "&eClick to choose."));

        inv.setItem(21, makeStack(Material.PLAYER_HEAD, "&bAssign Characters",
        "&7Which characters can use this ability.",
        "&7Custom heroes always have access.",
        "",
        "&eCurrent: &f" + draft.characters.size() + " assigned",
        "",
        "&eClick to edit."));

        inv.setItem(22, makeStack(Material.ENCHANTING_TABLE, "&bEdit Fields",
        "&7Config values: damage, radius, duration,",
        "&7knockback, velocity, range, etc.",
        "",
        "&eCurrent fields: &f" + draft.fields.size(),
        "",
        "&eClick to edit."));

        inv.setItem(23, makeStack(Material.REDSTONE, "&bSet Effects / Behaviour",
        "&7Choose what this ability does:",
        "&7damage, knockback, potion, launch,",
        "&7heal, teleport, AoE, etc.",
        "",
        "&eCurrent effects: &f" + draft.effects.size(),
        "",
        "&eClick to configure."));

        if (draft.isValid()) {
            inv.setItem(31, makeStack(Material.EMERALD_BLOCK, "&a&lSave Ability",
            "&7Writes to config.yml and registers",
            "&7the ability as a custom ability.",
            "",
            "&eClick to save."));
        } else {
            inv.setItem(31, makeStack(Material.REDSTONE_BLOCK, "&c&lCannot Save Yet",
            "&cMissing required fields:",
            draft.key.isEmpty() ? "&7 - Ability Key" : "",
            draft.displayName.isEmpty() ? "&7 - Display Name" : ""));
        }

        inv.setItem(49, makeStack(Material.BARRIER, "&cDiscard Draft",
        "&7Clears the current draft without saving.",
        "",
        "&eClick to discard."));

        fill(inv);
        player.openInventory(inv);
    }

    public void openTriggerPicker(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, legacy("&5&l" + TRIGGER_TITLE));

        ActivationTrigger[] triggers = ActivationTrigger.values();
        int[] slots = {
            10, 11, 12, 13, 14};
        String[] descs = {
            "&7Left-click on hotbar slot",
            "&7Right-click on hotbar slot",
            "&7Sneak + left-click",
            "&7Sneak + right-click",
            "&7F key (swap hands)",
            "&7Fires when you hit an entity"
        };
        for (int i = 0; i < triggers.length && i < slots.length; i++) {
            inv.setItem(slots[i], makeStack(Material.COMPASS,
            "&e" + triggers[i].name(),
            descs[Math.min(i, descs.length - 1)]));
        }

        inv.setItem(22, makeStack(Material.ARROW, "&7Back"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openCategoryPicker(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, legacy("&5&l" + CATEGORY_TITLE));
        AbilityCategory[] cats = AbilityCategory.values();
        Material[] mats = {
            Material.IRON_SWORD, Material.SHIELD, Material.FEATHER, Material.COMPASS};
        int[] slots = {
            11, 12, 13, 14};
        for (int i = 0; i < cats.length; i++) {
            inv.setItem(slots[i], makeStack(mats[i],
            CharacterType.ampCode(cats[i].getColor()) + cats[i].getDisplayName(),
            "&7Click to select."));
        }
        inv.setItem(22, makeStack(Material.ARROW, "&7Back"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openCharacterPicker(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy("&5&l" + CHARS_TITLE));
        AbilityDraft draft = drafts.getOrDefault(player.getUniqueId(), new AbilityDraft());

        CharacterType[] chars = CharacterType.values();
        int slot = 0;
        for (CharacterType ct : chars) {
            if (slot >= 45) break;
            boolean selected = draft.characters.contains(ct.name());
            Material mat = selected ? Material.LIME_DYE : Material.GRAY_DYE;
            inv.setItem(slot, makeStack(mat,
            CharacterType.ampCode(ct.getColor()) + (selected ? "&l✔ " : "") + ct.getDisplayName(),
            selected ? "&aCurrently assigned — click to remove." : "&7Click to assign."));
            slot++;
        }

        inv.setItem(49, makeStack(Material.ARROW, "&7Back to Creator"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openFieldsEditor(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy("&5&l" + FIELDS_TITLE));
        AbilityDraft draft = drafts.getOrDefault(player.getUniqueId(), new AbilityDraft());

        String[] standardFields = {
            "damage", "radius", "target-radius", "range", "knockback", "velocity",
            "duration-ticks", "heal", "regen-ticks", "regen-level",
            "resistance-level", "strength-ticks", "strength-level",
            "slowness-ticks", "slowness-level", "weakness-ticks", "weakness-level",
            "explosion-power", "execute-threshold-percent"
        };

        int slot = 0;
        for (String field : standardFields) {
            if (slot >= 45) break;
            String current = draft.fields.getOrDefault(field, "");
            boolean set = !current.isEmpty();
            inv.setItem(slot, makeStack(
            set ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE,
            "&e" + field,
            set ? "&aCurrent: &f" + current : "&7Not set — click to set.",
            "",
            "&eLeft-click to set / edit.",
            set ? "&cRight-click to clear." : ""));
            slot++;
        }

        inv.setItem(46, makeStack(Material.WRITABLE_BOOK, "&aAdd Custom Field",
        "&7Type any key not in the list above.",
        "&eClick to type."));

        inv.setItem(49, makeStack(Material.ARROW, "&7Back to Creator"));
        fill(inv);
        player.openInventory(inv);
    }

    public void openEffectsPicker(Player player) {
        Inventory inv = Bukkit.createInventory(null, SIZE, legacy("&5&l" + EFFECTS_TITLE));
        AbilityDraft draft = drafts.getOrDefault(player.getUniqueId(), new AbilityDraft());

        String[] effects = {
            "DAMAGE_NEARBY", "KNOCKBACK_NEARBY", "LAUNCH_TARGET_UP", "LAUNCH_TARGET_AWAY",
            "LAUNCH_SELF_FORWARD", "HEAL_SELF", "APPLY_STRENGTH", "APPLY_SPEED",
            "APPLY_RESISTANCE", "APPLY_REGEN", "APPLY_SLOWNESS_TARGET",
            "APPLY_WEAKNESS_TARGET", "EXPLODE", "STRIKE_NEAREST", "VELOCITY_DIVE_SELF"
        };
        String[] descs = {
            "Damage all entities within radius",
            "Knock back all entities within radius",
            "Launch nearest target straight up",
            "Launch nearest target away from you",
            "Launch yourself forward",
            "Restore health based on heal field",
            "Give yourself Strength for duration-ticks",
            "Give yourself Speed for duration-ticks",
            "Give yourself Resistance for duration-ticks",
            "Give yourself Regeneration for regen-ticks",
            "Apply Slowness to nearest target",
            "Apply Weakness to nearest target",
            "Create explosion at location",
            "Damage nearest entity (damage field)",
            "Dive straight down and AoE on land"
        };

        int slot = 0;
        for (int i = 0; i < effects.length; i++) {
            boolean selected = draft.effects.contains(effects[i]);
            inv.setItem(slot, makeStack(
            selected ? Material.LIME_GLAZED_TERRACOTTA : Material.GRAY_GLAZED_TERRACOTTA,
            (selected ? "&a&l✔ " : "&7") + effects[i],
            "&7" + descs[i],
            "",
            selected ? "&aEnabled — click to disable." : "&7Disabled — click to enable."));
            slot++;
        }

        inv.setItem(49, makeStack(Material.ARROW, "&7Back to Creator"));
        fill(inv);
        player.openInventory(inv);
    }

    public void saveAbility(Player player) {
        AbilityDraft draft = drafts.get(player.getUniqueId());
        if (draft == null || !draft.isValid()) {
            player.sendMessage(legacy("&cDraft is invalid. Set a key and display name first."));
            return;
        }

        String key = draft.key.toUpperCase().replace(" ", "_");

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        String path = "abilities." + key;
        config.set(path + ".display-name", draft.displayName);
        config.set(path + ".description", draft.description);
        config.set(path + ".enabled", true);
        config.set(path + ".cooldown-ms", draft.cooldownMs);
        config.set(path + ".category", draft.category.name());
        config.set(path + ".activation-trigger", draft.trigger.name());
        config.set(path + ".death-message", "&c%victim% &7was defeated by %killer%&7 using &e" + draft.displayName + "&7.");

        if (!draft.permission.isEmpty()) {
            config.set(path + ".permission", draft.permission);
        }
        if (!draft.characters.isEmpty()) {
            config.set(path + ".allowed-characters", new ArrayList<>(draft.characters));
        }
        for (Map.Entry<String, String> entry : draft.fields.entrySet()) {
            try {
                config.set(path + "." + entry.getKey(), Double.parseDouble(entry.getValue()));
            } catch (NumberFormatException e) {
                config.set(path + "." + entry.getKey(), entry.getValue());
            }
        }
        config.set(path + ".effects", new ArrayList<>(draft.effects));

        try {
            config.save(configFile);
            plugin.reloadPluginConfig();
            drafts.remove(player.getUniqueId());
            player.sendMessage(legacy("&aAbility &f" + key + " &asaved and registered!"));
            player.sendMessage(legacy("&7It is now bindable for all assigned characters and fully functional."));
            player.sendMessage(legacy("&7Your selected effects will execute automatically when used."));
        } catch (IOException e) {
            player.sendMessage(legacy("&cFailed to save config: " + e.getMessage()));
        }
    }

    private static ItemStack makeStack(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(legacy(name));
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) {
            if (!line.isEmpty()) loreList.add(legacy(line));
        }
        meta.lore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    private static void fill(Inventory inv) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty()); filler.setItemMeta(meta); }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
    }

    private static Component legacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static class AbilityDraft {
        public String key = "";
        public String displayName = "";
        public String description = "";
        public int cooldownMs = 5000;
        public AbilityCategory category = AbilityCategory.OFFENSE;
        public ActivationTrigger trigger = ActivationTrigger.LEFT_CLICK;
        public String permission = "";
        public Set<String> characters = new LinkedHashSet<>();
        public Map<String, String> fields = new LinkedHashMap<>();
        public Set<String> effects = new LinkedHashSet<>();
        public String pendingFieldKey = "";

        public boolean isValid() {
            return !key.isEmpty() && !displayName.isEmpty();
        }
    }
}
