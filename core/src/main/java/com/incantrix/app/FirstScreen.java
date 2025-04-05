package com.incantrix.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private ShapeRenderer shapeRenderer;
    private Vector2 dotPosition;
    private final float dotRadius = 20f;
    private final float movementSpeed = 200f;

    public FirstScreen() {
        shapeRenderer = new ShapeRenderer();
        // Start the dot at the center of the screen
        dotPosition = new Vector2(
            Gdx.graphics.getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f
        );
    }

    @Override
    public void show() {
        // Initialization when screen becomes current
    }

    @Override
    public void render(float delta) {
        // Handle input
        handleInput(delta);

        // Clear screen with white
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the black dot
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(dotPosition.x, dotPosition.y, dotRadius);
        shapeRenderer.end();
    }

    private void handleInput(float delta) {
        // Get the distance to move based on time since last frame
        float moveDistance = movementSpeed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dotPosition.x -= moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dotPosition.x += moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dotPosition.y += moveDistance;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dotPosition.y -= moveDistance;
        }

        // Keep dot within screen bounds
        dotPosition.x = Math.max(dotRadius, Math.min(Gdx.graphics.getWidth() - dotRadius, dotPosition.x));
        dotPosition.y = Math.max(dotRadius, Math.min(Gdx.graphics.getHeight() - dotRadius, dotPosition.y));
    }

    @Override
    public void resize(int width, int height) {
        // Handle screen resizing
    }

    @Override
    public void pause() {
        // Game paused
    }

    @Override
    public void resume() {
        // Game resumed
    }

    @Override
    public void hide() {
        // When another screen replaces this one
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
