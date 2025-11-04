package com.sanketika.course_backend.enums;

/**
 * Enum for educational boards
 */
public enum Board {
    STATE("State"),
    CBSE("CBSE"),
    ICSE("ICSE");

    private final String displayName;

    Board(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
