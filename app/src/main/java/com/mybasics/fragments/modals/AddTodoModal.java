package com.mybasics.fragments.modals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mybasics.R;

public class AddTodoModal extends BottomSheetDialogFragment {

    public static final String TAG = "AddTodoModal";

    private EditText todoText;
    private Button addButton;
    private View.OnClickListener listener;

    public AddTodoModal(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_modal, null);
        todoText = view.findViewById(R.id.todo_modal_text);
        addButton = view.findViewById(R.id.todo_modal_add);
        addButton.setOnClickListener(listener);
        return view;
    }

    public String getTodoText() {
        return todoText.getText().toString();
    }

    public void clearText() {
        todoText.setText("");
    }

}
