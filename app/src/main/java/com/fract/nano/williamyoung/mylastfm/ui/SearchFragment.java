package com.fract.nano.williamyoung.mylastfm.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.fract.nano.williamyoung.mylastfm.R;

public class SearchFragment extends Fragment {
    private OnSearchQueryListener mListener;
    private EditText twoText;

    private int fragID = 2;
    private String queryOne = "";
    private String queryTwo = "";

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search, container, false);

        // setup first Text field
        EditText oneText = (EditText) view.findViewById(R.id.query_one_edit);
        oneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) { queryOne = s.toString(); }
        });

        // RadioButtons and clicks
        RadioButton radioTag = (RadioButton) view.findViewById(R.id.radio_tag);
        radioTag.setOnClickListener(mClickListener);

        RadioButton radioArtist = (RadioButton) view.findViewById(R.id.radio_artist);
        radioArtist.setOnClickListener(mClickListener);

        RadioButton radioTrack = (RadioButton) view.findViewById(R.id.radio_track);
        radioTrack.setOnClickListener(mClickListener);

        RadioButton radioArtAlb = (RadioButton) view.findViewById(R.id.radio_artalb);
        radioArtAlb.setOnClickListener(mClickListener);

        // setup second Text field
        twoText = (EditText) view.findViewById(R.id.query_two_edit);
        twoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) { queryTwo = s.toString(); }
        });

        // Search query button - initiates searchQuery
        Button mButton = (Button) view.findViewById(R.id.search_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Send query details back to MainActivity for Fragment initialization
             * @param v : v
             */
            @Override
            public void onClick(View v) {
                if (queryOne.equals("") || (fragID == 5 && queryTwo.equals(""))) {
                    Snackbar.make(view, getString(R.string.search_please), Snackbar.LENGTH_LONG).show();
                } else {
                    mListener.onSearchQuery(fragID, queryOne.trim(), queryTwo.trim());
                }
            }
        });

        return view;
    }

    /**
     * OnClickListener for the RadioButtons
     * sets fragID for query selection
     */
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = ((RadioButton) view).isChecked();

            switch(view.getId()) {
                case R.id.radio_tag:
                    if (checked) {
                        fragID = 2;
                        twoText.setEnabled(false);
                        twoText.setInputType(InputType.TYPE_NULL);
                    }
                    break;
                case R.id.radio_artist:
                    if (checked) {
                        fragID = 3;
                        twoText.setEnabled(false);
                        twoText.setInputType(InputType.TYPE_NULL);
                    }
                    break;
                case R.id.radio_track:
                    if (checked) {
                        fragID = 4;
                        twoText.setEnabled(false);
                        twoText.setInputType(InputType.TYPE_NULL);
                    }
                    break;
                case R.id.radio_artalb:
                    if (checked) {
                        fragID = 5;
                        twoText.setEnabled(true);
                        twoText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        getActivity().setTitle(getString(R.string.search_frag_name));

        if (context instanceof OnSearchQueryListener) {
            mListener = (OnSearchQueryListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchQueryListener {
        void onSearchQuery(int fragID, String queryOne, String queryTwo);
    }
}