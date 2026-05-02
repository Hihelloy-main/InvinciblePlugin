package com.hihelloy.invincible.commands;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.characters.CharacterType;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvincibleAdminCommand implements CommandExecutor, TabCompleter {

    private final InvinciblePlugin plugin;

    public InvincibleAdminCommand(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "givepoints" -> {
                if (!sender.hasPermission("invincible.admin.givepoints")) { adminNoPerm(sender); return true; }
                handleGivePoints(sender, args);
            }
            case "reset" -> {
                if (!sender.hasPermission("invincible.admin.reset")) { adminNoPerm(sender); return true; }
                handleReset(sender, args);
            }
            case "setchar" -> {
                if (!sender.hasPermission("invincible.admin.setchar")) { adminNoPerm(sender); return true; }
                handleSetChar(sender, args);
            }
            case "reload" -> {
                if (!sender.hasPermission("invincible.admin.reload")) { adminNoPerm(sender); return true; }
                handleReload(sender);
            }
            case "info" -> {
                if (!sender.hasPermission("invincible.admin.info")) { adminNoPerm(sender); return true; }
                handlePlayerInfo(sender, args);
            }
            default -> sendAdminHelp(sender);
        }

        return true;
    }

    private void handleGivePoints(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Usage: /invincibleadmin givepoints <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Player not found: " + args[1]));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Invalid amount: " + args[2]));
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Amount must be greater than 0."));
            return;
        }

        plugin.getStatManager().giveStatPoints(target, amount);
        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&aGave " + amount + " stat points to " + target.getName() + "."));
        target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&6" + "An admin gave you " + amount + " stat points!"));
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Usage: /invincibleadmin reset <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Player not found: " + args[1]));
            return;
        }

        plugin.getDataManager().getCharacterManager().removeCharacter(target);
        plugin.getAbilityManager().clearPlayer(target);
        plugin.getDataManager().setStatPoints(target.getUniqueId(), 0);

        for (com.hihelloy.invincible.stats.StatType stat : com.hihelloy.invincible.stats.StatType.values()) {
            plugin.getDataManager().setStatLevel(target.getUniqueId(), stat, 0);
        }

        plugin.getFlightManager().stopFlight(target);
        plugin.getScoreboardManager().updateScoreboard(target);

        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&aReset all data for " + target.getName() + "."));
        target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Your Invincible data has been reset by an admin."));
    }

    private void handleSetChar(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Usage: /invincibleadmin setchar <player> <character>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Player not found: " + args[1]));
            return;
        }

        CharacterType character;
        try {
            character = CharacterType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Unknown character: " + args[2]));
            sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&7Valid characters: " + getCharacterNames()));
            return;
        }

        plugin.getDataManager().getCharacterManager().setCharacter(target, character);
        plugin.getAbilityManager().clearPlayer(target);
        plugin.getScoreboardManager().updateScoreboard(target);

        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&aSet " + target.getName() + "'s character to " + character.getDisplayName() + "."));
        target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(CharacterType.ampCode(character.getColor()) + "&lAn admin set your character to " + character.getDisplayName() + "!"));
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadPluginConfig();
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a" + "InvinciblePlugin configuration reloaded."));
    }

    private void handlePlayerInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Usage: /invincibleadmin info <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&c" + "Player not found: " + args[1]));
            return;
        }

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(target);
        int points = plugin.getStatManager().getStatPoints(target);

        sender.sendMessage("");
        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&6━━━ Info for " + target.getName() + " ━━━"));
        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&eCharacter: " + (character != null ? CharacterType.ampCode(character.getColor()) + character.getDisplayName() : "&cNone")));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "Stat Points: " + "&f" + points));
        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize("&eFlying: " + (plugin.getFlightManager().isFlying(target) ? "&aYes" : "&cNo")));

        for (com.hihelloy.invincible.stats.StatType stat : com.hihelloy.invincible.stats.StatType.values()) {
            int level = plugin.getStatManager().getStatLevel(target, stat);
            if (level > 0) {
                sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacyAmpersand().deserialize(CharacterType.ampCode(stat.getColor()) + "  " + stat.getDisplayName() + ": Lv." + level));
            }
        }
        sender.sendMessage("");
    }

    private String getCharacterNames() {
        StringBuilder sb = new StringBuilder();
        for (CharacterType type : CharacterType.values()) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(type.name());
        }
        return sb.toString();
    }

    private void adminNoPerm(CommandSender sender) {
        sender.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize("&cYou don't have permission for that subcommand."));
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&6" + "" + "&l" + "━━━ InvinciblePlugin Admin Commands ━━━"));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "/invadmin givepoints <player> <amount> " + "&7" + "- Give stat points"));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "/invadmin reset <player> " + "&7" + "- Reset all player data"));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "/invadmin setchar <player> <character> " + "&7" + "- Force-set character"));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "/invadmin info <player> " + "&7" + "- View player data"));
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&e" + "/invadmin reload " + "&7" + "- Reload config"));
        sender.sendMessage("");
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("givepoints", "reset", "setchar", "info", "reload"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "givepoints", "reset", "setchar", "info" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setchar")) {
            for (CharacterType type : CharacterType.values()) {
                completions.add(type.name().toLowerCase());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("givepoints")) {
            completions.addAll(Arrays.asList("5", "10", "25", "50", "100"));
        }

        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }
}