package com.incantrix.core.timers;

public class Timer {
    private float currentTime;

    Timer(float initialTime) {
        currentTime = initialTime;
    }

    void decrease(float delta) {
        currentTime -= delta;
    }

    boolean isReady() {
        return currentTime <= 0;
    }

    void setCurrentTime(float time) {
        currentTime = time;
    }
}
