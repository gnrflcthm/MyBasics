package com.mybasics.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mybasics.R;
import com.mybasics.db.DBHelper;
import com.mybasics.models.Note;
import com.mybasics.util.DeletedItemListener;
import com.mybasics.util.ItemClickHelper;
import com.mybasics.util.NoteItemCallback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;

    private List<Note> notes;

    private DBHelper db;

    private ItemClickHelper itemClickHelper;

    private DeletedItemListener deletedItemListener;

    // Comparator for sorting notes according to its date last modified.
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
        this.db = DBHelper.getInstance(context.getApplicationContext());
        initializeData();
    }

    // Initialized Adapted Data
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

    /**
     * Adds Note to database and refreshes data.
     * @param note Note to be added
     */
    public void addNote(Note note) {
        db.addNote(note);
        refreshData();
    }

    /**
     * Updates Note and refreshes data.
     * @param note Note to be updated
     */
    public void updateNote(Note note) {
        db.updateNote(note);
        refreshData();
    }

    /**
     * Deletes Note temporarily.
     * This ensures user can undo the action within the time frame.
     * @param position index of note to be deleted
     * @return deleted Note item.
     */
    public Note deleteNote(int position) {
        Note note = notes.remove(position);
        notifyItemRemoved(position);
        deletedItemListener.onItemDelete();
        return note;
    }

    /**
     * Inserts note at the specified position.
     * Used after deleteNote() for undoing deletes.
     * @param note to be inserted
     * @param position index for insertion
     */
    public void insertNote(Note note, int position) {
        notes.add(position, note);
        notifyItemInserted(position);
        deletedItemListener.onItemDelete();
    }

    /**
     * Deletes Note from the database.
     * @param note Note to be deleted
     */
    public void confirmDelete(Note note) {
        db.deleteNote(note);
    }

    /**
     * Refreshes Data by comparing its differences using DiffUtil
     */
    private void refreshData() {
        List<Note> newNotes = db.fetchNotes();
        Collections.sort(newNotes, sortedByDateLastModified);
        DiffUtil.DiffResult res = DiffUtil.calculateDiff(new NoteItemCallback(notes, newNotes));
        notes = newNotes;
        res.dispatchUpdatesTo(this);
    }

    /**
     * Gets Note at the specified position.
     * @param position index of the note.
     * @return Note at the specified position
     */
    public Note getNote(int position) {
        return notes.get(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Sets ItemClickHelper to be used by the notes
     * @param itemClickHelper implementation of ItemClickHelper interface
     */
    public void setItemClickHelper(ItemClickHelper itemClickHelper) {
        this.itemClickHelper = itemClickHelper;
    }
    /**
     * Sets DeletedItemListener
     * @param deletedItemListener implementation of DeletedItemListener interface
     */
    public void setDeletedItemListener(DeletedItemListener deletedItemListener) {
        this.deletedItemListener = deletedItemListener;
    }

    protected class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView noteTitle;
        private TextView noteModified;
        private TextView notePreview;

        private ItemClickHelper clickHelper;

        public NoteViewHolder(@NonNull View view) {
            super(view);
            noteTitle = view.findViewById(R.id.noteTitle);
            noteModified = view.findViewById(R.id.noteModified);
            notePreview = view.findViewById(R.id.notePreview);
            itemView.setOnClickListener(this);
        }

        /**
         * Sets Data for the Note Item View
         * @param note Note object containing data to be used
         */
        public void setData(Note note) {
            noteTitle.setText(note.getTitle());
            boolean modifiedToday = LocalDate.now().equals(note.getDateLastModified().toLocalDate());
            DateTimeFormatter format = (modifiedToday) ? DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT) : DateTimeFormatter.ISO_LOCAL_DATE;
            noteModified.setText(note.getDateLastModified().format(format));
            Spanned noteContent = Html.fromHtml(note.getContent());
            notePreview.setText(noteContent);
        }

        /**
         * Sets ItemClickHelper for the note
         * @param helper
         */
        public void setItemClickHelper(ItemClickHelper helper) {
            clickHelper = helper;
        }

        @Override
        public void onClick(View v) {
            clickHelper.onListItemClick(getAdapterPosition());
        }

    }
}
