package com.calorietracker.ui.controller;

import com.calorietracker.model.*;
import com.calorietracker.service.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class CalendarController implements Refreshable {

    @FXML private Label monthYearLabel;
    @FXML private HBox weekdayHeader;
    @FXML private GridPane dayGrid;

    @FXML private Label selectedDateLabel;
    @FXML private Label selectedCaloriesLabel;
    @FXML private VBox selectedMacrosBox;
    @FXML private VBox selectedEntriesBox;

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMAN);
    private static final DateTimeFormatter DAY_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, d. MMMM yyyy", Locale.GERMAN);

    private final AppContext context = AppContext.get();
    private YearMonth currentMonth = YearMonth.now();

    @FXML
    public void initialize() {
        for (DayOfWeek day : DayOfWeek.values()) {
            Label label = new Label(day.getDisplayName(TextStyle.SHORT, Locale.GERMAN));
            label.getStyleClass().add("weekday-label");
            label.setMaxWidth(Double.MAX_VALUE);
            label.setStyle("-fx-alignment: CENTER;");
            HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);
            weekdayHeader.getChildren().add(label);
        }
    }

    @Override
    public void onShow() {
        currentMonth = YearMonth.from(context.getSelectedDate());
        rebuildGrid();
        refreshDetail();
    }

    @FXML
    public void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        rebuildGrid();
    }

    @FXML
    public void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        rebuildGrid();
    }

    @FXML
    public void jumpToToday() {
        context.setSelectedDate(LocalDate.now());
        currentMonth = YearMonth.now();
        rebuildGrid();
        refreshDetail();
    }

    private void rebuildGrid() {
        dayGrid.getChildren().clear();
        monthYearLabel.setText(capitalize(currentMonth.format(MONTH_FORMAT)));

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstColumn = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate selected = context.getSelectedDate();
        LocalDate today = LocalDate.now();

        int row = 0;
        int col = firstColumn;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            dayGrid.add(buildDayCell(date, date.equals(selected), date.equals(today)), col, row);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private VBox buildDayCell(LocalDate date, boolean selected, boolean isToday) {
        VBox cell = new VBox(4);
        cell.getStyleClass().add("calendar-day");
        if (selected) cell.getStyleClass().add("calendar-day-selected");
        if (isToday) cell.getStyleClass().add("calendar-day-today");
        cell.setPrefSize(90, 62);
        cell.setMinSize(60, 56);

        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.getStyleClass().add("calendar-day-number");

        List<DiaryEntry> entries = context.getData().entriesFor(date);
        Label indicator = new Label();
        indicator.getStyleClass().add("calendar-day-indicator");
        if (!entries.isEmpty()) {
            double calories = context.getData().totalsFor(date).calories;
            NutrientTargets targets = resolveTargets();
            double ratio = calories / Math.max(1, targets.calorieGoal);
            indicator.setText(String.format(Locale.GERMANY, "%.0f kcal", calories));
            if (ratio > 1.1) {
                indicator.getStyleClass().add("calendar-indicator-over");
            } else if (ratio > 0.9) {
                indicator.getStyleClass().add("calendar-indicator-near");
            } else {
                indicator.getStyleClass().add("calendar-indicator-under");
            }
        }

        cell.getChildren().addAll(dayNumber, indicator);
        cell.setOnMouseClicked(e -> {
            context.setSelectedDate(date);
            rebuildGrid();
            refreshDetail();
        });
        return cell;
    }

    private void refreshDetail() {
        LocalDate date = context.getSelectedDate();
        selectedDateLabel.setText(capitalize(date.format(DAY_FORMAT)));

        AppData data = context.getData();
        NutritionInfo totals = data.totalsFor(date);
        NutrientTargets targets = resolveTargets();

        selectedCaloriesLabel.setText(String.format(Locale.GERMANY, "%.0f / %.0f kcal",
                totals.calories, targets.calorieGoal));

        selectedMacrosBox.getChildren().setAll(
                macroRow("Protein", totals.proteinG, targets.proteinGoalG),
                macroRow("Kohlenhydrate", totals.carbsG, targets.carbsGoalG),
                macroRow("Fett", totals.fatG, targets.fatGoalG)
        );

        List<DiaryEntry> entries = data.entriesFor(date);
        selectedEntriesBox.getChildren().clear();
        if (entries.isEmpty()) {
            Label placeholder = new Label("Keine Einträge an diesem Tag");
            placeholder.getStyleClass().add("empty-placeholder");
            selectedEntriesBox.getChildren().add(placeholder);
        } else {
            for (DiaryEntry entry : entries) {
                Label row = new Label(String.format(Locale.GERMANY, "%s · %s (%.0f g) · %.0f kcal",
                        entry.meal, entry.foodName, entry.grams, entry.nutrition.calories));
                row.getStyleClass().add("entry-detail");
                row.setWrapText(true);
                selectedEntriesBox.getChildren().add(row);
            }
        }
    }

    private Label macroRow(String name, double value, double goal) {
        Label label = new Label(String.format(Locale.GERMANY, "%s: %.0f / %.0f g", name, value, goal));
        label.getStyleClass().add("micro-value");
        return label;
    }

    private NutrientTargets resolveTargets() {
        UserProfile profile = context.getData().profile;
        return profile.targets != null ? profile.targets : context.getNutritionCalculator().calculate(new UserProfile());
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
