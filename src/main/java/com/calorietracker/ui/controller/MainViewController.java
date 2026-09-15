package com.calorietracker.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainViewController {

    @FXML private StackPane contentArea;
    @FXML private Button navToday;
    @FXML private Button navCalendar;
    @FXML private Button navProfile;
    @FXML private Button navSettings;

    private final Map<String, Parent> viewCache = new HashMap<>();
    private final Map<String, Object> controllerCache = new HashMap<>();

    @FXML
    public void initialize() {
        showToday();
    }

    @FXML
    public void showToday() {
        show("Today", navToday);
    }

    @FXML
    public void showCalendar() {
        show("Calendar", navCalendar);
    }

    @FXML
    public void showProfile() {
        show("Profile", navProfile);
    }

    @FXML
    public void showSettings() {
        show("Settings", navSettings);
    }

    private void show(String viewName, Button activeButton) {
        try {
            Parent view = viewCache.get(viewName);
            if (view == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/calorietracker/fxml/" + viewName + ".fxml"));
                view = loader.load();
                viewCache.put(viewName, view);
                controllerCache.put(viewName, loader.getController());
            }
            contentArea.getChildren().setAll(view);

            Object controller = controllerCache.get(viewName);
            if (controller instanceof Refreshable refreshable) {
                refreshable.onShow();
            }

            for (Node btn : new Button[]{navToday, navCalendar, navProfile, navSettings}) {
                btn.getStyleClass().remove("nav-button-active");
            }
            activeButton.getStyleClass().add("nav-button-active");
        } catch (IOException e) {
            throw new RuntimeException("Ansicht konnte nicht geladen werden: " + viewName, e);
        }
    }
}
