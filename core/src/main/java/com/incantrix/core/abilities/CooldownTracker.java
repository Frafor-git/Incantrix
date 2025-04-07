package com.incantrix.core.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownTracker {
    private Map<Integer, Timer> cooldowns;

    public CooldownTracker() {
        cooldowns = new ConcurrentHashMap<>();
    }

    public void updateCooldowns(float delta) {
        if (cooldowns.isEmpty()) {
            return;
        }
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Timer> entry : cooldowns.entrySet()) {
            entry.getValue().decrease(delta);
            if (entry.getValue().isReady()) {
                toRemove.add(entry.getKey());
            }
        }
        toRemove.forEach(cooldowns::remove);
    }

    public void trackCooldown(int id) {
        cooldowns.put(id, new Timer(AbilityMapper.getAbility(id).getTotalCooldown()));
    }

    public boolean isAbilityReady(int id) {
        return !cooldowns.containsKey(id);
    }

    static class Timer {
        private float currentTime;

        Timer(float initialTime) {
            currentTime = initialTime;
        }

        void decrease(float delta) {
            currentTime -= delta;
        }

        boolean isReady() {
            return currentTime <= 0;
        }
    }
}
