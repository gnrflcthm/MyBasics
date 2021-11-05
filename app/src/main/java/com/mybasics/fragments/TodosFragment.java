package com.mybasics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mybasics.R;

public class TodosFragment extends IndexableFragment {

    private Context context;

    /**
     * Add Button for todos.
     * RecyclerView is used for displaying to-do Items
     */
    private ExtendedFloatingActionButton addButton;
    private RecyclerView todoListView;
//    private AddTodoModal addTodoModal;

    /**
     * Adapter to be used with recycler view
     */
//    private TodoAdapter adapter;

    public TodosFragment(Context ctx, int index) {
        super(index);
        this.context = ctx;
    }

    /**
     * Creates an instant of TodosFragment with the specified index.
     * @param index the position of the fragment.
     * @return TodosFragment instance with the specified index.
     */
    public static TodosFragment newInstance(Context context, int index) {
        TodosFragment fragment = new TodosFragment(context, index);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View todoFragment = inflater.inflate(R.layout.fragment_todos, container, false);

        // initialize add button
//        addButton = todoFragment.findViewById(R.id.btnAddTodo);

        // initialize recycler view and adapter
//        todoListView = todoFragment.findViewById(R.id.todoListView);
//        adapter = new TodoAdapter(container.getContext());

        // set adapter and layout manager for recycler view
//        todoListView.setAdapter(adapter);
//        todoListView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
//        todoListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

//        ItemTouchHelper touchHelper = new ItemTouchHelper(new TodoItemTouchHelperCallback(adapter, container));
//        touchHelper.attachToRecyclerView(todoListView);

//        addTodoModal = new AddTodoModal(v -> {
//            String todoText = addTodoModal.getTodoText();
//            adapter.addTodo(todoText);
//            addTodoModal.clearText();
//            getParentFragmentManager().beginTransaction().remove(addTodoModal).commit();
//        });

        // set on click listener for add button
//        addButton.setOnClickListener(view -> {
//            addTodoModal.show(getParentFragmentManager(), AddTodoModal.TAG);
//        });

        return todoFragment;
    }
}