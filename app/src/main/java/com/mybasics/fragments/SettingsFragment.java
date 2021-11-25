package com.mybasics.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.mybasics.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final String APP_THEME = "app_theme";
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        preferenceChangeListener = ((sharedPreferences, key) -> {
            switch(key) {
                case APP_THEME:
                    Preference appTheme = findPreference(key);
                    appTheme.setSummary("Current: " + sharedPreferences.getString(key, "Light"));
                    loadThemes();
                    break;
            }
        });
    }

    /**
     * Unbinds the preference listener.
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    /**
     * Registers the Preference Listener and sets the summary
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        Preference appTheme = findPreference(APP_THEME);
        appTheme.setSummary("Current: " + sharedPreferences.getString(APP_THEME, "light"));
    }

    /**
     * Loads themes based on shared preferences.
     */
    private void loadThemes() {
        String theme = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("app_theme", "Light");
        switch (theme) {
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "System Default":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
