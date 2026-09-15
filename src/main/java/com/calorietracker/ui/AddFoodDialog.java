package com.calorietracker.ui;

import com.calorietracker.model.DiaryEntry;
import com.calorietracker.model.FoodItem;
import com.calorietracker.model.MealType;
import com.calorietracker.model.NutritionInfo;
import com.calorietracker.service.FoodDatabaseService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Locale;

/** Dialog for logging a food: the user types a name, picks a match from the built-in
 *  nutrition database (automatic nutrient recognition), enters the amount in grams and
 *  sees a live preview of the resulting calories/macros before adding it to the diary. */
public class AddFoodDialog extends Dialog<DiaryEntry> {

    private final FoodDatabaseService foodDatabase;
    private FoodItem selectedFood;

    private final TextField searchField = new TextField();
    private final ListView<FoodItem> resultsList = new ListView<>();
    private final TextField gramsField = new TextField();
    private final ComboBox<MealType> mealCombo = new ComboBox<>();
    private final Label previewLabel = new Label("Wähle ein Lebensmittel aus der Liste.");
    private final ButtonType addButtonType = new ButtonType("Hinzufügen", ButtonBar.ButtonData.OK_DONE);

    public AddFoodDialog(FoodDatabaseService foodDatabase, MealType defaultMeal) {
        this.foodDatabase = foodDatabase;

        setTitle("Lebensmittel hinzufügen");
        setHeaderText("Lebensmittel suchen und Menge eingeben");
        getDialogPane().getStyleClass().add("apple-dialog");
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        searchField.setPromptText("z. B. Apfel, Hähnchenbrust, Reis ...");
        resultsList.setPrefHeight(180);
        resultsList.setPlaceholder(new Label("Keine Treffer"));
        resultsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(FoodItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.GERMANY, "%s  ·  %s  ·  %.0f kcal/100g",
                            item.getName(), item.getCategory(), item.per100g().calories));
                }
            }
        });
        resultsList.setItems(FXCollections.observableArrayList(foodDatabase.search("")));

        gramsField.setPromptText("Menge in Gramm");

        mealCombo.getItems().setAll(MealType.values());
        mealCombo.setValue(defaultMeal);

        HBox mealRow = new HBox(10, new Label("Mahlzeit:"), mealCombo);
        mealRow.setStyle("-fx-alignment: CENTER_LEFT;");

        HBox gramsRow = new HBox(10, new Label("Menge (g):"), gramsField);
        gramsRow.setStyle("-fx-alignment: CENTER_LEFT;");
        HBox.setHgrow(gramsField, Priority.ALWAYS);

        previewLabel.getStyleClass().add("dialog-preview");
        previewLabel.setWrapText(true);

        VBox content = new VBox(10, searchField, resultsList, gramsRow, mealRow, previewLabel);
        content.setPadding(new Insets(12, 4, 4, 4));
        content.setPrefWidth(420);
        getDialogPane().setContent(content);

        Button addButton = (Button) getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                resultsList.setItems(FXCollections.observableArrayList(foodDatabase.search(newVal))));

        resultsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedFood = newVal;
            if (newVal != null && gramsField.getText().isBlank()) {
                gramsField.setText(String.valueOf((int) Math.round(newVal.getServingSizeGrams())));
            }
            updatePreview();
            addButton.setDisable(!isValid());
        });

        gramsField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePreview();
            addButton.setDisable(!isValid());
        });

        setResultConverter(buttonType -> {
            if (buttonType == addButtonType && isValid()) {
                double grams = parseGrams();
                NutritionInfo nutrition = selectedFood.forGrams(grams);
                return new DiaryEntry(selectedFood.getName(), grams, mealCombo.getValue(), nutrition);
            }
            return null;
        });
    }

    private boolean isValid() {
        return selectedFood != null && parseGrams() > 0;
    }

    private double parseGrams() {
        try {
            return Double.parseDouble(gramsField.getText().trim().replace(",", "."));
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    private void updatePreview() {
        if (selectedFood == null) {
            previewLabel.setText("Wähle ein Lebensmittel aus der Liste.");
            return;
        }
        double grams = parseGrams();
        if (grams <= 0) {
            previewLabel.setText(selectedFood.getName() + ": Menge in Gramm eingeben.");
            return;
        }
        NutritionInfo n = selectedFood.forGrams(grams);
        previewLabel.setText(String.format(Locale.GERMANY,
                "%s (%.0f g) → %.0f kcal · Protein %.1f g · Kohlenhydrate %.1f g · Fett %.1f g",
                selectedFood.getName(), grams, n.calories, n.proteinG, n.carbsG, n.fatG));
    }
}
