package com.mybasics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;
import com.mybasics.adapters.TodoAdapter;
import com.mybasics.fragments.modals.AddTodoDialog;
import com.mybasics.util.TodoItemTouchHelper;

public class TodosFragment extends IndexableFragment {

    private Context context;

    private ExtendedFloatingActionButton addButton;
    private RecyclerView todoListView;
    private TextView emptyText;
    private AddTodoDialog addTodoDialog;

    private TodoAdapter adapter;

    /**
     * Creates a new instance of to-do fragment.
     * @param index index of the fragment
     * @return TodosFragment instance
     */
    public static TodosFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt("index", index);
        TodosFragment todosFragment = new TodosFragment();
        todosFragment.setArguments(args);
        return todosFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIndex(getArguments().getInt("index"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View todoFragment = inflater.inflate(R.layout.fragment_todos, container, false);

        this.context = container.getContext();

        todoListView = todoFragment.findViewById(R.id.todoListView);
        addButton = todoFragment.findViewById(R.id.btnAddTodo);
        emptyText = todoFragment.findViewById(R.id.emptyText);

        addTodoDialog = new AddTodoDialog(getActivity(), this::addTodo);

        addButton.setOnClickListener(this::showAddDialog);

        new Handler(getActivity().getMainLooper()).post(() -> {
            initializeRecyclerView(container);
            toggleEmptyList();
        });

        return todoFragment;
    }

    /**
     * Toggles an empty text if the adapter has no data.
     */
    public void toggleEmptyList() {
        if (adapter.getItemCount() == 0) {
            todoListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            todoListView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Shows the add dialog.
     * @param v View associated with the event.
     */
    private void showAddDialog(View v) {
        addTodoDialog.show();
    }

    /**
     * Adds to-do and dismisses the add to-do dialog.
     * @param v
     */
    private void addTodo(View v) {
        String content = addTodoDialog.getTextContent();
        adapter.addTodo(content);
        toggleEmptyList();
        addTodoDialog.dismiss();
    }

    /**
     * Initializes RecyclerView
     * @param container
     */
    private void initializeRecyclerView(View container) {
        adapter = new TodoAdapter(context);
        adapter.setDeletedItemListener(this::toggleEmptyList);

        todoListView.setAdapter(adapter);
        todoListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        ItemTouchHelper itemHelper = new TodoItemTouchHelper(context, adapter, container, this);
        itemHelper.attachToRecyclerView(todoListView);
    }

}