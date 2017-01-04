package com.fract.nano.williamyoung.mylastfm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrackHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mylastfm.db";
    private static final int DATABASE_VERSION = 1;

    public TrackHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    /**
     * Creates database table schema
     * @param db : writable database object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TrackContract.TrackEntry.TABLE_NAME + " ("
            + TrackContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TrackContract.TrackEntry.COLUMN_ARTiST + " TEXT NOT NULL,"
            + TrackContract.TrackEntry.COLUMN_ALBUM + " TEXT NOT NULL,"
            + TrackContract.TrackEntry.COLUMN_TRACK + " TEXT NOT NULL,"
            + TrackContract.TrackEntry.COLUMN_DURATION + " INTEGER NOT NULL DEFAULT 0,"
            + TrackContract.TrackEntry.COLUMN_IMAGE + " TEXT NOT NULL,"
            + TrackContract.TrackEntry.COLUMN_COVER + " TEXT NOT NULL,"
            + TrackContract.TrackEntry.COLUMN_URL + " TEXT NOT NULL"
            + ")"
        );
    }

    /**
     * Recreates table if database version is increased
     * @param db         : database currently open
     * @param oldVersion : old database version
     * @param newVersion : new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TrackContract.TrackEntry.TABLE_NAME);
        onCreate(db);
    }
}