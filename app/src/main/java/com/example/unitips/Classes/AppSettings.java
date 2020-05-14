package com.example.unitips.Classes;

import androidx.appcompat.app.AppCompatDelegate;

public class AppSettings {

    // 1 = on, 0 = off

    static int currentNightMode;

    int pushNotifications = 1;

    // gets the default settings from the
    public static int getDefaultNightModeSettings() {
        return AppCompatDelegate.getDefaultNightMode();
    }

    public int toggleNightMode() {
        if (currentNightMode == 0) {
            currentNightMode = 1;
        } else {
            currentNightMode = 0;
        }

        return currentNightMode;
    }
}
