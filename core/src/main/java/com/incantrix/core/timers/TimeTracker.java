package com.incantrix.core.timers;

import java.util.function.Supplier;

public interface TimeTracker {

    public void updateTimers(float delta);

    public void trackTime(long id, Timer timer);

    public boolean isReady(long id);
}
