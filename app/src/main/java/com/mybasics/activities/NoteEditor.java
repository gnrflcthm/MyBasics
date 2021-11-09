package com.mybasics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mybasics.R;
import com.mybasics.models.Note;
import com.mybasics.util.NoteEditorToolbar;

import net.dankito.richtexteditor.android.RichTextEditor;

import java.time.LocalDateTime;

public class NoteEditor extends AppCompatActivity {

    private TextView titleDisplay;
    private EditText titleEdit;

    private ImageButton btnReturn;
    private Button btnSave;

    private RichTextEditor textEditor;
    private NoteEditorToolbar toolbar;

    private Note note;
    private boolean isNewlyCreated;

    private String initialTextValue;
    private String initialTitle;

    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        i = getIntent();
        note = (Note) i.getSerializableExtra("note_item");
        note = (note != null) ? note : Note.newInstance("");
        isNewlyCreated = i.getBooleanExtra("new", true);

        i = new Intent();
        i.putExtra("NEW", isNewlyCreated);

        init();
    }

    private void init() {
        titleDisplay = findViewById(R.id.titleDisplay);
        titleEdit = findViewById(R.id.titleEdit);
        btnReturn = findViewById(R.id.btnReturn);
        btnSave = findViewById(R.id.btnSave);
        textEditor = findViewById(R.id.textEditor);
        toolbar = findViewById(R.id.noteEditorToolbar);

        initializeTitle();

        btnSave.setOnClickListener(this::saveOnClick);
        btnReturn.setOnClickListener(this::returnOnClick);

        toolbar.setEditor(textEditor);
        textEditor.setPadding(4);

        textEditor.setHtml(note.getContent());

        initialTitle = titleEdit.getText().toString();
        initialTextValue = textEditor.getCachedHtml();
    }

    private void returnOnClick(View v) {
        verifyResults();
    }

    @Override
    protected void onPause() {
        super.onPause();
        verifyResults();
    }

    @Override
    protected void onStop() {
        super.onStop();
        verifyResults();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        verifyResults();
    }

    private void saveOnClick(View v) {
        saveNote();
    }

    private void verifyResults() {
        if (!textEditor.getCachedHtml().equals(initialTextValue) ||
            !titleEdit.getText().toString().equals(initialTitle)) {
            saveNote();
            i.putExtra("MODIFIED", true);
            i.putExtra("NOTE", note);
        } else {
            i.putExtra("MODIFIED", false);
        }
        setResult(RESULT_OK, i);
        finish();
    }

    private void saveNote() {
        String noteTitle = titleEdit.getText().toString();
        note.setTitle(noteTitle.equals("") ? "Untitled Note" : noteTitle);
        note.setContent(textEditor.getCachedHtml());
        note.setDateLastModified(LocalDateTime.now());
        Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();
    }

    private void initializeTitle() {
        titleDisplay.setText(note.getTitle());
        titleEdit.setText(isNewlyCreated ? "" : titleDisplay.getText());

        titleEdit.setVisibility(View.GONE);

        titleDisplay.setOnClickListener(v -> {
            titleDisplay.setVisibility(View.GONE);
            titleEdit.setVisibility(View.VISIBLE);
            titleEdit.requestFocus();
        });

        titleEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String titleText = titleEdit.getText().toString();
                titleDisplay.setText((titleText.equals("") ? "Untitled Note" : titleText));
                titleDisplay.setVisibility(View.VISIBLE);
                titleEdit.setVisibility(View.GONE);
            }
        });
    }
}