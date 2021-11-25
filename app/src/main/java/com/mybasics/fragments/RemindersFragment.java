package com.mybasics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;
import com.mybasics.adapters.ReminderAdapter;
import com.mybasics.fragments.modals.AddReminderDialog;
import com.mybasics.receivers.ReminderAlarm;
import com.mybasics.util.ReminderSimpleCallback;

public class RemindersFragment extends IndexableFragment {

    private RecyclerView remindersListView;
    private ReminderAdapter adapter;
    private ExtendedFloatingActionButton addButton;
    private TextView emptyText;
    private AddReminderDialog addDialog;

    private Context context;

    /**
     * Creates a new instance of reminder fragment
     * @param index index of the fragment
     * @return RemindersFragment Instance
     */
    public static RemindersFragment newInstance(int index) {
        Bundle args = new Bundle();
        RemindersFragment fragment = new RemindersFragment();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIndex(getArguments().getInt("index"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View reminderFragment = inflater.inflate(R.layout.fragment_reminders, container, false);

        this.context = container.getContext();

        remindersListView = reminderFragment.findViewById(R.id.remindersListView);
        addButton = reminderFragment.findViewById(R.id.btnAddReminder);
        addDialog = new AddReminderDialog(getActivity());
        emptyText = reminderFragment.findViewById(R.id.emptyText);

        addButton.setOnClickListener(this::showAddDialog);

        new Handler(getActivity().getMainLooper()).post(() -> {
            initializeRecyclerView(container);
            toggleEmptyList();
        });


        return reminderFragment;
    }

    /**
     * Shows the add reminder dialog
     * @param v view associated with the click event.
     */
    private void showAddDialog(View v) {
        addDialog.setOnReminderSave((reminder, isInvalidTitle, isInvalidDate) -> {
            if (isInvalidTitle) {
                Toast.makeText(context, "Invalid Title Entered", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isInvalidDate) {
                Toast.makeText(context, "Invalid Date Entered", Toast.LENGTH_SHORT).show();
                return;
            }
            int id = adapter.addReminder(reminder);
            if (id == -1) {
                Toast.makeText(context, "Error in adding reminder", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleEmptyList();
            reminder.setId(id);
            ReminderAlarm.setAlarm(reminder, context);
        });
        addDialog.show();
    }

    /**
     * Initializes RecyclerView
     * @param container View to be passed to the itemtouchhelper
     */
    private void initializeRecyclerView(View container) {
        adapter = new ReminderAdapter(context);

        adapter.setDeletedItemListener(this::toggleEmptyList);

        remindersListView.setAdapter(adapter);
        new ItemTouchHelper(new ReminderSimpleCallback(adapter, context, container, this)).attachToRecyclerView(remindersListView);
        remindersListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Toggles the empty text view if there are no items in the adapter.
     */
    public void toggleEmptyList() {
        if (adapter.getItemCount() == 0) {
            remindersListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            remindersListView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }
}