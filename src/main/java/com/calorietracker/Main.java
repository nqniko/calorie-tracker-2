package com.calorietracker;

/** Plain launcher class (no javafx.application.Application supertype) so the app
 *  also starts cleanly when run as a classpath fat-jar without module-path setup. */
public class Main {
    public static void main(String[] args) {
        CalorieTrackerApp.main(args);
    }
}
