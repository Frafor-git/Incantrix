package com.incantrix.app.server.handlers;

import com.incantrix.app.server.ClientMapper;
import com.incantrix.core.abilities.AbilityIds;
import com.incantrix.core.entities.Entity;
import com.incantrix.core.entities.PlayerEntity;
import com.incantrix.core.entities.projectiles.FireballEntity;
import com.incantrix.core.entities.projectiles.HomingMissileEntity;
import com.incantrix.core.enums.Allegiance;
import com.incantrix.network.Network.UseAbilityConfirm;
import com.incantrix.network.Network.UseAbility;

import java.util.List;

public class ServerAbilityHandler {
    private final ClientMapper clientMapper;
    private final List<Entity> npcEntities;

    public ServerAbilityHandler(ClientMapper clientMapper, List<Entity> npcEntities) {
        this.clientMapper = clientMapper;
        this.npcEntities = npcEntities;
    }

    public void handleAbilityUsage(UseAbility useAbility, long id) {
        PlayerEntity caster = clientMapper.getPlayerByEntityId(useAbility.casterId);
        if (caster == null) {
            return;
        }

        if (!clientMapper.getCooldownTracker(caster.getEntityId()).isAbilityReady(useAbility.abilityId)) {
            return;
        }

        if (useAbility.abilityId == AbilityIds.FIREBALL_ID) {
            synchronized (npcEntities) {
                npcEntities.add(new FireballEntity(
                    id, caster, Allegiance.CREATOR_ONLY, caster.getPosition().cpy(), caster.getFacingAngle()));
            }
        } else if (useAbility.abilityId == AbilityIds.HOMING_MISSILE_ID) {
            PlayerEntity target = clientMapper.findEnemyPlayerClosestTo(useAbility.cursorX, useAbility.cursorY, caster);
            synchronized (npcEntities) {
                npcEntities.add(new HomingMissileEntity(
                    id,
                    caster,
                    Allegiance.CREATOR_ONLY,
                    caster.getPosition().cpy(),
                    caster.getFacingAngle(),
                    target));
            }
        }
        clientMapper.getCooldownTracker(caster.getEntityId()).trackCooldown(useAbility.abilityId);
        UseAbilityConfirm confirm = new UseAbilityConfirm();
        confirm.abilityId = useAbility.abilityId;
        clientMapper.getPlayerConnection(caster.getEntityId()).sendTCP(confirm);
    }

    public void updateAllCooldowns(float delta) {
        clientMapper.forEachCooldownTracker(tracker -> tracker.updateCooldowns(delta));
    }
}
