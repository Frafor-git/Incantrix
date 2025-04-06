package com.incantrix.app.server.handlers;

import com.incantrix.core.abilities.AbilityIds;
import com.incantrix.core.entities.Entity;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.projectiles.FireballEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.network.Network.UseAbility;

import java.util.List;
import java.util.Map;

public class ServerAbilityHandler {
    private final Map<Long, PlayerEntity> playerIdMap;
    private final List<Entity> npcEntities;

    public ServerAbilityHandler(Map<Long, PlayerEntity> playerIdMap, List<Entity> npcEntities) {
        this.playerIdMap = playerIdMap;
        this.npcEntities = npcEntities;
    }

    public void handleAbilityUsage(UseAbility useAbility, long id) {
        PlayerEntity caster = playerIdMap.get(useAbility.casterId);
        if (caster == null) {
            return;
        }

        if (useAbility.abilityId == AbilityIds.FIREBALL_ID) {
            synchronized (npcEntities) {
                npcEntities.add(new FireballEntity(
                    id, caster, Allegiance.CREATOR_ONLY, caster.getPosition().cpy(), caster.getAngle()));
            }
        }
    }
}
