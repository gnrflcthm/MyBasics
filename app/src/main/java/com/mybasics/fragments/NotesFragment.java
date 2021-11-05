package com.mybasics.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybasics.R;

public class NotesFragment extends IndexableFragment {

    public NotesFragment(int index) {
        super(index);
    }

    /**
     * Creates an instant of NotesFragment with the specified index.
     * @param index the position of the fragment.
     * @return NotesFragment instance with the specified index.
     */
    public static NotesFragment newInstance(int index) {
        NotesFragment fragment = new NotesFragment(index);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }
}