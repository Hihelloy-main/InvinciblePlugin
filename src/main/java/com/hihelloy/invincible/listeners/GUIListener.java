package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.gui.AbilityBindGUI;
import com.hihelloy.invincible.gui.CustomCharGUI;
import com.hihelloy.invincible.gui.ForgeShopGUI;
import com.hihelloy.invincible.gui.StatsGUI;
import com.hihelloy.invincible.shop.ForgeRecipe;
import com.hihelloy.invincible.stats.StatType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIListener implements Listener {

    private final InvinciblePlugin plugin;

    public GUIListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.contains(ForgeShopGUI.TITLE) || title.contains("OmniMC Ingredient")) {
            handleForgeShopClick(event, player);
        } else if (title.contains("Select Your Character")) {
            handleCharacterSelectClick(event, player);
        } else if (title.contains("Character Stats")) {
            handleStatsClick(event, player);
        } else if (title.contains(CustomCharGUI.ABILITY_TITLE_PREFIX)) {
            handleCustomAbilityPickerClick(event, player, title);
        } else if (title.contains(CustomCharGUI.TITLE)) {
            handleCustomCharEditorClick(event, player);
        } else if (title.contains("Abilities:")) {
            handleAbilityBindClick(event, player);
        }
    }

    private void handleAbilityBindClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE
        || item.getType() == Material.BOOK
        || item.getType() == Material.GRAY_DYE) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        int currentPage = 0;
        if (title.contains("(p")) {
            try {
                String pageStr = title.substring(title.indexOf("(p") + 2, title.indexOf("/"));
                currentPage = Integer.parseInt(pageStr.trim()) - 1;
            } catch (Exception ignored) {}
        }

        if (item.getType() == Material.ARROW) {
            ItemMeta arrowMeta = item.getItemMeta();
            if (arrowMeta == null) return;
            String arrowName = PlainTextComponentSerializer.plainText().serialize(arrowMeta.displayName());
            if (arrowName.contains("Next")) {
                AbilityBindGUI.open(player, plugin, currentPage + 1);
            } else if (arrowName.contains("Previous")) {
                AbilityBindGUI.open(player, plugin, currentPage - 1);
            }
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        String cleaned = displayName.replace("✦ ", "").trim();

        AbilityType matchedAbility = null;
        for (AbilityType ability : AbilityType.values()) {
            if (cleaned.equalsIgnoreCase(ability.getDisplayName())) {
                matchedAbility = ability;
                break;
            }
        }

        if (matchedAbility == null) return;

        int slot;
        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbar = event.getHotbarButton();
            if (hotbar < 0 || hotbar > 3) {
                player.sendMessage(Component.text("Press keys 1-4 to bind to a slot.", NamedTextColor.RED));
                return;
            }
            slot = hotbar + 1;
        } else {
            boolean isRightClick = event.isRightClick();
            boolean isShift = event.isShiftClick();
            if (!isShift && !isRightClick) slot = 1;
            else if (!isShift) slot = 2;
            else if (!isRightClick) slot = 3;
            else slot = 4;
        }

        if (plugin.getAbilityManager().bindAbility(player, matchedAbility, slot)) {
            CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
            String color = character != null ? CharacterType.ampCode(character.getColor()) : "&f";
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
            .deserialize(color + matchedAbility.getDisplayName() + "&a bound to slot " + slot + "!"));
            plugin.getScoreboardManager().updateScoreboard(player);
            AbilityBindGUI.open(player, plugin, currentPage);
        } else {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&cThis ability is not available for your character!"));
        }
    }

    private void handleCustomCharEditorClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        Material type = item.getType();

        if (type == Material.NAME_TAG) {
            player.closeInventory();
            plugin.getCustomNameInputManager().startNameInput(player);
            return;
        }

        if (type == Material.ENCHANTING_TABLE) {
            CustomCharGUI.openAbilityPicker(player, plugin, 0);
            return;
        }

        if (type == Material.ELYTRA) {
            boolean current = plugin.getDataManager().getCustomHeroFly(player.getUniqueId());
            plugin.getDataManager().saveCustomHeroFly(player.getUniqueId(), !current);
            if (plugin.getDataManager().getCharacterManager().getCharacter(player) == CharacterType.CUSTOM) {
                player.setAllowFlight(!current);
                if (!(!current)) {
                    player.setFlying(false);
                    plugin.getFlightManager().stopFlight(player);
                }
            }
            CustomCharGUI.openEditor(player, plugin);
            return;
        }

        if (type == Material.IRON_SWORD) {
            boolean current = plugin.getDataManager().getCustomHeroStrength(player.getUniqueId());
            plugin.getDataManager().saveCustomHeroStrength(player.getUniqueId(), !current);
            CustomCharGUI.openEditor(player, plugin);
            return;
        }

        if (type == Material.FEATHER) {
            boolean current = plugin.getDataManager().getCustomHeroSpeed(player.getUniqueId());
            plugin.getDataManager().saveCustomHeroSpeed(player.getUniqueId(), !current);
            CustomCharGUI.openEditor(player, plugin);
        }
    }

    private void handleCustomAbilityPickerClick(InventoryClickEvent event, Player player, String title) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        int currentPage = 0;
        try {
            String pageStr = title.substring(
            title.indexOf(CustomCharGUI.ABILITY_TITLE_PREFIX)
            + CustomCharGUI.ABILITY_TITLE_PREFIX.length());
            currentPage = Integer.parseInt(pageStr.trim()) - 1;
        } catch (Exception ignored) {}

        if (item.getType() == Material.BARRIER) {
            CustomCharGUI.openEditor(player, plugin);
            return;
        }

        if (item.getType() == Material.BOOK) return;

        if (item.getType() == Material.ARROW) {
            ItemMeta arrowMeta = item.getItemMeta();
            if (arrowMeta == null) return;
            String arrowName = PlainTextComponentSerializer.plainText().serialize(arrowMeta.displayName());
            if (arrowName.contains("Next")) {
                CustomCharGUI.openAbilityPicker(player, plugin, currentPage + 1);
            } else if (arrowName.contains("Previous")) {
                CustomCharGUI.openAbilityPicker(player, plugin, currentPage - 1);
            }
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        String cleaned = displayName.replace("✦ ", "").replace("✔ ", "").trim();

        AbilityType matched = null;
        for (AbilityType ability : AbilityType.values()) {
            if (cleaned.equalsIgnoreCase(ability.getDisplayName())) {
                matched = ability;
                break;
            }
        }

        if (matched == null) return;

        java.util.List<String> abilities = plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId());
        if (abilities.contains(matched.name())) {
            abilities.remove(matched.name());
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&c" + matched.getDisplayName() + " removed from custom hero."));
        } else {
            abilities.add(matched.name());
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&a" + matched.getDisplayName() + " added to custom hero."));
        }
        plugin.getDataManager().saveCustomHeroAbilities(player.getUniqueId(), abilities);

        if (plugin.getDataManager().getCharacterManager().getCharacter(player) == CharacterType.CUSTOM) {
            plugin.getDataManager().getCharacterManager().applyCustomHeroAbilities(player);
        }

        CustomCharGUI.openAbilityPicker(player, plugin, currentPage);
    }

    private void handleCharacterSelectClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        if (item.getType() == Material.NETHER_STAR) {
            if (!player.hasPermission("invincible.customchar")) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("&cYou don't have permission to create a custom hero."));
                return;
            }
            player.closeInventory();
            plugin.getCustomNameInputManager().startNameInput(player);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());

        for (CharacterType type : CharacterType.values()) {
            if (type == CharacterType.CUSTOM) continue;
            if (displayName.contains(type.getDisplayName())) {
                plugin.getDataManager().getCharacterManager().setCharacter(player, type);
                plugin.getAbilityManager().clearPlayer(player);
                plugin.getAbilityManager().loadPlayer(player);
                player.closeInventory();
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(CharacterType.ampCode(type.getColor()) + "&lYou are now " + type.getDisplayName() + "!"));
                plugin.getScoreboardManager().updateScoreboard(player);
                return;
            }
        }
    }

    private void handleStatsClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());

        for (StatType stat : StatType.values()) {
            if (displayName.contains(stat.getDisplayName())) {
                int cost = plugin.getConfig().getInt("stats." + stat.name() + ".cost-per-level", 1);
                int points = plugin.getStatManager().getStatPoints(player);
                if (points < cost) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&cNot enough stat points. You need " + cost + ", you have " + points + "."));
                    return;
                }
                plugin.getStatManager().upgradeStat(player, stat);
                StatsGUI.open(player, plugin);
                return;
            }
        }
    }

    private void handleForgeShopClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE
        || item.getType() == Material.AIR) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.contains("OmniMC Ingredient")) {

            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());

        for (ForgeRecipe recipe : ForgeRecipe.values()) {
            if (displayName.contains(recipe.displayName)) {

                ForgeShopGUI.open(player, plugin);
                return;
            }
        }
    }
}
