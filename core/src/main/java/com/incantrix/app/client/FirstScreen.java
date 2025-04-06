package com.incantrix.app.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.projectiles.FireballEntity;
import com.incantrix.core.enums.EntityType;
import com.incantrix.network.Network.NetworkEntity;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private OrthographicCamera camera;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final GameClient gameClient;

    public FirstScreen() {
        // Start the dot at the center of the screen
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameClient = new GameClient();
    }

    @Override
    public void show() {
        // Initialization when screen becomes current

    }

    @Override
    public void render(float delta) {
        if (gameClient.gameState == null) {
            return;
        }

        // Handle input
        gameClient.handleInput(delta);

        // Clear screen with white
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameClient.gameState.players.length > 0) {
            renderPlayers();
        }

        if (gameClient.gameState.npcEntities.length > 0) {
            renderNpcEntities();
        }
    }

    private void renderNpcEntities() {
        for (NetworkEntity entity : gameClient.gameState.npcEntities) {
            if (entity == null) {
                continue;
            }
            switch (EntityType.from(entity.entityType)) {
                case FIREBALL:
                    FireballEntity.render(shapeRenderer, entity);
                    break;
            }
        }
    }

    private void renderPlayers() {
        for (NetworkEntity player : gameClient.gameState.players) {
            if (player == null) {
                continue;
            }
            PlayerEntity.render(shapeRenderer, player);
        }
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
