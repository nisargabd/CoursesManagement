package com.sanketika.course_backend.enums;

/**
 * Enum for grade levels
 */
public enum Grade {
    GRADE_1("1"),
    GRADE_2("2"),
    GRADE_3("3"),
    GRADE_4("4"),
    GRADE_5("5"),
    GRADE_6("6"),
    GRADE_7("7"),
    GRADE_8("8"),
    GRADE_9("9"),
    GRADE_10("10"),
    GRADE_11("11"),
    GRADE_12("12");

    private final String displayName;

    Grade(String displayName) {
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
