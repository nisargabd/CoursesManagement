package com.sanketika.course_backend.enums;

/**
 * Enum for medium of instruction
 */
public enum Medium {
    ENGLISH("English"),
    HINDI("Hindi"),
    KANNADA("Kannada"),
    TELUGU("Telugu"),
    TAMIL("Tamil");

    private final String displayName;

    Medium(String displayName) {
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
