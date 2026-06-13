package com.hihelloy.invincible.commands;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.gui.AbilityBindGUI;
import com.hihelloy.invincible.gui.CharacterSelectGUI;
import com.hihelloy.invincible.gui.CosmeticsGUI;
import com.hihelloy.invincible.gui.ForgeShopGUI;
import com.hihelloy.invincible.gui.StatsGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvincibleCommand implements CommandExecutor, TabCompleter {

    private final InvinciblePlugin plugin;

    public static boolean boardtoggled = true;

    public InvincibleCommand(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    private void handleAddAbility(Player player, String[] args) {
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character != CharacterType.CUSTOM) {
            player.sendMessage(legacy("&cOnly custom hero players can add abilities. Select a custom hero first."));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(legacy("&cUsage: /inv addability <ability>"));
            return;
        }
        String abilityKey = args[1].toUpperCase();
        AbilityType ability = AbilityType.fromName(abilityKey);
        if (ability == null) {
            player.sendMessage(legacy("&cUnknown ability: " + args[1]));
            return;
        }
        List<String> abilities = plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId());
        if (abilities.contains(abilityKey)) {
            player.sendMessage(legacy("&cYou already have &f" + ability.getDisplayName() + "&c in your custom hero."));
            return;
        }
        abilities.add(abilityKey);
        plugin.getDataManager().saveCustomHeroAbilities(player.getUniqueId(), abilities);
        plugin.getDataManager().getCharacterManager().applyCustomHeroAbilities(player);
        player.sendMessage(legacy("&a" + ability.getDisplayName() + " &added to your custom hero!"));
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    private void handleWho(Player viewer, String targetName) {
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayer(targetName);
        if (target == null) {
            viewer.sendMessage(legacy("&cPlayer not found: " + targetName));
            return;
        }
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(target);
        String charDisplay = character != null
        ? CharacterType.ampCode(character.getColor()) + character.getDisplayName()
        : "&7None";
        String heroName = character == CharacterType.CUSTOM
        ? plugin.getDataManager().getCustomHeroName(target.getUniqueId())
        : (character != null ? character.getDisplayName() : "None");

        viewer.sendMessage(legacy("&6&l━━━ " + target.getName() + "'s Binds ━━━"));
        viewer.sendMessage(legacy("&eCharacter: " + charDisplay
        + (character == CharacterType.CUSTOM ? " &8(" + heroName + ")" : "")));
        viewer.sendMessage(legacy("&8▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(target);
        boolean anyBound = false;
        for (int i = 0; i < 4; i++) {
            AbilityType ability = bound[i];
            if (ability != null) {
                viewer.sendMessage(legacy("&e[" + (i + 1) + "] "
                + CharacterType.ampCode(ability.getColor()) + ability.getDisplayName()
                + " &8— " + CharacterType.ampCode(ability.getCategory().getColor())
                + ability.getCategory().getDisplayName()));
                anyBound = true;
            } else {
                viewer.sendMessage(legacy("&8[" + (i + 1) + "] &7Empty"));
            }
        }
        if (!anyBound) {
            viewer.sendMessage(legacy("&7No abilities bound."));
        }
    }


    private static Component legacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Player only.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "select", "character" -> {
                CharacterSelectGUI.open(player, plugin);
            }
            case "abilities", "ability" -> {
                int page = 0;
                if (args.length >= 2) {
                    try { page = Integer.parseInt(args[1]) - 1; } catch (NumberFormatException ignored) {}
                }
                AbilityBindGUI.open(player, plugin, page);
            }
            case "stats" -> {
                StatsGUI.open(player, plugin);
            }
            case "cosmetics", "cosmetic" -> {
                CosmeticsGUI.open(player, plugin);
            }
            case "shop", "forge" -> {
                ForgeShopGUI.open(player, plugin);
            }
            case "bind" -> {
                handleBind(player, args);
            }
            case "bindrandom" -> {
                if (!player.hasPermission("invincible.abilities")) {
                    noPermission(player);
                    return true;
                }
                handleBindRandom(player, args);
            }
            case "info" -> {
                handleInfo(player, args);
            }
            case "fly" -> {
                handleFly(player);
            }
            case "board" -> {
                handleBoard(player, args);
            }
            case "reload" -> {
                if (!player.hasPermission("invincible.admin.reload")) {
                    noPermission(player);
                    return true;
                }
                handleReload(player);
            }
            case "preset" -> {
                if (!player.hasPermission("invincible.preset")) {
                    noPermission(player);
                    return true;
                }
                plugin.getPresetCommand().onCommand(player, command, label,
                Arrays.copyOfRange(args, 1, args.length));
            }
            case "customchar", "custom" -> {
                if (!player.hasPermission("invincible.customchar")) {
                    noPermission(player);
                    return true;
                }
                com.hihelloy.invincible.gui.CustomCharGUI.openEditor(player, plugin);
            }
            case "helpbook" -> {
                handleHelpBook(player);
            }
            case "copy" -> {
                if (args.length < 2) {
                    player.sendMessage(legacy("&cUsage: /inv copy <player>"));
                    return true;
                }
                handleCopyBinds(player, args[1]);
            }
            case "addability" -> {
                handleAddAbility(player, args);
            }
            case "who" -> {
                if (args.length < 2) {
                    player.sendMessage(legacy("&cUsage: /inv who <player>"));
                    return true;
                }
                handleWho(player, args[1]);
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private void handleHelpBook(Player player) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        if (meta == null) return;
        meta.setTitle("Invincible Guide");
        meta.setAuthor("Server");
        List<Component> pages = new ArrayList<>();
        pages.add(legacy("&lWelcome to Invincible\n\nThis plugin lets you play as characters from the Invincible universe.\n\nEach character has unique abilities, stats, and flight."));
        pages.add(legacy("&lGetting Started\n\nRun /inv select to choose a character. Once selected, your abilities load and the scoreboard appears."));
        pages.add(legacy("&lBinding Abilities\n\nRun /inv abilities to open the ability menu. Hover slots 1-4 and press a number key to bind that ability. Four active at once."));
        pages.add(legacy("&lUsing Abilities\n\nEquip hotbar slot 1-4. Left-click fires LEFT_CLICK abilities. Sneak+click for SNEAK variants. Some fire on hitting an enemy."));
        pages.add(legacy("&lFlight\n\nFlying characters double-jump to take off. Left-click mid-air to toggle Hover or Glide mode."));
        pages.add(legacy("&lStats\n\nKilling players earns Stat Points. Use /inv stats to spend them.\n- Damage\n- Speed\n- Duration\n- Defense\n- Regeneration"));
        pages.add(legacy("&lCustom Hero\n\nWith the custom hero permission, run /inv customchar to design your own character. Pick abilities, toggle flight, and set a name."));
        pages.add(legacy("&lPresets\n\n/inv preset create <name> saves your current binds. /inv preset bind <name> restores them."));
        pages.add(legacy("&lRandom Binds\n\n/inv bindrandom fills all four slots randomly. Add 1-4 to randomise only that slot."));
        pages.add(legacy("&lGrapple & Aerial\n\nGrapple pulls a target toward you and deals tick damage. Aerial Grab and Aerial Slam require flight."));
        meta.pages(pages);
        book.setItemMeta(meta);
        player.getInventory().addItem(book);
        player.sendMessage(legacy("&6You've received the &lInvincible Guide&r&6 book."));
    }

    private void handleCopyBinds(Player player, String targetName) {
        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayer(targetName);
        if (target == null || target == player) {
            player.sendMessage(legacy("&cPlayer not found or invalid."));
            return;
        }
        CharacterType myChar = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (myChar == null) {
            player.sendMessage(legacy("&cSelect a character first."));
            return;
        }
        AbilityType[] theirBinds = plugin.getAbilityManager().getBoundAbilities(target);
        List<String> myPool = new ArrayList<>();
        if (myChar == CharacterType.CUSTOM) {
            myPool.addAll(plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId()));
        } else {
            myPool.addAll(Arrays.asList(myChar.getAbilityKeys()));
        }
        myPool.addAll(plugin.getAbilityManager().getCosmeticAbilityKeys(player));
        int copied = 0, skipped = 0;
        for (int i = 0; i < 4; i++) {
            AbilityType ability = theirBinds[i];
            if (ability == null) continue;
            if (myPool.contains(ability.name())) {
                plugin.getAbilityManager().bindAbility(player, ability, i + 1);
                copied++;
            } else {
                skipped++;
            }
        }
        plugin.getScoreboardManager().updateScoreboard(player);
        player.sendMessage(legacy("&aCopied &f" + copied + "&a bind(s) from &f" + target.getName()
        + (skipped > 0 ? " &7(" + skipped + " skipped)" : "") + "&a."));
    }

    private void handleBind(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(legacy("&cUsage: /inv bind <ability> <slot 1-4>"));
            return;
        }
        AbilityType ability = AbilityType.fromName(args[1]);
        if (ability == null) {
            player.sendMessage(legacy("&cUnknown ability: " + args[1]));
            return;
        }
        int slot;
        try {
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(legacy("&cSlot must be 1-4."));
            return;
        }
        if (slot < 1 || slot > 4) {
            player.sendMessage(legacy("&cSlot must be 1-4."));
            return;
        }
        if (plugin.getAbilityManager().bindAbility(player, ability, slot)) {
            CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
            String color = ch != null ? CharacterType.ampCode(ch.getColor()) : "&f";
            player.sendMessage(legacy(color + ability.getDisplayName() + "&a bound to slot " + slot + "."));
            plugin.getScoreboardManager().updateScoreboard(player);
        } else {
            player.sendMessage(legacy("&cYou don't have access to that ability."));
        }
    }

    private void handleBindRandom(Player player, String[] args) {
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null) {
            player.sendMessage(legacy("&cSelect a character first with /inv select"));
            return;
        }
        List<AbilityType> pool = new ArrayList<>();
        if (character == CharacterType.CUSTOM) {
            for (String key : plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId())) {
                AbilityType a = AbilityType.fromName(key);
                if (a != null) pool.add(a);
            }
        } else {
            for (String key : character.getAbilityKeys()) {
                AbilityType a = AbilityType.fromName(key);
                if (a != null) pool.add(a);
            }
        }
        for (String key : plugin.getAbilityManager().getCosmeticAbilityKeys(player)) {
            AbilityType a = AbilityType.fromName(key);
            if (a != null && !pool.contains(a)) pool.add(a);
        }
        if (pool.isEmpty()) {
            player.sendMessage(legacy("&cNo abilities available to bind."));
            return;
        }
        java.util.Collections.shuffle(pool);
        if (args.length >= 2) {
            int slot;
            try {
                slot = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(legacy("&cUsage: /inv bindrandom [1-4]"));
                return;
            }
            if (slot < 1 || slot > 4) {
                player.sendMessage(legacy("&cSlot must be between 1 and 4."));
                return;
            }
            AbilityType pick = pool.get(0);
            plugin.getAbilityManager().bindAbility(player, pick, slot);
            player.sendMessage(legacy(CharacterType.ampCode(character.getColor())
            + pick.getDisplayName() + "&a randomly bound to slot " + slot + "."));
        } else {
            int bound = 0;
            for (int slot = 1; slot <= 4 && !pool.isEmpty(); slot++) {
                AbilityType pick = pool.remove(0);
                plugin.getAbilityManager().bindAbility(player, pick, slot);
                bound++;
            }
            player.sendMessage(legacy(CharacterType.ampCode(character.getColor())
            + "Randomly bound " + bound + " abilities."));
        }
        plugin.getScoreboardManager().updateScoreboard(player);
        AbilityBindGUI.open(player, plugin);
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(legacy("&cUsage: /inv info <ability>"));
            return;
        }
        AbilityType ability = AbilityType.fromName(args[1]);
        if (ability == null) {
            player.sendMessage(legacy("&cUnknown ability: " + args[1]));
            return;
        }
        player.sendMessage(legacy(CharacterType.ampCode(ability.getColor()) + "&l" + ability.getDisplayName()));
        player.sendMessage(legacy("&7" + ability.getDescription()));
        player.sendMessage(legacy("&eCategory: " + CharacterType.ampCode(ability.getCategory().getColor())
        + ability.getCategory().getDisplayName()));
        long cd = plugin.getAbilityConfig().getCooldown(ability);
        player.sendMessage(legacy("&eCooldown: &f" + (cd > 0 ? cd / 1000 + "s" : "Passive")));
        player.sendMessage(legacy("&eActivation: &f" + plugin.getAbilityConfig().getActivationTrigger(ability).name()));
    }

    private void handleFly(Player player) {
        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (ch == null || !ch.canFly()) {
            player.sendMessage(legacy("&cYour character cannot fly."));
            return;
        }
        if (plugin.getFlightManager().isFlying(player)) {
            plugin.getFlightManager().stopFlight(player);
            player.sendMessage(legacy("&7Flight stopped."));
        } else {
            plugin.getFlightManager().startFlight(player);
            player.sendMessage(legacy("&bFlight started. Double-jump to toggle."));
        }
    }

    private void handleBoard(Player player, String[] args) {
        if (args.length < 2) {
            boardtoggled = !boardtoggled;
        } else {
            boardtoggled = Boolean.parseBoolean(args[1]);
        }
        if (boardtoggled) {
            plugin.getScoreboardManager().updateScoreboard(player);
            player.sendMessage(legacy("&aScoreboard enabled."));
        } else {
            plugin.getScoreboardManager().removeScoreboard(player);
            player.sendMessage(legacy("&7Scoreboard disabled."));
        }
    }

    private void handleReload(Player player) {
        plugin.reloadPluginConfig();
        player.sendMessage(legacy("&aConfiguration reloaded."));
    }

    private void noPermission(Player player) {
        player.sendMessage(legacy("&cYou don't have permission for that."));
    }

    private void sendHelp(Player player) {
        player.sendMessage(legacy("&6&l━━━ /inv commands ━━━"));
        player.sendMessage(legacy("&e/inv select &7- Choose your character"));
        player.sendMessage(legacy("&e/inv abilities &7- Bind abilities"));
        player.sendMessage(legacy("&e/inv stats &7- Spend stat points"));
        player.sendMessage(legacy("&e/inv cosmetics &7- Equip cosmetics"));
        player.sendMessage(legacy("&e/inv fly &7- Toggle flight"));
        player.sendMessage(legacy("&e/inv board [true/false] &7- Toggle scoreboard"));
        player.sendMessage(legacy("&e/inv preset <create|bind|list|delete> <name> &7- Ability presets"));
        player.sendMessage(legacy("&e/inv bindrandom [1-4] &7- Random ability bind"));
        player.sendMessage(legacy("&e/inv copy <player> &7- Copy another player's binds"));
        player.sendMessage(legacy("&e/inv customchar &7- Edit custom hero"));
        player.sendMessage(legacy("&e/inv helpbook &7- Receive the guide book"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player player)) return completions;

        if (args.length == 1) {
            completions.addAll(java.util.Arrays.asList(
            "select","character","info","abilities","bind","bindrandom",
            "stats","cosmetics","fly","board","helpbook","copy","shop","who"));
            if (player.hasPermission("invincible.admin.reload")) completions.add("reload");
            if (player.hasPermission("invincible.customchar")) {
                completions.add("customchar");
                completions.add("addability");
            }
            if (player.hasPermission("invincible.preset")) completions.add("preset");
        } else if (args.length >= 2) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "preset" -> {
                    return plugin.getPresetCommand().onTabComplete(sender, command, label,
                    Arrays.copyOfRange(args, 1, args.length));
                }
                case "copy" -> {
                    if (args.length == 2) {
                        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                            completions.add(p.getName());
                        }
                    }
                }
                case "info", "bind" -> {
                    if (args.length == 2) {
                        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
                        if (ch != null) {
                            for (String key : ch.getAbilityKeys()) completions.add(key.toLowerCase());
                        } else {
                            for (AbilityType a : AbilityType.values()) completions.add(a.name().toLowerCase());
                        }
                    }
                    if (sub.equals("bind") && args.length == 3) {
                        completions.addAll(Arrays.asList("1","2","3","4"));
                    }
                }
                case "board" -> {
                    if (args.length == 2) completions.addAll(Arrays.asList("true","false"));
                }
                case "bindrandom" -> {
                    if (args.length == 2) completions.addAll(Arrays.asList("1","2","3","4"));
                }
                case "addability" -> {
                    if (args.length == 2) {
                        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
                        if (ch == CharacterType.CUSTOM) {
                            for (AbilityType a : AbilityType.values()) completions.add(a.name().toLowerCase());
                        }
                    }
                }
                case "who" -> {
                    if (args.length == 2) {
                        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                            completions.add(p.getName());
                        }
                    }
                }
            }
        }

        String typed = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(typed));
        return completions;
    }
}
