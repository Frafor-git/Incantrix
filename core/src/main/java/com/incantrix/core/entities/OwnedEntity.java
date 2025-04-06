package com.incantrix.core.entities;

public abstract class OwnedEntity extends Entity {
    protected PlayerEntity creator;
    protected boolean shouldBeRemoved;

    public PlayerEntity getCreator() {
        return creator;
    }
}
