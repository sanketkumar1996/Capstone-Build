package com.fract.nano.williamyoung.mylastfm.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.fract.nano.williamyoung.mylastfm.R;
import com.fract.nano.williamyoung.mylastfm.data.TrackContract;
import com.fract.nano.williamyoung.mylastfm.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class TrackWidgetIntentService extends IntentService {
    private static final String[] TRACK_COLUMNS = {
        TrackContract.TrackEntry._ID,
        TrackContract.TrackEntry.COLUMN_ARTiST,
        TrackContract.TrackEntry.COLUMN_TRACK,
        TrackContract.TrackEntry.COLUMN_COVER
    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_ARTIST = 1;
    private static final int INDEX_TRACK = 2;
    private static final int INDEX_COVER = 3;

    private static final String ACTION_PLAYLIST = "action_playlist";

    public TrackWidgetIntentService() { super("TrackWidgetIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        // retrieve widget IDs: widgets to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TrackWidgetProvider.class));

        // get data from Content Provider
        Uri trackUri = TrackContract.TrackEntry.CONTENT_URI;

        Cursor data = getContentResolver().query(trackUri,
            TRACK_COLUMNS,
            null,
            null,
            TrackContract.TrackEntry._ID + " DESC"
        );

        if (data == null) {
            //Log.w("WidgetIntent", "Data is null");
            return;
        } else if (!data.moveToFirst()) {
            //Log.w("WidgetIntent", "Data has no rows");
            data.close();
            return;
        }

        // extract track data from cursor
        int id = data.getInt(INDEX_ID);

        String artist = data.getString(INDEX_ARTIST);
        String track = data.getString(INDEX_TRACK);
        String cover = data.getString(INDEX_COVER);
        String coverDesc = getString(R.string.widget_cover_desc, artist);

        data.close();

        String open = getString(R.string.widget_open);

        for (int appWidgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

            int widgetLayout = R.layout.widget_track_small;

            if (minHeight >= 80) { widgetLayout = R.layout.widget_track; }

            RemoteViews views = new RemoteViews(getPackageName(), widgetLayout);

            // set views
            views.setTextViewText(R.id.widget_track, track);
            if (minHeight >= 80) {
                views.setTextViewText(R.id.widget_artist, artist);
                views.setTextViewText(R.id.widget_mylastfm_text, open);
            }

            if (cover.contains("http")) {
                try {
                    Bitmap bitmap = Picasso.with(getApplicationContext()).load(cover).get();
                    views.setImageViewBitmap(R.id.widget_cover, bitmap);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        views.setContentDescription(R.id.widget_cover, coverDesc);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // create click intent to application
            Intent launchIntent = new Intent(this, MainActivity.class);
            launchIntent.putExtra(ACTION_PLAYLIST, 6);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}