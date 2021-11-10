package com.mybasics.util;

import androidx.recyclerview.widget.DiffUtil;

import com.mybasics.models.Note;

import java.util.List;

public class NoteItemCallback extends DiffUtil.Callback {
    private List<Note> oldList;
    private List<Note> newList;

    public NoteItemCallback(List<Note> oldList, List<Note> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldNote = oldList.get(oldItemPosition);
        Note newNote = newList.get(newItemPosition);

        return oldNote.getContent().equals(newNote.getContent())
            && oldNote.getTitle().equals(newNote.getTitle());
    }
}
