package com.mybasics.fragments.modals;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.mybasics.R;

public class AddTodoDialog extends Dialog {

    private Context context;

    private EditText todoTextInput;
    private Button addButton;

    private View.OnClickListener addButtonListener;

    public AddTodoDialog(@NonNull Context context, View.OnClickListener addButtonListener) {
        super(context);
        this.context = context;
        this.addButtonListener = addButtonListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo_dialog);

        todoTextInput = findViewById(R.id.addTodoText);
        addButton = findViewById(R.id.btnAdd);

        addButton.setOnClickListener(addButtonListener);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public String getTextContent() {
        return todoTextInput.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        todoTextInput.setText("");
    }
}
