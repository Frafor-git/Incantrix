package com.incantrix.core.entities;

import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;

public abstract class Entity {
    protected Vector2 position;
    protected double movementAngle;
    protected double facingAngle;
    protected long id;
    protected EntityType type;
    protected float movementSpeed;
    protected float currentPushSpeed;
    protected Allegiance team;

    public long getEntityId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public double getMovementAngle() {
        return movementAngle;
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

    public void updatePosition(float newX, float newY) {
        position.x = newX;
        position.y = newY;
    }

    public void updateFacingAngle(double newAngle) {
        facingAngle = newAngle;
    }

    public float getCurrentPushSpeed() {
        return currentPushSpeed;
    }

    public abstract void updatePosition(float delta);

    public abstract boolean shouldBeRemoved();

    public abstract void dispose();
}
