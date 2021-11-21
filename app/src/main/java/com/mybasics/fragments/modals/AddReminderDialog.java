package com.mybasics.fragments.modals;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mybasics.R;
import com.mybasics.models.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AddReminderDialog extends Dialog {

    private Context context;

    private EditText titleText;
    private TextView setDateTime;
    private Button btnSave;

    private LocalDateTime current;
    private LocalDateTime dateTimeSelection;

    private OnReminderSave onReminderSave;

    private int year, month, day, hour, minute;

    private boolean isInvalidDate, isInvalidTitle;

    public AddReminderDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        clear();
        isInvalidDate = false;
        isInvalidTitle = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder_dialog);

        current = LocalDateTime.now();
        dateTimeSelection = current;

        titleText = findViewById(R.id.setTitle);
        setDateTime = findViewById(R.id.textSetDateTime);
        btnSave = findViewById(R.id.btnSave);

        setDateTime.setOnClickListener(this::showDatePicker);

        btnSave.setOnClickListener(v -> {
            isInvalidTitle = titleText.getText().toString().trim().equals("");
            String title = titleText.getText().toString();
            Reminder reminder = Reminder.newInstance(title, dateTimeSelection);
            onReminderSave.saveReminder(reminder, isInvalidTitle, isInvalidDate);
            dismiss();
            clear();
        });

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setOnReminderSave(OnReminderSave onReminderSave) {
        this.onReminderSave = onReminderSave;
    }

    private void showDatePicker(View v) {
        DatePickerDialog datePicker = new DatePickerDialog(context, this::onDatePicked, current.getYear(), current.getMonth().ordinal(), current.getDayOfMonth());
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void onDatePicked(View v, int y, int m, int d) {
        year = y;
        month =  m;
        day = d;
        new TimePickerDialog(context, this::onTimePicked, current.getHour(), current.getMinute(), false).show();
    }

    private void onTimePicked(View v, int h, int m) {
        hour = h;
        minute = m;
        LocalDate date = LocalDate.of(year, month + 1, day);
        if (date.compareTo(current.toLocalDate()) == 0) {
            LocalTime time = LocalTime.of(hour, minute);
            if (time.compareTo(current.toLocalTime()) <= 0) {
                isInvalidDate = true;
                Toast.makeText(context, "Invalid Date Entered", Toast.LENGTH_SHORT).show();
            }
        }

        dateTimeSelection = LocalDateTime.of(year, month + 1, day, hour, minute);
        setDateTime.setText(dateTimeSelection.format(DateTimeFormatter.ofPattern("MM/dd/yy h:mm a")));
    }

    private void clear() {
        titleText.setText("");
        setDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yy h:mm a")));
    }

    public interface OnReminderSave {
        void saveReminder(Reminder reminder, boolean isInvalidTitle, boolean isInvalidDate);
    }
}
