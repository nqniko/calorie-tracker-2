package com.calorietracker.service;

import com.calorietracker.model.AppData;
import com.calorietracker.model.DiaryEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Central, shared application state: the loaded data, services and a couple of
 *  observable properties (selected date, dark mode) so different screens stay in sync. */
public final class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    public static AppContext get() {
        return INSTANCE;
    }

    private final StorageService storageService = new StorageService();
    private final FoodDatabaseService foodDatabaseService = new FoodDatabaseService();
    private final NutritionCalculator nutritionCalculator = new NutritionCalculator();

    private final AppData data;
    private final ObjectProperty<LocalDate> selectedDate = new SimpleObjectProperty<>(LocalDate.now());
    private final BooleanProperty darkMode;

    private AppContext() {
        this.data = storageService.load();
        this.darkMode = new SimpleBooleanProperty(data.settings.darkMode);
        darkMode.addListener((obs, oldVal, newVal) -> {
            data.settings.darkMode = newVal;
            save();
        });
    }

    public AppData getData() {
        return data;
    }

    public FoodDatabaseService getFoodDatabase() {
        return foodDatabaseService;
    }

    public NutritionCalculator getNutritionCalculator() {
        return nutritionCalculator;
    }

    public ObjectProperty<LocalDate> selectedDateProperty() {
        return selectedDate;
    }

    public LocalDate getSelectedDate() {
        return selectedDate.get();
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.set(date);
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public void addDiaryEntry(LocalDate date, DiaryEntry entry) {
        List<DiaryEntry> entries = data.diary.computeIfAbsent(date, d -> new ArrayList<>());
        entries.add(entry);
        save();
    }

    public void removeDiaryEntry(LocalDate date, DiaryEntry entry) {
        List<DiaryEntry> entries = data.diary.get(date);
        if (entries != null) {
            entries.remove(entry);
            if (entries.isEmpty()) {
                data.diary.remove(date);
            }
        }
        save();
    }

    public void save() {
        storageService.save(data);
    }

    public void resetAllData() {
        data.diary.clear();
        data.profile = new com.calorietracker.model.UserProfile();
        save();
    }
}
