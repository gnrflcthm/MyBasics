package com.mybasics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mybasics.models.Note;
import com.mybasics.models.Reminder;
import com.mybasics.models.Todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static DBHelper dbInstance;

    public static final String DB_NAME = "MyBasics.db";
    private static final int DB_VERSION = 1;

    private final String TABLE_TODOS = "todos";
    private final String ID_TODOS = "id";
    private final String CONTENT_TODOS = "content";
    private final String STATUS_TODOS = "status";
    private final String DATE_ADDED_TODOS = "date_added";

    private final String TABLE_NOTES = "notes";
    private final String ID_NOTES = "id";
    private final String TITLE_NOTES = "title";
    private final String CONTENT_NOTES = "content";
    private final String DATE_CREATED_NOTES = "date_created";
    private final String DATE_LAST_MODIFIED_NOTES = "date_last_modified";

    private final String TABLE_REMINDERS = "reminders";
    private final String ID_REMINDERS = "id";
    private final String TITLE_REMINDERS = "title";
    private final String DATE_SCHEDULED_REMINDERS = "date_scheduled";

    private final String CREATE_TABLE_TODOS = String.format(
            "CREATE TABLE %s (" +
                    "%s TEXT PRIMARY_KEY," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL DEFAULT 0," +
                    "%s TEXT NOT NULL" +
                    ");",
            TABLE_TODOS, ID_TODOS,
            CONTENT_TODOS, STATUS_TODOS, DATE_ADDED_TODOS);

    private final String CREATE_TABLE_NOTES = String.format(
            "CREATE TABLE %s (" +
                    "%s TEXT PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL" +
                    ");",
            TABLE_NOTES, ID_NOTES, TITLE_NOTES, CONTENT_NOTES,
            DATE_CREATED_NOTES, DATE_LAST_MODIFIED_NOTES
    );

    private final String CREATE_TABLE_REMINDERS = String.format(
            "CREATE TABLE %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL" +
                    ");",
            TABLE_REMINDERS, ID_REMINDERS,
            TITLE_REMINDERS, DATE_SCHEDULED_REMINDERS
    );

    private final String DROP_TABLE_TODOS = String.format("DROP TABLE IF EXISTS %s", TABLE_TODOS);
    private final String DROP_TABLE_NOTES = String.format("DROP TABLE IF EXISTS %s", TABLE_NOTES);
    private final String DROP_TABLE_REMINDERS = String.format("DROP TABLE IF EXISTS %s", TABLE_REMINDERS);

    private SQLiteDatabase db; // Database Object

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DATABASE", "Initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODOS);
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_REMINDERS);
        Log.d("TABLES CREATED", "True");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_TODOS);
        db.execSQL(DROP_TABLE_NOTES);
        db.execSQL(DROP_TABLE_REMINDERS);
        onCreate(db);
    }

    /**
     * Fetches all todos from the database and returns them as a list.
     * Will return an empty ArrayList if there are no database entries.
     * @return List of Todos
     */
    public List<Todo> fetchTodos() {
        db = getReadableDatabase();
        List<Todo> todos = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLE_TODOS);
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String content = cursor.getString(1);
            boolean status = cursor.getInt(2) != 0;
            LocalDateTime dateAdded = LocalDateTime.parse(cursor.getString(3));
            todos.add(Todo.newInstance(id, content, status, dateAdded));
        }
        cursor.close();
        db.close();
        Log.d("Fetch To-Dos", "Done");
        return todos;
    }

    /**
     * Adds a to-do item to the databse.
     * @param todo Item to add
     * @return boolean indicating if item addition was successful.
     */
    public boolean addTodo(Todo todo) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_TODOS, todo.getId());
        cv.put(CONTENT_TODOS, todo.getContent());
        cv.put(STATUS_TODOS, todo.isCompleted() ? 1 : 0);
        cv.put(DATE_ADDED_TODOS, todo.getDateAdded().toString());
        long status = db.insert(TABLE_TODOS, null, cv);
        db.close();
        return status != -1;
    }

    /**
     * Deletes a to-do item from the database.
     * @param todo Item to delete
     * @return boolean indicating if item deletion was successful.
     */
    public boolean deleteTodo(Todo todo) {
        db = getWritableDatabase();
        int status = db.delete(TABLE_TODOS, "id = ?", new String[] { todo.getId() });
        db.close();
        return status != 0;
    }

    /**
     * Updates a to-do item from the databse.
     * @param todo Item to update
     * @return boolean indicating if item update was successful.
     */
    public boolean updateTodo(Todo todo) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONTENT_TODOS, todo.getContent());
        cv.put(STATUS_TODOS, todo.isCompleted());
        int status = db.update(TABLE_TODOS, cv, "id = ?", new String[] {todo.getId()});
        db.close();
        return status != 0;
    }

    /**
     * Fetches all notes and returns them as a list.
     * May return an empty list if there are no records.
     * @return List of Notes
     */
    public List<Note> fetchNotes() {
        db = getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLE_NOTES);
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            LocalDateTime dateCreated = LocalDateTime.parse(cursor.getString(3));
            LocalDateTime dateLastModified = LocalDateTime.parse(cursor.getString(4));
            notes.add(Note.newInstance(id, title, content, dateCreated, dateLastModified));
        }
        cursor.close();
        db.close();
        Log.d("Fetch Notes", "Done");
        return notes;
    }

    /**
     * Adds a note to the database
     * @param note note to be added
     * @return boolean indicating if note insertion was successful
     */
    public boolean addNote(Note note) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID_NOTES, note.getId());
        cv.put(TITLE_NOTES, note.getTitle());
        cv.put(CONTENT_NOTES, note.getContent());
        cv.put(DATE_CREATED_NOTES, note.getDateCreated().toString());
        cv.put(DATE_LAST_MODIFIED_NOTES, note.getDateLastModified().toString());
        long status = db.insert(TABLE_NOTES, null, cv);
        db.close();
        return status != -1;
    }

    /**
     * Updates a note from the databse
     * @param note note to be updated
     * @return boolean indicating if the update was successful
     */
    public boolean updateNote(Note note) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE_NOTES, note.getTitle());
        cv.put(CONTENT_NOTES, note.getContent());
        cv.put(DATE_LAST_MODIFIED_NOTES, note.getDateLastModified().toString());
        int res = db.update(TABLE_NOTES, cv, "id = ?", new String[] {note.getId()});
        db.close();
        return res != 0;
    }

    /**
     * Deletes the specified note.
     * @param note note to be deleted
     * @return boolean indicating if deletion was successful.
     */
    public boolean deleteNote(Note note) {
        db = getWritableDatabase();
        int status = db.delete(TABLE_NOTES, "id = ?", new String[] { note.getId() });
        db.close();
        return status != 0;
    }

    /**
     * Fetches all Reminders and returns them as a List.
     * @return List of reminders.
     */
    public List<Reminder> fetchReminders() {
        db = getReadableDatabase();
        List<Reminder> reminders = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLE_REMINDERS);
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            LocalDateTime schedule = LocalDateTime.parse(cursor.getString(2));
            reminders.add(Reminder.newInstance(id, title, schedule));
        }
        cursor.close();
        db.close();
        Log.d("Fetch Reminders", "Done");
        return reminders;
    }

    /**
     * Adds a reminder to the database.
     * @param reminder Reminder to be added.
     * @return id of newly inserted reminder if successful.
     */
    public long addReminder(Reminder reminder) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE_REMINDERS, reminder.getTitle());
        cv.put(DATE_SCHEDULED_REMINDERS, reminder.getSchedule().toString());
        return db.insert(TABLE_REMINDERS, null, cv);
    }

    /**
     * Deletes a reminder from the databse.
     * @param reminder Reminder to be deleted.
     * @return boolean indicating if deletion was successful.
     */
    public boolean deleteReminder(Reminder reminder) {
        db = getWritableDatabase();
        int status = db.delete(TABLE_REMINDERS, String.format("id = %s", reminder.getId()), null);
        return status != 0;
    }

    /**
     * Return an instance of the database.
     * Used to prevent multiple instances.
     * @param context Context to be used for database instantiation
     * @return DBHelper instance
     */
    public static DBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBHelper(context);
        }
        return dbInstance;
    }

    /**
     * Closes the current instance if one exists.
     */
    public static void closeInstance() {
        dbInstance = null;
    }
}
