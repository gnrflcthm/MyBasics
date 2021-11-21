package com.mybasics.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;
import com.mybasics.activities.NoteEditor;
import com.mybasics.adapters.NoteAdapter;
import com.mybasics.models.Note;
import com.mybasics.util.NoteSimpleCallback;

public class NotesFragment extends IndexableFragment {

    private RecyclerView notesListView;
    private NoteAdapter adapter;
    private ExtendedFloatingActionButton addButton;
    private TextView emptyText;

    private Context context;

    private ActivityResultLauncher<Intent> textEditorLauncher;

    public static NotesFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt("index", index);
        NotesFragment notesFragment = new NotesFragment();
        notesFragment.setArguments(args);
        return notesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIndex(getArguments().getInt("index"));

        textEditorLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View noteFragment = inflater.inflate(R.layout.fragment_notes, container, false);

        this.context = container.getContext();

        notesListView = noteFragment.findViewById(R.id.notesListView);
        addButton = noteFragment.findViewById(R.id.btnAddNote);
        addButton.setOnClickListener(this::openNewNote);
        emptyText = noteFragment.findViewById(R.id.emptyText);

        new Handler(getActivity().getMainLooper()).post(() -> {
            initializeRecyclerView(container);
            toggleEmptyList();
        });


        return noteFragment;
    }

    private void openNewNote(View v) {
        Intent i = new Intent(context, NoteEditor.class);
        i.putExtra("new", true);
        textEditorLauncher.launch(i);
    }

    private void initializeRecyclerView(View root) {
        adapter = new NoteAdapter(context);

        adapter.setItemClickHelper(position -> {
            Note note = adapter.getNote(position);
            Intent i = new Intent(context, NoteEditor.class);
            i.putExtra("note_item", note);
            i.putExtra("new", false);
            textEditorLauncher.launch(i);
        });

        adapter.setDeletedItemListener(this::toggleEmptyList);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new NoteSimpleCallback(adapter, context, root, this));

        notesListView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(notesListView);
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
                    toggleEmptyList();
                } else {
                    adapter.updateNote(note);
                }
            }
        }
    }

    public void toggleEmptyList() {
        if (adapter.getItemCount() == 0) {
            notesListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            notesListView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }
}