package com.incantrix.core.utils;

import com.badlogic.gdx.Gdx;

public class Boundary {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    private Boundary() {
        // Util class
    }

    public static boolean isOutOfBounds(float xPos, float yPos) {
        return isOutOfBoundsX(xPos) || isOutOfBoundsY(yPos);
    }

    public static boolean isOutOfBoundsX(float xPos) {
        return WIDTH < xPos || xPos < 0;
    }

    public static boolean isOutOfBoundsXRight(float xPos) {
        return WIDTH < xPos;
    }

    public static boolean isOutOfBoundsXLeft(float xPos) {
        return xPos < 0;
    }

    public static boolean isOutOfBoundsY(float yPos) {
        return HEIGHT < yPos || yPos < 0;
    }

    public static boolean isOutOfBoundsYUp(float yPos) {
        return HEIGHT < yPos;
    }

    public static boolean isOutOfBoundsYDown(float yPos) {
        return yPos < 0;
    }
}
