package com.calorietracker.service;

import com.calorietracker.model.Gender;
import com.calorietracker.model.NutrientTargets;
import com.calorietracker.model.UserProfile;

/** Computes BMR, TDEE and daily calorie/macro recommendations from a user's profile
 *  using the Mifflin-St Jeor equation. */
public class NutritionCalculator {

    private static final double MIN_CALORIES = 1200;
    private static final double PROTEIN_G_PER_KG = 1.8;
    private static final double FAT_PERCENT_OF_CALORIES = 0.28;

    public NutrientTargets calculate(UserProfile profile) {
        double bmr = bmr(profile);
        double tdee = bmr * profile.activityLevel.getFactor();
        double calorieGoal = Math.max(MIN_CALORIES, tdee + profile.goal.getCalorieAdjustment());

        double proteinG = profile.weightKg * PROTEIN_G_PER_KG;
        double fatG = (calorieGoal * FAT_PERCENT_OF_CALORIES) / 9.0;
        double remainingCalories = Math.max(0, calorieGoal - (proteinG * 4) - (fatG * 9));
        double carbsG = remainingCalories / 4.0;

        return new NutrientTargets(bmr, tdee, calorieGoal, proteinG, carbsG, fatG);
    }

    private double bmr(UserProfile profile) {
        double base = 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age;
        return profile.gender == Gender.MALE ? base + 5 : base - 161;
    }
}
