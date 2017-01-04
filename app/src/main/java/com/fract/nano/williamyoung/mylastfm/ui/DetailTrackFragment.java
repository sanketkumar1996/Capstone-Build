package com.fract.nano.williamyoung.mylastfm.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fract.nano.williamyoung.mylastfm.R;
import com.fract.nano.williamyoung.mylastfm.data.TrackContract;
import com.fract.nano.williamyoung.mylastfm.data.TrackHelper;
import com.fract.nano.williamyoung.mylastfm.util.DetailService;
import com.fract.nano.williamyoung.mylastfm.util.Track;
import com.fract.nano.williamyoung.mylastfm.util.TrackReceiver;
import com.squareup.picasso.Picasso;

public class DetailTrackFragment extends Fragment {
    private static final String SINGLE_TRACK = "single_track";
    public static final String RESULT_VALUE = "resultValue";
    public static final String ACTION_DATA_UPDATED = "com.fract.nano.williamyoung.mylastfm.app.ACTION_DATA_UPDATED";

    private View rootView;

    private Track mTrack;
    public TrackReceiver trackReceiver;

    private TrackHelper mHelper;
    private boolean toAdd = true;

    public DetailTrackFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTrack = getArguments().getParcelable(SINGLE_TRACK);
        }

        mHelper = new TrackHelper(getActivity());

        setupServiceReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail_track, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(SINGLE_TRACK)) {
            mTrack = savedInstanceState.getParcelable(SINGLE_TRACK);
        }

        updateViews();

        return rootView;
    }

    /**
     * Creates and sets a ServiceReceiver for acquiring the filled Track object
     * Receiver includes updating Layout Views
     */
    private void setupServiceReceiver() {
        trackReceiver = new TrackReceiver(new Handler());
        trackReceiver.setReceiver(new TrackReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == Activity.RESULT_OK) {
                    mTrack = resultData.getParcelable(RESULT_VALUE);
                    updateViews();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //Log.w("detailSR", "Device Not Connected");
                    Snackbar.make(rootView, getString(R.string.detail_disconnected), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Fills layout views with Track data
     */
    private void updateViews() {
        int iS = getResources().getInteger(R.integer.image_size);

        Button mBandButton = (Button) rootView.findViewById(R.id.band_url_button);
        final FloatingActionButton mFab = (FloatingActionButton) rootView.findViewById(R.id.add_fab);

        // two-pane mode and fragment without track
        if (mTrack == null) {
            mBandButton.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.INVISIBLE);
            return;
        }

        ImageView mTrackImageView = (ImageView) rootView.findViewById(R.id.track_image_view);
        Picasso.with(getActivity())
            .load(mTrack.getImage())
            .error(R.drawable.error)
            .placeholder(R.drawable.placeholder)
            .resize(iS, iS).centerCrop()
            .into(mTrackImageView);
        mTrackImageView.setContentDescription(mTrack.getTrackName());

        ImageView mTrackCoverView = (ImageView) rootView.findViewById(R.id.track_cover_view);
        if (!mTrack.getAlbumCover().equals("")) {
            Picasso.with(getActivity())
                .load(mTrack.getAlbumCover())
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder)
                .resize(iS, iS).centerCrop()
                .into(mTrackCoverView);
            mTrackCoverView.setContentDescription(mTrack.getAlbum());
        }

        TextView mTrackTextView = (TextView) rootView.findViewById(R.id.track_name_text);
        mTrackTextView.setText(mTrack.getTrackName());

        TextView mDurationTextView = (TextView) rootView.findViewById(R.id.track_duration_text);
        mDurationTextView.setText(mTrack.getFormattedLength());

        TextView mArtistTextView = (TextView) rootView.findViewById(R.id.detail_text_artist);
        mArtistTextView.setText(String.format(getString(R.string.format_artist), mTrack.getArtist()));

        TextView mAlbumTextView = (TextView) rootView.findViewById(R.id.detail_text_album);
        mAlbumTextView.setText(String.format(getString(R.string.format_album), mTrack.getAlbum()));

        mBandButton.setVisibility(View.VISIBLE);
        mBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url = Uri.parse(mTrack.getBandUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, url);

                startActivity(intent);
            }
        });

        mFab.setVisibility(View.VISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toAdd) {
                    ContentValues values = new ContentValues();

                    values.clear();

                    values.put(TrackContract.TrackEntry.COLUMN_ARTiST, mTrack.getArtist());
                    values.put(TrackContract.TrackEntry.COLUMN_ALBUM, mTrack.getAlbum());
                    values.put(TrackContract.TrackEntry.COLUMN_TRACK, mTrack.getTrackName());
                    values.put(TrackContract.TrackEntry.COLUMN_DURATION, mTrack.getLength());
                    values.put(TrackContract.TrackEntry.COLUMN_IMAGE, mTrack.getImage());
                    values.put(TrackContract.TrackEntry.COLUMN_COVER, mTrack.getAlbumCover());
                    values.put(TrackContract.TrackEntry.COLUMN_URL, mTrack.getBandUrl());

                    Uri uri = TrackContract.TrackEntry.CONTENT_URI;
                    getActivity().getContentResolver().insert(uri, values);

                    updateWidgets();

                    // Change FAB
                    mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_remove));
                    mFab.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.MaterialRed500, getActivity().getTheme()));

                    toAdd = false;
                } else {
                    Uri uri = TrackContract.TrackEntry.CONTENT_URI;
                    String selection = TrackContract.TrackEntry.COLUMN_ARTiST
                        + "=? AND "
                        + TrackContract.TrackEntry.COLUMN_ALBUM
                        + "=? AND "
                        + TrackContract.TrackEntry.COLUMN_TRACK
                        + "=?";

                    // TODO - Background Thread
                    getActivity().getContentResolver().delete(uri,
                        selection,
                        new String[]{
                            mTrack.getArtist(),
                            mTrack.getAlbum(),
                            mTrack.getTrackName()
                        }
                    );

                    updateWidgets();

                    // Change FAB back
                    mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add));
                    mFab.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.MaterialLightGreen500, getActivity().getTheme()));

                    toAdd = true;
                }
            }
        });

        String check = TrackContract.TrackEntry.COLUMN_ARTiST
            + "=? AND "
            + TrackContract.TrackEntry.COLUMN_ALBUM
            + "=? AND "
            + TrackContract.TrackEntry.COLUMN_TRACK
            + "=?";

        Cursor testTrack = getActivity().getContentResolver()
            .query(TrackContract.TrackEntry.CONTENT_URI,
                null,
                check,
                new String[]{
                    mTrack.getArtist(),
                    mTrack.getAlbum(),
                    mTrack.getTrackName()
                },
                null
            );

        if (testTrack != null && testTrack.getCount() != 0) {
            //Log.w("DetailTF", "toAdd");
            toAdd = false;

            mFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_remove));
            mFab.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.MaterialRed500, getActivity().getTheme()));

            testTrack.close();
        }
//        } else {
//            Log.w("DetailTF", "notToAdd");
//        }
    }

    /**
     * With a Content Provider data change, update Widgets with new data
     */
    private void updateWidgets() {
        //Log.w("MA", "updateWidgets");
        Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SINGLE_TRACK, mTrack);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(getString(R.string.detail_frag_name));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (mTrack != null && (mTrack.getAlbum().equals("") || mTrack.getAlbumCover().equals("") || mTrack.getLength() == 0)) {
            updateTrack();
        }
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Creates intent to pass Track object to the DetailService
     */
    private void updateTrack() {
        Intent detailIntent = new Intent(getActivity(), DetailService.class);
        detailIntent.putExtra(DetailService.RECEIVER, trackReceiver);
        detailIntent.putExtra(DetailService.TRACK_OBJ, mTrack);
        getActivity().startService(detailIntent);
    }
}