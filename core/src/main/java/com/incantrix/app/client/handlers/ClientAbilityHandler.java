package com.incantrix.app.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.incantrix.core.abilities.AbilityIds;
import com.incantrix.core.timers.CooldownTracker;
import com.incantrix.core.enums.GlobalCDType;
import com.incantrix.network.Network.NetworkEntity;
import com.incantrix.network.Network.UseAbility;
import com.incantrix.network.Network.UseAbilityConfirm;

public class ClientAbilityHandler {
    private static final CooldownTracker cooldownTracker = new CooldownTracker();

    private ClientAbilityHandler() {
        // Util class
    }

    public static void handleAbilityUsage(NetworkEntity clientPlayer, Client client) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && cooldownTracker.isReady(AbilityIds.FIREBALL_ID)) {
            UseAbility useAbility = new UseAbility();
            useAbility.abilityId = AbilityIds.FIREBALL_ID;
            useAbility.casterId = clientPlayer.id;
            client.sendTCP(useAbility);
            cooldownTracker.setGcdCooldown(GlobalCDType.INCANTATION);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E) && cooldownTracker.isReady(AbilityIds.HOMING_MISSILE_ID)) {
            UseAbility useAbility = new UseAbility();
            useAbility.abilityId = AbilityIds.HOMING_MISSILE_ID;
            useAbility.casterId = clientPlayer.id;
            useAbility.cursorX = Gdx.input.getX();
            useAbility.cursorY = Gdx.graphics.getHeight() - Gdx.input.getY();
            client.sendTCP(useAbility);
            cooldownTracker.setGcdCooldown(GlobalCDType.INCANTATION);
        }
    }

    public static void updateAbilityTimer(float delta) {
        cooldownTracker.updateTimers(delta);
    }

    public static void handleAbilityConfirm(UseAbilityConfirm confirm) {
        cooldownTracker.trackCooldown(confirm.abilityId);
    }
}
