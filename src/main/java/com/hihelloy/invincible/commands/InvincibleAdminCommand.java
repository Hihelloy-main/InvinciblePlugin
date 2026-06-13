package com.hihelloy.invincible.commands;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
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
                plugin.reloadPluginConfig();
                sender.sendMessage(msg("&aInvinciblePlugin configuration reloaded."));
            }
            case "info" -> {
                if (!sender.hasPermission("invincible.admin.info")) { adminNoPerm(sender); return true; }
                handlePlayerInfo(sender, args);
            }
            case "abilitycreategui" -> {
                if (!sender.hasPermission("invincible.admin.abilitycreategui")) { adminNoPerm(sender); return true; }
                if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
                plugin.getAbilityCreateGUI().open(player);
            }
            case "abilitylist" -> {
                if (!sender.hasPermission("invincible.admin.abilitycreategui")) { adminNoPerm(sender); return true; }
                if (!(sender instanceof Player player)) { sender.sendMessage("Player only."); return true; }
                plugin.getAbilityListGUI().open(player);
            }
            case "config" -> {
                if (!sender.hasPermission("invincible.admin.config")) { adminNoPerm(sender); return true; }
                handleConfig(sender, args);
            }
            default -> sendAdminHelp(sender);
        }
        return true;
    }

    private void handleGivePoints(CommandSender sender, String[] args) {
        if (args.length < 3) { sender.sendMessage(msg("&cUsage: /invadmin givepoints <player> <amount>")); return; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(msg("&cPlayer not found: " + args[1])); return; }
        int amount;
        try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException e) { sender.sendMessage(msg("&cInvalid amount.")); return; }
        if (amount <= 0) { sender.sendMessage(msg("&cAmount must be > 0.")); return; }
        plugin.getStatManager().giveStatPoints(target, amount);
        sender.sendMessage(msg("&aGave &f" + amount + "&a stat points to &f" + target.getName() + "&a."));
        target.sendMessage(msg("&6An admin gave you &f" + amount + "&6 stat points!"));
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage(msg("&cUsage: /invadmin reset <player>")); return; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(msg("&cPlayer not found: " + args[1])); return; }
        plugin.getDataManager().getCharacterManager().removeCharacter(target);
        plugin.getAbilityManager().clearPlayer(target);
        plugin.getDataManager().setStatPoints(target.getUniqueId(), 0);
        for (com.hihelloy.invincible.stats.StatType stat : com.hihelloy.invincible.stats.StatType.values()) {
            plugin.getDataManager().setStatLevel(target.getUniqueId(), stat, 0);
        }
        plugin.getFlightManager().stopFlight(target);
        plugin.getScoreboardManager().updateScoreboard(target);
        sender.sendMessage(msg("&aReset all data for &f" + target.getName() + "&a."));
        target.sendMessage(msg("&cYour Invincible data has been reset by an admin."));
    }

    private void handleSetChar(CommandSender sender, String[] args) {
        if (args.length < 3) { sender.sendMessage(msg("&cUsage: /invadmin setchar <player> <character>")); return; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(msg("&cPlayer not found: " + args[1])); return; }
        CharacterType character;
        try { character = CharacterType.valueOf(args[2].toUpperCase()); }
        catch (IllegalArgumentException e) { sender.sendMessage(msg("&cUnknown character: " + args[2])); return; }
        plugin.getDataManager().getCharacterManager().setCharacter(target, character);
        plugin.getAbilityManager().clearPlayer(target);
        plugin.getScoreboardManager().updateScoreboard(target);
        sender.sendMessage(msg("&aSet &f" + target.getName() + "&a to &f" + character.getDisplayName() + "&a."));
        target.sendMessage(msg(CharacterType.ampCode(character.getColor()) + "&lAn admin set your character to " + character.getDisplayName() + "!"));
    }

    private void handlePlayerInfo(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage(msg("&cUsage: /invadmin info <player>")); return; }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { sender.sendMessage(msg("&cPlayer not found: " + args[1])); return; }
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(target);
        int points = plugin.getStatManager().getStatPoints(target);
        sender.sendMessage("");
        sender.sendMessage(msg("&6━━━ Info for " + target.getName() + " ━━━"));
        sender.sendMessage(msg("&eCharacter: " + (character != null ? CharacterType.ampCode(character.getColor()) + character.getDisplayName() : "&cNone")));
        sender.sendMessage(msg("&eStat Points: &f" + points));
        sender.sendMessage(msg("&eFlying: " + (plugin.getFlightManager().isFlying(target) ? "&aYes" : "&cNo")));
        for (com.hihelloy.invincible.stats.StatType stat : com.hihelloy.invincible.stats.StatType.values()) {
            int level = plugin.getStatManager().getStatLevel(target, stat);
            if (level > 0) {
                sender.sendMessage(msg(CharacterType.ampCode(stat.getColor()) + "  " + stat.getDisplayName() + ": Lv." + level));
            }
        }
        sender.sendMessage("");
    }


    private void handleConfig(CommandSender sender, String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("changevalue")) {
            sender.sendMessage(msg("&cUsage: /invadmin config changevalue <path> <value>"));
            sender.sendMessage(msg("&7Example: /invadmin config changevalue abilities.ATOMIC_PUNCH.damage 17"));
            return;
        }
        String path = args[2];
        String rawValue = args[3];
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration diskConfig = YamlConfiguration.loadConfiguration(configFile);
        Object value;
        try { value = Integer.parseInt(rawValue); }
        catch (NumberFormatException e1) {
            try { value = Double.parseDouble(rawValue); }
            catch (NumberFormatException e2) {
                if (rawValue.equalsIgnoreCase("true")) value = Boolean.TRUE;
                else if (rawValue.equalsIgnoreCase("false")) value = Boolean.FALSE;
                else value = rawValue;
            }
        }
        diskConfig.set(path, value);
        try {
            diskConfig.save(configFile);
            plugin.reloadPluginConfig();
            sender.sendMessage(msg("&aSet &f" + path + "&a to &f" + rawValue + "&a and reloaded."));
        } catch (IOException e) {
            sender.sendMessage(msg("&cFailed to save: " + e.getMessage()));
        }
    }

    private void adminNoPerm(CommandSender sender) {
        sender.sendMessage(msg("&cYou don't have permission for that subcommand."));
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(msg("&6&l━━━ InvinciblePlugin Admin Commands ━━━"));
        sender.sendMessage(msg("&e/invadmin givepoints <player> <amount> &7- Give stat points"));
        sender.sendMessage(msg("&e/invadmin reset <player> &7- Reset all player data"));
        sender.sendMessage(msg("&e/invadmin setchar <player> <character> &7- Force-set character"));
        sender.sendMessage(msg("&e/invadmin info <player> &7- View player data"));
        sender.sendMessage(msg("&e/invadmin reload &7- Reload config"));
        sender.sendMessage(msg("&e/invadmin abilitycreategui &7- Open in-game ability creator"));
        sender.sendMessage(msg("&e/invadmin abilitylist &7- Browse, edit, and delete custom abilities"));
        sender.sendMessage(msg("&e/invadmin config changevalue <path> <value> &7- Edit config value"));
        sender.sendMessage("");
    }

    private static net.kyori.adventure.text.Component msg(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("invincible.admin.givepoints")) completions.add("givepoints");
            if (sender.hasPermission("invincible.admin.reset")) completions.add("reset");
            if (sender.hasPermission("invincible.admin.setchar")) completions.add("setchar");
            if (sender.hasPermission("invincible.admin.info")) completions.add("info");
            if (sender.hasPermission("invincible.admin.reload")) completions.add("reload");
            if (sender.hasPermission("invincible.admin.abilitycreategui")) {
                completions.add("abilitycreategui");
                completions.add("abilitylist");
            }
            if (sender.hasPermission("invincible.admin.config")) completions.add("config");
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "givepoints","reset","setchar","info" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
                }
                case "config" -> completions.add("changevalue");
            }
        } else if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "setchar" -> {
                    for (CharacterType type : CharacterType.values()) completions.add(type.name().toLowerCase());
                }
                case "givepoints" -> completions.addAll(Arrays.asList("5","10","25","50","100"));
                case "addability" -> {
                    for (AbilityType a : AbilityType.values()) completions.add(a.name().toLowerCase());
                }
                case "config" -> {
                    if (args[1].equalsIgnoreCase("changevalue")) {
                        addConfigPaths(args[2], completions);
                    }
                }
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("changevalue")) {
            completions.addAll(suggestConfigValue(args[2]));
        }
        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }

    private void addConfigPaths(String typedSoFar, List<String> out) {
        String[] LEAF_KEYS = {"damage","cooldown-ms","radius","range","target-radius",
            "knockback","velocity","duration-ticks","enabled","heal","death-message",
            "activation-trigger","permission","regen-ticks","regen-level","strength-level"};
        File cf = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration dc = YamlConfiguration.loadConfiguration(cf);
        if (typedSoFar.startsWith("abilities.")) {
            String after = typedSoFar.substring("abilities.".length());
            int dot = after.indexOf('.');
            if (dot == -1) {
                org.bukkit.configuration.ConfigurationSection sec = dc.getConfigurationSection("abilities");
                if (sec != null) {
                    for (String key : sec.getKeys(false)) {
                        String full = "abilities." + key;
                        if (full.startsWith(typedSoFar)) out.add(full + ".");
                    }
                }
            } else {
                String abilityKey = after.substring(0, dot);
                for (String leaf : LEAF_KEYS) {
                    String full = "abilities." + abilityKey + "." + leaf;
                    if (full.startsWith(typedSoFar)) out.add(full);
                }
            }
        } else if (typedSoFar.startsWith("settings.")) {
            org.bukkit.configuration.ConfigurationSection sec = dc.getConfigurationSection("settings");
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    String full = "settings." + key;
                    if (full.startsWith(typedSoFar)) out.add(full);
                }
            }
        } else if (typedSoFar.startsWith("stats.")) {
            org.bukkit.configuration.ConfigurationSection sec = dc.getConfigurationSection("stats");
            if (sec != null) {
                for (String key : sec.getKeys(false)) {
                    String full = "stats." + key;
                    if (full.startsWith(typedSoFar)) out.add(full);
                }
            }
        } else {
            out.addAll(Arrays.asList("abilities.","settings.","stats.","flight.","combat."));
        }
    }

    private List<String> suggestConfigValue(String path) {
        List<String> s = new ArrayList<>();
        String lower = path.toLowerCase();
        if (lower.contains("cooldown")) s.addAll(Arrays.asList("1000","3000","5000","8000","10000","15000","30000"));
        else if (lower.contains("damage")) s.addAll(Arrays.asList("1.0","3.0","5.0","8.0","10.0","15.0","20.0","25.0"));
        else if (lower.contains("radius") || lower.contains("range")) s.addAll(Arrays.asList("3.0","5.0","8.0","10.0","12.0","15.0"));
        else if (lower.contains("knockback") || lower.contains("velocity")) s.addAll(Arrays.asList("0.5","1.0","1.5","2.0","2.5","3.0"));
        else if (lower.contains("duration")) s.addAll(Arrays.asList("20","40","60","80","100","200","400"));
        else if (lower.contains("enabled")) s.addAll(Arrays.asList("true","false"));
        else if (lower.contains("heal")) s.addAll(Arrays.asList("2.0","4.0","6.0","8.0","10.0"));
        else s.addAll(Arrays.asList("true","false","1","5","10","0.5","1.0","2.0"));
        return s;
    }
}
