package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityCategory;
import com.hihelloy.invincible.abilities.ActivationTrigger;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.gui.AbilityCreateGUI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityCreateListener implements Listener {

    private final InvinciblePlugin plugin;
    private final AbilityCreateGUI gui;

    public AbilityCreateListener(InvinciblePlugin plugin, AbilityCreateGUI gui) {
        this.plugin = plugin;
        this.gui = gui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }

        String title = PlainTextComponentSerializer.plainText()
        .serialize(event.getView().title());

        boolean isCreatorGUI = title.contains(AbilityCreateGUI.TITLE)
        || title.contains(AbilityCreateGUI.FIELDS_TITLE)
        || title.contains(AbilityCreateGUI.CHARS_TITLE)
        || title.contains(AbilityCreateGUI.TRIGGER_TITLE)
        || title.contains(AbilityCreateGUI.EFFECTS_TITLE)
        || title.contains(AbilityCreateGUI.CATEGORY_TITLE);
        if (!isCreatorGUI) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String displayName = getName(clicked);

        if (title.contains(AbilityCreateGUI.TITLE)) {
            handleMainClick(player, clicked, displayName);
        } else if (title.contains(AbilityCreateGUI.TRIGGER_TITLE)) {
            handleTriggerClick(player, displayName);
        } else if (title.contains(AbilityCreateGUI.CATEGORY_TITLE)) {
            handleCategoryClick(player, displayName);
        } else if (title.contains(AbilityCreateGUI.CHARS_TITLE)) {
            handleCharsClick(player, clicked, displayName);
        } else if (title.contains(AbilityCreateGUI.FIELDS_TITLE)) {
            handleFieldsClick(player, clicked, displayName, event.isRightClick());
        } else if (title.contains(AbilityCreateGUI.EFFECTS_TITLE)) {
            handleEffectsClick(player, displayName);
        }
    }

    private void handleMainClick(Player player, ItemStack item, String name) {
        if (name.contains("Discard")) {
            gui.getDrafts().remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage(legacy("&7Draft discarded."));
            return;
        }
        if (name.contains("Save Ability")) {
            player.closeInventory();
            gui.saveAbility(player);
            return;
        }
        if (name.contains("Set Ability Key")) {
            requestInput(player, "key", "&eType the ability key (e.g. MY_ABILITY). No spaces.");
            return;
        }
        if (name.contains("Set Display Name")) {
            requestInput(player, "displayName", "&eType the display name (e.g. My Ability).");
            return;
        }
        if (name.contains("Set Cooldown")) {
            requestInput(player, "cooldownMs", "&eType cooldown in milliseconds (e.g. 8000 for 8 seconds).");
            return;
        }
        if (name.contains("Set Description")) {
            requestInput(player, "description", "&eType a short description of what this ability does.");
            return;
        }
        if (name.contains("Set Permission")) {
            requestInput(player, "permission", "&eType the permission node (e.g. invincible.ability.myability). Type 'none' to clear.");
            return;
        }
        if (name.contains("Set Activation Trigger")) {
            gui.openTriggerPicker(player);
            return;
        }
        if (name.contains("Set Category")) {
            gui.openCategoryPicker(player);
            return;
        }
        if (name.contains("Assign Characters")) {
            gui.openCharacterPicker(player);
            return;
        }
        if (name.contains("Edit Fields")) {
            gui.openFieldsEditor(player);
            return;
        }
        if (name.contains("Effects") || name.contains("Behaviour")) {
            gui.openEffectsPicker(player);
        }
    }

    private void handleTriggerClick(Player player, String name) {
        if (name.contains("Back")) {
            gui.open(player);
            return;
        }
        for (ActivationTrigger trigger : ActivationTrigger.values()) {
            if (name.contains(trigger.name())) {
                AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
                .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());
                draft.trigger = trigger;
                player.sendMessage(legacy("&aTrigger set to &f" + trigger.name() + "&a."));
                gui.open(player);
                return;
            }
        }
    }

    private void handleCategoryClick(Player player, String name) {
        if (name.contains("Back")) {
            gui.open(player);
            return;
        }
        for (AbilityCategory cat : AbilityCategory.values()) {
            if (name.contains(cat.getDisplayName())) {
                AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
                .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());
                draft.category = cat;
                player.sendMessage(legacy("&aCategory set to &f" + cat.getDisplayName() + "&a."));
                gui.open(player);
                return;
            }
        }
    }

    private void handleCharsClick(Player player, ItemStack item, String name) {
        if (name.contains("Back")) {
            gui.open(player);
            return;
        }
        String cleanName = name.replace("✔ ", "").trim();
        for (CharacterType ct : CharacterType.values()) {
            if (cleanName.contains(ct.getDisplayName())) {
                AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
                .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());
                if (draft.characters.contains(ct.name())) {
                    draft.characters.remove(ct.name());
                    player.sendMessage(legacy("&7Removed &f" + ct.getDisplayName() + "&7 from assigned characters."));
                } else {
                    draft.characters.add(ct.name());
                    player.sendMessage(legacy("&aAdded &f" + ct.getDisplayName() + "&a to assigned characters."));
                }
                gui.openCharacterPicker(player);
                return;
            }
        }
    }

    private void handleFieldsClick(Player player, ItemStack item, String name, boolean rightClick) {
        if (name.contains("Back")) {
            gui.open(player);
            return;
        }
        if (name.contains("Add Custom Field")) {
            requestInput(player, "__customfield__", "&eType: &f<fieldname> <value> &e(space-separated). E.g. &fmy-field 3.5");
            return;
        }

        AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
        .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());

        String fieldKey = name.trim().toLowerCase().replace(" ", "-");

        if (rightClick && draft.fields.containsKey(fieldKey)) {
            draft.fields.remove(fieldKey);
            player.sendMessage(legacy("&7Cleared field &f" + fieldKey + "&7."));
            gui.openFieldsEditor(player);
            return;
        }

        draft.pendingFieldKey = fieldKey;
        requestInput(player, "__fieldvalue__", "&eType a value for &f" + fieldKey + "&e (number or text).");
    }

    private void handleEffectsClick(Player player, String name) {
        if (name.contains("Back")) {
            gui.open(player);
            return;
        }
        String effectName = name.replace("✔ ", "").trim().toUpperCase().replace(" ", "_");
        AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
        .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());

        if (draft.effects.contains(effectName)) {
            draft.effects.remove(effectName);
            player.sendMessage(legacy("&7Effect &f" + effectName + "&7 disabled."));
        } else {
            draft.effects.add(effectName);
            player.sendMessage(legacy("&aEffect &f" + effectName + "&a enabled."));
        }
        gui.openEffectsPicker(player);
    }

    private void requestInput(Player player, String field, String prompt) {
        player.closeInventory();
        gui.getAwaitingInput().put(player.getUniqueId(), field);
        player.sendMessage(legacy(prompt));
        player.sendMessage(legacy("&7Type &ccancel&7 to go back."));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String field = gui.getAwaitingInput().get(player.getUniqueId());
        if (field == null) return;

        event.setCancelled(true);
        String input = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();

        if (input.equalsIgnoreCase("cancel")) {
            gui.getAwaitingInput().remove(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    gui.open(player);
                }
            }.runTask(plugin);
            return;
        }

        AbilityCreateGUI.AbilityDraft draft = gui.getDrafts()
        .computeIfAbsent(player.getUniqueId(), k -> new AbilityCreateGUI.AbilityDraft());

        final String finalField = field;
        gui.getAwaitingInput().remove(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (finalField) {
                    case "key" -> {
                        draft.key = input.toUpperCase().replace(" ", "_");
                        player.sendMessage(legacy("&aKey set to &f" + draft.key + "&a."));
                    }
                    case "displayName" -> {
                        draft.displayName = input;
                        player.sendMessage(legacy("&aDisplay name set to &f" + input + "&a."));
                    }
                    case "cooldownMs" -> {
                        try {
                            draft.cooldownMs = Integer.parseInt(input);
                            player.sendMessage(legacy("&aCooldown set to &f" + draft.cooldownMs + "ms&a."));
                        } catch (NumberFormatException e) {
                            player.sendMessage(legacy("&cInvalid number. Cooldown unchanged."));
                        }
                    }
                    case "description" -> {
                        draft.description = input;
                        player.sendMessage(legacy("&aDescription set."));
                    }
                    case "permission" -> {
                        draft.permission = input.equalsIgnoreCase("none") ? "" : input;
                        player.sendMessage(legacy("&aPermission set to &f" + draft.permission + "&a."));
                    }
                    case "__fieldvalue__" -> {
                        draft.fields.put(draft.pendingFieldKey, input);
                        player.sendMessage(legacy("&aField &f" + draft.pendingFieldKey + "&a set to &f" + input + "&a."));
                        gui.openFieldsEditor(player);
                        return;
                    }
                    case "__customfield__" -> {
                        String[] parts = input.split(" ", 2);
                        if (parts.length == 2) {
                            draft.fields.put(parts[0].toLowerCase(), parts[1]);
                            player.sendMessage(legacy("&aCustom field &f" + parts[0] + "&a set to &f" + parts[1] + "&a."));
                        } else {
                            player.sendMessage(legacy("&cExpected: <fieldname> <value>"));
                        }
                        gui.openFieldsEditor(player);
                        return;
                    }
                }
                gui.open(player);
            }
        }.runTask(plugin);
    }

    private String getName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.displayName() == null) return "";
        return PlainTextComponentSerializer.plainText().serialize(meta.displayName());
    }

    private static net.kyori.adventure.text.Component legacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }
}
