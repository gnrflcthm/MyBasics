package com.mybasics.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mybasics.R;

public class DevelopersActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        toolbar = findViewById(R.id.devActToolbar);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

}