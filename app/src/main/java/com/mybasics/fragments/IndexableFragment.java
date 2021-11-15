package com.mybasics.fragments;

import androidx.fragment.app.Fragment;

/**
 * Custom Fragment subclass with an additional index field.
 * Used mainly for navigation purposes.
 */
public abstract class IndexableFragment extends Fragment {

    private int index;

    public IndexableFragment() {}

    public IndexableFragment(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) { this.index = index; }
}
