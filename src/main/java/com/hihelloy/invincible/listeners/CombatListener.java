package com.hihelloy.invincible.listeners;

import com.hihelloy.invincible.InvinciblePlugin;
import com.hihelloy.invincible.abilities.AbilityType;
import com.hihelloy.invincible.characters.CharacterManager;
import com.hihelloy.invincible.characters.CharacterType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntityResurrectEvent;

public class CombatListener implements Listener {

    private final InvinciblePlugin plugin;
    private final java.util.Set<java.util.UUID> abilityKilledPlayers = new java.util.HashSet<>();

    public CombatListener(InvinciblePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAbilityDamageBypass(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof org.bukkit.entity.LivingEntity target)) return;
        if (!plugin.getAbilityManager().isAbilityDamageActive(target)) return;
        if (event.isCancelled()) return;
        double raw = event.getDamage();
        for (org.bukkit.event.entity.EntityDamageEvent.DamageModifier mod :
                org.bukkit.event.entity.EntityDamageEvent.DamageModifier.values()) {
            if (mod == org.bukkit.event.entity.EntityDamageEvent.DamageModifier.BASE) continue;
            if (!event.isApplicable(mod)) continue;
            event.setDamage(mod, 0);
        }
        event.setDamage(org.bukkit.event.entity.EntityDamageEvent.DamageModifier.BASE, raw);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (plugin.getWorldGuard().isDisabled(attacker)) return;

        CharacterType character = plugin.getDataManager().getCharacterManager().getCharacter(attacker);
        if (character == null) return;

        if (event.getEntity() instanceof Player victim) {
            if (attacker == victim) return;
            if (plugin.getWorldGuard().isDisabled(victim)) return;
            if (!plugin.getWorldGuard().isPvPAllowed(attacker, victim)) {
                event.setCancelled(true);
                attacker.sendMessage(net.kyori.adventure.text.Component.text(
                        "PvP is not allowed in this claim.", net.kyori.adventure.text.format.NamedTextColor.RED));
                return;
            }
        }

        if (event.getEntity() instanceof Player victim && attacker != victim) {
            if (!plugin.getAbilityManager().isAbilityDamageActive(victim)) {
                int combo = plugin.getCombatManager().registerHit(attacker);
                double comboMult = plugin.getCombatManager().getComboMultiplier(combo);
                event.setDamage(event.getDamage() * comboMult);

                if (combo >= 3) {
                    String bar = switch (combo) {
                        case 3 -> "&e3x Combo!";
                        case 4 -> "&6&l4x Combo!";
                        case 5 -> "&c5x Combo!";
                        case 6 -> "&c&l6x Combo!!";
                        case 7 -> "&4&l7x COMBO!!";
                        case 8 -> "&4&lMAX COMBO!!! &cx8";
                        default -> "";
                    };
                    attacker.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                            .legacyAmpersand().deserialize(bar));

                    victim.getWorld().spawnParticle(org.bukkit.Particle.CRIT,
                            victim.getLocation().add(0, 1.2, 0), combo * 3, 0.4, 0.4, 0.4, 0.15);
                    if (combo >= 6) {
                        victim.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK,
                                victim.getLocation().add(0, 1, 0), 4, 0.5, 0.3, 0.5, 0.1);
                    }
                    if (combo == 8) {
                        victim.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION,
                                victim.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.05);
                        attacker.getWorld().playSound(attacker.getLocation(),
                                org.bukkit.Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.5f, 0.6f);
                    }
                }
            }
        }

        double flatBonus = plugin.getStatManager().getDamageFlatBonus(attacker);
        double strengthBonus = character.hasSuperStrength() ? 1.2 : 0.0;
        event.setDamage(event.getDamage() + flatBonus + strengthBonus);

        if (event.getEntity() instanceof Player victim) {
            CharacterType victimChar = plugin.getDataManager().getCharacterManager().getCharacter(victim);
            if (victimChar != null) {
                double defReduction = plugin.getStatManager().getDefenseFlatReduction(victim);
                event.setDamage(Math.max(0.5, event.getDamage() - defReduction));

                double underdogReduction = plugin.getStatManager().getUnderdogReduction(victim, attacker);
                if (underdogReduction > 0) {
                    event.setDamage(event.getDamage() * (1.0 - underdogReduction));
                }

                if (plugin.getAbilityManager().isAbilityActive(victim, AbilityType.DEFLECT)
                        || plugin.getAbilityManager().isAbilityActive(victim, AbilityType.CLONE_SHIELD)
                        || plugin.getAbilityManager().isAbilityActive(victim, AbilityType.ENERGY_SHIELD)
                        || plugin.getAbilityManager().isAbilityActive(victim, AbilityType.FORCE_FIELD)) {
                    event.setDamage(event.getDamage() * 0.4);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                }

                if (plugin.getAbilityManager().isAbilityActive(victim, AbilityType.INVULNERABILITY)) {
                    event.setDamage(event.getDamage() * 0.1);
                }

                if (plugin.getAbilityManager().isAbilityActive(victim, AbilityType.RIFT_SHIELD)) {
                    event.setDamage(event.getDamage() * 0.15);
                    victim.getWorld().spawnParticle(Particle.PORTAL,
                            victim.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.3);
                }

                if (plugin.getAbilityManager().isAbilityActive(victim, AbilityType.BULLET_CATCH)) {
                    double returned = event.getDamage() * 2.0;
                    event.setCancelled(true);
                    plugin.getAbilityManager().setActiveAbility(victim, AbilityType.BULLET_CATCH, false);
                    attacker.damage(returned, victim);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 1.5f);
                    victim.sendMessage(Component.text("Hit caught and returned!", NamedTextColor.DARK_GREEN));
                    return;
                }

                if (plugin.getAbilityManager().isAbilityActive(victim, AbilityType.KINETIC_FEEDBACK)) {
                    plugin.getAbilityManager().addStoredKineticDamage(victim, event.getDamage());
                    event.setDamage(event.getDamage() * 0.5);
                    victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_ANVIL_USE, 0.5f, 2.0f);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        if (plugin.getDataManager().getCharacterManager().getCharacter(killer) == null) return;
        plugin.getStatManager().onKill(killer);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        CharacterType victimChar = plugin.getDataManager().getCharacterManager().getCharacter(victim);
        CharacterType killerChar = killer != null
                ? plugin.getDataManager().getCharacterManager().getCharacter(killer) : null;

        AbilityType killingAbility = plugin.getAbilityManager().getAndClearLastAbilityHit(victim);

        if (killingAbility != null) {
            abilityKilledPlayers.add(victim.getUniqueId());
        }

        if (killingAbility != null && killer != null && killerChar != null) {
            String template = plugin.getAbilityConfig().getDeathMessage(killingAbility);
            String victimColor = victimChar != null ? CharacterType.ampCode(victimChar.getColor()) : "&f";
            String killerColor = CharacterType.ampCode(killerChar.getColor());
            String abilityColor = CharacterType.ampCode(killingAbility.getColor());
            String msg = template
                    .replace("%victim%", victimColor + victim.getName())
                    .replace("%killer%", killerColor + killer.getName())
                    .replace("%ability%", abilityColor + killingAbility.getDisplayName());
            event.deathMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
        } else if (victimChar != null) {
            event.deathMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(CharacterType.ampCode(victimChar.getColor()) + victim.getName() + "&7 has fallen"));
        }

        if (killer == null || killer == victim) return;
        if (killerChar == null) return;

        plugin.getStatManager().onKill(killer);

        killer.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(CharacterType.ampCode(killerChar.getColor()) + "You defeated "
                        + (victimChar != null ? CharacterType.ampCode(victimChar.getColor()) : "&f")
                        + victim.getName() + "&e!"));
        plugin.getScoreboardManager().updateScoreboard(killer);
    }

    @EventHandler
    public void onTotemPop(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        CharacterManager charMgr = plugin.getDataManager().getCharacterManager();
        if (!charMgr.hasCharacter(player)) return;
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                player.setHealth(maxHp * 0.5);
                player.sendActionBar(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacyAmpersand().deserialize("&6Totem activated — &c50% health restored."));
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!abilityKilledPlayers.contains(player.getUniqueId())) return;
        abilityKilledPlayers.remove(player.getUniqueId());
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                player.setHealth(0);
                player.sendMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacyAmpersand().deserialize(
                                "&cYou were killed by an ability. Use &f/kill &cto release your spirit."));
            }
        }.runTaskLater(plugin, 1L);
    }
}