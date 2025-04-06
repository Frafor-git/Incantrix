package com.incantrix.app.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.incantrix.core.utils.Boundary;
import com.incantrix.network.Network;
import com.incantrix.network.Network.*;

import java.util.List;

public class ClientMovementHandler {

    private ClientMovementHandler() {
        // Util class
    }

    public static void handlePositionUpdate(
        NetworkEntity clientPlayer,
        long lastReceivedTick,
        List<UpdatePosition> pendingInputs,
        Client client) {
        // Store current position for prediction
        float prevX = clientPlayer.x;
        float prevY = clientPlayer.y;

        // Process input
        byte inputFlags = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) inputFlags |= 0x01;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) inputFlags |= 0x02;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) inputFlags |= 0x04;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) inputFlags |= 0x08;

        if (inputFlags != 0) {
            // Apply input immediately for prediction
            applyInput(clientPlayer, inputFlags);

            if (!isValidMovement(clientPlayer.x, clientPlayer.y)) {
                // Revert if invalid
                clientPlayer.x = prevX;
                clientPlayer.y = prevY;
                return;
            }

            // Create and store input
            UpdatePosition input = new UpdatePosition();
            input.id = clientPlayer.id;
            input.x = clientPlayer.x;
            input.y = clientPlayer.y;
            input.inputFlags = inputFlags;
            input.tickNumber = lastReceivedTick + 1; // Predict next tick

            pendingInputs.add(input);

            // Send to server
            client.sendTCP(input);
        }
    }

    public static void applyInput(NetworkEntity player, byte inputFlags) {
        // Apply movement based on input
        float moveDistance = player.movementSpeed * Network.TICK_INTERVAL;

        if ((inputFlags & 0x01) != 0) {
            player.x -= moveDistance; // Left
        }
        if ((inputFlags & 0x02) != 0){
            player.x += moveDistance; // Right
        }
        if ((inputFlags & 0x04) != 0){
            player.y += moveDistance; // Up
        }
        if ((inputFlags & 0x08) != 0){
            player.y -= moveDistance; // Down
        }
    }

    private static boolean isValidMovement(float newX, float newY) {
        return !Boundary.isOutOfBounds(newX, newY);
    }

    public static void handleAngleUpdate(NetworkEntity clientPlayer, Client client) {
        clientPlayer.angle = updateAngle(clientPlayer);
        UpdateAngle updateAngle = new UpdateAngle();
        updateAngle.id = clientPlayer.id;
        updateAngle.angle = clientPlayer.angle;
        client.sendTCP(updateAngle);
    }

    private static double updateAngle(NetworkEntity clientPlayer) {
        float x = clientPlayer.x;
        float y = clientPlayer.y;
        float cursorX = Gdx.input.getX();
        float cursorY = Gdx.graphics.getHeight() - Gdx.input.getY();

        double xRelative = cursorX - x;
        double yRelative = cursorY - y;
        if (xRelative == 0 && yRelative > 0) {
            return Math.PI / 2;
        }
        if (xRelative == 0 && yRelative < 0) {
            return  - Math.PI / 2;
        }
        if ((yRelative == 0 && xRelative > 0) || (yRelative == 0 && xRelative == 0) ) {
            return 0;
        }
        if (yRelative == 0 && xRelative < 0 ) {
            return Math.PI;
        }
        if (xRelative < 0) {
            return Math.atan((y - cursorY)/(x - cursorX)) - Math.PI;
        }
        return Math.atan((y - cursorY)/(x - cursorX));
    }
}
