package com.incantrix.app.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.app.FirstScreen;
import com.incantrix.app.entities.projectiles.FireballEntity;
import com.incantrix.app.enums.Allegiance;

public class ActorEntity implements RenderedEntity {
    private static final float CORNER_DISTANCE = 10f;
    private final long entityId;
    private final Allegiance team;
    private final float movementSpeed = 200f;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    private final Vector2 position;
    private final Vector2 cursorPosition;
    private double lastRenderedDirectionAngle = Math.PI;

    public ActorEntity(long entityId, Allegiance team, Vector2 startPosition) {
        this.entityId = entityId;
        this.team = team;
        this.position = startPosition;
        this.cursorPosition = new Vector2(
            Gdx.input.getX(),
            Gdx.graphics.getHeight() - Gdx.input.getY());
    }

    @Override
    public void render(float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        renderShape();
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    public Allegiance getTeamId() {
        return team;
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    public void handleInput(float delta) {
        // Get the distance to move based on time since last frame
        float moveDistance = movementSpeed * delta;
        cursorPosition.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            FirstScreen.entities.add(
                new FireballEntity(this, Allegiance.CREATOR_ONLY, position.cpy(), lastRenderedDirectionAngle));
        }

        // Keep position within screen bounds
        position.x = Math.max(0, Math.min(Gdx.graphics.getWidth(), position.x));
        position.y = Math.max(0, Math.min(Gdx.graphics.getHeight(), position.y));
    }

    private void renderShape() {
        lastRenderedDirectionAngle = updateLastRenderedDirectionAngle();
        double directionCornerX = CORNER_DISTANCE * Math.cos(lastRenderedDirectionAngle);
        double directionCornerY = CORNER_DISTANCE * Math.sin(lastRenderedDirectionAngle);
        double leftBackCornerX = CORNER_DISTANCE * Math.cos(lastRenderedDirectionAngle + Math.PI*4/3);
        double leftBackCornerY = CORNER_DISTANCE * Math.sin(lastRenderedDirectionAngle + Math.PI*4/3);
        double rightBackCornerX = CORNER_DISTANCE * Math.cos(lastRenderedDirectionAngle + Math.PI*2/3);
        double rightBackCornerY = CORNER_DISTANCE * Math.sin(lastRenderedDirectionAngle + Math.PI*2/3);
        System.out.println(lastRenderedDirectionAngle);

        shapeRenderer.triangle(
            (float) (position.x + leftBackCornerX),
            (float) (position.y + leftBackCornerY),
            (float) (position.x + rightBackCornerX),
            (float) (position.y + rightBackCornerY),
            (float) (position.x + directionCornerX),
            (float) (position.y + directionCornerY));
    }

    private double updateLastRenderedDirectionAngle() {
        double xRelative = position.x - cursorPosition.x;
        double yRelative = position.y - cursorPosition.y;
        if (xRelative == 0) {
            return lastRenderedDirectionAngle;
        }
        if (yRelative == 0) {
            return lastRenderedDirectionAngle;
        }
        if (xRelative < 0) {
            return Math.atan((position.y - cursorPosition.y)/(position.x - cursorPosition.x));
        }
        return Math.PI + Math.atan((position.y - cursorPosition.y)/(position.x - cursorPosition.x));
    }
}
