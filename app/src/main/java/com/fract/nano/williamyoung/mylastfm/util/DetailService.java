package com.fract.nano.williamyoung.mylastfm.util;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailService extends IntentService {
    private final String LOG_TAG = DetailService.class.getSimpleName();

    public static final String RECEIVER = "com.fract.nano.williamyoung.mylastfm.extra.RECEIVER";
    public static final String TRACK_OBJ = "com.fract.nano.williamyoung.mylastfm.extra.TRACK_OBJ";
    public static final String RESULT_VALUE = "resultValue";

    private ResultReceiver receiver;
    private Track mTrack;

    public DetailService() { super("MyLastFM"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER);
        mTrack = intent.getParcelableExtra(TRACK_OBJ);

        if (isNetworkAvailable() && mTrack != null) {
            fetchTrackDetails();
        } else {
            // not connected
            receiver.send(Activity.RESULT_CANCELED, new Bundle());
        }
    }

    /**
     * Determines if a network connection is present
     * @return : Connected?
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        return (activeNetwork != null && activeNetwork.isConnected());
    }

    private void fetchTrackDetails() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trackJsonStr = null;
        String apiKey = Utility.getAPIKey(getApplicationContext());
        String format = "json";
        String method = "track.getInfo";

        try {
            final String TRACK_BASE_URL = "https://ws.audioscrobbler.com/2.0/?";
            final String METHOD_PARAM = "method";
            final String ARTIST_PARAM = "artist";
            final String TRACK_PARAM = "track";
            final String KEY_PARAM = "api_key";
            final String FORMAT_PARAM = "format";

            Uri builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                .appendQueryParameter(METHOD_PARAM, method)
                .appendQueryParameter(ARTIST_PARAM, mTrack.getArtist())
                .appendQueryParameter(TRACK_PARAM, mTrack.getTrackName())
                .appendQueryParameter(KEY_PARAM, apiKey)
                .appendQueryParameter(FORMAT_PARAM, format)
                .build();

            URL url = new URL(builtUri.toString());
            //Log.w("DetailService", builtUri.toString());

            // TODO - Research backend/networking libraries
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) { return; }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) { buffer.append(line + "\n"); }

            if (buffer.length() == 0) { return; }
            trackJsonStr = buffer.toString();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "ERROR: ", e);
        } finally {
            if (urlConnection != null) { urlConnection.disconnect(); }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    //Log.e(LOG_TAG, "ERROR closing stream: ", e);
                }
            }
        }

        try {
            getTrackDetailsFromJson(trackJsonStr);
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Extracts Track Detail data from API call
     * @param listJsonStr : JSON response string from API call
     * @throws JSONException
     */
    private void getTrackDetailsFromJson(String listJsonStr) throws JSONException {
        final String OWM_TRACK = "track";
        final String OWM_DURATION = "duration";
        final String OWM_ALBUM = "album";
        final String OWM_ALBUM_NAME = "title";
        final String OWM_COVER = "image";
        final String OWM_COVER_URL = "#text";

        JSONObject trackJson = new JSONObject(listJsonStr);
        JSONObject trackObject = trackJson.getJSONObject(OWM_TRACK);

        int length = trackObject.getInt(OWM_DURATION);
        if (length == 0) { length = 1; }

        JSONObject albumObject = trackObject.optJSONObject(OWM_ALBUM);
        String albumName;
        String coverURL;
        if (albumObject == null) {
            albumName = "NA";
            coverURL = "ERROR";
        } else {
            albumName = albumObject.getString(OWM_ALBUM_NAME);

            JSONArray coverArray = albumObject.getJSONArray(OWM_COVER);
            JSONObject coverObject = coverArray.getJSONObject(coverArray.length() - 1);
            coverURL = coverObject.getString(OWM_COVER_URL);
            if (coverURL.isEmpty()) { coverURL = "ERROR"; }
        }

        mTrack.setLength(length);
        mTrack.setAlbum(albumName);
        mTrack.setAlbumCover(coverURL);

        Bundle bundle = new Bundle();
        bundle.putParcelable(RESULT_VALUE, mTrack);
        receiver.send(Activity.RESULT_OK, bundle);
    }
}