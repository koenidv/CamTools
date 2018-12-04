package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FocusFragment extends Fragment {

    int selectedButton = -1;
    int buttonDisabledColor = Color.WHITE;
    int buttonDisabledTextColor = 0;

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_focus, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final ModuleManager mModuleManager = new ModuleManager();
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        CardView mHyperCard = view.findViewById(R.id.selectHyperCard);
        CardView mLimitsCard = view.findViewById(R.id.selectLimitsCard);
        CardView mReverseCard = view.findViewById(R.id.selectReverseCard);
        TextView mMoreTextView = view.findViewById(R.id.moreTextView);

        mHyperCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.addToHistory(getActivity(), "CalculateHyperFocalActivity");
                Intent intent = new Intent(getActivity(), CalculateHyperFocalActivity.class);
                startActivity(intent);
            }
        });
        mLimitsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.addToHistory(getActivity(), "CalculateFocusLimitsActivity");
                Intent intent = new Intent(getActivity(), CalculateFocusLimitsActivity.class);
                startActivity(intent);
            }
        });
        mReverseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.addToHistory(getActivity(), "CalculateReverseFocusActivity");
                Intent intent = new Intent(getActivity(), CalculateReverseFocusActivity.class);
                startActivity(intent);
            }
        });

        if (!prefs.getBoolean("show_hidden_cards", false)) {
            if (!mFirebaseRemoteConfig.getBoolean("show_focus_hyper") || !prefs.getBoolean("show_focus_hyper", true)) {
                mHyperCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_focus_limits") || !prefs.getBoolean("show_focus_limits", true)) {
                mLimitsCard.setVisibility(View.GONE);
            }
            if (!mFirebaseRemoteConfig.getBoolean("show_focus_reverse") || !prefs.getBoolean("show_focus_reverse", true)) {
                mReverseCard.setVisibility(View.GONE);
            }
            if (mFirebaseRemoteConfig.getBoolean("show_focus_more")) {
                mMoreTextView.setVisibility(View.VISIBLE);
            }
        }

    }
}