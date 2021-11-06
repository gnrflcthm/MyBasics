package com.mybasics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mybasics.models.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyBasics.db";
    private static final int DB_VERSION = 1;

    private final String TABLE_NAME = "todos";
    private final String ID = "id";
    private final String CONTENT = "content";
    private final String STATUS = "status";

    private SQLiteDatabase db;

    public TodoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE %s (" +
                "%s TEXT PRIMARY_KEY," +
                "%s TEXT NOT NULL," +
                "%s INTEGER NOT NULL DEFAULT 0" +
                ")",
                TABLE_NAME, ID,
                CONTENT, STATUS);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(sql);
        onCreate(db);
    }

    public List<Todo> fetchTodos() {
        db = getReadableDatabase();
        List<Todo> todos = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLE_NAME);
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String content = cursor.getString(1);
            boolean status = cursor.getInt(2) == 0 ? false : true;
            todos.add(Todo.newInstance(id, content, status));
        }
        cursor.close();
        db.close();
        return todos;
    }

    public boolean addTodo(Todo todo) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, todo.getId());
        cv.put(CONTENT, todo.getContent());
        cv.put(STATUS, todo.isCompleted() ? 1 : 0);
        long status = db.insert(TABLE_NAME, null, cv);
        db.close();
        return status == 0 ? false : true;
    }

    public boolean deleteTodo(Todo todo) {
        db = getWritableDatabase();
        int status = db.delete(TABLE_NAME, "id = ?", new String[] { todo.getId() });
        db.close();
        return status == 0 ? false : true;
    }

    public boolean updateTodo(Todo todo) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CONTENT, todo.getContent());
        cv.put(STATUS, todo.isCompleted() ? true : false);
        int status = db.update(TABLE_NAME, cv, "id = %s", new String[] {todo.getId()});
        db.close();
        return status == 0 ? false : true;
    }
}
