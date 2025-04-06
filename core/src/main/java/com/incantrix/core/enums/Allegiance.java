package com.incantrix.core.enums;

public enum Allegiance {
    NONE((byte) 0),
    CREATOR_ONLY((byte) 1),
    TEAM_ONE((byte) 2),
    TEAM_TWO((byte) 3),
    TEAM_THREE((byte) 4),
    TEAM_FOUR((byte) 5);

    private final byte value;

    Allegiance(byte value) {
        this.value = value;
    }

    public static Allegiance from(byte value) {
        return switch (value) {
            case 0 -> NONE;
            case 1 -> CREATOR_ONLY;
            case 2 -> TEAM_ONE;
            case 3 -> TEAM_TWO;
            case 4 -> TEAM_THREE;
            case 5 -> TEAM_FOUR;
            default -> null;
        };
    }

    public byte getValue() {
        return value;
    }
}
