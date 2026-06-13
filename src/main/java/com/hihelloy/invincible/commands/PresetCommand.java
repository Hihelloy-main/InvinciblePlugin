package com.hihelloy.invincible.commands;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PresetCommand implements CommandExecutor, TabCompleter {

    private final InvinciblePlugin plugin;
    private final File presetsDir;

    public PresetCommand(InvinciblePlugin plugin) {
        this.plugin = plugin;
        this.presetsDir = new File(plugin.getDataFolder(), "presets");
        this.presetsDir.mkdirs();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Players only.", NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("invincible.preset")) {
            player.sendMessage(net.kyori.adventure.text.Component.text(
            "You don't have permission to use ability presets.",
            net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sendHelp(player); return true; }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /inv preset create <name>", NamedTextColor.RED));
                    return true;
                }
                String name = args[1].toLowerCase();
                if (name.equals("cancel") || name.length() > 32) {
                    player.sendMessage(Component.text("Invalid preset name.", NamedTextColor.RED));
                    return true;
                }
                File file = presetFile(player, name);
                if (file.exists()) {
                    player.sendMessage(Component.text("Preset '" + name + "' already exists.", NamedTextColor.RED));
                    return true;
                }
                AbilityType[] current = plugin.getAbilityManager().getBoundAbilities(player);
                savePreset(player, name, current);
                player.sendMessage(Component.text("Preset '" + name + "' saved.", NamedTextColor.GREEN));
                for (int i = 0; i < current.length; i++) {
                    player.sendMessage(Component.text("  Slot " + (i+1) + ": ", NamedTextColor.GRAY)
                    .append(Component.text(current[i] != null ? current[i].getDisplayName() : "Empty",
                    NamedTextColor.WHITE)));
                }
            }
            case "bind" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /inv preset bind <name>", NamedTextColor.RED));
                    return true;
                }
                String name = args[1].toLowerCase();
                AbilityType[] loaded = loadPreset(player, name);
                if (loaded == null) {
                    player.sendMessage(Component.text("No preset named '" + name + "'.", NamedTextColor.RED));
                    return true;
                }
                plugin.getAbilityManager().setBoundAbilities(player, loaded);
                plugin.getDataManager().saveBoundAbilities(player.getUniqueId(), loaded);
                plugin.getScoreboardManager().updateScoreboard(player);
                player.sendMessage(Component.text("Preset '" + name + "' applied.", NamedTextColor.GREEN));
            }
            case "delete" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Usage: /inv preset delete <name>", NamedTextColor.RED));
                    return true;
                }
                String name = args[1].toLowerCase();
                File file = presetFile(player, name);
                if (file.exists() && file.delete()) {
                    player.sendMessage(Component.text("Preset '" + name + "' deleted.", NamedTextColor.YELLOW));
                } else {
                    player.sendMessage(Component.text("No preset named '" + name + "'.", NamedTextColor.RED));
                }
            }
            case "list" -> {
                List<String> names = listPresets(player);
                if (names.isEmpty()) {
                    player.sendMessage(Component.text("You have no saved presets.", NamedTextColor.GRAY));
                    return true;
                }
                player.sendMessage(Component.text("Your presets:", NamedTextColor.GOLD));
                for (String n : names) {
                    AbilityType[] bound = loadPreset(player, n);
                    StringBuilder sb = new StringBuilder();
                    if (bound != null) {
                        for (int i = 0; i < bound.length; i++) {
                            if (i > 0) sb.append(", ");
                            sb.append(bound[i] != null ? bound[i].getDisplayName() : "Empty");
                        }
                    }
                    player.sendMessage(Component.text("  " + n + ": ", NamedTextColor.YELLOW)
                    .append(Component.text(sb.toString(), NamedTextColor.WHITE)));
                }
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private File presetFile(Player player, String name) {
        File playerDir = new File(presetsDir, player.getUniqueId().toString());
        playerDir.mkdirs();
        return new File(playerDir, name + ".yml");
    }

    private void savePreset(Player player, String name, AbilityType[] bound) {
        File file = presetFile(player, name);
        FileConfiguration cfg = new YamlConfiguration();
        List<String> keys = new ArrayList<>();
        for (AbilityType a : bound) keys.add(a != null ? a.name() : "NONE");
        cfg.set("abilities", keys);
        try {
            cfg.save(file); } catch (IOException e) {
            plugin.getLogger().warning("Failed to save preset " + name + ": " + e.getMessage());
        }
    }

    private AbilityType[] loadPreset(Player player, String name) {
        File file = presetFile(player, name);
        if (!file.exists()) return null;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> keys = cfg.getStringList("abilities");
        AbilityType[] bound = new AbilityType[4];
        for (int i = 0; i < Math.min(keys.size(), 4); i++) {
            String k = keys.get(i);
            if (!k.equals("NONE")) {
                try {
                    bound[i] = AbilityType.valueOf(k); }
                catch (IllegalArgumentException ignored) {}
            }
        }
        return bound;
    }

    private List<String> listPresets(Player player) {
        File playerDir = new File(presetsDir, player.getUniqueId().toString());
        if (!playerDir.exists()) return Collections.emptyList();
        File[] files = playerDir.listFiles((dir, n) -> n.endsWith(".yml"));
        if (files == null) return Collections.emptyList();
        List<String> names = new ArrayList<>();
        for (File f : files) names.add(f.getName().replace(".yml", ""));
        Collections.sort(names);
        return names;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("Ability Presets:", NamedTextColor.GOLD));
        player.sendMessage(Component.text("  /inv preset create <name>", NamedTextColor.YELLOW)
        .append(Component.text(" — save current bindings", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  /inv preset bind <name>  ", NamedTextColor.YELLOW)
        .append(Component.text(" — load a saved preset", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  /inv preset delete <name>", NamedTextColor.YELLOW)
        .append(Component.text(" — delete a preset", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("  /inv preset list          ", NamedTextColor.YELLOW)
        .append(Component.text(" — list all presets", NamedTextColor.GRAY)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd,
    String alias, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        if (args.length == 1)
        return List.of("create", "bind", "delete", "list");
        if (args.length == 2 && !args[0].equalsIgnoreCase("create"))
        return listPresets(player);
        return List.of();
    }
}