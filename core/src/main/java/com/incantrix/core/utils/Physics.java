package com.incantrix.core.utils;

public class Physics {
    private static final float FRICTION_CONSTANT = 3f;
    private static final float FRICTION_RATE = 0.5f;

    private Physics() {
        // Util class
    }

    public static float applyFriction(float speed, float delta) {
        float newSpeed = (float) (speed * Math.pow(2, - delta / 1000 * FRICTION_RATE) - FRICTION_CONSTANT);
        return newSpeed <= 0 ? 0 : newSpeed;
    }
}
