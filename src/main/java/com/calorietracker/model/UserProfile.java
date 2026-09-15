package com.calorietracker.model;

public class UserProfile {

    public String name = "";
    public Gender gender = Gender.MALE;
    public int age = 30;
    public double heightCm = 175;
    public double weightKg = 75;
    public ActivityLevel activityLevel = ActivityLevel.MODERATE;
    public Goal goal = Goal.MAINTAIN;
    public NutrientTargets targets;

    public boolean isComplete() {
        return age > 0 && heightCm > 0 && weightKg > 0;
    }
}
