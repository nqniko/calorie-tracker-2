package com.calorietracker.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The full persisted state of the application: profile, settings and the food diary. */
public class AppData {

    public UserProfile profile = new UserProfile();
    public AppSettings settings = new AppSettings();
    public Map<LocalDate, List<DiaryEntry>> diary = new LinkedHashMap<>();

    /** Read-only view of a day's entries; never mutates the diary map (so merely
     *  displaying a date, e.g. in the calendar grid, doesn't create empty entries). */
    public List<DiaryEntry> entriesFor(LocalDate date) {
        return diary.getOrDefault(date, Collections.emptyList());
    }

    public NutritionInfo totalsFor(LocalDate date) {
        NutritionInfo total = NutritionInfo.empty();
        for (DiaryEntry entry : entriesFor(date)) {
            total = total.plus(entry.nutrition);
        }
        return total;
    }

    public double caloriesFor(LocalDate date) {
        return totalsFor(date).calories;
    }
}
