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

    /**
     * Gets the index.
     * @return index position of the fragment
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index.
     * @param index new position of the fragment
     */
    public void setIndex(int index) { this.index = index; }
}
