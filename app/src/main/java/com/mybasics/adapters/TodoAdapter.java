package com.mybasics.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
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

    private DBHelper db;    // Database Object

    private Context context;    // App Context

    private List<Todo> todos;   // List of To-Dos

    private DeletedItemListener deletedItemListener;

    public TodoAdapter(Context context) {
        this.context = context;

        db = DBHelper.getInstance(context.getApplicationContext());

        initializeData();
    }

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
//        holder.setDataAlt(todos.get(position));
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void addTodo(String content) {
        Todo todo = Todo.newInstance(content);
        Log.d("ITEM INSERTION: ", "" + db.addTodo(todo));
        todos = db.fetchTodos();
        notifyItemInserted(todos.size() - 1);
    }

    public Todo deleteTodo(int index) {
        Todo todo = todos.remove(index);
        notifyItemRemoved(index);
        deletedItemListener.onItemDelete();
        return todo;
    }

    public void confirmDelete(Todo todo) {
        db.deleteTodo(todo);
    }

    public void insertTodo(Todo todo, int position) {
        todos.add(position, todo);
        notifyItemInserted(position);
    }

    public void toggleTodo(int position) {
        Todo todo = todos.get(position);
        todo.setCompleted(!todo.isCompleted());
        db.updateTodo(todo);
        todos = db.fetchTodos();
        notifyItemChanged(position);
    }

    public Todo getTodo(int position) {
        try {
            return todos.get(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

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

        private void setDataAlt(Todo todo) {
            SpannableString content = new SpannableString(todo.getContent());
            if (todo.isCompleted()) {
                StrikethroughSpan styleSpan = new StrikethroughSpan();
                content.setSpan(styleSpan, 0, content.length(), 0);
            }
            textContent.setText(content);

        }
    }
}
