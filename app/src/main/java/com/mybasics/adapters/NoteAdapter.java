package com.mybasics.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mybasics.R;
import com.mybasics.db.NoteDBHelper;
import com.mybasics.models.Note;
import com.mybasics.util.ItemClickHelper;
import com.mybasics.util.NoteItemCallback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;

    private List<Note> notes;
    private List<Note> selectedNotes;

    private NoteDBHelper db;

    private ItemClickHelper itemClickHelper;

    private boolean isOnActionMode;

    private Comparator<Note> sortedByDateLastModified = (n1, n2) -> {
        if (n1.getDateLastModified().compareTo(n2.getDateLastModified()) > 0) {
            return -1;
        } else if (n1.getDateLastModified().compareTo(n2.getDateLastModified()) == 0) {
            return 0;
        } else {
            return 1;
        }
    };

    public NoteAdapter(Context context) {
        this.context = context;
        this.db = new NoteDBHelper(context);
        this.isOnActionMode = false;

        initializeData();
    }

    private void initializeData() {
        notes = new ArrayList<>();
        notes = db.fetchNotes();
        Collections.sort(notes, sortedByDateLastModified);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setData(notes.get(position));
        holder.setItemClickHelper(itemClickHelper);
    }

    public void addNote(Note note) {
        db.addNote(note);
        refreshData();
    }

    public void updateNote(Note note) {
        db.updateNote(note);
        refreshData();
    }

    public void deleteSelected() {
        List<String> noteIds =
                selectedNotes
                .stream()
                .map(note -> note.getId())
                .collect(Collectors.toList());
        db.deleteSelectedNotes(noteIds);
        refreshData();
    }

    private void refreshData() {
        List<Note> newNotes = db.fetchNotes();
        Collections.sort(newNotes, sortedByDateLastModified);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(new NoteItemCallback(notes, newNotes));
        notes = newNotes;
        res.dispatchUpdatesTo(this);
    }

    public void toggleSelected(int position) {
        Note note = notes.get(position);
        note.toggleSelected();
        notifyItemChanged(position);
        if (note.isSelected()) {
            selectedNotes.add(note);
        } else {
            selectedNotes.remove(note);
        }
    }

    public int getSelectedCount() {
        return selectedNotes.size();
    }

    public void setIsOnActionMode(boolean isOnActionMode) {
        this.isOnActionMode = isOnActionMode;
        if (isOnActionMode) {
            selectedNotes = new ArrayList<>();
        } else {
            selectedNotes = null;
        }
    }

    public void selectAll() {
        notes.stream().forEach(n -> n.setSelected(true));
        notifyDataSetChanged();
        selectedNotes.clear();
        selectedNotes.addAll(notes);
    }

    public void deselectAll() {
        notes.stream().forEach(n -> n.setSelected(false));
        notifyDataSetChanged();
        if (selectedNotes != null) {
            selectedNotes.clear();
        }
    }

    public Note getNote(int position) {
        return notes.get(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setItemClickHelper(ItemClickHelper itemClickHelper) {
        this.itemClickHelper = itemClickHelper;
    }

    protected class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView noteTitle;
        private TextView noteModified;
        private TextView notePreview;

        private View selectedOverlay;

        private ItemClickHelper clickHelper;

        public NoteViewHolder(@NonNull View view) {
            super(view);
            noteTitle = view.findViewById(R.id.noteTitle);
            noteModified = view.findViewById(R.id.noteModified);
            notePreview = view.findViewById(R.id.notePreview);
            selectedOverlay = view.findViewById(R.id.selected_overlay);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setData(Note note) {
            noteTitle.setText(note.getTitle());
            boolean modifiedToday = LocalDate.now().equals(note.getDateLastModified().toLocalDate());
            DateTimeFormatter format = (modifiedToday) ? DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) : DateTimeFormatter.ISO_LOCAL_DATE;
            noteModified.setText(note.getDateLastModified().format(format));
            Spanned noteContent = Html.fromHtml(note.getContent());
            notePreview.setText(noteContent);
            selectedOverlay.setVisibility(View.GONE);
            if (isOnActionMode) {
                if(note.isSelected()) {
                    selectedOverlay.setVisibility(View.VISIBLE);
                }
            }
        }

        public void setItemClickHelper(ItemClickHelper helper) {
            clickHelper = helper;
        }

        @Override
        public void onClick(View v) {
            clickHelper.onListItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("LONG_PRESSED: ", "" + getAdapterPosition());
            return clickHelper.onListItemLongClick(getAdapterPosition());
        }
    }
}
