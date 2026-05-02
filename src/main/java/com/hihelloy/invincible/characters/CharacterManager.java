package com.hihelloy.invincible.characters;

import com.hihelloy.invincible.InvinciblePlugin;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CharacterManager {

    private final InvinciblePlugin plugin;
    private final Map<UUID, CharacterType> playerCharacters = new HashMap<>();

    public CharacterManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        startPassiveTask();
    }

    public void setCharacter(Player player, CharacterType type) {
        CharacterType old = getCharacter(player);
        var event = new com.hihelloy.invincible.api.events.CharacterSelectEvent(player, old, type);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        removeCharacterEffects(player);
        plugin.getAbilityManager().clearPlayer(player);
        plugin.getDataManager().saveBoundAbilities(player.getUniqueId(),
                new com.hihelloy.invincible.abilities.AbilityType[4]);

        playerCharacters.put(player.getUniqueId(), type);
        plugin.getDataManager().setCharacter(player.getUniqueId(), type);
        applyCharacterEffects(player, type);
    }

    public CharacterType getCharacter(Player player) {
        if (playerCharacters.containsKey(player.getUniqueId())) {
            return playerCharacters.get(player.getUniqueId());
        }
        CharacterType loaded = plugin.getDataManager().getCharacter(player.getUniqueId());
        if (loaded != null) {
            playerCharacters.put(player.getUniqueId(), loaded);
        }
        return loaded;
    }

    public boolean hasCharacter(Player player) {
        return getCharacter(player) != null;
    }

    public void removeCharacter(Player player) {
        removeCharacterEffects(player);
        plugin.getAbilityManager().clearPlayer(player);
        playerCharacters.remove(player.getUniqueId());
    }

    private void applyCharacterEffects(Player player, CharacterType type) {
        if (type.canFly()) {
            player.setAllowFlight(true);
            plugin.getFlightManager().applyFlightSpeed(player);
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.getFlightManager().stopFlight(player);
        }
        plugin.getScoreboardManager().updateScoreboard(player);
    }

    private void removeCharacterEffects(Player player) {
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFlySpeed(0.1f);
        player.setWalkSpeed(0.2f);
        plugin.getFlightManager().stopFlight(player);
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.SPEED);
    }

    private void startPassiveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    CharacterType type = getCharacter(player);
                    if (type == null) continue;

                    double speedMult = plugin.getStatManager().getSpeedMultiplier(player);
                    double strengthMult = plugin.getStatManager().getDamageMultiplier(player);

                    if (type.hasSuperStrength()) {
                        int amp = (int) Math.round((strengthMult - 1.0) / 0.15) + 2;
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.STRENGTH, 35, Math.max(0, amp), true, false, false));
                    }

                    if (type.hasSuperSpeed()) {
                        int amp = (int) Math.round((speedMult - 1.0) / 0.1) + 1;
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SPEED, 35, Math.max(0, amp), true, false, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 25L);
    }

    public void loadPlayer(Player player) {
        CharacterType type = plugin.getDataManager().getCharacter(player.getUniqueId());
        if (type != null) {
            playerCharacters.put(player.getUniqueId(), type);
            applyCharacterEffects(player, type);
        }
    }

    public void unloadPlayer(Player player) {
        playerCharacters.remove(player.getUniqueId());
    }
}