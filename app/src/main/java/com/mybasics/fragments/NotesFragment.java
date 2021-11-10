package com.mybasics.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;
import com.mybasics.activities.MainActivity;
import com.mybasics.activities.NoteEditor;
import com.mybasics.adapters.NoteAdapter;
import com.mybasics.models.Note;
import com.mybasics.util.ItemClickHelper;

public class NotesFragment extends IndexableFragment {

    private RecyclerView notesListView;
    private NoteAdapter adapter;
    private ExtendedFloatingActionButton addButton;

    private Context context;

    private ActivityResultLauncher<Intent> textEditorLauncher;

    private ActionMode actionMode;
    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.action_mode_menu, menu);
            addButton.setVisibility(View.GONE);
            ((MainActivity) getActivity()).toggleBottomNavigation(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.delete:
                    adapter.deleteSelected();
                    actionMode.finish();
                    break;
                case R.id.selectAll:
                    if (adapter.getSelectedCount() == adapter.getItemCount()) {
                        adapter.deselectAll();
                    } else {
                        adapter.selectAll();
                    }
                    actionMode.setTitle(String.format("%s Selected", adapter.getSelectedCount()));
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            addButton.setVisibility(View.VISIBLE);
            adapter.setIsOnActionMode(false);
            adapter.deselectAll();
            ((MainActivity) getActivity()).toggleBottomNavigation(View.VISIBLE);
        }
    };

    public NotesFragment(Context context, int index) {
        super(index);
        this.context = context;
        textEditorLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    }

    public static NotesFragment newInstance(Context context, int index) {
        NotesFragment fragment = new NotesFragment(context, index);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View noteFragment = inflater.inflate(R.layout.fragment_notes, container, false);

        notesListView = noteFragment.findViewById(R.id.notesListView);
        addButton = noteFragment.findViewById(R.id.btnAddNote);
        addButton.setOnClickListener(this::openNewNote);

        initializeRecyclerView();

        return noteFragment;
    }

    private void openNewNote(View v) {
        Intent i = new Intent(context, NoteEditor.class);
        i.putExtra("new", true);
        textEditorLauncher.launch(i);
    }

    private ItemClickHelper itemClickHelper = new ItemClickHelper() {
        @Override
        public void onListItemClick(int position) {
            if (actionMode == null) {
                Note note = adapter.getNote(position);
                Intent i = new Intent(context, NoteEditor.class);
                i.putExtra("note_item", note);
                i.putExtra("new", false);
                textEditorLauncher.launch(i);
            } else {
                adapter.toggleSelected(position);
                actionMode.setTitle(String.format("%s Selected", adapter.getSelectedCount()));
            }
        }

        @Override
        public boolean onListItemLongClick(int position) {
            if (actionMode != null) { return false; }

            actionMode = getActivity().startActionMode(callback);
            adapter.setIsOnActionMode(true);
            adapter.toggleSelected(position);
            actionMode.setTitle(String.format("%s Selected", adapter.getSelectedCount()));
            return true;
        }
    };

    private void initializeRecyclerView() {
        adapter = new NoteAdapter(context);
        adapter.setItemClickHelper(itemClickHelper);

        notesListView.setAdapter(adapter);
        notesListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    private void onActivityResult(ActivityResult res) {
        Log.d("RESULT CODE: ", "" + res.getResultCode());
        if (res.getResultCode() == Activity.RESULT_OK) {
            Intent i = res.getData();
            boolean isModified = i.getBooleanExtra("MODIFIED", false);
            Log.d("IS_MODIFIED: ", "" + isModified);
            boolean isNewlyCreated = i.getBooleanExtra("NEW", false);
            Log.d("IS_NEWLY_CREATED: ", "" + isNewlyCreated);
            if (isModified) {
                Note note = (Note) i.getSerializableExtra("NOTE");
                if (isNewlyCreated) {
                    adapter.addNote(note);
                } else {
                    adapter.updateNote(note);
                }
            } else {
                return;
            }
        }
    }
}