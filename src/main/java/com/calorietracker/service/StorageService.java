package com.calorietracker.service;

import com.calorietracker.model.AppData;
import com.calorietracker.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

/** Loads and saves the application's persisted state as a single JSON file
 *  under the user's home directory, so data survives between app restarts. */
public class StorageService {

    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), ".calorietracker");
    private static final Path DATA_FILE = APP_DIR.resolve("data.json");

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    public AppData load() {
        if (!Files.exists(DATA_FILE)) {
            return new AppData();
        }
        try (Reader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            AppData data = gson.fromJson(reader, AppData.class);
            return data != null ? data : new AppData();
        } catch (IOException e) {
            System.err.println("Konnte gespeicherte Daten nicht laden, starte mit leerem Zustand: " + e.getMessage());
            return new AppData();
        }
    }

    public void save(AppData data) {
        try {
            Files.createDirectories(APP_DIR);
            try (Writer writer = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Konnte Daten nicht speichern: " + e.getMessage());
        }
    }
}
