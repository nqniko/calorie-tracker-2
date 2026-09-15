package com.calorietracker;

import com.calorietracker.service.AppContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class CalorieTrackerApp extends Application {

    private static final String BASE_CSS = "/com/calorietracker/css/base.css";
    private static final String LIGHT_CSS = "/com/calorietracker/css/light.css";
    private static final String DARK_CSS = "/com/calorietracker/css/dark.css";

    @Override
    public void start(Stage stage) throws IOException {
        URL fxml = getClass().getResource("/com/calorietracker/fxml/MainView.fxml");
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1180, 760);
        scene.getStylesheets().add(getClass().getResource(BASE_CSS).toExternalForm());

        AppContext context = AppContext.get();
        applyTheme(scene, context.darkModeProperty().get());
        context.darkModeProperty().addListener((obs, oldVal, newVal) -> applyTheme(scene, newVal));

        stage.setTitle("Kalorien Tracker");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> context.save());
        stage.show();
    }

    private void applyTheme(Scene scene, boolean darkMode) {
        scene.getStylesheets().removeIf(s -> s.endsWith("light.css") || s.endsWith("dark.css"));
        String theme = darkMode ? DARK_CSS : LIGHT_CSS;
        scene.getStylesheets().add(getClass().getResource(theme).toExternalForm());
        if (scene.getRoot() != null) {
            scene.getRoot().getStyleClass().removeAll("light", "dark");
            scene.getRoot().getStyleClass().add(darkMode ? "dark" : "light");
        }
    }

    @Override
    public void stop() {
        AppContext.get().save();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
