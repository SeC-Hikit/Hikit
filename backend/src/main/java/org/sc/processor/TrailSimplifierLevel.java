package org.sc.processor;

public enum TrailSimplifierLevel {
    LOW ("low"),
    MEDIUM ("medium"),
    HIGH ("high"),
    FULL ("full");

    private final String level;

    TrailSimplifierLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}
