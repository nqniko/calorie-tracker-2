package com.calorietracker.model;

public enum Gender {
    MALE("Männlich"),
    FEMALE("Weiblich");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
