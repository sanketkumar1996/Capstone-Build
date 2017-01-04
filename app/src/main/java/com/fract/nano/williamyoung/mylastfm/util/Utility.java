package com.fract.nano.williamyoung.mylastfm.util;

import android.content.Context;

import com.fract.nano.williamyoung.mylastfm.R;

import java.util.Locale;

public class Utility {
    /**
     * Returns API Key for the Last.FM API
     * @param context : Activity/Fragment context
     * @return API key string
     */
    public static String getAPIKey(Context context) { return context.getString(R.string.apiKey); }

    /**
     * gets formatted Band Last.FM URL
     * @param band : Band name
     * @return : Last.FM URL
     */
    public static String getBandURL(String band) {
        return String.format(Locale.getDefault(), band, R.string.band_url_format);
    }
}