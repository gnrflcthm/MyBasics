package com.mybasics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mybasics.models.Note;
import com.mybasics.models.Todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyBasics.db";    // Database File Name
    private static final int DB_VERSION = 1;                // Database Version

    private final String TABLE_TODOS = "todos";              // Todos Table Name
    private final String ID_TODOS = "id";                         // ID Column Name
    private final String CONTENT_TODOS = "content";               // Content Column Name
    private final String STATUS_TODOS = "status";                 // Status Column Name
    private final String DATE_ADDED_TODOS = "date_added";         // Date Added Column Name

    private final String TABLE_NOTES = "notes";
    private final String ID_NOTES = "id";
    private final String TITLE_NOTES = "title";
    private final String CONTENT_NOTES = "content";
    private final String DATE_CREATED_NOTES = "date_created";
    private final String DATE_LAST_MODIFIED_NOTES = "date_last_modified";

    private final String CREATE_TABLE_TODOS = String.format(
            "CREATE TABLE %s (" +
                    "%s TEXT PRIMARY_KEY," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL DEFAULT 0," +
                    "%s TEXT NOT NULL" +
                    ")",
            TABLE_TODOS, ID_TODOS,
            CONTENT_TODOS, STATUS_TODOS, DATE_ADDED_TODOS);

    private final String CREATE_TABLE_NOTES = String.format(
            "CREATE TABLE %s (" +
                    "%s TEXT PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL" +
                    ")",
            TABLE_NOTES, ID_NOTES, TITLE_NOTES, CONTENT_NOTES,
            DATE_CREATED_NOTES, DATE_LAST_MODIFIED_NOTES
    );

    private final String DROP_TABLE_TODOS = String.format("DROP TABLE IF EXISTS %s", TABLE_TODOS);
    private final String DROP_TABLE_NOTES = String.format("DROP TABLE IF EXISTS %s", TABLE_NOTES);

    private SQLiteDatabase db;                              // Database Object

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODOS);
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_TODOS);
        db.execSQL(DROP_TABLE_NOTES);
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

    public List<Note> fetchNotes() {
        db = getWritableDatabase();
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
        return notes;
    }


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

    public boolean deleteSelectedNotes(List<String> ids) {
        db = getWritableDatabase();
        StringBuilder selection = new StringBuilder();
        for (String id : ids) {
            selection.append(String.format("\"%s\" ", id));
        }
        String finalSelection = selection.toString().trim().replaceAll(" ", ", ");
        Log.d("SELECTED IDS: ", finalSelection);
        int status = db.delete(TABLE_NOTES, String.format("%s IN (%s)", ID_NOTES, finalSelection), null);
        db.close();
        return status != 0;
    }
}
