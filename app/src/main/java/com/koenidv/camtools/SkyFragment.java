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

public class SkyFragment extends Fragment {

    int selectedButton = -1;
    int buttonDisabledColor = Color.WHITE;
    int buttonDisabledTextColor = 0;

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_sky, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        CardView mOverviewCard = view.findViewById(R.id.selectSkyOverviewCard);
        CardView mDetailsCard = view.findViewById(R.id.selectSkyDetailsCard);
        CardView mArCard = view.findViewById(R.id.selectSkyArCard);
        CardView mPollutionCard = view.findViewById(R.id.selectLightpollutionCard);
        TextView mMoreTextView = view.findViewById(R.id.moreTextView);


        mOverviewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculateSunOverviewActivity.class);
                startActivity(intent);
            }
        });
        mDetailsCard.setOnClickListener(new View.OnClickListener() {
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
        mArCard.setOnClickListener(new View.OnClickListener() {
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
        mPollutionCard.setOnClickListener(new View.OnClickListener() {
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
            if (!mFirebaseRemoteConfig.getBoolean("show_sky_overview") || !prefs.getBoolean("show_sky_overview", true)) {
                mOverviewCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_sky_details") || !prefs.getBoolean("show_sky_details", true)) {
                mDetailsCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_sky_ar") || !prefs.getBoolean("show_sky_ar", true)) {
                mArCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_sky_lightpollution") || !prefs.getBoolean("show_sky_lightpollution", true)) {
                mPollutionCard.setVisibility(View.GONE);
            }
            if (mFirebaseRemoteConfig.getBoolean("show_sky_more")) {
                mMoreTextView.setVisibility(View.VISIBLE);
            }
        }


    }
}