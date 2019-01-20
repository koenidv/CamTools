package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ExposureFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_exposure, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final ModuleManager mModuleManager = new ModuleManager();

        CardView mNdCard = view.findViewById(R.id.selectNdCard);
        CardView mStarsCard = view.findViewById(R.id.selectStarsCard);
        CardView mTrailsCard = view.findViewById(R.id.selectStartrailsCard);

        mNdCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateNdActivity");
            Intent intent = new Intent(getActivity(), CalculateNdActivity.class);
            startActivity(intent);
        });
        mStarsCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateSpotStarsActivity");
            Intent intent = new Intent(getActivity(), CalculateSpotStarsActivity.class);
            startActivity(intent);
        });
        mTrailsCard.setOnClickListener(v -> {
        });

    }
}