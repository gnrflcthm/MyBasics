package com.mybasics.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mybasics.R;
import com.mybasics.adapters.NoteAdapter;
import com.mybasics.models.Note;

public class NoteSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private NoteAdapter adapter;
    private Context context;
    private View root;

    private Fragment fragmentContainer;

    private final int PADDING = 75;

    public NoteSimpleCallback(NoteAdapter adapter, Context context, View root, Fragment fragmentContainer) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;
        this.root = root;
        this.fragmentContainer = fragmentContainer;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        switch(direction) {
            case ItemTouchHelper.LEFT:
            case ItemTouchHelper.RIGHT:
                Note note = adapter.deleteNote(position);
                Snackbar.make(root, "Note Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> adapter.insertNote(note, position))
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                switch (event) {
                                    case DISMISS_EVENT_TIMEOUT:
                                    case DISMISS_EVENT_CONSECUTIVE:
                                        adapter.confirmDelete(note);
                                }
                            }
                        })
                        .show();

        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View item = viewHolder.itemView;

        if (isCurrentlyActive) {
            drawDeleteNote(canvas, item, dX < 0);
        } else {
            if (dX < 0) {
                drawDeleteNote(canvas, item, true);
            } else if (dX > 0) {
                drawDeleteNote(canvas, item, false);
            }
        }
    }

    private void drawDeleteNote(Canvas canvas, View item, boolean isLeft) {
        ColorDrawable bg = new ColorDrawable();
        bg.setColor(ContextCompat.getColor(context, R.color.red_accent_400));
        bg.setBounds(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
        bg.draw(canvas);
        Drawable icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_delete_24);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY);
        if (isLeft) {
            icon.setBounds(
                    item.getRight() - (item.getBottom() - item.getTop()) + PADDING + 25,
                    item.getTop() + PADDING,
                    item.getRight() - PADDING + 25,
                    item.getBottom() - PADDING
            );
        } else {
            icon.setBounds(
                    item.getLeft() + PADDING - 25,
                    item.getTop() + PADDING,
                    item.getBottom() - item.getTop() - PADDING,
                    item.getBottom() - PADDING
            );
        }
        icon.draw(canvas);
        Paint textStyle = new Paint();
        if (isLeft) {
            textStyle.setTextAlign(Paint.Align.RIGHT);
        } else {
            textStyle.setTextAlign(Paint.Align.LEFT);
        }
        textStyle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, context.getResources().getDisplayMetrics()));
        textStyle.setColor(0xFFFFFFFF);
        if (isLeft) {
            canvas.drawText(
                    "Delete Note",
                    icon.getBounds().left - (PADDING - 35),
                    icon.getBounds().bottom - (PADDING - 40),
                    textStyle
            );
        } else {
            canvas.drawText(
                    "Delete Note",
                    icon.getBounds().right + (PADDING - 35),
                    icon.getBounds().bottom - (PADDING - 40),
                    textStyle
            );
        }

    }
}
