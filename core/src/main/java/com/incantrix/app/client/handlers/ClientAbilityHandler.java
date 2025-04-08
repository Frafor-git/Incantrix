package com.incantrix.app.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.incantrix.core.abilities.AbilityIds;
import com.incantrix.core.abilities.CooldownTracker;
import com.incantrix.network.Network.NetworkEntity;
import com.incantrix.network.Network.UseAbility;
import com.incantrix.network.Network.UseAbilityConfirm;

public class ClientAbilityHandler {
    private static final CooldownTracker cooldownTracker = new CooldownTracker();

    private ClientAbilityHandler() {
        // Util class
    }

    public static void handleAbilityUsage(NetworkEntity clientPlayer, Client client) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && cooldownTracker.isAbilityReady(AbilityIds.FIREBALL_ID)) {
            UseAbility useAbility = new UseAbility();
            useAbility.abilityId = AbilityIds.FIREBALL_ID;
            useAbility.casterId = clientPlayer.id;
            client.sendTCP(useAbility);
            cooldownTracker.setGcdCooldown();
        }
    }

    public static void updateAbilityTimer(float delta) {
        cooldownTracker.updateCooldowns(delta);
    }

    public static void handleAbilityConfirm(UseAbilityConfirm confirm) {
        cooldownTracker.trackCooldown(confirm.abilityId);
    }
}
