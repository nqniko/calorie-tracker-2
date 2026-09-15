package com.calorietracker.model;

/** A food from the built-in nutrition database, with values per 100g. */
public class FoodItem {

    private String name;
    private String category;
    private double servingSizeGrams;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;
    private double sugar;
    private double sodiumMg;
    private double potassiumMg;
    private double calciumMg;
    private double ironMg;
    private double vitaminCMg;
    private double vitaminAMcg;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getServingSizeGrams() {
        return servingSizeGrams;
    }

    public NutritionInfo per100g() {
        return new NutritionInfo(calories, protein, carbs, fat, fiber, sugar,
                sodiumMg, potassiumMg, calciumMg, ironMg, vitaminCMg, vitaminAMcg);
    }

    /** Returns the nutrition values scaled to the given amount of grams. */
    public NutritionInfo forGrams(double grams) {
        return per100g().scale(grams / 100.0);
    }
}
