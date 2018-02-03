package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FocusFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_focus, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //TextView starsText = (TextView) view.findViewById(R.id.sunText);
        //starsText.setText("Sterne: Es hat geklappt!! - ♥");
    }
}
