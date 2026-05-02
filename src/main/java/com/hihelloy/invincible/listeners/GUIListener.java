package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.gui.AbilityBindGUI;
import com.hihelloy.invincible.gui.StatsGUI;
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

public class GUIListener implements Listener {

    private final InvinciblePlugin plugin;

    public GUIListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getView().title() == null) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        if (title.contains("Select Your Character")) {
            handleCharacterSelectClick(event, player);
        } else if (title.contains("Character Stats")) {
            handleStatsClick(event, player);
        } else if (title.contains("Abilities:")) {
            handleAbilityBindClick(event, player);
        }
    }

    private void handleCharacterSelectClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        if (item.getItemMeta() == null) return;

        if (item.getType() == Material.NETHER_STAR) {
            player.closeInventory();
            plugin.getCustomNameInputManager().startNameInput(player);
            return;
        }

        String rawName = item.getItemMeta().displayName() != null
                ? PlainTextComponentSerializer.plainText().serialize(item.getItemMeta().displayName())
                : "";

        String cleaned = rawName.replace("▶ ", "").trim();

        CharacterType matched = null;
        for (CharacterType type : CharacterType.values()) {
            if (type == CharacterType.CUSTOM) continue;
            if (type.getDisplayName().equalsIgnoreCase(cleaned)) {
                matched = type;
                break;
            }
        }

        if (matched == null) return;

        CharacterType finalMatched = matched;
        plugin.getDataManager().getCharacterManager().setCharacter(player, finalMatched);
        plugin.getAbilityManager().clearPlayer(player);
        plugin.getScoreboardManager().updateScoreboard(player);

        player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(CharacterType.ampCode(finalMatched.getColor())
                        + "&lYou are now " + finalMatched.getDisplayName() + "!"));
        if (finalMatched.canFly()) {
            player.sendMessage(Component.text(
                    "Double-jump or use /inv fly to take flight!", NamedTextColor.AQUA));
        }
        player.sendMessage(Component.text(
                "Use /inv abilities to bind your powers.", NamedTextColor.YELLOW));
        player.closeInventory();
    }

    private void handleStatsClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        String cleaned = displayName.trim();

        StatType matchedStat = null;
        for (StatType stat : StatType.values()) {
            if (cleaned.contains(stat.getDisplayName())) {
                matchedStat = stat;
                break;
            }
        }

        if (matchedStat == null) return;

        if (plugin.getStatManager().upgradeStat(player, matchedStat)) {
            int newLevel = plugin.getStatManager().getStatLevel(player, matchedStat);
            player.sendMessage(Component.text(matchedStat.getDisplayName(), matchedStat.getColor())
                    .append(Component.text(" upgraded to level " + newLevel + "!", NamedTextColor.GREEN)));
            StatsGUI.open(player, plugin);
        }
    }

    private void handleAbilityBindClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null
                || item.getType() == Material.BLACK_STAINED_GLASS_PANE
                || item.getType() == Material.BOOK
                || item.getType() == Material.GRAY_DYE) return;

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
                player.sendMessage(Component.text("Press keys 1–4 to bind to a slot.", NamedTextColor.RED));
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
                    .deserialize(color + matchedAbility.getDisplayName()
                            + "&a bound to slot " + slot + "!"));
            plugin.getScoreboardManager().updateScoreboard(player);
            AbilityBindGUI.open(player, plugin);
        } else {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize("&cThis ability is not available for your character!"));
        }
    }
}