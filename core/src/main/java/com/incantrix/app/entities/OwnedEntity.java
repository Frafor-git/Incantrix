package com.incantrix.app.entities;

import com.incantrix.app.enums.Allegiance;

public abstract class OwnedEntity implements RenderedEntity {
    protected ActorEntity creator;
    protected Allegiance allegiance;
    protected boolean shouldBeRemoved = false;

    public OwnedEntity(ActorEntity creator, Allegiance allegiance) {
        this.creator = creator;
        this.allegiance = allegiance;
    }

    public Allegiance getAllegiance() {
        return allegiance;
    }

    public ActorEntity getCreator() {
        return creator;
    }

    @Override
    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }
}
