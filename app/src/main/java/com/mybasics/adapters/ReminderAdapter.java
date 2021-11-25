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
    private DBHelper db;

    private List<Reminder> reminders;
    private DeletedItemListener deletedItemListener;

    public ReminderAdapter(Context context) {
        this.context = context;
        db = DBHelper.getInstance(context.getApplicationContext());
        initializeData();
    }

    /**
     * Initialized the data of the adapter.
     */
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

    /**
     * Adds Reminder to the database.
     * @param reminder Reminder to be added
     * @return id of new reminder added
     */
    public int addReminder(Reminder reminder) {
        long id = db.addReminder(reminder);
        reminders = db.fetchReminders();
        notifyItemInserted(reminders.size() - 1);
        return (int) id;
    }

    /**
     * Inserts reminder at the specified position
     * @param reminder Reminder to be inserted
     * @param position index to be inserted at.
     */
    public void insertReminder(Reminder reminder, int position) {
        reminders.add(position, reminder);
        notifyItemInserted(position);
        deletedItemListener.onItemDelete();
    }

    /**
     * Deletes reminder at the specified position.
     * @param position index of reminder to be deleted
     * @return deleted reminder.
     */
    public Reminder deleteReminder(int position) {
        Reminder reminder = reminders.remove(position);
        notifyItemRemoved(position);
        deletedItemListener.onItemDelete();
        return reminder;
    }

    /**
     * Deletes reminder from the database and cancels the alarm created.
     * @param reminder Reminder to be deleted
     */
    public void confirmDelete(Reminder reminder) {
        ReminderAlarm.cancelAlarm(reminder, context);
        db.deleteReminder(reminder);
    }

    /**
     * Sets the DeletedItemListener
     * @param deletedItemListener implementation of the DeletedItemListener Interface
     */
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

        /**
         * Sets data for the Reminder Item view
         * @param reminder Reminder object containing data to be used.
         */
        public void setData(Reminder reminder) {
            title.setText(reminder.getTitle());
            schedule.setText(reminder.getSchedule().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        }
    }
}
