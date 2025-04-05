package com.incantrix.app.entities;

public interface RenderedEntity {

    public long getEntityId();

    public void render(float delta);

    public void dispose();

    public boolean shouldBeRemoved();
}
