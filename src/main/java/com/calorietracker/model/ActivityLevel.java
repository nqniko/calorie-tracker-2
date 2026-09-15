package com.calorietracker.model;

public enum ActivityLevel {
    SEDENTARY("Sitzend (wenig/keine Bewegung)", 1.2),
    LIGHT("Leicht aktiv (1-3x Sport/Woche)", 1.375),
    MODERATE("Mäßig aktiv (3-5x Sport/Woche)", 1.55),
    ACTIVE("Sehr aktiv (6-7x Sport/Woche)", 1.725),
    VERY_ACTIVE("Extrem aktiv (körperliche Arbeit + Sport)", 1.9);

    private final String label;
    private final double factor;

    ActivityLevel(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }

    @Override
    public String toString() {
        return label;
    }
}
