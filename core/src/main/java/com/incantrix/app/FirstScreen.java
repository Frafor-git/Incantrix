package com.incantrix.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.incantrix.app.entities.ActorEntity;
import com.incantrix.app.entities.RenderedEntity;
import com.incantrix.app.enums.Allegiance;

import java.util.ArrayList;
import java.util.List;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    public static List<RenderedEntity> entities = new ArrayList<>();
    public static ActorEntity actorEntity;

    public FirstScreen() {
        // Start the dot at the center of the screen
        actorEntity = new ActorEntity(
            1,
            Allegiance.NONE,
            new Vector2(
                Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f));
        entities.add(actorEntity);
    }

    @Override
    public void show() {
        // Initialization when screen becomes current

    }

    @Override
    public void render(float delta) {
        entities.removeIf(RenderedEntity::shouldBeRemoved);
        // Handle input
        actorEntity.handleInput(delta);

        // Clear screen with white
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        entities.forEach(entity -> entity.render(delta));
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
        entities.forEach(RenderedEntity::dispose);
    }
}
