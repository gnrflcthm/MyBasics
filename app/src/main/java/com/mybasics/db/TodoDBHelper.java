package com.mybasics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mybasics.models.Todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodoDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyBasics.db";    // Database File Name
    private static final int DB_VERSION = 1;                // Database Version

    private final String TABLE_NAME = "todos";              // Todos Table Name
    private final String ID = "id";                         // ID Column Name
    private final String CONTENT = "content";               // Content Column Name
    private final String STATUS = "status";                 // Status Column Name
    private final String DATE_ADDED = "date_added";         // Date Added Column Name

    private SQLiteDatabase db;                              // Database Object

    public TodoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE %s (" +
                "%s TEXT PRIMARY_KEY," +
                "%s TEXT NOT NULL," +
                "%s INTEGER NOT NULL DEFAULT 0," +
                "%s TEXT NOT NULL" +
                ")",
                TABLE_NAME, ID,
                CONTENT, STATUS, DATE_ADDED);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(sql);
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
        String sql = String.format("SELECT * FROM %s", TABLE_NAME);
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
        cv.put(ID, todo.getId());
        cv.put(CONTENT, todo.getContent());
        cv.put(STATUS, todo.isCompleted() ? 1 : 0);
        cv.put(DATE_ADDED, todo.getDateAdded().toString());
        long status = db.insert(TABLE_NAME, null, cv);
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
        int status = db.delete(TABLE_NAME, "id = ?", new String[] { todo.getId() });
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
        cv.put(CONTENT, todo.getContent());
        cv.put(STATUS, todo.isCompleted());
        int status = db.update(TABLE_NAME, cv, "id = ?", new String[] {todo.getId()});
        db.close();
        return status != 0;
    }
}
