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
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        CardView mHyperCard = view.findViewById(R.id.selectHyperCard);
        CardView mLimitsCard = view.findViewById(R.id.selectLimitsCard);
        CardView mReverseCard = view.findViewById(R.id.selectReverseCard);

        mHyperCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculateHyperfocalActivity.class);
                //intent.putExtra("image", "hyper")
                //        .putExtra("title", getString(R.string.select_hyper))
                //        .putExtra("description", getString(R.string.description_hyper))
                //        .putExtra("layout", "fragment_calculate_hyper");
                startActivity(intent);
            }
        });
        mLimitsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "limits")
                        .putExtra("title", getString(R.string.select_limits))
                        .putExtra("description", getString(R.string.description_limits))
                        .putExtra("layout", "fragment_calculate_limits");
                startActivity(intent);
            }
        });
        mReverseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalculatorActivity.class);
                intent.putExtra("image", "reverse")
                        .putExtra("title", getString(R.string.select_reverse))
                        .putExtra("description", getString(R.string.description_reverse))
                        .putExtra("layout", "fragment_calculate_reverse");
                startActivity(intent);
            }
        });

    }
}