package com.calorietracker.model;

public enum MealType {
    BREAKFAST("Frühstück"),
    LUNCH("Mittagessen"),
    DINNER("Abendessen"),
    SNACK("Snacks");

    private final String label;

    MealType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
