package com.incantrix.core.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.network.Network;

public abstract class Entity {
    protected Vector2 position;
    protected double angle;
    protected long id;
    protected EntityType type;
    protected float movementSpeed;
    protected Allegiance team;

    public long getEntityId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public double getAngle() {
        return angle;
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

    public void updateAngle(double newAngle) {
        angle = newAngle;
    }

    public abstract void updatePosition(float delta);

    public abstract boolean shouldBeRemoved();

    public abstract void dispose();
}
