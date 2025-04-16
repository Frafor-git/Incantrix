package com.incantrix.app.server;

import com.esotericsoftware.kryonet.Connection;
import com.incantrix.core.abilities.CooldownTracker;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.utils.Trigonometry;

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

    public PlayerEntity findEnemyPlayerClosestTo(float cursorX, float cursorY, PlayerEntity caster) {
        float distanceSqr = Float.MAX_VALUE;
        PlayerEntity closestEnemy = null;

        for (PlayerEntity player : playerIdMap.values()) {
            if (player.getEntityId() == caster.getEntityId()) {
                continue;
            }

            if (isEnemy(caster, player)) {
                continue;
            }
            float newDistanceSqr = Trigonometry.getDistanceSqr(
                cursorX, cursorY, player.getPosition().x, player.getPosition().y);

            if (newDistanceSqr < distanceSqr) {
                distanceSqr = newDistanceSqr;
                closestEnemy = player;
            }
        }

        return closestEnemy;
    }

    private static boolean isEnemy(PlayerEntity reference, PlayerEntity other) {
        return reference.getTeam() != Allegiance.NONE
            && reference.getTeam() != Allegiance.CREATOR_ONLY
            && other.getTeam() == reference.getTeam();
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
