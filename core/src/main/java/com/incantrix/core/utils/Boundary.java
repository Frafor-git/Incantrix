package com.incantrix.core.utils;

import com.badlogic.gdx.Gdx;

public class Boundary {
    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;


    private Boundary() {
        // Util class
    }

    public static boolean isOutOfBounds(float xPos, float yPos) {
        return WIDTH < xPos || xPos < 0 ||
            HEIGHT < yPos || yPos < 0;
    }
}
