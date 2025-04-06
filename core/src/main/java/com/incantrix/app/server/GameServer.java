package com.incantrix.app.server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.Entity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.network.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private final Server server;
    private final Map<Long, PlayerEntity> playerIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, PlayerEntity> connectionIdMap = new ConcurrentHashMap<>();
    private final List<Entity> players = new ArrayList<>(8);
    private final List<Entity> npcEntities = new ArrayList<>();
    private long nextId = 1;
    private long ticksProcessed = 0;
    private long lastReportTime = System.currentTimeMillis();

    public GameServer() throws IOException {
        server = new Server();
        Network.register(server);
        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof Network.RegisterName) {
                    handlePlayerRegistration(connection, (Network.RegisterName)object);
                }

                if (object instanceof Network.UpdatePosition) {
                    updatePlayerPosition((Network.UpdatePosition)object);
                }

                if (object instanceof Network.UpdateAngle) {
                    updatePlayerAngle((Network.UpdateAngle)object);
                }
            }

            public void disconnected(Connection connection) {
                handlePlayerDisconnection(connection);
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
                gameTick(delta);

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

    private void gameTick(float delta) {
        // Send game state to all clients
        players.removeIf(Entity::shouldBeRemoved);
        npcEntities.removeIf(Entity::shouldBeRemoved);
        npcEntities.forEach(entity -> entity.updatePosition(delta));
        Network.GameState gameState = new Network.GameState();
        gameState.players = getNetworkEntities(players);
        gameState.npcEntities = getNetworkEntities(npcEntities);
        gameState.tickNumber = ticksProcessed++;
        server.sendToAllTCP(gameState);

        // Log performance every second
        long now = System.currentTimeMillis();
        if (now - lastReportTime >= 1000) {
            System.out.printf("Server running at %.1f Hz with %d players%n",
                ticksProcessed / ((now - lastReportTime) / 1000f), playerIdMap.size());
            ticksProcessed = 0;
            lastReportTime = now;
        }
    }

    private void handlePlayerRegistration(Connection connection, Network.RegisterName register) {
        Vector2 startPosition = new Vector2(100, 100);
        long playerId = nextId++;
        PlayerEntity entity = new PlayerEntity(playerId, Allegiance.NONE, startPosition, register.name);
        playerIdMap.put(playerId, entity);
        connectionIdMap.put(connection.getID(), entity);
        Network.NetworkEntity networkEntity = Network.NetworkEntity.from(entity);
        players.add(entity);
        connection.sendTCP(networkEntity);

        // Send full player list to new player
        Network.GameState gameState = new Network.GameState();
        gameState.players = getNetworkEntities(players);
        gameState.npcEntities = getNetworkEntities(npcEntities);
        gameState.tickNumber = ticksProcessed;
        connection.sendTCP(gameState);
    }

    private Network.NetworkEntity[] getNetworkEntities(List<Entity> entities) {
        Network.NetworkEntity[] networkEntities = new Network.NetworkEntity[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            networkEntities[i] = Network.NetworkEntity.from(entities.get(i));
        }
        return networkEntities;
    }

    private void updatePlayerPosition(Network.UpdatePosition update) {
        PlayerEntity player = playerIdMap.get(update.id);
        if (player != null) {
            player.updatePosition(update.x, update.y);
        }
    }

    private void updatePlayerAngle(Network.UpdateAngle update) {
        PlayerEntity player = playerIdMap.get(update.id);
        if (player != null) {
            player.updateAngle(update.angle);
        }
    }

    private void handlePlayerDisconnection(Connection connection) {
        PlayerEntity player = connectionIdMap.get(connection.getID());
        if (player == null) {
            return;
        }
        connectionIdMap.remove(connection.getID());
        playerIdMap.remove(player.getEntityId());
        player.dispose();
    }
}
