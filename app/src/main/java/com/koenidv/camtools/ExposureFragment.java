package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
        final ModuleManager mModuleManager = new ModuleManager();
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        CardView mNdCard = view.findViewById(R.id.selectNdCard);
        CardView mStarsCard = view.findViewById(R.id.selectStarsCard);
        CardView mTrailsCard = view.findViewById(R.id.selectStartrailsCard);
        TextView mMoreTextView = view.findViewById(R.id.moreTextView);

        mNdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.addToHistory(getActivity(), "CalculateNdActivity");
                Intent intent = new Intent(getActivity(), CalculateNdActivity.class);
                startActivity(intent);
            }
        });
        mStarsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.addToHistory(getActivity(), "CalculateSpotStarsActivity");
                Intent intent = new Intent(getActivity(), CalculateSpotStarsActivity.class);
                startActivity(intent);
            }
        });
        mTrailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomDialog.Builder(getContext())
                        .setTitle(R.string.coming_soon_title)
                        .setContent(R.string.coming_soon_description)
                        .setPositiveText(R.string.okay)
                        .setPositiveBackgroundColorResource(R.color.colorAccent)
                        .show();
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