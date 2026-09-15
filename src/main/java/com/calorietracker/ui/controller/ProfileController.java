package com.calorietracker.ui.controller;

import com.calorietracker.model.ActivityLevel;
import com.calorietracker.model.Gender;
import com.calorietracker.model.Goal;
import com.calorietracker.model.NutrientTargets;
import com.calorietracker.model.UserProfile;
import com.calorietracker.service.AppContext;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.Locale;

public class ProfileController implements Refreshable {

    @FXML private TextField nameField;
    @FXML private ToggleButton genderMaleToggle;
    @FXML private ToggleButton genderFemaleToggle;
    @FXML private Spinner<Integer> ageSpinner;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private ComboBox<ActivityLevel> activityCombo;
    @FXML private ToggleButton goalLoseToggle;
    @FXML private ToggleButton goalMaintainToggle;
    @FXML private ToggleButton goalGainToggle;
    @FXML private Label savedNoticeLabel;

    @FXML private Label bmrValueLabel;
    @FXML private Label tdeeValueLabel;
    @FXML private Label calorieGoalValueLabel;
    @FXML private Label proteinGoalLabel;
    @FXML private Label carbsGoalLabel;
    @FXML private Label fatGoalLabel;

    private final AppContext context = AppContext.get();

    @FXML
    public void initialize() {
        genderMaleToggle.setUserData(Gender.MALE);
        genderFemaleToggle.setUserData(Gender.FEMALE);
        goalLoseToggle.setUserData(Goal.LOSE_WEIGHT);
        goalMaintainToggle.setUserData(Goal.MAINTAIN);
        goalGainToggle.setUserData(Goal.GAIN_WEIGHT);

        activityCombo.setItems(FXCollections.observableArrayList(ActivityLevel.values()));
        ageSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 100, 30));
    }

    @Override
    public void onShow() {
        loadFromProfile();
    }

    private void loadFromProfile() {
        UserProfile profile = context.getData().profile;
        nameField.setText(profile.name);
        (profile.gender == Gender.MALE ? genderMaleToggle : genderFemaleToggle).setSelected(true);
        ageSpinner.getValueFactory().setValue(profile.age);
        heightField.setText(formatNumber(profile.heightCm));
        weightField.setText(formatNumber(profile.weightKg));
        activityCombo.setValue(profile.activityLevel);

        ToggleButton goalToggle = switch (profile.goal) {
            case LOSE_WEIGHT -> goalLoseToggle;
            case MAINTAIN -> goalMaintainToggle;
            case GAIN_WEIGHT -> goalGainToggle;
        };
        goalToggle.setSelected(true);

        if (profile.targets != null) {
            updateResults(profile.targets);
        }
    }

    @FXML
    public void saveProfile() {
        UserProfile profile = context.getData().profile;

        Double height = parseDouble(heightField.getText());
        Double weight = parseDouble(weightField.getText());
        if (height == null || height <= 0 || weight == null || weight <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Bitte gib eine gültige Größe und ein gültiges Gewicht ein.");
            alert.setHeaderText("Ungültige Eingabe");
            alert.showAndWait();
            return;
        }

        profile.name = nameField.getText() == null ? "" : nameField.getText().trim();
        profile.gender = (Gender) genderMaleToggle.getToggleGroup().getSelectedToggle().getUserData();
        profile.age = ageSpinner.getValue();
        profile.heightCm = height;
        profile.weightKg = weight;
        profile.activityLevel = activityCombo.getValue() != null ? activityCombo.getValue() : ActivityLevel.MODERATE;
        profile.goal = (Goal) goalLoseToggle.getToggleGroup().getSelectedToggle().getUserData();

        NutrientTargets targets = context.getNutritionCalculator().calculate(profile);
        profile.targets = targets;
        context.save();

        updateResults(targets);
        showSavedNotice();
    }

    private void updateResults(NutrientTargets targets) {
        bmrValueLabel.setText(String.format(Locale.GERMANY, "%.0f kcal / Tag", targets.bmr));
        tdeeValueLabel.setText(String.format(Locale.GERMANY, "%.0f kcal / Tag", targets.tdee));
        calorieGoalValueLabel.setText(String.format(Locale.GERMANY, "%.0f kcal", targets.calorieGoal));
        proteinGoalLabel.setText(String.format(Locale.GERMANY, "Protein: %.0f g", targets.proteinGoalG));
        carbsGoalLabel.setText(String.format(Locale.GERMANY, "Kohlenhydrate: %.0f g", targets.carbsGoalG));
        fatGoalLabel.setText(String.format(Locale.GERMANY, "Fett: %.0f g", targets.fatGoalG));
    }

    private void showSavedNotice() {
        savedNoticeLabel.setVisible(true);
        savedNoticeLabel.setManaged(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> {
            savedNoticeLabel.setVisible(false);
            savedNoticeLabel.setManaged(false);
        });
        pause.play();
    }

    private static String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private static Double parseDouble(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
