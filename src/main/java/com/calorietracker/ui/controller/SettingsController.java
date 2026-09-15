package com.calorietracker.ui.controller;

import com.calorietracker.service.AppContext;
import com.calorietracker.ui.ToggleSwitch;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class SettingsController {

    @FXML private StackPane darkModeSwitchContainer;

    private final AppContext context = AppContext.get();

    @FXML
    public void initialize() {
        ToggleSwitch darkModeSwitch = new ToggleSwitch(context.darkModeProperty().get());
        darkModeSwitch.selectedProperty().bindBidirectional(context.darkModeProperty());
        darkModeSwitchContainer.getChildren().add(darkModeSwitch);
    }

    @FXML
    public void resetData() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Möchtest du wirklich dein Profil und alle Tagebucheinträge löschen? Dies kann nicht rückgängig gemacht werden.",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Alle Daten zurücksetzen");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            context.resetAllData();
            Alert done = new Alert(Alert.AlertType.INFORMATION, "Alle Daten wurden zurückgesetzt.");
            done.setHeaderText(null);
            done.showAndWait();
        }
    }
}
