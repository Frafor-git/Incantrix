package com.incantrix.app.server;

import com.esotericsoftware.kryonet.Connection;
import com.incantrix.core.abilities.CooldownTracker;
import com.incantrix.core.entities.PlayerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClientMapper {
    private final Map<Long, PlayerEntity> playerIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, PlayerEntity> connectionIdMap = new ConcurrentHashMap<>();
    private final Map<Long, Connection> clientConnectionsMap = new ConcurrentHashMap<>();
    private final Map<Long, CooldownTracker> playerCooldowns = new ConcurrentHashMap<>();

    public int getAmountOfPlayers() {
        return playerIdMap.size();
    }

    public void registerNewPlayer(Connection connection, PlayerEntity entity) {
        playerIdMap.put(entity.getEntityId(), entity);
        connectionIdMap.put(connection.getID(), entity);
        clientConnectionsMap.put(entity.getEntityId(), connection);
        playerCooldowns.put(entity.getEntityId(), new CooldownTracker());
    }

    public PlayerEntity getPlayerByEntityId(long entityId) {
        return playerIdMap.get(entityId);
    }

    public CooldownTracker getCooldownTracker(long entityId) {
        return playerCooldowns.get(entityId);
    }

    public Connection getPlayerConnection(long entityId) {
        return clientConnectionsMap.get(entityId);
    }

    public void forEachCooldownTracker(Consumer<CooldownTracker> consumer) {
        playerCooldowns.values().forEach(consumer);
    }

    public void removePlayer(int connectionId) {
        PlayerEntity player = connectionIdMap.get(connectionId);
        if (player == null) {
            return;
        }
        playerIdMap.remove(player.getEntityId());
        connectionIdMap.remove(connectionId);
        clientConnectionsMap.remove(player.getEntityId());
        playerCooldowns.remove(player.getEntityId());
        player.dispose();
    }
}
