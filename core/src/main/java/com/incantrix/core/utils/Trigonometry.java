package com.incantrix.core.utils;

import com.incantrix.core.entities.Entity;
import com.incantrix.network.Network.*;

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

    public static void collisionVelocityChange(
            float speed1,
            double angle1,
            float speed2,
            double angle2,
            Entity entity) {
        if (Math.abs(speed1) < 1e-8 && Math.abs(speed2) < 1e-8) {
            entity.setCurrentPushSpeed(0);
            entity.setPushAngle(0);
            return;
        }
        if (Math.abs(speed1) < 1e-8) {
            entity.setCurrentPushSpeed(speed2);
            entity.setPushAngle(angle2);
            return;
        }
        if (Math.abs(speed2) < 1e-8) {
            entity.setCurrentPushSpeed(speed1);
            entity.setPushAngle(angle1);
            return;
        }
        double speed1x = Math.cos(angle1) * speed1;
        double speed1y = Math.sin(angle1) * speed1;
        double speed2x = Math.cos(angle2) * speed2;
        double speed2y = Math.sin(angle2) * speed2;
        double totX = speed1x + speed2x;
        double totY = speed1y + speed2y;
        double totalSpeed = Math.sqrt(totX * totX + totY * totY);
        double newAngle = getAngle(totX, totY);
        entity.setCurrentPushSpeed((float) totalSpeed);
        entity.setPushAngle(newAngle);
    }

    public static double mirrorAngleX(double angle) {
        return Math.PI - angle;
    }

    public static double mirrorAngleY(double angle) {
        return - angle;
    }
}
