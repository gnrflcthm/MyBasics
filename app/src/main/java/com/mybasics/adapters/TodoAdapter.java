package com.mybasics.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mybasics.R;
import com.mybasics.db.DBHelper;
import com.mybasics.models.Todo;
import com.mybasics.util.DeletedItemListener;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private DBHelper db;

    private Context context;
    private List<Todo> todos;

    private DeletedItemListener deletedItemListener;

    public TodoAdapter(Context context) {
        this.context = context;
        db = DBHelper.getInstance(context.getApplicationContext());
        initializeData();
    }

    /**
     * Initialized the data of the adapter.
     */
    private void initializeData() {
        this.todos = new ArrayList<>();
        todos = db.fetchTodos();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.setData(todos.get(position));
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    /**
     * Adds the to-do item to the database.
     * @param content Content of the to-do item.
     */
    public void addTodo(String content) {
        Todo todo = Todo.newInstance(content);
        Log.d("ITEM INSERTION: ", "" + db.addTodo(todo));
        todos = db.fetchTodos();
        notifyItemInserted(todos.size() - 1);
    }

    /**
     * Deletes the to-do item temporarily at the specified index.
     * @param index position of the to-do to be deleted.
     * @return To-do item
     */
    public Todo deleteTodo(int index) {
        Todo todo = todos.remove(index);
        notifyItemRemoved(index);
        deletedItemListener.onItemDelete();
        return todo;
    }

    /**
     * Deletes to-do from the database.
     * @param todo
     */
    public void confirmDelete(Todo todo) {
        db.deleteTodo(todo);
    }

    /**
     * Inserts to-do at the specified position.
     * @param todo to-do to be inserted
     * @param position index to be inserted at
     */
    public void insertTodo(Todo todo, int position) {
        todos.add(position, todo);
        notifyItemInserted(position);
    }

    /**
     * Updates the state of the To-Do item
     * @param position index of the to-do toggled
     */
    public void toggleTodo(int position) {
        Todo todo = todos.get(position);
        todo.setCompleted(!todo.isCompleted());
        db.updateTodo(todo);
        todos = db.fetchTodos();
        notifyItemChanged(position);
    }

    /**
     * Returns the to-do at the specified position.
     * @param position index of the to-do
     * @return To-do item at the specified position.
     */
    public Todo getTodo(int position) {
        try {
            return todos.get(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Sets the DeletedItemListener
     * @param deletedItemListener implementation of the DeletedItemListener interface.
     */
    public void setDeletedItemListener(DeletedItemListener deletedItemListener) {
        this.deletedItemListener = deletedItemListener;
    }

    protected static class TodoViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iconStatus;
        protected TextView textContent;
        protected TextView textStatus;
        protected TextView textDateAdded;
        protected Context context;

        public TodoViewHolder(@NonNull View view) {
            super(view);
            context = view.getContext();
            iconStatus = view.findViewById(R.id.todoIconStatus);
            textContent = view.findViewById(R.id.todoTextContent);
            textStatus = view.findViewById(R.id.todoTextStatus);
            textDateAdded = view.findViewById(R.id.todoTextDateAdded);
        }

        /**
         * Sets the data for the to-do item view.
         * @param todo To-do object containing data to be used.
         */
        private void setData(Todo todo) {
            textContent.setText(todo.getContent());
            textDateAdded.setText(todo.getDateAdded().format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (todo.isCompleted()) {
                iconStatus.setBackgroundResource(R.drawable.round_green_bg);
                textStatus.setText("COMPLETED");
            } else {
                iconStatus.setBackgroundResource(R.drawable.round_grey_bg);
                textStatus.setText("PENDING");
            }
        }
    }
}
