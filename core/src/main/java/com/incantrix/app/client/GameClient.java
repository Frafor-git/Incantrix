package com.incantrix.app.client;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.incantrix.app.client.handlers.ClientAbilityHandler;
import com.incantrix.app.client.handlers.ClientMovementHandler;
import com.incantrix.network.Network;
import com.incantrix.network.Network.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameClient {
    private static final float PREDICTION_LERP_FACTOR = 0.3f;
    private final Map<Long, NetworkEntity> predictedPlayers = new HashMap<>();
    private final List<UpdatePosition> pendingInputs = new ArrayList<>();
    private final Client client;
    private long lastReceivedTick = -1;

    public final Map<Long, NetworkEntity> npcEntityMap = new ConcurrentHashMap<>();
    public GameState gameState;
    public NetworkEntity clientPlayer;

    public GameClient() {
        // Setup network client
        client = new Client();
        Network.register(client);
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof NetworkEntity player) {
                    clientPlayer = player;
                }

                if (object instanceof GameState state) {
                    handleGameState(state);
                }

                if (object instanceof UpdatedEntities updated) {
                    handleEntityUpdate(updated);
                }

                if (object instanceof RemovedEntities removed) {
                    handleRemoveEntities(removed);
                }

                if (object instanceof UseAbilityConfirm confirm) {
                    ClientAbilityHandler.handleAbilityConfirm(confirm);
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

    private void handleEntityUpdate(Network.UpdatedEntities updated) {
        for (NetworkEntity entity : updated.npcEntities) {
            if (entity == null) {
                break;
            }
            npcEntityMap.put(entity.id, entity);
        }
    }

    private void handleRemoveEntities(Network.RemovedEntities removed) {
        for (long id : removed.removedEntities) {
            if (id == 0) {
                break;
            }
            npcEntityMap.remove(id);
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
                if (serverPlayer.id == clientPlayer.id) {
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

    public void handleInput(float delta) {
        if (clientPlayer == null) return;

        synchronized (pendingInputs) {
            ClientMovementHandler.handlePositionUpdate(clientPlayer, lastReceivedTick, pendingInputs, client);
        }
        ClientMovementHandler.handleAngleUpdate(clientPlayer, client);
        ClientAbilityHandler.handleAbilityUsage(clientPlayer, client);
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
        synchronized (pendingInputs) {
            for (UpdatePosition input : pendingInputs) {
                if (input.id == player.id) {
                    ClientMovementHandler.applyInput(player, input.inputFlags);
                }
            }
        }
    }

    private void removeAcknowledgedInputs(long serverTick) {
        // Remove inputs that the server has processed
        // This assumes inputs include the tick number they were sent on
        synchronized (pendingInputs) {
            pendingInputs.removeIf(input -> input.tickNumber <= serverTick);
        }
    }
}
