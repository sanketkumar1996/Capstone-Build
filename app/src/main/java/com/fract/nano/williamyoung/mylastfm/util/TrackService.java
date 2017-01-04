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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Handles acquiring JSON data from Last.FM API
 */
public class TrackService extends IntentService {
    private final String LOG_TAG = TrackService.class.getSimpleName();

    public static final String RECEIVER = "com.fract.nano.williamyoung.mylastfm.extra.RECEIVER";
    public static final String FRAG_ID = "com.fract.nano.williamyoung.mylastfm.extra.FRAG_ID";
    public static final String QUERY_ONE = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_ONE";
    public static final String QUERY_TWO = "com.fract.nano.williamyoung.mylastfm.extra.QUERY_TWO";
    public static final String RESULT_VALUE = "resultValue";

    private ResultReceiver receiver;
    private int fragID;
    private String queryOne;
    private String queryTwo;

    public TrackService() { super("MyLastFM"); }

    /**
     * Captures intent from TrackListFragment to begin Service
     * @param intent : container of extras
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(RECEIVER);
        fragID = intent.getIntExtra(FRAG_ID, 6);
        queryOne = intent.getStringExtra(QUERY_ONE);
        queryTwo = intent.getStringExtra(QUERY_TWO);

        if (isNetworkAvailable()) {
            fetchTrackList();
        } else {
            // not connected,
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

    /**
     * Builds the API URL and fetches JSON response
     */
    private void fetchTrackList() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trackJsonStr = null;
        String apiKey = Utility.getAPIKey(getApplicationContext());
        String format = "json";

        // 6 method variant API calls
        String chartTop = "chart.gettoptracks";
        String geoTop = "geo.gettoptracks";
        String tagTop = "tag.gettoptracks";
        String artistTop = "artist.gettoptracks";
        String trackSearch = "track.search";
        String albumInfo = "album.getinfo";

        try {
            final String TRACK_BASE_URL = "https://ws.audioscrobbler.com/2.0/?";
            final String METHOD_PARAM = "method";
            final String COUNTRY_PARAM = "country";
            final String TAG_PARAM = "tag";
            final String ARTIST_PARAM = "artist";
            final String TRACK_PARAM = "track";
            final String ALBUM_PARAM = "album";
            final String KEY_PARAM = "api_key";
            final String FORMAT_PARAM = "format";

            Uri builtUri = null;

            switch(fragID) {
                case 0:                                                 // Top Chart Tracks
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, chartTop)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 1:                                                 // Country Top Chart Tracks
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, geoTop)
                        .appendQueryParameter(COUNTRY_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 2:                                                 // Search by Tag
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, tagTop)
                        .appendQueryParameter(TAG_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 3:                                                 // Search by Artist
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, artistTop)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 4:                                                 // Search by Track
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, trackSearch)
                        .appendQueryParameter(TRACK_PARAM, queryOne)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
                case 5:                                                 // Search by Artist/Album
                    builtUri = Uri.parse(TRACK_BASE_URL).buildUpon()
                        .appendQueryParameter(METHOD_PARAM, albumInfo)
                        .appendQueryParameter(ARTIST_PARAM, queryOne)
                        .appendQueryParameter(ALBUM_PARAM, queryTwo)
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .build();
                    break;
            }

            URL url = new URL(builtUri.toString());
            //Log.w("TrackService", builtUri.toString());

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
            Log.e(LOG_TAG, "ERROR: ", e);
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

        // API results vary
        // JSON parsing segregation necessary
        try {
            switch(fragID) {
                case 0:
                case 1:
                case 2:
                    getTrackDataFromJson(trackJsonStr, false);
                    break;
                case 3:
                    getTrackDataFromJson(trackJsonStr, true);
                    break;
                case 4:
                    getTrackSearchFromJson(trackJsonStr);
                    break;
                case 5:
                    getAlbumTracksFromJson(trackJsonStr);
                    break;
            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Extracts track data from a list of tracks
     * @param listJsonStr : JSON response string from API calls
     * @param top         : toptracks or tracks
     * @throws JSONException
     */
    private void getTrackDataFromJson(String listJsonStr, boolean top) throws JSONException {
        final String OWM_TRACKS = "tracks";
        final String OWM_TOPTRACKS = "toptracks";
        final String OWM_LIST = "track";
        final String OWM_NAME = "name";
        final String OWM_DURATION = "duration";
        final String OWM_ARTIST = "artist";
        final String OWM_BAND = "name";
        final String OWM_URL = "url";
        final String OWM_EGAMI = "image";
        final String OWM_IMAGE = "#text";

        JSONObject listJson = new JSONObject(listJsonStr);
        JSONObject skcart = listJson.optJSONObject(top
            ? OWM_TOPTRACKS
            : OWM_TRACKS
        );
        // If error object, no results
        // Notify Fragment
        if (skcart == null) {
            receiver.send(Activity.RESULT_FIRST_USER, new Bundle());
            return;
        }

        JSONArray trackArray = skcart.getJSONArray(OWM_LIST);

        // If no results, Notify Fragment
        if (trackArray.length() == 0) {
            receiver.send(Activity.RESULT_FIRST_USER, new Bundle());
            return;
        }

        ArrayList<Track> resultTrack = new ArrayList<>();

        for (int i = 0; i < trackArray.length(); i++) {
            JSONObject singleTrack = trackArray.getJSONObject(i);

            String trackName = singleTrack.getString(OWM_NAME);
            int length = (top) ? 0 : singleTrack.getInt(OWM_DURATION);

            JSONObject artistObject = singleTrack.getJSONObject(OWM_ARTIST);
            String artistName = artistObject.getString(OWM_BAND);
            String bandURL = artistObject.getString(OWM_URL);

            JSONArray imageArray = singleTrack.getJSONArray(OWM_EGAMI);
            JSONObject egamiObject = imageArray.getJSONObject(imageArray.length() - 1);
            String imageURL = egamiObject.getString(OWM_IMAGE);
            if (imageURL.isEmpty()) { imageURL = "ERROR"; }

            String album = "";
            String albumCover = "";

            Track track = Track.newInstance(artistName, album, trackName, length, imageURL, albumCover, bandURL);
            resultTrack.add(track);
        }

        // return ArrayList of tracks to Fragment
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_VALUE, resultTrack);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    /**
     * Extracts track information from a track search
     * @param trackJsonStr : JSON response string from API calls
     * @throws JSONException
     */
    private void getTrackSearchFromJson(String trackJsonStr) throws JSONException {
        final String OWM_RESULTS = "results";
        final String OWM_TRACK_MATCH = "trackmatches";
        final String OWM_TRACK_ARRAY = "track";
        final String OWM_NAME = "name";
        final String OWM_ARTIST = "artist";
        final String OWM_EGAMI = "image";
        final String OWM_IMAGE = "#text";

        JSONObject listJson = new JSONObject(trackJsonStr);
        JSONObject results = listJson.getJSONObject(OWM_RESULTS);
        JSONObject matches = results.getJSONObject(OWM_TRACK_MATCH);
        JSONArray trackArray = matches.getJSONArray(OWM_TRACK_ARRAY);

        // If no results, Notify Fragment
        if (trackArray.length() == 0) {
            receiver.send(Activity.RESULT_FIRST_USER, new Bundle());
            return;
        }

        ArrayList<Track> resultTrack = new ArrayList<>();

        for (int i = 0; i < trackArray.length(); i++) {
            JSONObject singleTrack = trackArray.getJSONObject(i);

            String trackName = singleTrack.getString(OWM_NAME);
            String artistName = singleTrack.getString(OWM_ARTIST);
            String bandURL = Utility.getBandURL(artistName);

            JSONArray imageArray = singleTrack.getJSONArray(OWM_EGAMI);
            JSONObject egamiObject = imageArray.getJSONObject(imageArray.length() - 1);
            String imageURL = egamiObject.getString(OWM_IMAGE);
            if (imageURL.isEmpty()) { imageURL = "ERROR"; }

            int length = 0;
            String album = "";
            String albumCover = "";

            Track track = Track.newInstance(artistName, album, trackName, length, imageURL, albumCover, bandURL);
            resultTrack.add(track);
        }

        // return ArrayList of tracks to Fragment
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_VALUE, resultTrack);
        receiver.send(Activity.RESULT_OK, bundle);
    }

    /**
     * Extracts track information from an album info result
     * @param trackJsonStr : JSON response string from API calls
     * @throws JSONException
     */
    private void getAlbumTracksFromJson(String trackJsonStr) throws JSONException {
        final String OWM_RESULT = "album";
        final String OWM_ALBUM = "name";
        final String OWM_COVER = "image";
        final String OWM_COVER_URL = "#text";
        final String OWM_TRACKS = "tracks";
        final String OWM_TRACK_ARRAY = "track";
        final String OWM_NAME = "name";
        final String OWM_ARTIST = "artist";
        final String OWM_ARTIST_NAME = "name";
        final String OWM_URL = "url";

        JSONObject listJson = new JSONObject(trackJsonStr);
        JSONObject albumResult = listJson.optJSONObject(OWM_RESULT);

        // If error object, no results
        // Notify Fragment
        if (albumResult == null) {
            receiver.send(Activity.RESULT_FIRST_USER, new Bundle());
            return;
        }
        String albumName = albumResult.getString(OWM_ALBUM);

        JSONArray albumCoverArray = albumResult.getJSONArray(OWM_COVER);
        JSONObject coverObject = albumCoverArray.getJSONObject(3);
        String albumCover = coverObject.getString(OWM_COVER_URL);
        if (albumCover.isEmpty()) { albumCover = "ERROR"; }

        JSONObject trackObject = albumResult.getJSONObject(OWM_TRACKS);
        JSONArray trackArray = trackObject.getJSONArray(OWM_TRACK_ARRAY);

        // If no results, Notify Fragment
        if (trackArray.length() == 0) {
            receiver.send(Activity.RESULT_FIRST_USER, new Bundle());
            return;
        }

        ArrayList<Track> resultTrack = new ArrayList<>();

        for (int i = 0; i < trackArray.length(); i++) {
            JSONObject singleTrack = trackArray.getJSONObject(i);

            String trackName = singleTrack.getString(OWM_NAME);

            JSONObject artistObject = singleTrack.getJSONObject(OWM_ARTIST);
            String artistName = artistObject.getString(OWM_ARTIST_NAME);
            String bandURL = artistObject.getString(OWM_URL);

            int length = 0;

            Track track = Track.newInstance(artistName, albumName, trackName, length, albumCover, albumCover, bandURL);
            resultTrack.add(track);
        }

        // return ArrayList of tracks to Fragment
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RESULT_VALUE, resultTrack);
        receiver.send(Activity.RESULT_OK, bundle);
    }
}