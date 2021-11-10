package com.mybasics.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mybasics.models.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyBasics.db";
    private static final int DB_VERSION = 1;

    private final String TABLE_NAME = "notes";
    private final String ID = "id";
    private final String TITLE = "title";
    private final String CONTENT = "content";
    private final String DATE_CREATED = "date_created";
    private final String DATE_LAST_MODIFIED = "date_last_modified";

    private SQLiteDatabase db;

    public NoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format(
                "CREATE TABLE %s (" +
                "%s TEXT PRIMARY KEY," +
                "%s TEXT NOT NULL," +
                "%s TEXT NOT NULL," +
                "%s TEXT NOT NULL," +
                "%s TEXT NOT NULL" +
                ")",
                TABLE_NAME, ID, TITLE, CONTENT,
                DATE_CREATED, DATE_LAST_MODIFIED
        );
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(sql);
        onCreate(db);
    }

    public List<Note> fetchNotes() {
        db = getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLE_NAME);
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
        cv.put(ID, note.getId());
        cv.put(TITLE, note.getTitle());
        cv.put(CONTENT, note.getContent());
        cv.put(DATE_CREATED, note.getDateCreated().toString());
        cv.put(DATE_LAST_MODIFIED, note.getDateLastModified().toString());
        long status = db.insert(TABLE_NAME, null, cv);
        db.close();
        return status != -1;
    }

    public boolean updateNote(Note note) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, note.getTitle());
        cv.put(CONTENT, note.getContent());
        cv.put(DATE_LAST_MODIFIED, note.getDateLastModified().toString());
        int res = db.update(TABLE_NAME, cv, "id = ?", new String[] {note.getId()});
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
        int status = db.delete(TABLE_NAME, String.format("%s IN (%s)", ID, finalSelection), null);
        db.close();
        return status != 0;
    }
}
