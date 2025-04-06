package com.incantrix.app.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.incantrix.core.utils.Boundary;
import com.incantrix.network.Network;
import com.incantrix.network.Network.NetworkEntity;
import com.incantrix.network.Network.GameState;
import com.incantrix.network.Network.RegisterName;
import com.incantrix.network.Network.UpdatePosition;
import com.incantrix.network.Network.UpdateAngle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameClient {
    private static final float PREDICTION_LERP_FACTOR = 0.3f;
    private final Map<Long, NetworkEntity> predictedPlayers = new HashMap<>();
    private final List<UpdatePosition> pendingInputs = new ArrayList<>();
    private final Client client;
    private long lastReceivedTick = -1;
    public GameState gameState;
    public NetworkEntity clientActor;

    public GameClient() {
        // Setup network client
        client = new Client();
        Network.register(client);
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetworkEntity entity) {
                    clientActor = entity;
                }

                if (object instanceof GameState state) {
                    handleGameState(state);
                }
            }
        });

        client.start();
        try {
            client.connect(5000, "localhost", Network.PORT);

            RegisterName register = new RegisterName();
            register.name = "playerName";
            client.sendTCP(register);
        } catch (IOException e) {
            Gdx.app.error("Network", "Connection failed", e);
        }
    }

    private void handleGameState(GameState state) {
        gameState = state;
        lastReceivedTick = state.tickNumber;

        // Update player positions
        for (NetworkEntity serverPlayer : gameState.players) {
            if (serverPlayer == null) {
                continue;
            }
            NetworkEntity localPlayer = predictedPlayers.get(serverPlayer.id);
            if (localPlayer != null) {
                if (serverPlayer.id == clientActor.id) {
                    // Reconciliation for our player
                    reconcilePlayer(localPlayer, serverPlayer);
                } else {
                    // Direct update for remote players
                    localPlayer.x = serverPlayer.x;
                    localPlayer.y = serverPlayer.y;
                    localPlayer.angle = serverPlayer.angle;
                }
            } else {
                predictedPlayers.put(serverPlayer.id, serverPlayer);
            }
        }

        // Remove acknowledged inputs
        removeAcknowledgedInputs(state.tickNumber);
    }

    private void handleServerCorrection(NetworkEntity serverPlayer) {
        clientActor.x = serverPlayer.x;
        clientActor.y = serverPlayer.y;
        clientActor.angle = serverPlayer.angle;
    }

    public void handleInput(float delta) {
        if (clientActor == null) return;

        sendUpdatePosition();
        sendUpdateAngle();

//        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
//            FirstScreen.entities.add(
//                new FireballEntity(this, Allegiance.CREATOR_ONLY, position.cpy(), lastRenderedDirectionAngle));
//        }
    }

    private void sendUpdateAngle() {
        clientActor.angle = updateAngle();
        UpdateAngle updateAngle = new UpdateAngle();
        updateAngle.id = clientActor.id;
        updateAngle.angle = clientActor.angle;
        client.sendTCP(updateAngle);
    }

    private void sendUpdatePosition() {
        // Store current position for prediction
        float prevX = clientActor.x;
        float prevY = clientActor.y;

        // Process input
        byte inputFlags = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) inputFlags |= 0x01;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) inputFlags |= 0x02;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) inputFlags |= 0x04;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) inputFlags |= 0x08;

        if (inputFlags != 0) {
            // Apply input immediately for prediction
            applyInput(clientActor, inputFlags);

            if (!isValidMovement(clientActor.x, clientActor.y)) {
                // Revert if invalid
                clientActor.x = prevX;
                clientActor.y = prevY;
                return;
            }

            // Create and store input
            UpdatePosition input = new UpdatePosition();
            input.id = clientActor.id;
            input.x = clientActor.x;
            input.y = clientActor.y;
            input.inputFlags = inputFlags;
            input.tickNumber = lastReceivedTick + 1; // Predict next tick

            pendingInputs.add(input);


            // Send to server
            client.sendTCP(input);
        }
    }

    private double updateAngle() {
        float x = clientActor.x;
        float y = clientActor.y;
        float cursorX = Gdx.input.getX();
        float cursorY = Gdx.graphics.getHeight() - Gdx.input.getY();

        double xRelative = cursorX - x;
        double yRelative = cursorY - y;
        if (xRelative == 0 && yRelative > 0) {
            return Math.PI / 2;
        }
        if (xRelative == 0 && yRelative < 0) {
            return  - Math.PI / 2;
        }
        if ((yRelative == 0 && xRelative > 0) || (yRelative == 0 && xRelative == 0) ) {
            return 0;
        }
        if (yRelative == 0 && xRelative < 0 ) {
            return Math.PI;
        }
        if (xRelative < 0) {
            return Math.atan((y - cursorY)/(x - cursorX)) - Math.PI;
        }
        return Math.atan((y - cursorY)/(x - cursorX));
    }

    private void reconcilePlayer(NetworkEntity localPlayer, NetworkEntity serverPlayer) {
        // Calculate difference between client and server positions
        float dx = serverPlayer.x - localPlayer.x;
        float dy = serverPlayer.y - localPlayer.y;
        float distance = (float)Math.sqrt(dx*dx + dy*dy);

        // If the difference is significant, correct the position
        if (distance > 5f) { // Threshold in pixels
            localPlayer.x = serverPlayer.x;
            localPlayer.y = serverPlayer.y;

            // Replay unacknowledged inputs
            replayPendingInputs(localPlayer);
        } else {
            // Smooth small corrections
            localPlayer.x += (serverPlayer.x - localPlayer.x) * PREDICTION_LERP_FACTOR;
            localPlayer.y += (serverPlayer.y - localPlayer.y) * PREDICTION_LERP_FACTOR;
        }
    }

    private void replayPendingInputs(NetworkEntity player) {
        // Re-apply all unacknowledged inputs
        for (UpdatePosition input : pendingInputs) {
            if (input.id == player.id) {
                applyInput(player, input.inputFlags);
            }
        }
    }

    private void removeAcknowledgedInputs(long serverTick) {
        // Remove inputs that the server has processed
        // This assumes inputs include the tick number they were sent on
        pendingInputs.removeIf(input -> input.tickNumber <= serverTick);
    }

    private void applyInput(NetworkEntity player, byte inputFlags) {
        // Apply movement based on input
        float moveDistance = clientActor.movementSpeed * Network.TICK_INTERVAL;

        if ((inputFlags & 0x01) != 0) {
            player.x -= moveDistance; // Left
        }
        if ((inputFlags & 0x02) != 0){
            player.x += moveDistance; // Right
        }
        if ((inputFlags & 0x04) != 0){
            player.y += moveDistance; // Up
        }
        if ((inputFlags & 0x08) != 0){
            player.y -= moveDistance; // Down
        }
    }

    private boolean isValidMovement(float newX, float newY) {
        return !Boundary.isOutOfBounds(newX, newY);
    }
}
