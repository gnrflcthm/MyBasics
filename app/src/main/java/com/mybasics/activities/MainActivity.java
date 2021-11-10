package com.mybasics.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mybasics.R;
import com.mybasics.fragments.IndexableFragment;
import com.mybasics.fragments.NotesFragment;
import com.mybasics.fragments.RemindersFragment;
import com.mybasics.fragments.TodosFragment;

public class MainActivity extends AppCompatActivity {
    /**
     * App Bar
     */
    private MaterialToolbar appBar;

    /**
     * Root Layout (RelativeLayout in activity_main.xml)
     */
    private View rootView;

    /**
     * Bottom Navigation and Fragment Container
     */
    private BottomNavigationView bottomNav;
    private FragmentContainerView fragmentContainer;

    /**
     * Used For handling fragment transactions
     */
    private FragmentManager fragmentManager;

    /**
     * Boolean to ensure the app closes after pressing the back button twice
     */
    private boolean exitOnNextBack;


    /**
     * Fragments representing the 3 different application components (Todos, Notes, Reminders)
     */
    private TodosFragment todos;
    private NotesFragment notes;
    private RemindersFragment reminders;

    /**
     * Keeps track of the current and previous fragments.
     * Mainly used for navigation purposes.
     */
    private IndexableFragment currentFragment = null;
    private IndexableFragment previousFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exitOnNextBack = false;
        init();
    }

    /**
     * Initializes all screen components, fragments, and adds
     * a Listener to the BottomNavigationView to navigate properly.
     */
    private void init() {
        rootView = findViewById(R.id.rootView);

        appBar = findViewById(R.id.appBar);

        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);

        fragmentManager = getSupportFragmentManager();

        todos = TodosFragment.newInstance(this, 0);
        notes = NotesFragment.newInstance(this, 1);
        reminders = RemindersFragment.newInstance(2);

        // Initially sets the Todos Fragment as the initial screen
        currentFragment = todos;
        fragmentManager
                .beginTransaction()
                .add(fragmentContainer.getId(), todos)
                .addToBackStack(null)
                .add(fragmentContainer.getId(), notes)
                .addToBackStack(null)
                .add(fragmentContainer.getId(), reminders)
                .addToBackStack(null)
                .hide(reminders)
                .hide(notes)
                .show(todos)
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            exitOnNextBack = false;
            switch(item.getItemId()) {
                case R.id.nav_todos:
                    previousFragment = (currentFragment != todos) ? currentFragment : previousFragment;
                    currentFragment = todos;
                    break;
                case R.id.nav_notes:
                    previousFragment = (currentFragment != notes) ? currentFragment : previousFragment;
                    currentFragment = notes;
                    break;
                case R.id.nav_reminders:
                    previousFragment = (currentFragment != reminders) ? currentFragment : previousFragment;
                    currentFragment = reminders;
                    break;
            }
            if (previousFragment != null) {
                navigate();
            }
            return true;
        });
        updateAppBarTitle();
    }

    /**
     * Replaces the the current displayed fragment with the current fragment and updates the
     * AppBar title/text.
     *
     * (babaguhin ko pa toh)**
     */
    private void navigate() {
        fragmentManager
                .beginTransaction()
                .hide(previousFragment)
                .show(currentFragment)
                .commit();
        updateAppBarTitle();
    }

    /**
     * Handles What Happens when the back button is pressed.
     * If previousFragment is not null, navigates to the previous fragment.
     * Otherwise, if exitOnNextBack is true, closes the application, else sets exitOnNextBack to true
     * and displays a Toast/Snackbar message notifying the user that the next time they
     * press the back button, the application will close.
     */
    @Override
    public void onBackPressed() {
        if (previousFragment != null) {
            fragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(previousFragment)
                    .commit();
            currentFragment = previousFragment;
            previousFragment = null;
            updateAppBarTitle();
            bottomNav.setSelectedItemId(getCurrentSelected());
        } else {
            if (exitOnNextBack) {
                finish();
            } else {
                exitOnNextBack = true;
                // Chinecheck pa kung mas maganda ba ang Snackbar or Toast
                Snackbar.make(rootView, "Press Back Again to Exit Application", Snackbar.LENGTH_SHORT).show();
//                Toast.makeText(this, "Press Back Again to Exit Application", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Updates the AppBar title according to the currentFragment being displayed
     */
    private void updateAppBarTitle() {
        int index = currentFragment.getIndex();
        String title = "";
        switch (index) {
            case 0:
                title = "To-Dos";
                break;
            case 1:
                title = "Notes";
                break;
            case 2:
                title = "Reminders";
                break;
        }
        appBar.setTitle(title);
    }

    /**
     * Returns the ID of the currently selected fragment.
     * @return navId id of current fragment
     */
    private int getCurrentSelected() {
        int navId = -1;
        switch(currentFragment.getIndex()) {
            case 0:
                navId = R.id.nav_todos;
                break;
            case 1:
                navId = R.id.nav_notes;
                break;
            case 2:
                navId = R.id.nav_reminders;
                break;
        }
        return navId;
    }

    /**
     * Method for toggling the visibility of the bottom navigation.
     * Will mostly be used whe starting action mode to prevent switching
     * between fragments.
     * @param visibility mode of visibility to use.
     *                   eg. View.VISIBLE, View.INVISIBLE, View.GONE
     */
    public void toggleBottomNavigation(int visibility) {
        bottomNav.setVisibility(visibility);
    }
}