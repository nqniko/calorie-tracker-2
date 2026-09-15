package com.calorietracker.model;

public enum Goal {
    LOSE_WEIGHT("Gewicht verlieren", -500),
    MAINTAIN("Gewicht halten", 0),
    GAIN_WEIGHT("Gewicht zunehmen", 400);

    private final String label;
    private final int calorieAdjustment;

    Goal(String label, int calorieAdjustment) {
        this.label = label;
        this.calorieAdjustment = calorieAdjustment;
    }

    public int getCalorieAdjustment() {
        return calorieAdjustment;
    }

    @Override
    public String toString() {
        return label;
    }
}
