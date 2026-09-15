package com.calorietracker.ui;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/** A small iOS-style on/off switch, since JavaFX has no built-in equivalent. */
public class ToggleSwitch extends StackPane {

    private static final double WIDTH = 46;
    private static final double HEIGHT = 26;
    private static final double KNOB_RADIUS = 11;
    private static final double TRAVEL = WIDTH - HEIGHT;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private final Region track = new Region();
    private final Circle knob = new Circle(KNOB_RADIUS);

    public ToggleSwitch(boolean initial) {
        track.getStyleClass().add("switch-track");
        track.setPrefSize(WIDTH, HEIGHT);
        track.setMinSize(WIDTH, HEIGHT);
        track.setMaxSize(WIDTH, HEIGHT);

        knob.getStyleClass().add("switch-knob");
        StackPane.setAlignment(knob, Pos.CENTER_LEFT);
        StackPane.setMargin(knob, new Insets(0, 0, 0, 3));

        getChildren().addAll(track, knob);
        setCursor(javafx.scene.Cursor.HAND);
        setOnMouseClicked(e -> setSelected(!isSelected()));

        selected.addListener((obs, oldVal, newVal) -> updateVisual(newVal, true));
        setSelected(initial);
        updateVisual(initial, false);
    }

    private void updateVisual(boolean isOn, boolean animate) {
        track.getStyleClass().removeAll("switch-track-on", "switch-track-off");
        track.getStyleClass().add(isOn ? "switch-track-on" : "switch-track-off");

        double target = isOn ? TRAVEL : 0;
        if (animate) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(140), knob);
            transition.setToX(target);
            transition.play();
        } else {
            knob.setTranslateX(target);
        }
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }
}
