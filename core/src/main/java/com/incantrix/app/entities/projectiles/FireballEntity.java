package com.incantrix.app.entities.projectiles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.app.entities.ActorEntity;
import com.incantrix.app.entities.OwnedEntity;
import com.incantrix.app.enums.Allegiance;
import com.incantrix.app.utils.Boundary;

public class FireballEntity extends OwnedEntity {
    private Vector2 position;
    private double angle;
    private static final float RADIUS = 5f;
    private float movementSpeed = 400f;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public FireballEntity(ActorEntity creator, Allegiance allegiance, Vector2 position, double angle) {
        super(creator, allegiance);
        this.position = position;
        this.angle = angle;
    }

    @Override
    public long getEntityId() {
        return 0;
    }

    @Override
    public void render(float delta) {
        updatePosition(delta);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(position.x, position.y, RADIUS);
        shapeRenderer.end();
    }

    private void updatePosition(float delta) {
        float moveDistance = movementSpeed * delta;
        position.x += (float) (moveDistance * Math.cos(angle));
        position.y += (float) (moveDistance * Math.sin(angle));

        if (Boundary.isOutOfBounds(position.x, position.y)) {
            shouldBeRemoved = true;
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
