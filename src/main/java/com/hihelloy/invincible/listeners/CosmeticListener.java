package com.hihelloy.invincible.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.cosmetics.CosmeticSlot;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import com.hihelloy.invincible.gui.CosmeticsGUI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Set;
import java.util.UUID;

public class CosmeticListener implements Listener {

    private static final Set<UUID> quittingPlayers = new java.util.HashSet<>();

    public static void markQuitting(Player player) {
        quittingPlayers.add(player.getUniqueId());
    }

    public static void unmarkQuitting(Player player) {
        quittingPlayers.remove(player.getUniqueId());
    }

    private final InvinciblePlugin plugin;

    public CosmeticListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGuiClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().title() == null) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Cosmetics")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().displayName() == null) return;

        String raw = PlainTextComponentSerializer.plainText().serialize(
                event.getCurrentItem().getItemMeta().displayName());
        String cleaned = raw.replaceAll("&[0-9a-fk-or]", "").replace("▶ ", "").trim();

        CosmeticType cosmetic = CosmeticType.fromName(cleaned);
        if (cosmetic == null) return;

        if (plugin.getCosmeticManager().isEquipped(player, cosmetic)) {
            plugin.getCosmeticManager().unequipCosmetic(player, cosmetic);
            player.sendMessage(Component.text("Unequipped: ", NamedTextColor.GRAY).append(Component.text(cosmetic.getDisplayName(), cosmetic.getColor())));
        } else {
            plugin.getCosmeticManager().purchaseAndEquip(player, cosmetic);
        }

        player.closeInventory();
        CosmeticsGUI.open(player, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();

        CosmeticSlot cosmeticSlot = switch (event.getSlotType()) {
            case CHEST -> CosmeticSlot.CHEST;
            case LEGS -> CosmeticSlot.LEGS;
            default -> null;
        };
        if (cosmeticSlot == null) return;

        ItemStack removed = event.getOldItem();
        ItemStack placed = event.getNewItem();

        if (isCosmeticItem(removed) && !isCosmeticItem(placed)) {
            plugin.getCosmeticManager().unequipCosmetic(player,
                    plugin.getCosmeticManager().getEquippedInSlot(player, cosmeticSlot));
        }

        if (isCosmeticItem(placed)) {
            CosmeticType cosmetic = plugin.getCosmeticManager().getCosmeticFromItem(placed);
            if (cosmetic != null && cosmetic.getSlot() == cosmeticSlot
                    && plugin.getCosmeticManager().isOwned(player, cosmetic)) {
                plugin.getCosmeticManager().equipCosmetic(player, cosmetic);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        ItemStack toOffhand = event.getOffHandItem();
        ItemStack fromOffhand = event.getMainHandItem();

        if (isCosmeticItem(fromOffhand) && !isCosmeticItem(toOffhand)) {
            CosmeticType eq = plugin.getCosmeticManager().getEquippedInSlot(player, CosmeticSlot.OFFHAND);
            if (eq != null) plugin.getCosmeticManager().unequipCosmetic(player, eq);
        }

        if (isCosmeticItem(toOffhand)) {
            CosmeticType cosmetic = plugin.getCosmeticManager().getCosmeticFromItem(toOffhand);
            if (cosmetic != null && cosmetic.getSlot() == CosmeticSlot.OFFHAND
                    && plugin.getCosmeticManager().isOwned(player, cosmetic)) {
                plugin.getCosmeticManager().equipCosmetic(player, cosmetic);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (title.contains("Cosmetics")) return;

        if (event.getRawSlot() != 40) return;

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (isCosmeticItem(current) && !isCosmeticItem(cursor)) {
            CosmeticType eq = plugin.getCosmeticManager().getEquippedInSlot(player, CosmeticSlot.OFFHAND);
            if (eq != null) plugin.getCosmeticManager().unequipCosmetic(player, eq);
        }

        if (isCosmeticItem(cursor)) {
            CosmeticType cosmetic = plugin.getCosmeticManager().getCosmeticFromItem(cursor);
            if (cosmetic != null && cosmetic.getSlot() == CosmeticSlot.OFFHAND
                    && plugin.getCosmeticManager().isOwned(player, cosmetic)) {
                plugin.getCosmeticManager().equipCosmetic(player, cosmetic);
            }
        }
    }

    private boolean isCosmeticItem(ItemStack item) {
        return plugin.getCosmeticManager().getCosmeticFromItem(item) != null;
    }
}