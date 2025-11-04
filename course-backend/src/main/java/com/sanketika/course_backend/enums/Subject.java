package com.sanketika.course_backend.enums;

/**
 * Enum for subjects
 */
public enum Subject {
    ENGLISH("English"),
    HINDI("Hindi"),
    KANNADA("Kannada"),
    MATHS("Maths"),
    SCIENCE("Science"),
    SOCIAL("Social"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    BIOLOGY("Biology"),
    HISTORY("History"),
    GEOGRAPHY("Geography"),
    CIVICS("Civics"),
    COMPUTER("Computer"),
    BUSINESS_STUDIES("Business Studies"),
    ECONOMICS("Economics"),
    PSYCHOLOGY("Psychology"),
    PHYSICAL_EDUCATION("Physical Education"),
    HOME_SCIENCE("Home Science"),
    AGRICULTURE("Agriculture"),
    TOURISM("Tourism"),
    ARTS("Arts"),
    MUSIC("Music"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    CLOUD_COMPUTING("Cloud Computing"),
    DATA_SCIENCE("Data Science"),
    CYBER_SECURITY("Cyber Security"),
    DIGITAL_MARKETING("Digital Marketing"),
    ENTREPRENEURSHIP("Entrepreneurship"),
    ETHICAL_HACKING("Ethical Hacking"),
    GRAPHIC_DESIGN("Graphic Design"),
    HUMAN_RESOURCE_MANAGEMENT("Human Resource Management"),
    INTERNATIONAL_BUSINESS("International Business"),
    JAVA("Java"),
    JAVASCRIPT("JavaScript"),
    MACHINE_LEARNING("Machine Learning"),
    MARKETING("Marketing"),
    MICROSOFT_OFFICE("Microsoft Office"),
    NETWORK_SECURITY("Network Security"),
    PYTHON("Python"),
    ROBOTICS("Robotics"),
    SOFTWARE_DEVELOPMENT("Software Development"),
    WEB_DEVELOPMENT("Web Development"),
    AI_AND_MACHINE_LEARNING("AI and Machine Learning"),
    BLOCKCHAIN("Blockchain"),
    DATA_ANALYTICS("Data Analytics");

    private final String displayName;

    Subject(String displayName) {
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
