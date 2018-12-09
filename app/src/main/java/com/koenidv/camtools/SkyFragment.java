package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.javiersantos.bottomdialogs.BottomDialog;

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

        CardView mOverviewCard = view.findViewById(R.id.selectSkyOverviewCard);
        CardView mDetailsCard = view.findViewById(R.id.selectSkyDetailsCard);
        CardView mArCard = view.findViewById(R.id.selectSkyArCard);
        CardView mPollutionCard = view.findViewById(R.id.selectLightpollutionCard);


        mOverviewCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CalculateSunOverviewActivity.class);
            startActivity(intent);
        });
        mDetailsCard.setOnClickListener(v -> new BottomDialog.Builder(getContext())
                .setTitle(R.string.coming_soon_title)
                .setContent(R.string.coming_soon_description)
                .setPositiveText(R.string.okay)
                .setPositiveBackgroundColorResource(R.color.colorAccent)
                .show());
        mArCard.setOnClickListener(v -> new BottomDialog.Builder(getContext())
                .setTitle(R.string.coming_soon_title)
                .setContent(R.string.coming_soon_description)
                .setPositiveText(R.string.okay)
                .setPositiveBackgroundColorResource(R.color.colorAccent)
                .show());
        mPollutionCard.setOnClickListener(v -> new BottomDialog.Builder(getContext())
                .setTitle(R.string.coming_soon_title)
                .setContent(R.string.coming_soon_description)
                .setPositiveText(R.string.okay)
                .setPositiveBackgroundColorResource(R.color.colorAccent)
                .show());

    }
}