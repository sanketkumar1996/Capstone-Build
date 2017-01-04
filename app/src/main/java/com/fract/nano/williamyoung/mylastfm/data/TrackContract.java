package com.fract.nano.williamyoung.mylastfm.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// Table/Column names for track database
public class TrackContract {
    public static final String CONTENT_AUTHORITY = "com.fract.nano.williamyoung.mylastfm";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TRACK = "track";
    public static final int TRACK_LIST = 1;
    public static final int TRACK_ITEM = 2;

    // Database Table Columns
    public static final class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "track";

        /** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
        public static final String _ID = "_id";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_ARTiST = "artist";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_ALBUM = "album";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_TRACK = "track_name";

        /** Type: INTEGER NOT NULL DEFAULT 0 */
        public static final String COLUMN_DURATION = "duration";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_IMAGE = "image_url";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_COVER = "cover_url";

        /** Type: TEXT NOT NULL */
        public static final String COLUMN_URL = "band_url";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_TRACK)
            .build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
    }
}