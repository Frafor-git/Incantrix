package com.incantrix.core.enums;

public enum EntityType {
    ACTOR((byte) 0),
    FIREBALL((byte) 1);

    private final byte value;

    EntityType(byte value) {
        this.value = value;
    }

    public static EntityType from(byte value) {
        return switch (value) {
            case 0 -> ACTOR;
            case 1 -> FIREBALL;
            default -> throw new IllegalArgumentException("Invalid byte value");
        };
    }

    public byte getValue() {
        return value;
    }
}
