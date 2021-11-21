package com.mybasics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mybasics.R;
import com.mybasics.db.DBHelper;
import com.mybasics.models.Reminder;
import com.mybasics.receivers.ReminderAlarm;
import com.mybasics.util.DeletedItemListener;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private Context context;

    private List<Reminder> reminders;

    private DBHelper db;

    private DeletedItemListener deletedItemListener;

    public ReminderAdapter(Context context) {
        this.context = context;
        db = DBHelper.getInstance(context.getApplicationContext());

        initializeData();
    }

    private void initializeData() {
        reminders = new ArrayList<>();
        reminders = db.fetchReminders();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.setData(reminders.get(position));
    }

    public int addReminder(Reminder reminder) {
        long id = db.addReminder(reminder);
        reminders = db.fetchReminders();
        notifyItemInserted(reminders.size() - 1);
        return (int) id;
    }

    public void insertReminder(Reminder reminder, int position) {
        reminders.add(position, reminder);
        notifyItemInserted(position);
        deletedItemListener.onItemDelete();
    }

    public Reminder deleteReminder(int position) {
        Reminder reminder = reminders.remove(position);
        notifyItemRemoved(position);
        deletedItemListener.onItemDelete();
        return reminder;
    }

    public void confirmDelete(Reminder reminder) {
        ReminderAlarm.cancelAlarm(reminder, context);
        db.deleteReminder(reminder);
    }

    public void setDeletedItemListener(DeletedItemListener deletedItemListener) {
        this.deletedItemListener = deletedItemListener;
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    protected class ReminderViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView schedule;

        public ReminderViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.reminder_title);
            schedule = view.findViewById(R.id.reminder_schedule);
        }

        public void setData(Reminder reminder) {
            title.setText(reminder.getTitle());
            schedule.setText(reminder.getSchedule().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        }
    }
}
