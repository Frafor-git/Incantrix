package com.incantrix.core.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownTracker {
    private static final float GCD_TIME = 0.2f;
    private Map<Integer, Timer> cooldowns;
    private Timer globalCooldown;

    public CooldownTracker() {
        cooldowns = new ConcurrentHashMap<>();
        globalCooldown = new Timer(0);
    }

    public void updateCooldowns(float delta) {
        if (!globalCooldown.isReady()) {
            globalCooldown.decrease(delta);
        }
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

    public void setGcdCooldown() {
        globalCooldown.setCurrentTime(GCD_TIME);
    }

    public boolean isAbilityReady(int id) {
        return globalCooldown.isReady() && !cooldowns.containsKey(id);
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

        void setCurrentTime(float time) {
            currentTime = time;
        }
    }
}
