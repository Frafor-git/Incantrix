package com.incantrix.core.entities;

import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.core.utils.Boundary;
import com.incantrix.core.utils.VelocityMomentum;

public abstract class Entity {
    protected Vector2 position;
    protected double facingAngle;
    protected long id;
    protected float movementSpeed;
    protected Allegiance team;
    protected VelocityMomentum momentum;

    protected float sizeRadius;

    public long getEntityId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public VelocityMomentum getVelocityMomentum() {
        return momentum;
    }

    public double getFacingAngle() {
        return facingAngle;
    }

    public EntityType getType() {
        throw new RuntimeException("Method not overridden");
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public Allegiance getTeam() {
        return team;
    }

    public void updatePositionWithDistance(float xDist, float yDist) {
        float oldX = position.x;
        float oldY = position.y;
        position.x += xDist;
        position.y += yDist;
        if (Boundary.isOutOfBounds(position.x, position.y)) {
            position.x = oldX;
            position.y = oldY;
        }
    }

    public void updateFacingAngle(double newAngle) {
        facingAngle = newAngle;
    }

    public float getSizeRadius() {
        return sizeRadius;
    }

    public void setVelocityMomentum(VelocityMomentum momentum) {
        this.momentum = momentum;
    }

    public abstract void updatePosition(float delta);

    public abstract boolean shouldBeRemoved();

    public abstract void dispose();
}
