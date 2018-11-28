package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class ExposureFragment extends Fragment {

    int selectedButton = -1;
    int buttonDisabledColor = Color.WHITE;
    int buttonDisabledTextColor = 0;

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_exposure, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        CardView mNdCard = view.findViewById(R.id.selectNdCard);
        CardView mStarsCard = view.findViewById(R.id.selectStarsCard);
        CardView mTrailsCard = view.findViewById(R.id.selectStartrailsCard);
        TextView mMoreTextView = view.findViewById(R.id.moreTextView);

        mNdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculateNdActivity.class);
                //intent.putExtra("image", "nd")
                //        .putExtra("title", getString(R.string.select_nd))
                //        .putExtra("description", getString(R.string.description_nd))
                //        .putExtra("layout", "fragment_calculate_nd");
                startActivity(intent);
            }
        });
        mStarsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculateSpotStarsActivity.class);
                //intent.putExtra("image", "stars")
                //        .putExtra("title", getString(R.string.select_stars))
                //        .putExtra("description", getString(R.string.description_stars))
                //        .putExtra("layout", "fragment_calculate_stars");
                startActivity(intent);
            }
        });
        mTrailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "startrails")
                        .putExtra("title", getString(R.string.select_startrails))
                        .putExtra("description", getString(R.string.description_startrails))
                        .putExtra("layout", "fragment_calculate_startrails");
                startActivity(intent);
            }
        });

        if (!prefs.getBoolean("show_hidden_cards", false)) {

            if (!mFirebaseRemoteConfig.getBoolean("show_exposure_nd") || !prefs.getBoolean("show_exposure_nd", true)) {
                mNdCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_exposure_spotstars") || !prefs.getBoolean("show_exposure_spotstars", true)) {
                mStarsCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_exposure_startrails") || !prefs.getBoolean("show_exposure_startrails", true)) {
                mTrailsCard.setVisibility(View.GONE);
            }
            if (mFirebaseRemoteConfig.getBoolean("show_exposure_more")) {
                mMoreTextView.setVisibility(View.VISIBLE);
            }
        }

    }
}