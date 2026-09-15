package com.calorietracker.service;

import com.calorietracker.model.FoodItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Provides the built-in nutrition database and lets the diary screen "recognize"
 *  a food the user types by matching it against known items. */
public class FoodDatabaseService {

    private final List<FoodItem> foods = new ArrayList<>();

    public FoodDatabaseService() {
        loadFoods();
    }

    private void loadFoods() {
        try (InputStream in = getClass().getResourceAsStream("/data/foods.json")) {
            if (in == null) {
                System.err.println("Nährstoffdatenbank nicht gefunden (data/foods.json).");
                return;
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FoodItem>>() {}.getType();
            List<FoodItem> loaded = gson.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), listType);
            if (loaded != null) {
                foods.addAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Nährstoffdatenbank: " + e.getMessage());
        }
    }

    public List<FoodItem> all() {
        return foods;
    }

    /** Searches for foods whose name contains the query (case-insensitive).
     *  Results that start with the query are ranked first, mimicking automatic
     *  nutrient recognition as the user types a food name. */
    public List<FoodItem> search(String query) {
        if (query == null || query.isBlank()) {
            return foods.stream()
                    .sorted(Comparator.comparing(FoodItem::getName))
                    .limit(30)
                    .toList();
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        return foods.stream()
                .filter(f -> f.getName().toLowerCase(Locale.ROOT).contains(q))
                .sorted(Comparator.comparingInt((FoodItem f) -> rank(f, q))
                        .thenComparing(FoodItem::getName))
                .limit(30)
                .toList();
    }

    private int rank(FoodItem f, String q) {
        return f.getName().toLowerCase(Locale.ROOT).startsWith(q) ? 0 : 1;
    }

    public FoodItem findByName(String name) {
        return foods.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }
}
