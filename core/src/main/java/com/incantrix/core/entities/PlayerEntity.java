package com.incantrix.core.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.enums.EntityType;
import com.incantrix.network.Network;

public class PlayerEntity extends Entity {
    private static final float CORNER_DISTANCE = 10f;
    private final String name;
    private boolean shouldBeRemoved = false;

    public PlayerEntity(long id, Allegiance team, Vector2 startPosition, String name) {
        this.movementSpeed = 200f;
        this.id = id;
        this.team = team;
        this.name = name;
        this.position = startPosition;
    }

    @Override
    public void dispose() {
        shouldBeRemoved = true;
    }

    @Override
    public void updatePosition(float delta) {

    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

    @Override
    public EntityType getType() {
        return EntityType.ACTOR;
    }

    public Allegiance getTeamId() {
        return team;
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
