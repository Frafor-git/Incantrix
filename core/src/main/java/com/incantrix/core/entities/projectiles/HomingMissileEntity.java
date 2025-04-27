package com.incantrix.core.entities.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.entities.OwnedEntity;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.core.utils.Boundary;
import com.incantrix.core.utils.Physics;
import com.incantrix.core.utils.VelocityMomentum;
import com.incantrix.network.Network;

public class HomingMissileEntity extends OwnedEntity {
    private static final float INITIAL_SPEED = 400f;
    private static final float RADIUS = 5f;
    private final long creationTime;
    private PlayerEntity target;

    public HomingMissileEntity(
            long id,
            PlayerEntity creator,
            Allegiance allegiance,
            Vector2 position,
            double angle,
            PlayerEntity target) {
        this.id = id;
        this.creator = creator;
        this.team = allegiance;
        this.position = position;
        this.movementSpeed = INITIAL_SPEED;
        this.sizeRadius = 5f;
        this.momentum = new VelocityMomentum(0.5f, angle, INITIAL_SPEED);
        this.target = target;
        this.creationTime = System.currentTimeMillis();
    }

    public static void render(ShapeRenderer shapeRenderer, Network.NetworkEntity entity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.circle(entity.x, entity.y, RADIUS);
        shapeRenderer.end();
    }

    @Override
    public void updatePosition(float delta) {
        if (target != null) {
            Physics.trajectoryModification(
                position.x,
                position.y,
                target.getPosition().x,
                target.getPosition().y,
                momentum,
                INITIAL_SPEED * 1.5f,
                delta,
                Physics.getAdjustmentFactor(0.3f, creationTime));
        }
        float moveDistance = momentum.speed * delta;
        position.x += (float) (moveDistance * Math.cos(momentum.angle));
        position.y += (float) (moveDistance * Math.sin(momentum.angle));

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
        return EntityType.HOMING_MISSILE;
    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
}
