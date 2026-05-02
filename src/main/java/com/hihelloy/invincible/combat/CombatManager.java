package com.hihelloy.invincible.combat;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private static final int MAX_COMBO = 8;

    private final Map<UUID, Integer> combos = new HashMap<>();
    private final Map<UUID, Long> lastHitTime = new HashMap<>();
    private final Map<String, Long> iframeMap = new HashMap<>();

    private final long comboWindowMs;
    private final long iframeTicks;

    public CombatManager(long comboWindowMs, long iframeTicks) {
        this.comboWindowMs = comboWindowMs;
        this.iframeTicks = iframeTicks;
    }

    public int registerHit(Player attacker) {
        UUID id = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastHitTime.getOrDefault(id, 0L);

        int combo;
        if (now - last <= comboWindowMs) {
            combo = Math.min(combos.getOrDefault(id, 0) + 1, MAX_COMBO);
        } else {
            combo = 1;
        }

        combos.put(id, combo);
        lastHitTime.put(id, now);
        return combo;
    }

    public void resetCombo(Player attacker) {
        combos.remove(attacker.getUniqueId());
        lastHitTime.remove(attacker.getUniqueId());
    }

    public int getCombo(Player attacker) {
        UUID id = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        if (now - lastHitTime.getOrDefault(id, 0L) > comboWindowMs) {
            combos.remove(id);
            return 0;
        }
        return combos.getOrDefault(id, 0);
    }

    public double getComboMultiplier(int combo) {
        return switch (combo) {
            case 1 -> 1.00;
            case 2 -> 1.10;
            case 3 -> 1.20;
            case 4 -> 1.30;
            case 5 -> 1.45;
            case 6 -> 1.60;
            case 7 -> 1.80;
            default -> 2.00;
        };
    }

    public boolean isOnIframes(Player attacker, Player victim) {
        String key = attacker.getUniqueId() + "->" + victim.getUniqueId();
        Long last = iframeMap.get(key);
        if (last == null) return false;
        return System.currentTimeMillis() - last < (iframeTicks * 50L);
    }

    public void setIframes(Player attacker, Player victim) {
        iframeMap.put(attacker.getUniqueId() + "->" + victim.getUniqueId(),
                System.currentTimeMillis());
    }

    public void removePlayer(UUID id) {
        combos.remove(id);
        lastHitTime.remove(id);
        iframeMap.entrySet().removeIf(e -> e.getKey().contains(id.toString()));
    }
}