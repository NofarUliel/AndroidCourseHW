package com.example.hw2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;


public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {


    private static final String TABLE_NAME = "ScoreDB";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_SCORE = "Score";
    private static final String COL_LOCATION_X = "LocationX";
    private static final String COL_LOCATION_Y = "LocationY";
    private static final int SIZE_RESULT = 10;


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT, " + COL_SCORE + " INT," + COL_LOCATION_X + " DOUBLE," + COL_LOCATION_Y + " DOUBLE )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name, int score, double location_x, double location_y) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_SCORE, score);
        contentValues.put(COL_LOCATION_X, location_x);
        contentValues.put(COL_LOCATION_Y, location_y);

        long newRowId = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (newRowId == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Cursor getData() {
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_SCORE + " DESC LIMIT " + SIZE_RESULT;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}
