package com.koenidv.camtools;
//  Created by koenidv on 19.01.2018.

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SunFragment extends Fragment {

    //Called when Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        //Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_sun, parent, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView sunText = (TextView) view.findViewById(R.id.sunText);
        sunText.setText("„Es hat geklappt!!“ ~ ♥");
        final ImageView aniView = (ImageView) view.findViewById(R.id.sunAniView);

        sunText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                Drawable drawable = aniView.getDrawable();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }
            }
        });
    }
}
