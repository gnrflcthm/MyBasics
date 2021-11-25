package com.mybasics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.mybasics.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadThemes();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Sets the default preferences when app is newly installed
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(() -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1000);
    }

    /**
     * Loads Themes based on shared preferences.
     */
    private void loadThemes() {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("app_theme", "Light");
        switch (theme) {
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "System Default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "Light":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}