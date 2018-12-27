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

public class FocusFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_focus, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final ModuleManager mModuleManager = new ModuleManager();


        CardView mHyperCard = view.findViewById(R.id.selectHyperCard);
        CardView mLimitsCard = view.findViewById(R.id.selectLimitsCard);
        CardView mReverseCard = view.findViewById(R.id.selectReverseCard);

        mHyperCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateHyperFocalActivity");
            Intent intent = new Intent(getActivity(), CalculateHyperFocalActivity.class);
            startActivity(intent);
        });
        mLimitsCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateFocusLimitsActivity");
            Intent intent = new Intent(getActivity(), CalculateFocusLimitsActivity.class);
            startActivity(intent);
        });
        mReverseCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateReverseFocusActivity");
            Intent intent = new Intent(getActivity(), CalculateReverseFocusActivity.class);
            startActivity(intent);
        });

    }
}