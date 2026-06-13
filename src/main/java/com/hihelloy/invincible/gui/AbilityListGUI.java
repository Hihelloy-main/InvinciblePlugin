package com.hihelloy.invincible.gui;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbilityListGUI implements Listener {

    public static final String TITLE = "\u2726 Custom Abilities";

    private static final int SIZE = 54;
    private static final int PER_PAGE = 45;

    private final InvinciblePlugin plugin;

    public AbilityListGUI(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        Map<String, AbilityType.ConfigAbilityMeta> registry = AbilityType.getConfigAbilities();
        List<AbilityType.ConfigAbilityMeta> abilities = new ArrayList<>(registry.values());

        int totalPages = Math.max(1, (int) Math.ceil((double) abilities.size() / PER_PAGE));
        int clampedPage = Math.min(Math.max(page, 0), totalPages - 1);

        Inventory inv = Bukkit.createInventory(null, SIZE,
        legacy("&5&l" + TITLE + " &8(" + (clampedPage + 1) + "/" + totalPages + ")"));

        int start = clampedPage * PER_PAGE;
        int end = Math.min(start + PER_PAGE, abilities.size());

        for (int i = start; i < end; i++) {
            AbilityType.ConfigAbilityMeta meta = abilities.get(i);
            ItemStack item = new ItemStack(Material.NETHER_STAR);
            ItemMeta im = item.getItemMeta();
            if (im != null) {
                im.displayName(legacy("&e&l" + meta.displayName));
                List<Component> lore = new ArrayList<>();
                lore.add(legacy("&8Key: &7" + meta.key));
                lore.add(legacy("&7" + (meta.description.isEmpty() ? "No description." : meta.description)));
                lore.add(Component.empty());
                lore.add(legacy("&eCategory: &f" + meta.category.getDisplayName()));
                lore.add(legacy("&eCooldown: &f" + meta.cooldownMs / 1000 + "s"));
                lore.add(legacy("&eEffects: &f" + (meta.effects.isEmpty() ? "none" : String.join(", ", meta.effects))));
                if (!meta.allowedCharacters.isEmpty()) {
                    lore.add(legacy("&eCharacters: &f" + String.join(", ", meta.allowedCharacters)));
                }
                lore.add(Component.empty());
                lore.add(legacy("&aLeft-click &7to edit"));
                lore.add(legacy("&cRight-click &7to delete"));
                im.lore(lore);
                item.setItemMeta(im);
            }
            inv.setItem(i - start, item);
        }

        if (abilities.isEmpty()) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta em = empty.getItemMeta();
            if (em != null) {
                em.displayName(legacy("&7No custom abilities created yet."));
                List<Component> lore = new ArrayList<>();
                lore.add(legacy("&7Use &f/invadmin abilitycreategui &7to create one."));
                em.lore(lore);
                empty.setItemMeta(em);
            }
            inv.setItem(22, empty);
        }

        if (clampedPage > 0) {
            inv.setItem(45, arrow("&ePrevious Page", "&7Page " + clampedPage));
        }

        inv.setItem(49, makeStack(Material.LIME_DYE, "&aCreate New Ability",
        "&7Opens the Ability Creator.", "", "&eClick to open."));

        if (clampedPage < totalPages - 1) {
            inv.setItem(53, arrow("&eNext Page", "&7Page " + (clampedPage + 2)));
        }

        fill(inv);
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.startsWith("\u2726 Custom Abilities")) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        int currentPage = 0;
        try {
            String pageStr = title.substring(title.indexOf('(') + 1, title.indexOf('/'));
            currentPage = Integer.parseInt(pageStr.trim()) - 1;
        } catch (Exception ignored) {}

        if (item.getType() == Material.ARROW) {
            ItemMeta am = item.getItemMeta();
            if (am == null) return;
            String name = PlainTextComponentSerializer.plainText().serialize(am.displayName());
            if (name.contains("Next")) open(player, currentPage + 1);
            else if (name.contains("Previous")) open(player, currentPage - 1);
            return;
        }

        if (item.getType() == Material.LIME_DYE) {
            plugin.getAbilityCreateGUI().open(player);
            return;
        }

        if (item.getType() == Material.NETHER_STAR) {
            ItemMeta im = item.getItemMeta();
            if (im == null || im.lore() == null) return;

            String keyLine = null;
            for (Component loreLine : im.lore()) {
                String plain = PlainTextComponentSerializer.plainText().serialize(loreLine);
                if (plain.startsWith("Key: ")) {
                    keyLine = plain.substring("Key: ".length()).trim();
                    break;
                }
            }
            if (keyLine == null) return;

            final String key = keyLine;

            if (event.isRightClick()) {
                deleteAbility(player, key);
                open(player, currentPage);
            } else {
                loadDraftFromConfig(player, key);
                plugin.getAbilityCreateGUI().open(player);
                player.sendMessage(legacy("&aLoaded &f" + key + "&a into the editor. Save to apply changes."));
            }
        }
    }

    private void deleteAbility(Player player, String key) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("abilities." + key, null);
        try {
            config.save(configFile);
            plugin.reloadPluginConfig();
            player.sendMessage(legacy("&cDeleted ability &f" + key + "&c."));
        } catch (IOException e) {
            player.sendMessage(legacy("&cFailed to delete: " + e.getMessage()));
        }
    }

    private void loadDraftFromConfig(Player player, String key) {
        AbilityType.ConfigAbilityMeta meta = AbilityType.getConfigMeta(key);
        if (meta == null) return;

        AbilityCreateGUI.AbilityDraft draft = new AbilityCreateGUI.AbilityDraft();
        draft.key = meta.key;
        draft.displayName = meta.displayName;
        draft.description = meta.description;
        draft.cooldownMs = (int) meta.cooldownMs;
        draft.category = meta.category;
        draft.effects.addAll(meta.effects);
        draft.characters.addAll(meta.allowedCharacters);

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        org.bukkit.configuration.ConfigurationSection sec = config.getConfigurationSection("abilities." + key);
        if (sec != null) {
            String triggerStr = sec.getString("activation-trigger", "LEFT_CLICK");
            try {
                draft.trigger = com.hihelloy.invincible.abilities.ActivationTrigger.valueOf(triggerStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
            for (String field : List.of("damage","radius","target-radius","range","knockback","velocity",
            "duration-ticks","heal","regen-ticks","regen-level","resistance-level","strength-ticks",
            "strength-level","slowness-ticks","slowness-level","weakness-ticks","weakness-level",
            "explosion-power","execute-threshold-percent")) {
                if (sec.contains(field)) {
                    draft.fields.put(field, String.valueOf(sec.get(field)));
                }
            }
            String perm = sec.getString("permission", "");
            if (!perm.isEmpty()) draft.permission = perm;
        }

        plugin.getAbilityCreateGUI().getDrafts().put(player.getUniqueId(), draft);
    }

    private ItemStack arrow(String name, String lore) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(legacy(name));
            meta.lore(List.of(legacy(lore)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack makeStack(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(legacy(name));
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) loreList.add(legacy(line));
        meta.lore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    private static void fill(Inventory inv) {
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            filler.setItemMeta(meta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, filler);
        }
    }

    private static Component legacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }
}
