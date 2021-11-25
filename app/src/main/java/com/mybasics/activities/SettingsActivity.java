package com.mybasics.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;

import com.mybasics.R;
import com.mybasics.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;    // Toolbar
    private FragmentContainerView fragmentContainer; // Fragment Container
    private SettingsFragment settingsFragment;  // Fragment which holds all preferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();

    }

    /**
     * Initializes Activity Components
     */
    private void init() {
        toolbar = findViewById(R.id.settingsToolbar);
        fragmentContainer = findViewById(R.id.settingsFragmentContainer);

        toolbar.setNavigationOnClickListener(v -> finish());

        settingsFragment = new SettingsFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentContainer.getId(), settingsFragment)
                .commit();

    }
}