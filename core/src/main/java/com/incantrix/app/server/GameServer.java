package com.incantrix.app.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.incantrix.app.server.handlers.CollisionHandler;
import com.incantrix.app.server.handlers.ServerAbilityHandler;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.Entity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.network.Network;
import com.incantrix.network.Network.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.incantrix.network.Network.MESSAGE_SIZE;

public class GameServer {
    private final Server server;
    private final List<Entity> players = new ArrayList<>(8);
    private final List<Entity> npcEntities = new ArrayList<>();
    private long nextId = 1;
    private long ticksProcessedThisSecond = 0;
    private long ticksProcessed = 0;
    private long lastReportTime = System.currentTimeMillis();
    private final ClientMapper clientMapper;
    private final ServerAbilityHandler abilityHandler;
    private final CollisionHandler collisionHandler;

    public GameServer() throws IOException {
        clientMapper = new ClientMapper();
        collisionHandler = new CollisionHandler(clientMapper);
        abilityHandler = new ServerAbilityHandler(clientMapper, npcEntities);

        server = new Server();
        Network.register(server);
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof RegisterName register) {
                    onPlayerRegistration(connection, register);
                }

                if (object instanceof UpdatePosition update) {
                    onUpdatePlayerPosition(update);
                }

                if (object instanceof UpdateAngle update) {
                    onUpdatePlayerAngle(update);
                }

                if (object instanceof UseAbility useAbility) {
                    abilityHandler.onAbilityUsage(useAbility, nextId++);
                }
            }

            public void disconnected(Connection connection) {
                onPlayerDisconnection(connection);
            }
        });

        server.bind(Network.PORT);
        server.start();

        // Start game loop thread
        new Thread(this::gameLoop).start();
    }

    private void gameLoop() {
        long lastTickTime = System.nanoTime();

        while (true) {
            long currentTime = System.nanoTime();
            float delta = (currentTime - lastTickTime) / 1_000_000_000f;

            if (delta >= Network.TICK_INTERVAL) {
                lastTickTime = currentTime;
                onGameTick(delta);

                // Sleep to prevent 100% CPU usage
                try {
                    long sleepTime = (long)((Network.TICK_INTERVAL - delta) * 1000);
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void onGameTick(float delta) {
        abilityHandler.updateAllCooldowns(delta);

        List<Long> entitiesToBeRemoved = new ArrayList<>();
        synchronized (npcEntities) {
            handleEntitiesOnTick(delta, entitiesToBeRemoved);

            // Send game state and new/removed entities to all clients
            sendGameState(server::sendToAllTCP, ticksProcessed++);
            sendBatchedEntities(npcEntities, server::sendToAllTCP);
            sendBatchedRemovedEntities(entitiesToBeRemoved);
        }

        updateGameTick();
    }

    private void handleEntitiesOnTick(float delta, List<Long> entitiesToBeRemoved) {
        players.removeIf(Entity::shouldBeRemoved);
        npcEntities.forEach(entity -> {
            if (entity.shouldBeRemoved()) {
                entitiesToBeRemoved.add(entity.getEntityId());
            }
        });
        npcEntities.removeIf(Entity::shouldBeRemoved);
        npcEntities.forEach(entity -> entity.updatePosition(delta));
        players.forEach(player -> player.updatePosition(delta));
        collisionHandler.handleCollisions(players, npcEntities);
    }

    private void updateGameTick() {
        ticksProcessedThisSecond++;

        // Log performance every second
        long now = System.currentTimeMillis();
        if (now - lastReportTime >= 1000) {
            System.out.printf("Server running at %.1f Hz with %d players%n",
                ticksProcessedThisSecond / ((now - lastReportTime) / 1000f), clientMapper.getAmountOfPlayers());
            ticksProcessedThisSecond = 0;
            lastReportTime = now;
        }
    }

    private void onPlayerRegistration(Connection connection, RegisterName register) {
        Vector2 startPosition = new Vector2(100, 100);
        long playerId = nextId++;
        PlayerEntity entity = new PlayerEntity(playerId, Allegiance.NONE, startPosition, register.name);
        NetworkEntity networkEntity = NetworkEntity.from(entity);
        clientMapper.registerNewPlayer(connection, entity);

        synchronized (players) {
            players.add(entity);

            connection.sendTCP(networkEntity);

            // Send full player list to new player
            sendGameState(connection::sendTCP, ticksProcessed);
        }

        synchronized (npcEntities) {
            sendBatchedEntities(npcEntities, connection::sendTCP);
        }
    }

    private void onUpdatePlayerPosition(UpdatePosition update) {
        PlayerEntity player = clientMapper.getPlayerByEntityId(update.id);
        if (player != null) {
            player.updatePositionWithDistance(update.xDist, update.yDist);
        }
    }

    private void onUpdatePlayerAngle(UpdateAngle update) {
        PlayerEntity player = clientMapper.getPlayerByEntityId(update.id);
        if (player != null) {
            player.updateFacingAngle(update.angle);
        }
    }

    private void onPlayerDisconnection(Connection connection) {
        clientMapper.removePlayer(connection.getID());
    }

    private void sendGameState(Consumer<GameState> stateConsumer, long ticksProcessed) {
        GameState gameState = new GameState();
        gameState.players = getNetworkEntities(players);
        gameState.tickNumber = ticksProcessed;
        stateConsumer.accept(gameState);
    }

    private void sendBatchedRemovedEntities(List<Long> entitiesToRemove) {
        long[] removed = new long[MESSAGE_SIZE];
        int k = 0;
        for (int i = 0; i < entitiesToRemove.size(); i++) {
            k = i % MESSAGE_SIZE;
            removed[k] = entitiesToRemove.get(i);
            if (k == MESSAGE_SIZE - 1) {
                RemovedEntities removedMsg = new RemovedEntities();
                removedMsg.removedEntities = Arrays.copyOf(removed, MESSAGE_SIZE);
                server.sendToAllTCP(removedMsg);
                removed = new long[MESSAGE_SIZE];
            }
        }
        if (entitiesToRemove.size() % MESSAGE_SIZE != 0) {
            RemovedEntities removedMsg = new RemovedEntities();
            removedMsg.removedEntities = Arrays.copyOf(removed, MESSAGE_SIZE);
            server.sendToAllTCP(removedMsg);
        }
    }

    private void sendBatchedEntities(List<Entity> entities, Consumer<UpdatedEntities> updateConsumer) {
        NetworkEntity[] updated = new NetworkEntity[MESSAGE_SIZE];
        int k = 0;
        for (int i = 0; i < entities.size(); i++) {
            k = i % MESSAGE_SIZE;
            updated[k] = NetworkEntity.from(entities.get(i));
            if (k == MESSAGE_SIZE - 1) {
                UpdatedEntities updatedMsg = new UpdatedEntities();
                updatedMsg.npcEntities = Arrays.copyOf(updated, MESSAGE_SIZE);
                updateConsumer.accept(updatedMsg);
                updated = new NetworkEntity[MESSAGE_SIZE];
            }
        }
        if (entities.size() % MESSAGE_SIZE != 0) {
            UpdatedEntities updatedMsg = new UpdatedEntities();
            updatedMsg.npcEntities = Arrays.copyOf(updated, MESSAGE_SIZE);
            updateConsumer.accept(updatedMsg);
        }
    }

    private NetworkEntity[] getNetworkEntities(List<Entity> entities) {
        NetworkEntity[] networkEntities = new NetworkEntity[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            networkEntities[i] = NetworkEntity.from(entities.get(i));
        }
        return networkEntities;
    }
}
