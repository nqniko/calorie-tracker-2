package com.calorietracker.ui.controller;

import com.calorietracker.model.*;
import com.calorietracker.service.AppContext;
import com.calorietracker.service.NutrientRda;
import com.calorietracker.ui.AddFoodDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TodayController implements Refreshable {

    @FXML private Label dateLabel;
    @FXML private Arc calorieRingArc;
    @FXML private Label caloriesConsumedLabel;
    @FXML private Label caloriesRemainingLabel;

    @FXML private ProgressBar proteinBar;
    @FXML private Label proteinValueLabel;
    @FXML private ProgressBar carbsBar;
    @FXML private Label carbsValueLabel;
    @FXML private ProgressBar fatBar;
    @FXML private Label fatValueLabel;

    @FXML private ProgressBar fiberBar;
    @FXML private Label fiberValueLabel;
    @FXML private ProgressBar sugarBar;
    @FXML private Label sugarValueLabel;
    @FXML private ProgressBar sodiumBar;
    @FXML private Label sodiumValueLabel;
    @FXML private ProgressBar potassiumBar;
    @FXML private Label potassiumValueLabel;
    @FXML private ProgressBar calciumBar;
    @FXML private Label calciumValueLabel;
    @FXML private ProgressBar ironBar;
    @FXML private Label ironValueLabel;
    @FXML private ProgressBar vitaminCBar;
    @FXML private Label vitaminCValueLabel;
    @FXML private ProgressBar vitaminABar;
    @FXML private Label vitaminAValueLabel;

    @FXML private VBox mealBreakfastBox;
    @FXML private VBox mealLunchBox;
    @FXML private VBox mealDinnerBox;
    @FXML private VBox mealSnackBox;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN);

    private final AppContext context = AppContext.get();

    @FXML
    public void initialize() {
        context.selectedDateProperty().addListener((obs, oldVal, newVal) -> refresh());
    }

    @Override
    public void onShow() {
        refresh();
    }

    @FXML
    public void previousDay() {
        context.setSelectedDate(context.getSelectedDate().minusDays(1));
    }

    @FXML
    public void nextDay() {
        context.setSelectedDate(context.getSelectedDate().plusDays(1));
    }

    @FXML
    public void jumpToToday() {
        context.setSelectedDate(LocalDate.now());
    }

    @FXML
    public void addBreakfast() {
        addFood(MealType.BREAKFAST);
    }

    @FXML
    public void addLunch() {
        addFood(MealType.LUNCH);
    }

    @FXML
    public void addDinner() {
        addFood(MealType.DINNER);
    }

    @FXML
    public void addSnack() {
        addFood(MealType.SNACK);
    }

    private void addFood(MealType mealType) {
        AddFoodDialog dialog = new AddFoodDialog(context.getFoodDatabase(), mealType);
        Optional<DiaryEntry> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            context.addDiaryEntry(context.getSelectedDate(), entry);
            refresh();
        });
    }

    private void refresh() {
        LocalDate date = context.getSelectedDate();
        boolean isToday = date.equals(LocalDate.now());
        String formatted = date.format(DATE_FORMAT);
        dateLabel.setText(isToday ? "Heute, " + formatted : formatted);

        AppData data = context.getData();
        List<DiaryEntry> entries = data.entriesFor(date);
        NutritionInfo totals = data.totalsFor(date);
        NutrientTargets targets = data.profile.targets != null
                ? data.profile.targets
                : context.getNutritionCalculator().calculate(new UserProfile());

        updateCalorieRing(totals, targets);
        updateMacro(proteinBar, proteinValueLabel, totals.proteinG, targets.proteinGoalG, "g");
        updateMacro(carbsBar, carbsValueLabel, totals.carbsG, targets.carbsGoalG, "g");
        updateMacro(fatBar, fatValueLabel, totals.fatG, targets.fatGoalG, "g");

        updateMicro(fiberBar, fiberValueLabel, totals.fiberG, NutrientRda.FIBER_G, "g");
        updateMicro(sugarBar, sugarValueLabel, totals.sugarG, NutrientRda.SUGAR_G_LIMIT, "g");
        updateMicro(sodiumBar, sodiumValueLabel, totals.sodiumMg, NutrientRda.SODIUM_MG_LIMIT, "mg");
        updateMicro(potassiumBar, potassiumValueLabel, totals.potassiumMg, NutrientRda.POTASSIUM_MG, "mg");
        updateMicro(calciumBar, calciumValueLabel, totals.calciumMg, NutrientRda.CALCIUM_MG, "mg");
        updateMicro(ironBar, ironValueLabel, totals.ironMg, NutrientRda.IRON_MG, "mg");
        updateMicro(vitaminCBar, vitaminCValueLabel, totals.vitaminCMg, NutrientRda.VITAMIN_C_MG, "mg");
        updateMicro(vitaminABar, vitaminAValueLabel, totals.vitaminAMcg, NutrientRda.VITAMIN_A_MCG, "µg");

        populateMeal(mealBreakfastBox, entries, MealType.BREAKFAST);
        populateMeal(mealLunchBox, entries, MealType.LUNCH);
        populateMeal(mealDinnerBox, entries, MealType.DINNER);
        populateMeal(mealSnackBox, entries, MealType.SNACK);
    }

    private void updateCalorieRing(NutritionInfo totals, NutrientTargets targets) {
        double goal = Math.max(1, targets.calorieGoal);
        double fraction = totals.calories / goal;
        double clamped = Math.max(0, Math.min(1, fraction));
        calorieRingArc.setLength(-360.0 * clamped);

        calorieRingArc.getStyleClass().removeAll("ring-normal", "ring-warning", "ring-over");
        if (fraction > 1.0) {
            calorieRingArc.getStyleClass().add("ring-over");
        } else if (fraction > 0.9) {
            calorieRingArc.getStyleClass().add("ring-warning");
        } else {
            calorieRingArc.getStyleClass().add("ring-normal");
        }

        caloriesConsumedLabel.setText(String.format(Locale.GERMANY, "%.0f", totals.calories));
        double remaining = goal - totals.calories;
        if (remaining >= 0) {
            caloriesRemainingLabel.setText(String.format(Locale.GERMANY, "Noch %.0f von %.0f kcal", remaining, goal));
        } else {
            caloriesRemainingLabel.setText(String.format(Locale.GERMANY, "%.0f kcal über Ziel (%.0f)", -remaining, goal));
        }
    }

    private void updateMacro(ProgressBar bar, Label label, double value, double goal, String unit) {
        double safeGoal = Math.max(1, goal);
        bar.setProgress(Math.max(0, Math.min(1, value / safeGoal)));
        label.setText(String.format(Locale.GERMANY, "%.0f / %.0f %s", value, goal, unit));
    }

    private void updateMicro(ProgressBar bar, Label label, double value, double goal, String unit) {
        bar.setProgress(Math.max(0, Math.min(1, value / goal)));
        label.setText(String.format(Locale.GERMANY, "%.0f / %.0f %s", value, goal, unit));
    }

    private void populateMeal(VBox box, List<DiaryEntry> entries, MealType mealType) {
        box.getChildren().clear();
        List<DiaryEntry> mealEntries = entries.stream().filter(e -> e.meal == mealType).toList();
        if (mealEntries.isEmpty()) {
            Label placeholder = new Label("Noch keine Einträge");
            placeholder.getStyleClass().add("empty-placeholder");
            box.getChildren().add(placeholder);
            return;
        }
        for (DiaryEntry entry : mealEntries) {
            box.getChildren().add(buildEntryRow(entry));
        }
    }

    private HBox buildEntryRow(DiaryEntry entry) {
        Label nameLabel = new Label(entry.foodName);
        nameLabel.getStyleClass().add("entry-name");

        Label gramsLabel = new Label(String.format(Locale.GERMANY, "%.0f g", entry.grams));
        gramsLabel.getStyleClass().add("entry-detail");

        Label kcalLabel = new Label(String.format(Locale.GERMANY, "%.0f kcal", entry.nutrition.calories));
        kcalLabel.getStyleClass().add("entry-detail");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("✕");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> {
            context.removeDiaryEntry(context.getSelectedDate(), entry);
            refresh();
        });

        HBox row = new HBox(12, nameLabel, gramsLabel, kcalLabel, spacer, deleteButton);
        row.getStyleClass().add("entry-row");
        row.setStyle("-fx-alignment: CENTER_LEFT;");
        return row;
    }
}
