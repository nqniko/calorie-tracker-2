package com.calorietracker.ui.controller;

/** Implemented by screen controllers that need to reload their data whenever
 *  the user navigates to them, since the underlying data may have changed
 *  while another screen was visible. */
public interface Refreshable {
    void onShow();
}
