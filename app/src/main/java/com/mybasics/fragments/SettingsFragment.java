package com.mybasics.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.mybasics.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final String APP_THEME = "app_theme";
    private final String ITEM_STYLE = "item_style";
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        preferenceChangeListener = ((sharedPreferences, key) -> {
            switch(key) {
                case APP_THEME:
                    Preference appTheme = findPreference(key);
                    appTheme.setSummary("Current: " + sharedPreferences.getString(key, "light"));
                    break;
                case ITEM_STYLE:
                    Preference itemStyle = findPreference(key);
                    itemStyle.setSummary(sharedPreferences.getString(key, "Detailed"));
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        Preference appTheme = findPreference(APP_THEME);
        appTheme.setSummary("Current: " + sharedPreferences.getString(APP_THEME, "light"));

        Preference itemStyle = findPreference(ITEM_STYLE);
        itemStyle.setSummary(sharedPreferences.getString(ITEM_STYLE, "Detailed"));
    }
}
