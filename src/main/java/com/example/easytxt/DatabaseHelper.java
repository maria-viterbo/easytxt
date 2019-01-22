package com.example.easytxt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mitch on 2016-05-13.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db = getWritableDatabase();

    public static final String DATABASE_NAME = "mylist.db";
    public static final String TABLE_NAME = "mylist_data";
    public static final String ID = "ID";
    public static final String MESSAGE = "MESSAGE";
    public static final String DAYS = "DAYS";
    public static final String START_TIME = "START_TIME";
    public static final String END_TIME = "END_TIME";
    public static final String ACTIVE = "ACTIVE";

    public static final int COL_INDEX_MESSAGE = 1;
    public static final int COL_INDEX_DAYS = 2;
    public static final int COL_INDEX_START_TIME = 3;
    public static final int COL_INDEX_END_TIME = 4;
    public static final int COL_INDEX_ACTIVE = 5;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME
                + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " MESSAGE TEXT, "
                + " DAYS TEXT, "
                + " START_TIME TEXT, "
                + " END_TIME TEXT, "
                + " ACTIVE TEXT)";

        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    void insert(String message, String days, String startTime,
                       String endTime, String active) {

        // Create a new row
        ContentValues row = new ContentValues();

        // Add values to row
        row.put(MESSAGE, message);
        row.put(DAYS, days);
        row.put(START_TIME, startTime);
        row.put(END_TIME, endTime);
        row.put(ACTIVE, active);

        // Add the row to the table
        db.insert(TABLE_NAME, null, row);
    }


    void update(int id, String message, String days, String startTime,
                           String endTime, String active) {

        // Create row
        ContentValues row = new ContentValues();

        // Add values to row
        row.put(MESSAGE, message);
        row.put(DAYS, days);
        row.put(START_TIME, startTime);
        row.put(END_TIME, endTime);
        row.put(ACTIVE, active);

        // Update the row where the ID matches
        db.update(TABLE_NAME, row, "ID=" + id, null);
    }


    public boolean containsKey(int id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id;

        Cursor data = db.rawQuery(query, null);

        return data.moveToFirst();
    }


    void delete(int id) {

        // Delete row where ID matches
        db.delete(TABLE_NAME, "ID=" + id, null);

    }


    Cursor get(int id) {

        // Query to find row where ID matches
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id;

        // Run query to get row
        return db.rawQuery(query, null);
    }


    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        return data;
    }

}
