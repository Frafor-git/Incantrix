package com.incantrix.core.utils;

public class Physics {
    private static final float FRICTION_CONSTANT = 3f;
    private static final float FRICTION_RATE = 0.5f;
    private static final float ADJUSTMENT_ACCELERATION = 10000f;

    private Physics() {
        // Util class
    }

    public static float applyFriction(float speed, float delta) {
        float newSpeed = (float) (speed * Math.pow(2, - delta / 1000 * FRICTION_RATE) - FRICTION_CONSTANT);
        return newSpeed <= 0 ? 0 : newSpeed;
    }

    public static void trajectoryModification(
            float posX,
            float posY,
            float targetX,
            float targetY,
            VelocityMomentum velocity,
            float maxSpeed,
            float delta,
            float adjustmentFactor) {
        float adjustmentSpeed = ADJUSTMENT_ACCELERATION * adjustmentFactor * delta;
        float diffX = targetX - posX;
        float diffY = targetY - posY;

        double angle = Trigonometry.getAngle(diffX, diffY);
        VelocityMomentum adjustment = new VelocityMomentum(velocity.weight, angle, adjustmentSpeed);
        VelocityMomentum adjusted = VelocityMomentum.velocitySum(velocity, adjustment);
        velocity.angle = adjusted.angle;
        velocity.speed = Math.min(adjusted.speed, maxSpeed);
    }

    public static float getAdjustmentFactor(float initial, long creationTime) {
        long timeDiff = System.currentTimeMillis() - creationTime;
        if (timeDiff < 10000) {
            return initial;
        }
        return initial + (float) (timeDiff - 10000) / 100000;
    }
}
