package com.calorietracker.model;

/** Daily targets computed from a user's profile (BMR/TDEE based). */
public class NutrientTargets {

    public double bmr;
    public double tdee;
    public double calorieGoal;
    public double proteinGoalG;
    public double carbsGoalG;
    public double fatGoalG;

    public NutrientTargets() {
    }

    public NutrientTargets(double bmr, double tdee, double calorieGoal,
                            double proteinGoalG, double carbsGoalG, double fatGoalG) {
        this.bmr = bmr;
        this.tdee = tdee;
        this.calorieGoal = calorieGoal;
        this.proteinGoalG = proteinGoalG;
        this.carbsGoalG = carbsGoalG;
        this.fatGoalG = fatGoalG;
    }
}
