package com.fract.nano.williamyoung.mylastfm.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fract.nano.williamyoung.mylastfm.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DetailTrackActivity extends AppCompatActivity {
    private static final String SINGLE_TRACK = "single_track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_track);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets up AdMob View at bottom of DetailTrackActivity
        AdView mAdView = (AdView) findViewById(R.id.adDetailView);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            .build();
        if (mAdView != null) { mAdView.loadAd(adRequest); }

        if (savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putParcelable(SINGLE_TRACK, getIntent().getParcelableExtra(SINGLE_TRACK));

            DetailTrackFragment fragment = new DetailTrackFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                .add(R.id.containerDetail, fragment)
                .commit();
        }

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}