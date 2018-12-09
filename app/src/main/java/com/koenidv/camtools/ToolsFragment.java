package com.koenidv.camtools;
//  Created by koenidv on 08.12.2018.

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ToolsFragment extends Fragment {
    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_tools, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        final ModuleManager mModuleManager = new ModuleManager();

        CardView mContrastCard = view.findViewById(R.id.selectContrastCard);

        mContrastCard.setOnClickListener(v -> {
            mModuleManager.addToHistory(getActivity(), "CalculateContrastActivity");
            Intent intent = new Intent(getActivity(), CalculateContrastActivity.class);
            startActivity(intent);
        });

    }
}
