package com.incantrix.core.entities;

import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.core.utils.Boundary;

public abstract class Entity {
    protected Vector2 position;
    protected double pushAngle;
    protected double facingAngle;
    protected long id;
    protected EntityType type;
    protected float movementSpeed;
    protected float currentPushSpeed;
    protected Allegiance team;

    protected float sizeRadius;

    public long getEntityId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public double getPushAngle() {
        return pushAngle;
    }

    public double getFacingAngle() {
        return facingAngle;
    }

    public EntityType getType() {
        return type;
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

    public float getCurrentPushSpeed() {
        return currentPushSpeed;
    }

    public float getSizeRadius() {
        return sizeRadius;
    }

    public void setPushAngle(double pushAngle) {
        this.pushAngle = pushAngle;
    }

    public void setCurrentPushSpeed(float currentPushSpeed) {
        this.currentPushSpeed = currentPushSpeed;
    }

    public abstract void updatePosition(float delta);

    public abstract boolean shouldBeRemoved();

    public abstract void dispose();
}
