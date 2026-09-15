package com.calorietracker.model;

/**
 * Immutable set of nutrition values. When created from a {@link FoodItem} the values
 * are per 100g; when scaled to a serving size or summed across diary entries they
 * represent an absolute amount.
 */
public class NutritionInfo {

    public final double calories;
    public final double proteinG;
    public final double carbsG;
    public final double fatG;
    public final double fiberG;
    public final double sugarG;
    public final double sodiumMg;
    public final double potassiumMg;
    public final double calciumMg;
    public final double ironMg;
    public final double vitaminCMg;
    public final double vitaminAMcg;

    public NutritionInfo(double calories, double proteinG, double carbsG, double fatG,
                          double fiberG, double sugarG, double sodiumMg, double potassiumMg,
                          double calciumMg, double ironMg, double vitaminCMg, double vitaminAMcg) {
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
        this.fiberG = fiberG;
        this.sugarG = sugarG;
        this.sodiumMg = sodiumMg;
        this.potassiumMg = potassiumMg;
        this.calciumMg = calciumMg;
        this.ironMg = ironMg;
        this.vitaminCMg = vitaminCMg;
        this.vitaminAMcg = vitaminAMcg;
    }

    public static NutritionInfo empty() {
        return new NutritionInfo(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /** Scales all values by the given factor, e.g. grams/100.0 to go from per-100g to an actual serving. */
    public NutritionInfo scale(double factor) {
        return new NutritionInfo(
                calories * factor, proteinG * factor, carbsG * factor, fatG * factor,
                fiberG * factor, sugarG * factor, sodiumMg * factor, potassiumMg * factor,
                calciumMg * factor, ironMg * factor, vitaminCMg * factor, vitaminAMcg * factor
        );
    }

    public NutritionInfo plus(NutritionInfo other) {
        return new NutritionInfo(
                calories + other.calories, proteinG + other.proteinG, carbsG + other.carbsG, fatG + other.fatG,
                fiberG + other.fiberG, sugarG + other.sugarG, sodiumMg + other.sodiumMg, potassiumMg + other.potassiumMg,
                calciumMg + other.calciumMg, ironMg + other.ironMg, vitaminCMg + other.vitaminCMg, vitaminAMcg + other.vitaminAMcg
        );
    }
}
