package com.calorietracker.model;

import java.util.UUID;

/** A single logged food entry on a given day. The nutrition snapshot is captured at
 *  creation time so that later edits to the food database don't rewrite history. */
public class DiaryEntry {

    public String id = UUID.randomUUID().toString();
    public String foodName;
    public double grams;
    public MealType meal;
    public NutritionInfo nutrition;

    public DiaryEntry() {
    }

    public DiaryEntry(String foodName, double grams, MealType meal, NutritionInfo nutrition) {
        this.foodName = foodName;
        this.grams = grams;
        this.meal = meal;
        this.nutrition = nutrition;
    }
}
