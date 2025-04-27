package com.incantrix.core.utils;

public class VelocityMomentum {
    public static final float MAX_SPEED = 2000f;
    public final float weight;
    public double angle = 0;
    public float speed = 0;

    public VelocityMomentum(float weight) {
        this.weight = weight;
    }

    public VelocityMomentum(float weight, double angle, float speed) {
        this.weight = weight;
        this.angle = angle;
        this.speed = speed;
    }

    public static VelocityMomentum from(VelocityMomentum other) {
        VelocityMomentum velocityMomentum = new VelocityMomentum(other.weight);
        velocityMomentum.angle = other.angle;
        velocityMomentum.speed = other.speed;
        return velocityMomentum;
    }

    public void stop() {
        angle = 0;
        speed = 0;
    }

    public void set(double angle, float speed) {
        this.angle = angle;
        this.speed = speed;
    }

    public void mirrorX() {
        angle = Trigonometry.mirrorAngleX(angle);
    }

    public void mirrorY() {
        angle = Trigonometry.mirrorAngleY(angle);
    }

    public double speedX() {
        return Math.cos(angle) * speed;
    }

    public double speedY() {
        return Math.sin(angle) * speed;
    }

    public double momentumX() {
        return speedX() * weight;
    }

    public double momentumY() {
        return speedY() * weight;
    }

    public static VelocityMomentum velocitySum(VelocityMomentum initial, VelocityMomentum collider) {
        VelocityMomentum total = new VelocityMomentum(initial.weight);
        double totalSpeedX = initial.momentumX() / collider.weight + collider.momentumX() / initial.weight;
        double totalSpeedY = initial.momentumY() / collider.weight + collider.momentumY() / initial.weight;
        total.speed = Math.min((float) Math.sqrt(totalSpeedX * totalSpeedX + totalSpeedY * totalSpeedY), MAX_SPEED);
        total.angle = Trigonometry.getAngle(totalSpeedX, totalSpeedY);
        return total;
    }

    public static VelocityMomentum collisionVelocityChange(VelocityMomentum initial, VelocityMomentum collider) {
        if (Math.abs(initial.speed) < 1e-8 && Math.abs(collider.speed) < 1e-8) {
            return new VelocityMomentum(initial.weight);
        }
        return VelocityMomentum.velocitySum(initial, collider);
    }
}
