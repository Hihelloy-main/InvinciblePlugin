package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.abilities.ActivationTrigger;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AbilityListener implements Listener {

    private final InvinciblePlugin plugin;

    public AbilityListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) return;
        if (plugin.getWorldGuard().isDisabled(player)) return;
        if (plugin.getFlightManager().isFlying(player)) return;

        int held = player.getInventory().getHeldItemSlot();

        if (held == 4 || held == 5) {
            event.setCancelled(true);
            plugin.getAbilityManager().activateAbility(player, held + 1);
            return;
        }

        if (held < 0 || held > 3) return;

        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        if (bound[held] == null) return;

        ActivationTrigger expected = plugin.getAbilityConfig().getActivationTrigger(bound[held]);
        if (expected != ActivationTrigger.SWAP) return;

        event.setCancelled(true);
        plugin.getAbilityManager().activateAbility(player, held + 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) return;
        if (plugin.getWorldGuard().isDisabled(player)) return;

        Action action = event.getAction();
        boolean leftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        boolean rightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        if (!leftClick && !rightClick) return;

        if (plugin.getFlightManager().isFlying(player)) return;

        int held = player.getInventory().getHeldItemSlot();

        if (held == 4 || held == 5) {
            if (leftClick) {
                event.setCancelled(true);
                plugin.getAbilityManager().activateAbility(player, held + 1);
            }
            return;
        }

        if (held < 0 || held > 3) return;

        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        if (bound[held] == null) return;

        boolean sneaking = player.isSneaking();
        ActivationTrigger expected = plugin.getAbilityConfig().getActivationTrigger(bound[held]);

        ActivationTrigger fired;
        if (leftClick && sneaking) fired = ActivationTrigger.LEFT_CLICK_SNEAK;
        else if (rightClick && sneaking) fired = ActivationTrigger.RIGHT_CLICK_SNEAK;
        else if (leftClick) fired = ActivationTrigger.LEFT_CLICK;
        else fired = ActivationTrigger.RIGHT_CLICK;

        if (fired != expected) {
            if (sneaking && (fired == ActivationTrigger.LEFT_CLICK_SNEAK
                    || fired == ActivationTrigger.RIGHT_CLICK_SNEAK)) {
                String hint = switch (expected) {
                    case LEFT_CLICK -> "Left Click";
                    case RIGHT_CLICK -> "Right Click";
                    case LEFT_CLICK_SNEAK -> "Sneak + Left";
                    case RIGHT_CLICK_SNEAK -> "Sneak + Right";
                    default -> "F key";
                };
                player.sendActionBar(Component.text(
                        bound[held].getDisplayName() + " — use " + hint, NamedTextColor.GRAY));
            }
            return;
        }

        event.setCancelled(true);
        plugin.getAbilityManager().activateAbility(player, held + 1);

        if (!plugin.getWorldGuard().isDisabled(player)) {
            player.sendActionBar(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(CharacterType.ampCode(
                            plugin.getDataManager().getCharacterManager().getCharacter(player).getColor())
                            + "&l" + bound[held].getDisplayName()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) return;
        if (plugin.getWorldGuard().isDisabled(player)) return;

        int held = player.getInventory().getHeldItemSlot();

        if (held == 4 || held == 5) {
            event.setCancelled(true);
            if (!plugin.getFlightManager().isFlying(player))
                plugin.getAbilityManager().activateAbility(player, held + 1);
            return;
        }

        if (held < 0 || held > 3) return;

        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
        if (bound[held] == null) return;

        event.setCancelled(true);
        if (plugin.getFlightManager().isFlying(player)) return;

        ActivationTrigger expected = plugin.getAbilityConfig().getActivationTrigger(bound[held]);
        if (expected != ActivationTrigger.LEFT_CLICK) return;

        plugin.getAbilityManager().activateAbility(player, held + 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (!plugin.getDataManager().getCharacterManager().hasCharacter(player)) return;
        if (plugin.getAbilityManager().isAbilityActive(player, AbilityType.GRAPPLE)) {
            plugin.getAbilityManager().setActiveAbility(player, AbilityType.GRAPPLE, false);
            player.sendActionBar(Component.text("Grapple broken.", NamedTextColor.GRAY));
        }
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (plugin.getWorldGuard().isDisabled(player)) return;

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null) return;

        int newSlot = event.getNewSlot();
        if (newSlot < 0 || newSlot > 5) return;

        AbilityType ability;
        if (newSlot == 4) ability = AbilityType.GENERIC_DASH;
        else if (newSlot == 5) ability = AbilityType.GRAPPLE;
        else {
            AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
            ability = bound[newSlot];
        }

        plugin.getAbilityManager().suppressSlotFlash(player, 1500);

        if (ability == null) {
            player.sendActionBar(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(CharacterType.ampCode(character.getColor())
                            + "[" + (newSlot + 1) + "] &8Empty"));
        } else {
            ActivationTrigger trigger = plugin.getAbilityConfig().getActivationTrigger(ability);
            String hint = switch (trigger) {
                case RIGHT_CLICK -> " &8[Right-click]";
                case LEFT_CLICK_SNEAK -> " &8[Sneak+Left]";
                case RIGHT_CLICK_SNEAK -> " &8[Sneak+Right]";
                default -> "";
            };
            player.sendActionBar(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(CharacterType.ampCode(character.getColor())
                            + "&l" + ability.getDisplayName() + hint));
        }
    }
}