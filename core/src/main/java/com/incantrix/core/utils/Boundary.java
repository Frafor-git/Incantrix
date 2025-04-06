package com.incantrix.core.utils;

import com.badlogic.gdx.Gdx;

public class Boundary {

    private Boundary() {
        // Util class
    }

    public static boolean isOutOfBounds(float xPos, float yPos) {
        return Gdx.graphics.getWidth() < xPos || xPos < 0 ||
            Gdx.graphics.getHeight() < yPos || yPos < 0;
    }
}
