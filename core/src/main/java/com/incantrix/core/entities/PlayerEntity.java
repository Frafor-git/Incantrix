package com.incantrix.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.core.utils.Boundary;
import com.incantrix.core.utils.Physics;
import com.incantrix.core.utils.VelocityMomentum;
import com.incantrix.network.Network;

public class PlayerEntity extends Entity {
    private static final float INITIAL_SPEED = 200f;
    private static final float CORNER_DISTANCE = 10f;
    private final String name;
    private boolean shouldBeRemoved = false;

    public PlayerEntity(long id, Allegiance team, Vector2 startPosition, String name) {
        this.movementSpeed = INITIAL_SPEED;
        this.sizeRadius = 10f;
        this.id = id;
        this.team = team;
        this.name = name;
        this.position = startPosition;
        this.momentum = new VelocityMomentum(1);
    }

    @Override
    public void dispose() {
        shouldBeRemoved = true;
    }

    @Override
    public void updatePosition(float delta) {
        outOfBoundsChecks();

        float moveDistance = momentum.speed * delta;
        float xBefore =  position.x;
        float yBefore =  position.y;
        position.x += (float) (moveDistance * Math.cos(momentum.angle));
        position.y += (float) (moveDistance * Math.sin(momentum.angle));

        if (Boundary.isOutOfBoundsX(position.x)) {
            momentum.mirrorX();
            position.x = xBefore + (float) (moveDistance * Math.cos(momentum.angle));
        }

        if (Boundary.isOutOfBoundsY(position.y)) {
            momentum.mirrorY();
            position.y = yBefore + (float) (moveDistance * Math.cos(momentum.angle));
        }

        momentum.speed = Physics.applyFriction(momentum.speed, delta);
    }

    private void outOfBoundsChecks() {
        if (Boundary.isOutOfBoundsXRight(position.x)) {
            position.x = Boundary.WIDTH - 1;
        }
        if (Boundary.isOutOfBoundsXLeft(position.x)) {
            position.x = 0;
        }
        if (Boundary.isOutOfBoundsYUp(position.y)) {
            position.y = Boundary.HEIGHT - 1;
        }
        if (Boundary.isOutOfBoundsYDown(position.y)) {
            position.y = 0;
        }
    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

    @Override
    public EntityType getType() {
        return EntityType.ACTOR;
    }

    public static void render(ShapeRenderer shapeRenderer, Network.NetworkEntity entity) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        renderShape(shapeRenderer, entity);
        shapeRenderer.end();
    }

    private static void renderShape(ShapeRenderer shapeRenderer, Network.NetworkEntity entity) {
        double directionCornerX = CORNER_DISTANCE * Math.cos(entity.facingAngle);
        double directionCornerY = CORNER_DISTANCE * Math.sin(entity.facingAngle);
        double leftBackCornerX = CORNER_DISTANCE * Math.cos(entity.facingAngle + Math.PI*4/3);
        double leftBackCornerY = CORNER_DISTANCE * Math.sin(entity.facingAngle + Math.PI*4/3);
        double rightBackCornerX = CORNER_DISTANCE * Math.cos(entity.facingAngle + Math.PI*2/3);
        double rightBackCornerY = CORNER_DISTANCE * Math.sin(entity.facingAngle + Math.PI*2/3);

        shapeRenderer.triangle(
            (float) (entity.x + leftBackCornerX),
            (float) (entity.y + leftBackCornerY),
            (float) (entity.x + rightBackCornerX),
            (float) (entity.y + rightBackCornerY),
            (float) (entity.x + directionCornerX),
            (float) (entity.y + directionCornerY));
    }
}
