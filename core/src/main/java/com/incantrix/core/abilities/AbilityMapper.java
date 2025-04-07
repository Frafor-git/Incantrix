package com.incantrix.core.abilities;

public class AbilityMapper {

    private AbilityMapper() {
        // Util class
    }

    public static Ability getAbility(int id) {
        return switch (id) {
            case AbilityIds.FIREBALL_ID -> Fireball.getInstance();
            case AbilityIds.DASH_ID -> Dash.getInstance();
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public static class Fireball implements Ability {
        private static Ability instance;

        @Override
        public float getTotalCooldown() {
            return 0.5f;
        }

        @Override
        public float getId() {
            return AbilityIds.FIREBALL_ID;
        }

        public static Ability getInstance() {
            if (instance == null) {
                instance = new Fireball();
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

        public static Ability getInstance() {
            if (instance == null) {
                instance = new Dash();
            }
            return instance;
        }
    }
}
