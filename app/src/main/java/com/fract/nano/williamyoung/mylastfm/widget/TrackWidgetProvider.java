package com.fract.nano.williamyoung.mylastfm.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.fract.nano.williamyoung.mylastfm.ui.TrackListFragment;

public class TrackWidgetProvider extends AppWidgetProvider {
    private final String LOG_TAG = TrackWidgetProvider.class.getSimpleName();
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Log.w(LOG_TAG, "onUpdate called");
        context.startService(new Intent(context, TrackWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //Log.w(LOG_TAG, "onAppWidgetOptionsChanged called");
        context.startService(new Intent(context, TrackWidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);

        if (TrackListFragment.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            //Log.w(LOG_TAG, "onReceive called");
            context.startService(new Intent(context, TrackWidgetIntentService.class));
        }
    }
}