package com.incantrix.core.entities.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.OwnedEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.core.utils.Boundary;
import com.incantrix.network.Network;

public class FireballEntity extends OwnedEntity {
    private static final float RADIUS = 5f;

    public FireballEntity(long id, PlayerEntity creator, Allegiance allegiance, Vector2 position, double angle) {
        this.id = id;
        this.creator = creator;
        this.team = allegiance;
        this.position = position;
        this.angle = angle;
        this.movementSpeed = 400f;
        this.type = EntityType.FIREBALL;
    }

    public static void render(ShapeRenderer shapeRenderer, Network.NetworkEntity entity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(entity.x, entity.y, RADIUS);
        shapeRenderer.end();
    }

    @Override
    public void updatePosition(float delta) {
        float moveDistance = movementSpeed * delta;
        position.x += (float) (moveDistance * Math.cos(angle));
        position.y += (float) (moveDistance * Math.sin(angle));

        if (Boundary.isOutOfBounds(position.x, position.y)) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        shouldBeRemoved = true;
    }

    @Override
    public EntityType getType() {
        return EntityType.FIREBALL;
    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
}
