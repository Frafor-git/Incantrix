package com.incantrix.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.incantrix.core.entities.Entity;

public class Network {
    public static final int PORT = 54555;
    public static final float TICK_RATE = 120f;
    public static final float TICK_INTERVAL = 1f / TICK_RATE;
    public static final int MESSAGE_SIZE = 10;

    public static class RegisterName {
        public String name;
    }

    public static class UpdatePosition {
        public long id;
        public float x, y;
        public byte inputFlags;
        public long tickNumber;
    }

    public static class UpdateAngle {
        public long id;
        public double angle;
    }

    public static class UseAbility {
        public long casterId;
        public int abilityId;
    }

    public static class UseAbilityConfirm {
        public int abilityId;
    }

    public static class GameState {
        public NetworkEntity[] players = new NetworkEntity[0];
        public long tickNumber;
    }

    public static class UpdatedEntities {
        public NetworkEntity[] npcEntities = new NetworkEntity[MESSAGE_SIZE];
    }

    public static class RemovedEntities {
        public long[] removedEntities = new long[MESSAGE_SIZE];
    }

    public static class NetworkEntity {
        public float x, y, movementSpeed;
        public double angle;
        public long id;
        public byte entityType;
        public byte allegiance;

        public static NetworkEntity from(Entity entity) {
            NetworkEntity networkEntity = new NetworkEntity();
            networkEntity.x = entity.getPosition().x;
            networkEntity.y = entity.getPosition().y;
            networkEntity.angle = entity.getAngle();
            networkEntity.entityType = entity.getType().getValue();
            networkEntity.allegiance = entity.getTeam().getValue();
            networkEntity.id = entity.getEntityId();
            networkEntity.movementSpeed = entity.getMovementSpeed();
            return networkEntity;
        }
    }

    // Register all network classes
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        // Register in order of frequency (most used first)
        kryo.register(UpdateAngle.class);
        kryo.register(UpdatePosition.class);
        kryo.register(GameState.class);
        kryo.register(NetworkEntity.class);
        kryo.register(NetworkEntity[].class);
        kryo.register(UpdatedEntities.class);
        kryo.register(RemovedEntities.class);
        kryo.register(UseAbility.class);
        kryo.register(UseAbilityConfirm.class);
        kryo.register(RegisterName.class);
        kryo.register(String.class);

        // Primitives
        kryo.register(float[].class);
        kryo.register(int[].class);
        kryo.register(byte[].class);
        kryo.register(long[].class);
    }
}
