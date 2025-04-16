package com.incantrix.app.server.handlers;

import com.esotericsoftware.kryonet.Connection;
import com.incantrix.app.server.ClientMapper;
import com.incantrix.core.entities.Entity;
import com.incantrix.core.entities.OwnedEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.core.utils.Trigonometry;
import com.incantrix.core.utils.VelocityMomentum;
import com.incantrix.network.Network.*;

import java.util.List;

public class CollisionHandler {
    private ClientMapper clientMapper;

    public CollisionHandler(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    public void handleCollisions(List<Entity> players, List<Entity> npcEntities) {
        for (Entity player : players) {
            npcEntities.forEach(entity -> {
                if (isUnfriendly(player, entity) && isColliding(player, entity)) {
                    handleCollision(player, entity);
                }
            });
        }
    }

    private boolean isUnfriendly(Entity player, Entity entity) {
        if (entity.getTeam() == Allegiance.NONE) {
            return true;
        }
        if (player.getTeam() == entity.getTeam()) {
            return false;
        }
        if (entity.getTeam() == Allegiance.CREATOR_ONLY && entity instanceof OwnedEntity owned) {
            return owned.getCreator().getEntityId() != player.getEntityId();
        }
        return true;
    }

    private boolean isColliding(Entity player, Entity entity) {
        float distanceSqr = Trigonometry.getDistanceSqr(player, entity);
        float minAllowedDistanceSqr = (player.getSizeRadius() + entity.getSizeRadius()) *
            (player.getSizeRadius() + entity.getSizeRadius());
        return distanceSqr <= minAllowedDistanceSqr;
    }

    private void handleCollision(Entity player, Entity entity) {
        CollisionEvent collision = collisionByType(player, entity);
        Connection connection = clientMapper.getPlayerConnection(player.getEntityId());
        connection.sendTCP(collision);
    }

    private CollisionEvent collisionByType(Entity player, Entity entity) {
        CollisionEvent collision = new CollisionEvent();
        collision.withType = entity.getType().getValue();
        switch (entity.getType()) {
            case FIREBALL, HOMING_MISSILE -> {
                handleDisposableCollider(player, entity);
            }
            default -> new CollisionEvent();
        };
        return collision;
    }

    private static void handleDisposableCollider(Entity player, Entity entity) {
        VelocityMomentum collider = VelocityMomentum.from(entity.getVelocityMomentum());
        collider.angle = Trigonometry.getAngle(
            player.getPosition().x - entity.getPosition().x,
            player.getPosition().y - entity.getPosition().y);
        player.setVelocityMomentum(VelocityMomentum.collisionVelocityChange(
            player.getVelocityMomentum(), collider));
        entity.dispose();
    }
}
