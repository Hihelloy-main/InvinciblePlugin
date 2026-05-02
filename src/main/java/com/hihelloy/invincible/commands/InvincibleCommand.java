package com.hihelloy.invincible.commands;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.gui.AbilityBindGUI;
import com.hihelloy.invincible.gui.CharacterSelectGUI;
import com.hihelloy.invincible.gui.CosmeticsGUI;
import com.hihelloy.invincible.gui.StatsGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvincibleCommand implements CommandExecutor, TabCompleter {

    private final InvinciblePlugin plugin;

    public InvincibleCommand(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    private static String chatColorAmp(org.bukkit.ChatColor cc) {
        return switch (cc) {
            case RED -> "&c";
            case DARK_RED -> "&4";
            case GREEN -> "&a";
            case DARK_GREEN -> "&2";
            case YELLOW -> "&e";
            case GOLD -> "&6";
            case AQUA -> "&b";
            case DARK_AQUA -> "&3";
            case BLUE -> "&9";
            case DARK_BLUE -> "&1";
            case WHITE -> "&f";
            case GRAY -> "&7";
            case DARK_GRAY -> "&8";
            case LIGHT_PURPLE -> "&d";
            case DARK_PURPLE -> "&5";
            default -> "&f";
        };
    }

    private static Component legacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("invincible.use")) {
            player.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0) { sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "select" -> {
                if (!player.hasPermission("invincible.select")) { noPermission(player); return true; }
                CharacterSelectGUI.open(player, plugin);
            }
            case "abilities", "ability" -> {
                if (!player.hasPermission("invincible.abilities")) { noPermission(player); return true; }
                AbilityBindGUI.open(player, plugin);
            }
            case "stats" -> {
                if (!player.hasPermission("invincible.stats")) { noPermission(player); return true; }
                StatsGUI.open(player, plugin);
            }
            case "cosmetics", "cosmetic" -> {
                if (!player.hasPermission("invincible.cosmetics")) { noPermission(player); return true; }
                CosmeticsGUI.open(player, plugin);
            }
            case "bind" -> {
                if (!player.hasPermission("invincible.abilities")) { noPermission(player); return true; }
                handleBind(player, args);
            }
            case "info" -> {
                if (!player.hasPermission("invincible.info")) { noPermission(player); return true; }
                handleInfo(player, args);
            }
            case "fly" -> {
                if (!player.hasPermission("invincible.fly")) { noPermission(player); return true; }
                handleFly(player);
            }
            case "character" -> {
                if (!player.hasPermission("invincible.character")) { noPermission(player); return true; }
                handleCharacterOverview(player);
            }
            case "preset" -> {
                if (!player.hasPermission("invincible.preset")) { noPermission(player); return true; }
                plugin.getPresetCommand().onCommand(player, command, label,
                        java.util.Arrays.copyOfRange(args, 1, args.length));
            }
            case "reload" -> {
                if (!player.hasPermission("invincible.admin.reload")) { noPermission(player); return true; }
                handleReload(player);
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Usage: /invincible info <ability name>", NamedTextColor.RED));
            CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
            if (character != null) {
                player.sendMessage(Component.text("Your abilities:", NamedTextColor.GRAY));
                for (String key : character.getAbilityKeys()) {
                    AbilityType a = AbilityType.fromName(key);
                    if (a != null) player.sendMessage(Component.text("  " + a.getDisplayName(), a.getColor()));
                }
            }
            return;
        }

        String raw = String.join("_", Arrays.copyOfRange(args, 1, args.length)).toUpperCase().replace("-","_");
        AbilityType ability = AbilityType.fromName(raw);
        if (ability == null) {
            player.sendMessage(Component.text("Unknown ability: " + args[1], NamedTextColor.RED));
            player.sendMessage(Component.text("Use /invincible info to list your abilities.", NamedTextColor.GRAY));
            return;
        }

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("━━━ " + ability.getDisplayName() + " ━━━", ability.getColor(), TextDecoration.BOLD));
        player.sendMessage(Component.text("Category: ", NamedTextColor.YELLOW)
                .append(Component.text(ability.getCategory().getDisplayName(), ability.getCategory().getColor())));

        if (ability.getCooldownMs() > 0) {
            player.sendMessage(Component.text("Cooldown: ", NamedTextColor.YELLOW)
                    .append(Component.text(ability.getCooldownMs() / 1000 + "s", NamedTextColor.WHITE)));

        } else {
            player.sendMessage(Component.text("Cooldown: Passive", NamedTextColor.YELLOW));
        }

        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Description:", NamedTextColor.AQUA));
        player.sendMessage(Component.text("  " + ability.getDescription(), NamedTextColor.WHITE));
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Activation:", NamedTextColor.AQUA));
        player.sendMessage(Component.text("  " + ability.getActivation(), NamedTextColor.WHITE));

        if (character != null) {
            player.sendMessage(Component.empty());
            AbilityType[] bound = plugin.getAbilityManager().getBoundAbilities(player);
            boolean found = false;
            for (int i = 0; i < bound.length; i++) {
                if (bound[i] == ability) {
                    player.sendMessage(Component.text("Bound to slot " + (i + 1) + ".", NamedTextColor.GREEN));
                    found = true; break;
                }
            }
            if (!found) {
                player.sendMessage(Component.text("Not bound. Use: ", NamedTextColor.GRAY)
                        .append(Component.text("/invincible bind " + ability.name().toLowerCase() + " <1-4>", NamedTextColor.YELLOW)));
            }
        }
        player.sendMessage(Component.empty());
    }

    private void handleBind(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(Component.text("Usage: /invincible bind <ability> <1-4>", NamedTextColor.RED));
            return;
        }
        String raw = args[1].toUpperCase().replace("-","_").replace(" ","_");
        AbilityType ability = AbilityType.fromName(raw);
        if (ability == null) {
            player.sendMessage(Component.text("Unknown ability: " + args[1], NamedTextColor.RED));
            player.sendMessage(Component.text("Use /invincible abilities to browse your moves.", NamedTextColor.GRAY));
            return;
        }
        int slot;
        try { slot = Integer.parseInt(args[2]); }
        catch (NumberFormatException e) {
            player.sendMessage(Component.text("Slot must be a number between 1 and 4.", NamedTextColor.RED));
            return;
        }
        if (slot < 1 || slot > 4) {
            player.sendMessage(Component.text("Slot must be between 1 and 4.", NamedTextColor.RED));
            return;
        }
        if (plugin.getAbilityManager().bindAbility(player, ability, slot)) {
            CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
            player.sendMessage(legacy(
                    (ch != null ? CharacterType.ampCode(ch.getColor()) : "&f")
                            + ability.getDisplayName() + "&a bound to slot " + slot + "!"
            ));
            plugin.getScoreboardManager().updateScoreboard(player);
        } else {
            player.sendMessage(Component.text("That ability is not available for your character.", NamedTextColor.RED));
            player.sendMessage(Component.text("Select your character first: /invincible select", NamedTextColor.GRAY));
        }
    }

    private void handleFly(Player player) {
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null || !character.canFly()) {
            player.sendMessage(Component.text("Your character does not have flight.", NamedTextColor.RED));
            return;
        }
        if (plugin.getFlightManager().isFlying(player)) {
            plugin.getFlightManager().stopFlight(player);
            player.sendMessage(Component.text("Flight deactivated.", NamedTextColor.AQUA));
        } else {
            plugin.getFlightManager().startFlight(player);
            player.sendMessage(Component.text("Flight activated! Double-jump to toggle.", NamedTextColor.AQUA));
        }
    }

    private void handleCharacterOverview(Player player) {
        CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (ch == null) {
            player.sendMessage(Component.text("No character selected. Use /invincible select", NamedTextColor.RED));
            return;
        }
        player.sendMessage(Component.empty());
        player.sendMessage(legacy(CharacterType.ampCode(ch.getColor()) + "&l━━━ " + ch.getDisplayName() + " ━━━"));
        player.sendMessage(Component.text("Flight: ", NamedTextColor.YELLOW)
                .append(ch.canFly() ? Component.text("Yes", NamedTextColor.GREEN) : Component.text("No", NamedTextColor.RED))
                .append(Component.text("  Strength: ", NamedTextColor.YELLOW))
                .append(ch.hasSuperStrength() ? Component.text("Yes", NamedTextColor.GREEN) : Component.text("No", NamedTextColor.RED))
                .append(Component.text("  Speed: ", NamedTextColor.YELLOW))
                .append(ch.hasSuperSpeed() ? Component.text("Yes", NamedTextColor.GREEN) : Component.text("No", NamedTextColor.RED)));
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Abilities ", NamedTextColor.AQUA)
                .append(Component.text("(use /invincible info <n> for details):", NamedTextColor.GRAY)));

        String[] keys = ch.getAbilityKeys();
        for (int i = 0; i < keys.length; i++) {
            AbilityType a = AbilityType.fromName(keys[i]);
            if (a != null) {
                player.sendMessage(Component.text("  " + (i+1) + ". " + a.getDisplayName(), a.getColor())
                        .append(Component.text(" [" + a.getCategory().getDisplayName() + "]", NamedTextColor.GRAY)));
            }
        }
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Stat Points: ", NamedTextColor.YELLOW)
                .append(Component.text(String.valueOf(plugin.getStatManager().getStatPoints(player)), NamedTextColor.WHITE)));
        player.sendMessage(Component.text("Upgrade with /invincible stats", NamedTextColor.GRAY));
        player.sendMessage(Component.empty());
    }

    private void handleReload(Player player) {
        plugin.reloadPluginConfig();
        player.sendMessage(Component.text("Config reloaded — ability attributes updated.", NamedTextColor.GREEN));
    }

    private void noPermission(Player player) {
        player.sendMessage(net.kyori.adventure.text.Component.text(
                "You don't have permission to use this command.",
                net.kyori.adventure.text.format.NamedTextColor.RED));
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("━━━ Invincible Commands ━━━", NamedTextColor.GOLD, TextDecoration.BOLD));
        player.sendMessage(Component.text("/invincible select", NamedTextColor.YELLOW).append(Component.text(" - Choose your character", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible character", NamedTextColor.YELLOW).append(Component.text(" - View your character and ability list", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible info <ability>", NamedTextColor.YELLOW).append(Component.text(" - Show ability details", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible abilities", NamedTextColor.YELLOW).append(Component.text(" - Open ability binding GUI", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible bind <ability> <1-4>", NamedTextColor.YELLOW).append(Component.text(" - Bind ability to a slot", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible stats", NamedTextColor.YELLOW).append(Component.text(" - Upgrade your stats", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible cosmetics", NamedTextColor.YELLOW).append(Component.text(" - Browse cosmetics", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible fly", NamedTextColor.YELLOW).append(Component.text(" - Toggle flight", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/invincible reload", NamedTextColor.YELLOW).append(Component.text(" - Reload config (admin)", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("Alias: /inv", NamedTextColor.GRAY));
        player.sendMessage(Component.empty());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("select","character","info","abilities","bind","stats","cosmetics","fly","reload"));
        } else if (args.length >= 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("bind")) {
                if (sender instanceof Player player) {
                    CharacterType ch = plugin.getDataManager().getCharacterManager().getCharacter(player);
                    if (ch != null) {
                        for (String key : ch.getAbilityKeys()) completions.add(key.toLowerCase());
                        try { for (String key : plugin.getAbilityManager().getCosmeticAbilityKeys(player)) completions.add(key.toLowerCase()); } catch (Exception ignored) {}
                    } else {
                        for (AbilityType a : AbilityType.values()) completions.add(a.name().toLowerCase());
                    }
                }
                if (sub.equals("bind") && args.length == 3) {
                    completions.clear();
                    completions.addAll(Arrays.asList("1","2","3","4"));
                }
            }
        }
        String typed = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.startsWith(typed));
        return completions;
    }
}