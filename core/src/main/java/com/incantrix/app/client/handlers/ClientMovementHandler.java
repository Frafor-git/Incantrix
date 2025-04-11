package com.incantrix.app.client.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.esotericsoftware.kryonet.Client;
import com.incantrix.core.utils.Boundary;
import com.incantrix.core.utils.Trigonometry;
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
            Client client,
            float delta) {
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
            applyInput(clientPlayer, inputFlags, delta);

            if (!isValidMovement(clientPlayer.x, clientPlayer.y)) {
                // Revert if invalid
                clientPlayer.x = prevX;
                clientPlayer.y = prevY;
                return;
            }

            // Create and store input
            UpdatePosition input = new UpdatePosition();
            input.id = clientPlayer.id;
            input.xDist = clientPlayer.x - prevX;
            input.yDist = clientPlayer.y - prevY;
            input.inputFlags = inputFlags;
            input.tickNumber = lastReceivedTick + 1; // Predict next tick

            pendingInputs.add(input);

            // Send to server
            client.sendTCP(input);
        }
    }

    public static void applyInput(NetworkEntity player, byte inputFlags, float delta) {
        // Apply movement based on input
        float moveDistance = player.movementSpeed * delta;

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
        clientPlayer.facingAngle = updateAngle(clientPlayer);
        UpdateAngle updateAngle = new UpdateAngle();
        updateAngle.id = clientPlayer.id;
        updateAngle.angle = clientPlayer.facingAngle;
        client.sendTCP(updateAngle);
    }

    private static double updateAngle(NetworkEntity clientPlayer) {
        float x = clientPlayer.x;
        float y = clientPlayer.y;
        float cursorX = Gdx.input.getX();
        float cursorY = Gdx.graphics.getHeight() - Gdx.input.getY();

        return Trigonometry.getAngle(cursorX - x, cursorY - y);
    }
}
