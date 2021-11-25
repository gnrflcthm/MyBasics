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
import com.mybasics.adapters.ReminderAdapter;
import com.mybasics.fragments.RemindersFragment;
import com.mybasics.models.Reminder;

public class ReminderSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private ReminderAdapter adapter;
    private Context context;
    private View root;

    private Fragment fragmentContainer;

    private final int PADDING = 30;

    public ReminderSimpleCallback(ReminderAdapter adapter, Context context, View root, Fragment fragmentContainer) {
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
                Reminder reminder = adapter.deleteReminder(position);
                Snackbar.make(root, "Reminder Deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", v -> {
                            adapter.insertReminder(reminder, position);
                            ((RemindersFragment) fragmentContainer).toggleEmptyList();
                        })
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                switch(event) {
                                    case DISMISS_EVENT_TIMEOUT:
                                    case DISMISS_EVENT_CONSECUTIVE:
                                        adapter.confirmDelete(reminder);
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
            if (dX < 0) {
                drawDeleteReminder(canvas, item, true);
            } else {
                drawDeleteReminder(canvas, item, false);
            }
        } else {
            if (dX < 0) {
                drawDeleteReminder(canvas, item, true);
            } else if (dX > 0) {
                drawDeleteReminder(canvas, item, false);
            }
        }
    }

    private void drawDeleteReminder(Canvas canvas, View item, boolean isLeft) {
        ColorDrawable bg = new ColorDrawable();
        bg.setColor(ContextCompat.getColor(context, R.color.red_accent_400));
        bg.setBounds(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
        bg.draw(canvas);
        Drawable icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_delete_24);
        icon.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY);
        if (isLeft) {
            icon.setBounds(
                    item.getRight() - (item.getBottom() - item.getTop()) + PADDING,
                    item.getTop() + PADDING,
                    item.getRight() - PADDING,
                    item.getBottom() - PADDING
            );
        } else {
            icon.setBounds(
                    item.getLeft() + PADDING,
                    item.getTop() + PADDING,
                    item.getLeft() + (item.getBottom() - item.getTop()) - (PADDING),
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
        textStyle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
        textStyle.setColor(0xFFFFFFFF);
        if (isLeft) {
            canvas.drawText("Delete Reminder", icon.getBounds().left - PADDING, icon.getBounds().bottom - PADDING, textStyle);
        } else {
            canvas.drawText("Delete Reminder", icon.getBounds().right + PADDING, icon.getBounds().bottom - PADDING, textStyle);
        }
    }
}
