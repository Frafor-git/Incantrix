package com.incantrix.app.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.incantrix.core.abilities.AbilityIds;
import com.incantrix.network.Network.*;

public class ClientAbilityHandler {

    private ClientAbilityHandler() {
        // Util class
    }

    public static void handleAbilityUsage(NetworkEntity clientPlayer, Client client) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            UseAbility useAbility = new UseAbility();
            useAbility.abilityId = AbilityIds.FIREBALL_ID;
            useAbility.casterId = clientPlayer.id;
            client.sendTCP(useAbility);
        }
    }
}
