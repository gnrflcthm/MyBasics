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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mybasics.R;
import com.mybasics.adapters.TodoAdapter;
import com.mybasics.models.Todo;

import java.util.concurrent.atomic.AtomicBoolean;

public class TodoItemTouchHelper extends ItemTouchHelper {

    private Context context;

    private static View container;

    public TodoItemTouchHelper(@NonNull Callback callback) {
        super(callback);
    }

    public TodoItemTouchHelper(Context context, TodoAdapter adapter, View container) {
        super(new TodoSimpleCallback(0, LEFT | RIGHT, adapter, context));
        this.context = context;
        this.container = container;
    }

    public static class TodoSimpleCallback extends ItemTouchHelper.SimpleCallback {

        private TodoAdapter adapter;
        private final int PADDING = 25;
        private boolean toggled = false;
        private Context context;

        public TodoSimpleCallback(int dragDirs, int swipeDirs, TodoAdapter adapter, Context context) {
            super(dragDirs, swipeDirs);
            this.adapter = adapter;
            this.context = context;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch(direction) {
                case LEFT:
                    Todo deletedTodo = adapter.deleteTodo(position);
                    AtomicBoolean undone = new AtomicBoolean(false);
                    Snackbar.make(container, "To-do Item Deleted", Snackbar.LENGTH_SHORT)
                            .setAction("Undo", v -> {
                                adapter.insertTodo(deletedTodo, position);
                                undone.set(true);
                            })
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    if (!undone.get()) {
                                        adapter.confirmDelete(deletedTodo);
                                    }
                                }
                            })
                            .show();
                    break;
                case RIGHT:
                    adapter.toggleTodo(position);
                    toggled = true;
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            View item = viewHolder.itemView;
            Todo todo = adapter.getTodo(viewHolder.getAdapterPosition());

            if (dX == 0 && toggled) {
                toggled = false;
            }
            if (todo != null) {
                if (isCurrentlyActive) {
                    drawTodoToggle(c, item, todo.isCompleted());
                } else {
                    if (toggled) {
                        drawTodoToggle(c, item, !todo.isCompleted());
                    } else {
                        drawTodoToggle(c, item, todo.isCompleted());
                    }
                }
            }
            if (dX < 0) {
                drawDeleteTodo(c, item);
            }
        }

        public void drawTodoToggle(Canvas canvas, View item, boolean isCompleted) {
            int color = isCompleted ? R.color.red_accent_400 : R.color.green_accent_400;
            int resId = isCompleted ? R.drawable.ic_baseline_close_24 : R.drawable.ic_baseline_check_24;
            ColorDrawable bg = new ColorDrawable();
            bg.setColor(ContextCompat.getColor(context, color));
            bg.setBounds(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
            bg.draw(canvas);
            Drawable icon = AppCompatResources.getDrawable(context, resId);
            icon.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.MULTIPLY);
            icon.setBounds(item.getLeft() + PADDING, item.getTop() + PADDING, item.getBottom() - item.getTop() - PADDING, item.getBottom() - PADDING);
            icon.draw(canvas);
            Paint textStyle = new Paint();
            textStyle.setTextAlign(Paint.Align.LEFT);
            textStyle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
            textStyle.setColor(0xFF333333);
            canvas.drawText("Toggle To-Do", icon.getBounds().right + PADDING, icon.getBounds().bottom - PADDING - 5, textStyle);
        }

        public void drawDeleteTodo(Canvas canvas, View item) {
            ColorDrawable bg = new ColorDrawable();
            bg.setColor(ContextCompat.getColor(context, R.color.red_accent_400));
            bg.setBounds(item.getLeft(), item.getTop(), item.getRight(), item.getBottom());
            bg.draw(canvas);
            Drawable icon = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_delete_24);
            icon.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY);
            icon.setBounds(item.getWidth() - (item.getBottom() - item.getTop()) + PADDING, item.getTop() + PADDING, item.getRight() - PADDING, item.getBottom() - PADDING);
            icon.draw(canvas);
            Paint textStyle = new Paint();
            textStyle.setTextAlign(Paint.Align.RIGHT);
            textStyle.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics()));
            textStyle.setColor(0xFFFFFFFF);
            canvas.drawText("Delete To-Do", icon.getBounds().left - PADDING, icon.getBounds().bottom - PADDING, textStyle);
        }
    }
}
