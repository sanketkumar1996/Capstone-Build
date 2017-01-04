package com.fract.nano.williamyoung.mylastfm.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

// https://www.sitepoint.com/using-androids-content-providers-manage-app-data/
// Assistance with ContentProvider

// TODO - Research Content Provider libraries
public class TrackProvider extends ContentProvider {
    private static final String LOG_TAG = TrackProvider.class.getSimpleName();

    private SQLiteDatabase db;
    private TrackHelper mHelper;
    public static final UriMatcher mMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TrackContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TrackContract.TrackEntry.TABLE_NAME, TrackContract.TRACK_LIST);
        matcher.addURI(authority, TrackContract.TrackEntry.TABLE_NAME + "/#", TrackContract.TRACK_ITEM);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        boolean ret = true;
        mHelper = new TrackHelper(getContext());
        db = mHelper.getWritableDatabase();

        if (db == null) { // does database not exist?
            ret = false;
        } else if (db.isReadOnly()) { // can we not write to database?
            db.close();
            db = null;
            ret = false;
        }

        return ret;
    }

    /**
     * Queries database for data
     * @param uri           : URI to query
     * @param projection    : set of columns to return
     * @param selection     : selection criteria when filtering rows
     * @param selectionArgs : criteria
     * @param sortOrder     : how rows in cursor are sorted
     * @return : cursor object containing table data / null
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Log.w(LOG_TAG, "Query Entries");
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TrackContract.TrackEntry.TABLE_NAME);

        switch (mMatcher.match(uri)) {
            case TrackContract.TRACK_LIST:
                break;
            case TrackContract.TRACK_ITEM:
                builder.appendWhere(TrackContract.TrackEntry._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        return builder.query(db,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
    }

    /**
     * Gets type of URI parameter
     * @param uri : URI to be checked
     * @return : type of URI
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mMatcher.match(uri);

        switch (match) {
            case TrackContract.TRACK_LIST:
                return TrackContract.TrackEntry.CONTENT_TYPE;
            case TrackContract.TRACK_ITEM:
                return TrackContract.TrackEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Inserts a row into a DB table
     * @param uri    : content URI of insertion request
     * @param values : set of column name/value pairs to add
     * @return : URI for newly inserted item
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        //Log.w(LOG_TAG, "Insert Entry");
        if (mMatcher.match(uri) != TrackContract.TRACK_LIST) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        long id = db.insert(TrackContract.TrackEntry.TABLE_NAME,
            null,
            values
        );

        // is record stored?
        if (id > 0) {
            return ContentUris.withAppendedId(uri, id);
        }

        throw new SQLException("Error inserting into table: " + TrackContract.TrackEntry.TABLE_NAME);
    }

    /**
     * Delete row(s) from DB table
     * @param uri           : content URI to query
     * @param selection     : column deletion restriction
     * @param selectionArgs : criteria
     * @return number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int delete = 0;
        //Log.w(LOG_TAG, "Delete Entry");

        switch (mMatcher.match(uri)) {
            case TrackContract.TRACK_LIST:
                db.delete(TrackContract.TrackEntry.TABLE_NAME,
                    selection,
                    selectionArgs
                );
                break;
            case TrackContract.TRACK_ITEM:
                String where = TrackContract.TrackEntry._ID + " = " + uri.getLastPathSegment();

                if (!selection.isEmpty()) { where += " AND " + selection; }

                delete = db.delete(TrackContract.TrackEntry.TABLE_NAME,
                    where,
                    selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        return delete;
    }

    /**
     * Update a row(s) in the DB table
     * @param uri           : content URI to query
     * @param values        : set of column name/value pairs to update
     * @param selection     : selection criteria when filtering rows
     * @param selectionArgs : criteria
     * @return number of rows affected
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int update = 0;
        //Log.w(LOG_TAG, "Update Entry");

        switch (mMatcher.match(uri)) {
            case TrackContract.TRACK_LIST:
                db.update(TrackContract.TrackEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
                );
                break;
            case TrackContract.TRACK_ITEM:
                String where = TrackContract.TrackEntry._ID + " = " + uri.getLastPathSegment();

                if (!selection.isEmpty()) { where += " AND " + selection; }

                update = db.update(TrackContract.TrackEntry.TABLE_NAME,
                    values,
                    where,
                    selectionArgs
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        return update;
    }
}