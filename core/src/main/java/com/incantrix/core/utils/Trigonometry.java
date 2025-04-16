package com.incantrix.core.utils;

import com.incantrix.core.entities.Entity;

public class Trigonometry {

    private Trigonometry() {
        // Util class
    }

    public static double getAngle(double x, double y) {
        if (x == 0 && y > 0) {
            return Math.PI / 2;
        }
        if (x == 0 && y < 0) {
            return  - Math.PI / 2;
        }
        if ((y == 0 && x > 0) || (y == 0 && x == 0) ) {
            return 0;
        }
        if (y == 0 && x < 0) {
            return Math.PI;
        }
        if (x < 0) {
            return Math.atan(y/x) - Math.PI;
        }
        return Math.atan(y/x);
    }

    public static double mirrorAngleX(double angle) {
        return Math.PI - angle;
    }

    public static double mirrorAngleY(double angle) {
        return - angle;
    }

    public static float getDistanceSqr(Entity player, Entity entity) {
        return getDistanceSqr(
            player.getPosition().x, player.getPosition().y, entity.getPosition().x, entity.getPosition().y);
    }

    public static float getDistanceSqr(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return x*x + y*y;
    }
}
