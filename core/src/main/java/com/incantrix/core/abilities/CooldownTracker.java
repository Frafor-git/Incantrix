package com.incantrix.core.abilities;

import com.incantrix.core.enums.GlobalCDType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownTracker {
    private static final float GCD_INCANTATION_TIME = 0.2f;
    private static final float GCD_MOBILITY_TIME = 0.8f;
    private final Map<Integer, Timer> cooldowns;
    private final Timer globalIncantationCooldown;
    private final Timer globalMobilityCooldown;

    public CooldownTracker() {
        cooldowns = new ConcurrentHashMap<>();
        globalIncantationCooldown = new Timer(0);
        globalMobilityCooldown = new Timer(0);
    }

    public void updateCooldowns(float delta) {
        if (!globalMobilityCooldown.isReady()) {
            globalMobilityCooldown.decrease(delta);
        }
        if (!globalIncantationCooldown.isReady()) {
            globalIncantationCooldown.decrease(delta);
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

    public void setGcdCooldown(GlobalCDType type) {
        switch (type) {
            case MOVEMENT -> globalMobilityCooldown.setCurrentTime(GCD_MOBILITY_TIME);
            case INCANTATION -> globalIncantationCooldown.setCurrentTime(GCD_INCANTATION_TIME);
        }
    }

    public boolean isAbilityReady(int id) {
        return globalCooldownIsReady(id) && !cooldowns.containsKey(id);
    }

    public boolean globalCooldownIsReady(int id) {
        Ability ability = AbilityMapper.getAbility(id);
        return switch (ability.getGlobalCDType()) {
            case MOVEMENT -> globalMobilityCooldown.isReady();
            case INCANTATION -> globalIncantationCooldown.isReady();
            case NONE -> true;
        };
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
