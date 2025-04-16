package com.incantrix.app.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.incantrix.app.client.handlers.ClientAbilityHandler;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.projectiles.FireballEntity;
import com.incantrix.core.entities.projectiles.HomingMissileEntity;
import com.incantrix.core.enums.EntityType;
import com.incantrix.network.Network.NetworkEntity;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final GameClient gameClient;
    private long rendersProcessedThisSecond = 0;
    private long lastReportTime = System.currentTimeMillis();
    private float frameRate = 0;

    public FirstScreen() {
        // Start the dot at the center of the screen
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameClient = new GameClient();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        spriteBatch = new SpriteBatch();
        spriteBatch.enableBlending();
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
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

        // Timers
        ClientAbilityHandler.updateAbilityTimer(delta);

        // Handle input
        gameClient.handleInput(delta);

        // Clear screen with white
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameClient.gameState.players.length > 0) {
            renderPlayers();
        }

        if (!gameClient.npcEntityMap.isEmpty()) {
            renderNpcEntities();
        }

        rendersProcessedThisSecond++;
        renderFrameRate();
    }

    private void renderNpcEntities() {
        for (NetworkEntity entity : gameClient.npcEntityMap.values()) {
            if (entity == null) {
                continue;
            }
            switch (EntityType.from(entity.entityType)) {
                case FIREBALL:
                    FireballEntity.render(shapeRenderer, entity);
                    break;
                case HOMING_MISSILE:
                    HomingMissileEntity.render(shapeRenderer, entity);
                    break;
            }
        }
    }

    private void renderPlayers() {
        for (NetworkEntity player : gameClient.gameState.players) {
            if (player == null) {
                continue;
            }
            if (player.id == gameClient.clientPlayer.id) {
                PlayerEntity.render(shapeRenderer, gameClient.clientPlayer);
            } else {
                PlayerEntity.render(shapeRenderer, player);
            }
        }
    }

    private void renderFrameRate() {
        long now = System.currentTimeMillis();
        if (now - lastReportTime >= 1000) {
            frameRate = rendersProcessedThisSecond / ((now - lastReportTime) / 1000f);
            rendersProcessedThisSecond = 0;
            lastReportTime = now;
        }
        spriteBatch.begin();
        font.draw(
            spriteBatch,
            String.format("FPS %.1f", frameRate),
            20,
            Gdx.graphics.getHeight() - 20);
        spriteBatch.end();
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
        font.dispose();
        spriteBatch.dispose();
    }
}
