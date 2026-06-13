package com.hihelloy.invincible.cosmetics;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.characters.CharacterType;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CosmeticManager {

    static final String COSMETIC_NBT_KEY = "invincible_cosmetic";

    private final InvinciblePlugin plugin;
    private final Map<UUID, Set<CosmeticType>> equippedCosmetics = new HashMap<>();
    private final Map<UUID, Set<CosmeticType>> ownedCosmetics = new HashMap<>();
    private final Set<UUID> loadingPlayers = new HashSet<>();

    public CosmeticManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isLoading(Player player) {
        return loadingPlayers.contains(player.getUniqueId());
    }

    public ItemStack createPhysicalItem(CosmeticType cosmetic) {
        ItemStack item = new ItemStack(cosmetic.getModelMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setCustomModelData(cosmetic.getCustomModelData());
        meta.displayName(legacy(CharacterType.ampCode(cosmetic.getColor()) + "&l" + cosmetic.getDisplayName()));

        List<Component> lore = new ArrayList<>();
        lore.add(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        lore.add(legacy("&7" + cosmetic.getDescription()));
        lore.add(Component.empty());
        lore.add(legacy("&eSlot: &f" + cosmetic.getSlot().name()));
        lore.add(legacy("&eGranted Abilities: &f" + cosmetic.getAbilityKeys().length));
        lore.add(Component.empty());
        lore.add(legacy("&8[" + COSMETIC_NBT_KEY + ":" + cosmetic.name() + "]"));

        meta.lore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(
        org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE,
        org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
        org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );
        item.setItemMeta(meta);
        return item;
    }

    public CosmeticType getCosmeticFromItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return null;
        List<Component> lore = item.getItemMeta().lore();
        if (lore == null) return null;
        for (Component line : lore) {
            String text = LegacyComponentSerializer.legacyAmpersand().serialize(line);
            if (text.contains(COSMETIC_NBT_KEY + ":")) {
                String after = text.substring(
                text.indexOf(COSMETIC_NBT_KEY + ":") + COSMETIC_NBT_KEY.length() + 1);
                return CosmeticType.fromName(after.replace("]", "").trim());
            }
        }
        return null;
    }

    public boolean purchaseAndEquip(Player player, CosmeticType cosmetic) {
        if (!plugin.hasEconomy()) {
            grantOwnership(player, cosmetic);
            giveAndPlace(player, cosmetic);
            return true;
        }

        if (isOwned(player, cosmetic)) {
            giveAndPlace(player, cosmetic);
            return true;
        }

        double price = cosmetic.getPrice();
        double balance = plugin.getEconomy().getBalance(player);

        if (!plugin.getEconomy().has(player, price)) {
            player.sendMessage(legacy("&cYou need &6$" + String.format("%,.0f", price)
            + "&c to purchase " + CharacterType.ampCode(cosmetic.getColor()) + cosmetic.getDisplayName() + "&c."));
            player.sendMessage(legacy("&cYour balance: &6$" + String.format("%,.0f", balance)));
            return false;
        }

        plugin.getEconomy().withdrawPlayer(player, price);
        grantOwnership(player, cosmetic);
        player.sendMessage(legacy("&6$" + String.format("%,.0f", price) + " &7deducted."));
        giveAndPlace(player, cosmetic);
        return true;
    }

    private void replaceArmorSlot(Player player, CosmeticType cosmetic,
    ItemStack existing, ItemStack newItem,
    Runnable setSlot) {
        if (existing != null && !existing.getType().isAir()
        && getCosmeticFromItem(existing) == null) {
            player.getInventory().addItem(existing);
        }
        setSlot.run();
        player.sendMessage(legacy(CharacterType.ampCode(cosmetic.getColor()) + cosmetic.getDisplayName()
        + " &7equipped to your " + cosmetic.getSlot().name().toLowerCase() + " slot."));
        equipCosmetic(player, cosmetic);
    }

    private void giveAndPlace(Player player, CosmeticType cosmetic) {
        ItemStack physicalItem = createPhysicalItem(cosmetic);

        switch (cosmetic.getSlot()) {
            case HEAD -> replaceArmorSlot(player, cosmetic,
            player.getInventory().getHelmet(), physicalItem,
            () -> player.getInventory().setHelmet(physicalItem));

            case CHEST -> replaceArmorSlot(player, cosmetic,
            player.getInventory().getChestplate(), physicalItem,
            () -> player.getInventory().setChestplate(physicalItem));

            case LEGS -> replaceArmorSlot(player, cosmetic,
            player.getInventory().getLeggings(), physicalItem,
            () -> player.getInventory().setLeggings(physicalItem));

            case FEET -> replaceArmorSlot(player, cosmetic,
            player.getInventory().getBoots(), physicalItem,
            () -> player.getInventory().setBoots(physicalItem));

            case OFFHAND -> {
                ItemStack existing = player.getInventory().getItemInOffHand();
                if (!existing.getType().isAir() && getCosmeticFromItem(existing) == null) {
                    player.getInventory().addItem(existing);
                }
                player.getInventory().setItemInOffHand(physicalItem);
                player.sendMessage(legacy(CharacterType.ampCode(cosmetic.getColor()) + cosmetic.getDisplayName()
                + " &7equipped to your offhand slot."));
                equipCosmetic(player, cosmetic);
            }
        }
    }

    private void grantOwnership(Player player, CosmeticType cosmetic) {
        ownedCosmetics.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(cosmetic);
        plugin.getDataManager().saveOwnedCosmetics(player.getUniqueId(),
        ownedCosmetics.get(player.getUniqueId()));
    }

    public void equipCosmetic(Player player, CosmeticType cosmetic) {
        if (isEquipped(player, cosmetic)) return;

        var event = new com.hihelloy.invincible.api.events.CosmeticEquipEvent(player, cosmetic, false);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        Set<CosmeticType> equipped = equippedCosmetics.computeIfAbsent(
        player.getUniqueId(), k -> new HashSet<>());

        CosmeticType existing = getEquippedInSlot(player, cosmetic.getSlot());
        if (existing != null) unequipCosmetic(player, existing);

        equipped.add(cosmetic);
        plugin.getDataManager().saveEquippedCosmetics(player.getUniqueId(), equipped);
        plugin.getAbilityManager().addCosmeticAbilities(player, cosmetic);
        attachModel(player, cosmetic);
        plugin.getScoreboardManager().updateScoreboard(player);

        player.sendMessage(legacy(CharacterType.ampCode(cosmetic.getColor()) + "&lEquipped: &r"
        + CharacterType.ampCode(cosmetic.getColor()) + cosmetic.getDisplayName()));
    }

    public void unequipCosmetic(Player player, CosmeticType cosmetic) {
        if (!isEquipped(player, cosmetic)) return;
        if (isLoading(player)) return;

        org.bukkit.Bukkit.getPluginManager().callEvent(
        new com.hihelloy.invincible.api.events.CosmeticUnequipEvent(player, cosmetic));

        Set<CosmeticType> equipped = equippedCosmetics.get(player.getUniqueId());
        if (equipped == null) return;
        equipped.remove(cosmetic);

        plugin.getDataManager().saveEquippedCosmetics(player.getUniqueId(), equipped);
        plugin.getAbilityManager().removeCosmeticAbilities(player, cosmetic);
        detachModel(player, cosmetic);
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    private void clearSlotAndReturn(Player player, CosmeticType cosmetic,
    ItemStack current, Runnable clearSlot) {
        if (current != null && !current.getType().isAir()
        && getCosmeticFromItem(current) == cosmetic) {
            clearSlot.run();
            HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(current);
            if (!overflow.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), overflow.get(0));
            }
        }
        unequipCosmetic(player, cosmetic);
    }

    public void unequipAndRemoveFromSlot(Player player, CosmeticType cosmetic) {
        switch (cosmetic.getSlot()) {
            case HEAD -> clearSlotAndReturn(player, cosmetic,
            player.getInventory().getHelmet(),
            () -> player.getInventory().setHelmet(null));

            case CHEST -> clearSlotAndReturn(player, cosmetic,
            player.getInventory().getChestplate(),
            () -> player.getInventory().setChestplate(null));

            case LEGS -> clearSlotAndReturn(player, cosmetic,
            player.getInventory().getLeggings(),
            () -> player.getInventory().setLeggings(null));

            case FEET -> clearSlotAndReturn(player, cosmetic,
            player.getInventory().getBoots(),
            () -> player.getInventory().setBoots(null));

            case OFFHAND -> {
                ItemStack item = player.getInventory().getItemInOffHand();
                if (!item.getType().isAir() && getCosmeticFromItem(item) == cosmetic) {
                    player.getInventory().setItemInOffHand(null);
                    player.getInventory().addItem(item);
                }
                unequipCosmetic(player, cosmetic);
            }
        }
    }

    private void attachModel(Player player, CosmeticType cosmetic) {
        try {
            ActiveModel model = ModelEngineAPI.createActiveModel(cosmetic.getModelId());
            if (model == null) return;
            ModeledEntity me = ModelEngineAPI.getOrCreateModeledEntity(player);
            me.addModel(model, true);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to attach model " + cosmetic.getModelId()
            + " to " + player.getName() + ": " + e.getMessage());
        }
    }

    private void detachModel(Player player, CosmeticType cosmetic) {
        try {
            ModeledEntity me = ModelEngineAPI.getModeledEntity(player);
            if (me == null) return;
            me.removeModel(cosmetic.getModelId());
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to detach model " + cosmetic.getModelId()
            + " from " + player.getName() + ": " + e.getMessage());
        }
    }

    public boolean isOwned(Player player, CosmeticType cosmetic) {
        if (!plugin.hasEconomy()) return true;
        Set<CosmeticType> owned = ownedCosmetics.get(player.getUniqueId());
        return owned != null && owned.contains(cosmetic);
    }

    public boolean isEquipped(Player player, CosmeticType cosmetic) {
        Set<CosmeticType> equipped = equippedCosmetics.get(player.getUniqueId());
        return equipped != null && equipped.contains(cosmetic);
    }

    public CosmeticType getEquippedInSlot(Player player, CosmeticSlot slot) {
        Set<CosmeticType> equipped = equippedCosmetics.get(player.getUniqueId());
        if (equipped == null) return null;
        for (CosmeticType c : equipped) {
            if (c.getSlot() == slot) return c;
        }
        return null;
    }

    public Set<CosmeticType> getEquipped(Player player) {
        return equippedCosmetics.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public void loadPlayer(Player player) {
        loadingPlayers.add(player.getUniqueId());
        try {
            Set<CosmeticType> owned = plugin.getDataManager().loadOwnedCosmetics(player.getUniqueId());
            Set<CosmeticType> equipped = plugin.getDataManager().loadEquippedCosmetics(player.getUniqueId());

            if (!owned.isEmpty()) ownedCosmetics.put(player.getUniqueId(), owned);
            if (!equipped.isEmpty()) {
                equippedCosmetics.put(player.getUniqueId(), equipped);
                for (CosmeticType cosmetic : equipped) {
                    plugin.getAbilityManager().addCosmeticAbilities(player, cosmetic);
                    attachModel(player, cosmetic);
                }
            }
        } finally {
            loadingPlayers.remove(player.getUniqueId());
        }
    }

    public void unloadPlayer(Player player) {
        Set<CosmeticType> equipped = equippedCosmetics.get(player.getUniqueId());
        if (equipped != null) {
            for (CosmeticType cosmetic : new HashSet<>(equipped)) {
                detachModel(player, cosmetic);
            }
        }
        equippedCosmetics.remove(player.getUniqueId());
        ownedCosmetics.remove(player.getUniqueId());
    }

    private static Component legacy(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}