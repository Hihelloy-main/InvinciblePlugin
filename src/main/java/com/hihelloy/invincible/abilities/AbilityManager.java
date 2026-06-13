package com.hihelloy.invincible.abilities;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.characters.CharacterType;
import com.hihelloy.invincible.cosmetics.CosmeticType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AbilityManager {

    private static final int BAR_LENGTH = 8;
    private static final char BAR_CHAR = '|';

    private final InvinciblePlugin plugin;
    private final Map<UUID, AbilityType[]> boundAbilities = new HashMap<>();
    private final Map<UUID, String[]> boundConfigKeys = new HashMap<>();
    private final Map<UUID, Map<AbilityType, Long>> cooldownEnds = new HashMap<>();
    private final Map<UUID, Map<AbilityType, Long>> cooldownTotals = new HashMap<>();
    private final Map<UUID, Map<String, Long>> configCooldowns = new HashMap<>();
    private final Map<UUID, Set<AbilityType>> activeAbilities = new HashMap<>();
    private final Map<UUID, Long> slotFlashExpiry = new HashMap<>();
    private final Map<UUID, Set<String>> cosmeticAbilityKeys = new HashMap<>();
    private final Map<UUID, Double> kineticDamageStore = new HashMap<>();
    private final Map<UUID, AbilityType> lastAbilityHit = new HashMap<>();

    public AbilityManager(InvinciblePlugin plugin) {
        this.plugin = plugin;
        startActionBarTask();
    }

    public boolean bindAbilityByKey(Player player, String abilityKey, int slot) {
        AbilityType ability = AbilityType.fromName(abilityKey);
        if (ability == null) return false;
        if (!bindAbilityInternal(player, ability, slot)) return false;
        if (ability == AbilityType.CONFIG_ABILITY) {
            String[] keys = boundConfigKeys.computeIfAbsent(player.getUniqueId(), k -> new String[4]);
            keys[slot - 1] = abilityKey.toUpperCase();
        }
        return true;
    }

    public boolean bindAbility(Player player, AbilityType ability, int slot) {
        return bindAbilityInternal(player, ability, slot);
    }

    private boolean bindAbilityInternal(Player player, AbilityType ability, int slot) {
        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        if (character == null) return false;

        boolean owned = false;

        if (character == CharacterType.CUSTOM) {
            List<String> customAbilities = plugin.getDataManager().getCustomHeroAbilities(player.getUniqueId());
            owned = customAbilities.contains(ability.name());
        } else {
            for (String key : character.getAbilityKeys()) {
                if (key.equalsIgnoreCase(ability.name())) {
                    owned = true;
                    break;
                }
            }
        }

        if (!owned) {
            java.util.List<String> allowedChars = plugin.getAbilityConfig()
                    .getAllowedCharacters(ability);
            if (!allowedChars.isEmpty()) {
                if (allowedChars.contains(character.name())) {
                    owned = true;
                }
                if (!owned && character == CharacterType.CUSTOM) {
                    owned = true;
                }
            }
        }

        if (!owned) {
            Set<String> cosmetic = cosmeticAbilityKeys.get(player.getUniqueId());
            if (cosmetic != null && cosmetic.contains(ability.name())) {
                owned = true;
            }
        }

        if (!owned) return false;

        AbilityType[] bound = boundAbilities.computeIfAbsent(player.getUniqueId(), k -> new AbilityType[4]);
        bound[slot - 1] = ability;
        plugin.getDataManager().saveBoundAbilities(player.getUniqueId(), bound);
        return true;
    }

    public void setBoundAbilities(Player player, AbilityType[] bound) {
        boundAbilities.put(player.getUniqueId(), bound);
    }

    public AbilityType[] getBoundAbilities(Player player) {
        UUID id = player.getUniqueId();
        if (!boundAbilities.containsKey(id)) {
            AbilityType[] loaded = plugin.getDataManager().loadBoundAbilities(id);
            boundAbilities.put(id, loaded != null ? loaded : new AbilityType[4]);
        }
        return boundAbilities.get(id);
    }

    public boolean activateAbility(Player player, int slot) {
        AbilityType ability;

        if (slot == 5) {
            ability = AbilityType.GENERIC_DASH;
        } else if (slot == 6) {
            ability = AbilityType.GRAPPLE;
        } else {
            AbilityType[] bound = getBoundAbilities(player);
            if (slot < 1 || slot > 4) return false;
            ability = bound[slot - 1];
        }

        if (ability == null) return false;
        if (!plugin.getAbilityConfig().isEnabled(ability)) return false;
        if (isOnCooldown(player, ability)) return false;
        if (ability == AbilityType.CONFIG_ABILITY) {
            String configKey = getBoundConfigKey(player, slot);
            if (configKey != null && isConfigAbilityOnCooldown(player, configKey)) return false;
        }

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        var event = new com.hihelloy.invincible.api.events.AbilityActivateEvent(
                player, ability, character, slot);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (ability == AbilityType.CONFIG_ABILITY) {
            String configKey = getBoundConfigKey(player, slot);
            if (configKey != null) {
                plugin.getAbilityConfig().activeConfigKey.set(configKey);
                AbilityExecutor.execute(player, ability, plugin, event.getDamage(), event.getDuration(), event.getRadius());
                plugin.getAbilityConfig().activeConfigKey.remove();
                AbilityType.ConfigAbilityMeta meta = AbilityType.getConfigMeta(configKey);
                long cd = meta != null ? meta.cooldownMs : 5000L;
                setRawCooldown(player, ability, configKey, cd);
            }
        } else {
            AbilityExecutor.execute(player, ability, plugin, event.getDamage(), event.getDuration(), event.getRadius());
            plugin.getAbilityConfig().activeConfigKey.remove();
            setCooldown(player, ability, event.getCooldownMs());
        }
        return true;
    }

    public boolean isOnCooldown(Player player, AbilityType ability) {
        Map<AbilityType, Long> ends = cooldownEnds.get(player.getUniqueId());
        if (ends == null) return false;
        Long end = ends.get(ability);
        return end != null && System.currentTimeMillis() < end;
    }

    public long getRemainingCooldown(Player player, AbilityType ability) {
        Map<AbilityType, Long> ends = cooldownEnds.get(player.getUniqueId());
        if (ends == null) return 0;
        Long end = ends.get(ability);
        if (end == null) return 0;
        return Math.max(0, end - System.currentTimeMillis());
    }

    private long getTotalCooldown(Player player, AbilityType ability) {
        Map<AbilityType, Long> totals = cooldownTotals.get(player.getUniqueId());
        if (totals == null) return ability.getCooldownMs();
        return totals.getOrDefault(ability, ability.getCooldownMs());
    }

    private void setCooldown(Player player, AbilityType ability, int eventCooldownMs) {
        long cd = eventCooldownMs > 0 ? (long) eventCooldownMs : plugin.getAbilityConfig().getCooldown(ability);
        UUID id = player.getUniqueId();
        cooldownEnds.computeIfAbsent(id, k -> new HashMap<>()).put(ability, System.currentTimeMillis() + cd);
        cooldownTotals.computeIfAbsent(id, k -> new HashMap<>()).put(ability, cd);
    }

    private void setCooldown(Player player, AbilityType ability) {
        long cd = plugin.getAbilityConfig().getCooldown(ability);
        UUID id = player.getUniqueId();
        cooldownEnds.computeIfAbsent(id, k -> new HashMap<>()).put(ability, System.currentTimeMillis() + cd);
        cooldownTotals.computeIfAbsent(id, k -> new HashMap<>()).put(ability, cd);
    }

    public void suppressSlotFlash(Player player, long durationMs) {
        slotFlashExpiry.put(player.getUniqueId(), System.currentTimeMillis() + durationMs);
    }

    public boolean isSlotFlashActive(Player player) {
        Long expiry = slotFlashExpiry.get(player.getUniqueId());
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    public void setActiveAbility(Player player, AbilityType ability, boolean active) {
        Set<AbilityType> set = activeAbilities.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (active) {
            set.add(ability);
        } else {
            if (set.remove(ability)) {
                org.bukkit.Bukkit.getPluginManager().callEvent(
                        new com.hihelloy.invincible.api.events.AbilityEndEvent(
                                player, ability,
                                com.hihelloy.invincible.api.events.AbilityEndEvent.EndReason.DURATION_EXPIRED));
            }
        }
    }

    public boolean isAbilityActive(Player player, AbilityType ability) {
        Set<AbilityType> set = activeAbilities.get(player.getUniqueId());
        return set != null && set.contains(ability);
    }

    public void addCosmeticAbilities(Player player, CosmeticType cosmetic) {
        Set<String> keys = cosmeticAbilityKeys.computeIfAbsent(
                player.getUniqueId(), k -> new HashSet<>());
        for (String key : cosmetic.getAbilityKeys()) {
            keys.add(key.toUpperCase());
        }
    }

    public void removeCosmeticAbilities(Player player, CosmeticType cosmetic) {
        Set<String> keys = cosmeticAbilityKeys.get(player.getUniqueId());
        if (keys == null) return;
        for (String key : cosmetic.getAbilityKeys()) {
            keys.remove(key.toUpperCase());
        }
        AbilityType[] bound = getBoundAbilities(player);
        for (int i = 0; i < bound.length; i++) {
            if (bound[i] != null) {
                boolean stillValid = false;
                CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
                if (character != null) {
                    for (String ck : character.getAbilityKeys()) {
                        if (ck.equalsIgnoreCase(bound[i].name())) {
                            stillValid = true; break; }
                    }
                }
                if (!stillValid && !keys.contains(bound[i].name())) {
                    bound[i] = null;
                }
            }
        }
        plugin.getDataManager().saveBoundAbilities(player.getUniqueId(), bound);
    }

    public String getBoundConfigKey(Player player, int slot) {
        String[] keys = boundConfigKeys.get(player.getUniqueId());
        if (keys == null || slot < 1 || slot > 4) return null;
        return keys[slot - 1];
    }

    public void setBoundConfigKey(Player player, int slot, String key) {
        String[] keys = boundConfigKeys.computeIfAbsent(player.getUniqueId(), k -> new String[4]);
        if (slot >= 1 && slot <= 4) keys[slot - 1] = key;
    }

    public String[] getBoundConfigKeys(Player player) {
        return boundConfigKeys.getOrDefault(player.getUniqueId(), new String[4]);
    }

    public Set<String> getCosmeticAbilityKeys(Player player) {
        return cosmeticAbilityKeys.getOrDefault(player.getUniqueId(), new HashSet<>());
    }

    public void clearPlayer(Player player) {
        UUID id = player.getUniqueId();
        boundAbilities.remove(id);
        cooldownEnds.remove(id);
        cooldownTotals.remove(id);
        activeAbilities.remove(id);
        slotFlashExpiry.remove(id);
        cosmeticAbilityKeys.remove(id);
    }

    public void loadPlayer(Player player) {
        AbilityType[] loaded = plugin.getDataManager().loadBoundAbilities(player.getUniqueId());
        AbilityType[] validated = loaded != null ? loaded : new AbilityType[4];

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
        Set<String> cosmeticKeys = cosmeticAbilityKeys.getOrDefault(player.getUniqueId(), new HashSet<>());

        for (int i = 0; i < validated.length; i++) {
            if (validated[i] == null) continue;
            boolean valid = false;

            if (character != null) {
                for (String key : character.getAbilityKeys()) {
                    if (key.equalsIgnoreCase(validated[i].name())) {
                        valid = true; break; }
                }
            }

            if (!valid && cosmeticKeys.contains(validated[i].name())) {
                valid = true;
            }
            if (!valid) validated[i] = null;
        }

        boundAbilities.put(player.getUniqueId(), validated);
    }

    private void startActionBarTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(player);
                    if (character == null) continue;
                    if (isSlotFlashActive(player)) continue;
                    sendPersistentBar(player, character);
                }
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private void sendPersistentBar(Player player, CharacterType character) {
        AbilityType[] bound = getBoundAbilities(player);
        int held = player.getInventory().getHeldItemSlot();
        String charColor = CharacterType.ampCode(character.getColor());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            if (i > 0) sb.append(" &8| ");
            AbilityType ability = bound[i];
            boolean isHeld = (i == held);

            if (ability == null) {
                sb.append(isHeld ? "&7" : "&8").append("[").append(i + 1).append("]");
                continue;
            }

            if (isAbilityActive(player, ability)) {
                sb.append("&a&l").append(ability.getDisplayName());
                sb.append(" &a[");
                for (int b = 0; b < BAR_LENGTH; b++) sb.append(BAR_CHAR);
                sb.append("]");

            } else if (isOnCooldown(player, ability)) {
                long remaining = getRemainingCooldown(player, ability);
                long total = getTotalCooldown(player, ability);
                double progress = 1.0 - ((double) remaining / total);
                int filled = (int) Math.round(progress * BAR_LENGTH);

                sb.append(isHeld ? "&f" : "&7").append(ability.getDisplayName());
                sb.append(" &8[");
                for (int b = 0; b < BAR_LENGTH; b++) {
                    sb.append(b < filled ? charColor : "&8").append(BAR_CHAR);
                }
                sb.append("&8] &7").append(remaining / 1000).append("s");

            } else {
                sb.append(isHeld ? charColor + "&l" : "&7").append(ability.getDisplayName());
                if (isHeld) {
                    sb.append(" &8[");
                    for (int b = 0; b < BAR_LENGTH; b++) sb.append(charColor).append(BAR_CHAR);
                    sb.append("&8]");
                }
            }
        }

        player.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(sb.toString()));
    }

    public double getStoredKineticDamage(Player player) {
        return kineticDamageStore.getOrDefault(player.getUniqueId(), 0.0);
    }

    public void addStoredKineticDamage(Player player, double amount) {
        kineticDamageStore.merge(player.getUniqueId(), amount, Double::sum);
    }

    public void clearStoredKineticDamage(Player player) {
        kineticDamageStore.remove(player.getUniqueId());
    }

    private final java.util.Set<UUID> abilityDamageActive = new java.util.HashSet<>();

    public void markAbilityDamage(org.bukkit.entity.LivingEntity target) {
        abilityDamageActive.add(target.getUniqueId());
    }

    public void unmarkAbilityDamage(org.bukkit.entity.LivingEntity target) {
        abilityDamageActive.remove(target.getUniqueId());
    }

    public boolean isAbilityDamageActive(org.bukkit.entity.LivingEntity target) {
        return abilityDamageActive.contains(target.getUniqueId());
    }

    public void setLastAbilityHit(Player victim, AbilityType ability) {
        if (ability.getCategory() != AbilityCategory.OFFENSE
                && ability.getCategory() != AbilityCategory.MOBILITY) {
            return;
        }
        lastAbilityHit.put(victim.getUniqueId(), ability);
    }

    public AbilityType getAndClearLastAbilityHit(Player victim) {
        return lastAbilityHit.remove(victim.getUniqueId());
    }

    public int getTotalStatScore(Player player) {
        int total = 0;
        for (com.hihelloy.invincible.stats.StatType stat :
                com.hihelloy.invincible.stats.StatType.values()) {
            total += plugin.getStatManager().getStatLevel(player, stat);
        }
        return total;
    }
}