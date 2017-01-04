package com.fract.nano.williamyoung.mylastfm.util;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fract.nano.williamyoung.mylastfm.R;
import com.fract.nano.williamyoung.mylastfm.data.TrackContract;
import com.squareup.picasso.Picasso;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.TrackViewHolder> {
    private Cursor mCursor;
    private Context mContext;
    private ClickListener mListener;

    // Database Columns with associated integer ids
    public static final String[] TRACK_COLUMNS = {
        TrackContract.TrackEntry.TABLE_NAME + "." + TrackContract.TrackEntry._ID,
        TrackContract.TrackEntry.COLUMN_ARTiST,
        TrackContract.TrackEntry.COLUMN_ALBUM,
        TrackContract.TrackEntry.COLUMN_TRACK,
        TrackContract.TrackEntry.COLUMN_DURATION,
        TrackContract.TrackEntry.COLUMN_IMAGE,
        TrackContract.TrackEntry.COLUMN_COVER,
        TrackContract.TrackEntry.COLUMN_URL
    };

    static final int COL_TRACK_ID = 0;
    static final int COL_TRACK_ARTIST = 1;
    static final int COL_TRACK_ALBUM = 2;
    static final int COL_TRACK_TRACK = 3;
    static final int COL_TRACK_DUR = 4;
    static final int COL_TRACK_IMAGE = 5;
    static final int COL_TRACK_COVER = 6;
    static final int COL_TRACK_URL = 7;

    /**
     * Constructor without initializing Cursor
     * @param context : Activity/Fragment context
     */
    public PlaylistAdapter(Context context) {
        this.mCursor = null;
        this.mContext = context;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_track_item, viewGroup, false);

        TrackViewHolder viewHolder = new TrackViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder viewHolder, int i) {
        mCursor.moveToPosition(i);

        viewHolder.trackTextView.setText(mCursor.getString(COL_TRACK_TRACK));
        viewHolder.artistTextView.setText(mCursor.getString(COL_TRACK_ARTIST));

        Picasso.with(mContext)
            .load(mCursor.getString(COL_TRACK_IMAGE))
            .error(R.drawable.error)
            .placeholder(R.drawable.placeholder)
            .into(viewHolder.imageView);

        viewHolder.imageView.setContentDescription(mCursor.getString(COL_TRACK_TRACK));
    }

    public void setOnItemClickListener(ClickListener listener) { this.mListener = listener; }

    @Override
    public int getItemCount() {
        return (null != mCursor) ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Used within TrackListFragment
     * Detects RecyclerView item click
     */
    public interface ClickListener {
        void onItemClick(Track track, View v);
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView trackTextView;
        protected TextView artistTextView;
        protected ImageView deleteButton;

        public TrackViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.trackTextView = (TextView) view.findViewById(R.id.trackText);
            this.artistTextView = (TextView) view.findViewById(R.id.artistText);
            this.deleteButton = (ImageView) view.findViewById(R.id.deleteButton);
            this.deleteButton.setVisibility(View.VISIBLE);
            this.deleteButton.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            Track track = Track.newInstance(
                mCursor.getString(COL_TRACK_ARTIST),
                mCursor.getString(COL_TRACK_ALBUM),
                mCursor.getString(COL_TRACK_TRACK),
                mCursor.getInt(COL_TRACK_DUR),
                mCursor.getString(COL_TRACK_IMAGE),
                mCursor.getString(COL_TRACK_COVER),
                mCursor.getString(COL_TRACK_URL)
            );

            // pass Track object back to Fragment
            mListener.onItemClick(track, v); }
    }
}