package com.mybasics.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mybasics.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Sets the text for the description for the app
        ((TextView) findViewById(R.id.tvDescription))
                .setText(
                "MyBasics is a project created by three 3rd year Information Technology students at the University of Santo Tomas during the First Semester of School Year 2021-2022 as a requirement for the course, Mobile Programming (ICS 26011)." +
                        "\n\nThe goal of this application is to provide compiled features that are important to the everyday mobile usage and interaction of mobile users namely the To-Do list, Notes/Memos, and Reminders. To further enhance the efficiency of these basic features, we incorporated the following convenient user interactions:");

        // Adds a listener to the navigation button of the toolbar
        ((Toolbar) findViewById(R.id.aboutToolbar)).setNavigationOnClickListener(v -> finish());
    }
}