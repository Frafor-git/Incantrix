package com.incantrix.core.abilities;

import com.incantrix.core.enums.GlobalCDType;

public class AbilityMapper {

    private AbilityMapper() {
        // Util class
    }

    public static Ability getAbility(int id) {
        return switch (id) {
            case AbilityIds.FIREBALL_ID -> Fireball.getInstance();
            case AbilityIds.HOMING_MISSILE_ID -> HomingMissile.getInstance();
            case AbilityIds.DASH_ID -> Dash.getInstance();
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public static class Fireball implements Ability {
        private static Ability instance;

        @Override
        public float getTotalCooldown() {
            return 0.01f;
        }

        @Override
        public float getId() {
            return AbilityIds.FIREBALL_ID;
        }

        @Override
        public GlobalCDType getGlobalCDType() {
            return GlobalCDType.INCANTATION;
        }

        public static Ability getInstance() {
            if (instance == null) {
                instance = new Fireball();
            }
            return instance;
        }
    }

    public static class HomingMissile implements Ability {
        private static Ability instance;

        @Override
        public float getTotalCooldown() {
            return 0.5f;
        }

        @Override
        public float getId() {
            return AbilityIds.HOMING_MISSILE_ID;
        }

        @Override
        public GlobalCDType getGlobalCDType() {
            return GlobalCDType.INCANTATION;
        }

        public static Ability getInstance() {
            if (instance == null) {
                instance = new HomingMissile();
            }
            return instance;
        }
    }

    public static class Dash implements Ability {
        private static Ability instance;

        @Override
        public float getTotalCooldown() {
            return 1.0f;
        }

        @Override
        public float getId() {
            return AbilityIds.FIREBALL_ID;
        }

        @Override
        public GlobalCDType getGlobalCDType() {
            return GlobalCDType.MOVEMENT;
        }

        public static Ability getInstance() {
            if (instance == null) {
                instance = new Dash();
            }
            return instance;
        }
    }
}
