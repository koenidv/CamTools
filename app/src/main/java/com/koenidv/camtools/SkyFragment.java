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
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "sky_details")
                        .putExtra("title", getString(R.string.select_sky_details))
                        .putExtra("description", getString(R.string.description_sky_details))
                        .putExtra("layout", "fragment_calculate_sky_details");
                startActivity(intent);
            }
        });
        mArCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "sky_ar")
                        .putExtra("title", getString(R.string.select_sky_ar))
                        .putExtra("description", getString(R.string.description_sky_ar))
                        .putExtra("layout", "fragment_calculate_sky_ar");
                startActivity(intent);
            }
        });
        mPollutionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "lightpollution")
                        .putExtra("title", getString(R.string.select_lightpollution))
                        .putExtra("description", getString(R.string.description_lightpollution))
                        .putExtra("layout", "fragment_calculate_lightpollution");
                startActivity(intent);
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