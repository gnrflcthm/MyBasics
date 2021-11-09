package com.mybasics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;
import com.mybasics.adapters.TodoAdapter;
import com.mybasics.fragments.modals.AddTodoModal;
import com.mybasics.util.TodoItemTouchHelper;

public class TodosFragment extends IndexableFragment {

    private Context context;

    private ExtendedFloatingActionButton addButton;
    private RecyclerView todoListView;
    private TextView emptyText;
    private AddTodoModal addTodoModal;

    private TodoAdapter adapter;

    public TodosFragment(Context context, int index) {
        super(index);
        this.context = context;
    }

    public static TodosFragment newInstance(Context context, int index) {
        return new TodosFragment(context, index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View todoFragment = inflater.inflate(R.layout.fragment_todos, container, false);

        todoListView = todoFragment.findViewById(R.id.todoListView);
        addButton = todoFragment.findViewById(R.id.btnAddTodo);
        emptyText = todoFragment.findViewById(R.id.emptyText);

        addTodoModal = new AddTodoModal(this::addTodo);
        addButton.setOnClickListener(this::showAddModal);

        initializeRecyclerView(container);

        toggleEmptyList();

        return todoFragment;
    }

    private void showAddModal(View v) {
        addTodoModal.show(getParentFragmentManager(), AddTodoModal.TAG);
    }

    private void addTodo(View v) {
        String todoText = addTodoModal.getTodoText();
        adapter.addTodo(todoText);
        addTodoModal.clearText();
        getParentFragmentManager().beginTransaction().remove(addTodoModal).commit();
        toggleEmptyList();
    }

    private void toggleEmptyList() {
        if (adapter.getItemCount() == 0) {
            todoListView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            todoListView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeRecyclerView(View container) {
        adapter = new TodoAdapter(context);
        adapter.setDeletedItemListener(this::toggleEmptyList);

        todoListView.setAdapter(adapter);
        todoListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        todoListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        ItemTouchHelper itemHelper = new TodoItemTouchHelper(context, adapter, container);
        itemHelper.attachToRecyclerView(todoListView);
    }

}