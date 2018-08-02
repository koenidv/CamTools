package com.koenidv.camtools;
//Created by koenidv on 27.05.2018.


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SunDetailsFragment extends Fragment {

    List<sunDetail> sunDetailList = new ArrayList<>();


    public SunDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calulcate_sky_overview, container, false);
    }

    //Called after onCreateView(). View setup here.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //TextView mDayTextView = view.findViewById(R.id.sunDayTextView);

        Date time = new Date();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dayDateFormat = new SimpleDateFormat(getString(R.string.sunDayDateFormat));
        //mDayTextView.setText(dayDateFormat.format(time));


        RecyclerView mRecyclerView = view.findViewById(R.id.sunDetailsRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new sunDetailsAdapter(sunDetailList);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        sunDetail mSunDetail = new sunDetail("00:42", getString(R.string.sun_details_night), getString(R.string.sun_details_evening_description_night), getResources().getColor(R.color.sun_night));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("02:05", getString(R.string.sun_details_astronomical), getString(R.string.sun_details_morning_description_astronomical), getResources().getColor(R.color.sun_astronomical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("03:46", getString(R.string.sun_details_nautical), getString(R.string.sun_details_morning_description_nautical), getResources().getColor(R.color.sun_nautical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("04:44", getString(R.string.sun_details_bluehour), getString(R.string.sun_details_morning_description_bluehour), getResources().getColor(R.color.sun_bluehour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("05:01", getString(R.string.sun_details_goldenhour), getString(R.string.sun_details_morning_description_goldenhour), getResources().getColor(R.color.sun_goldenhour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("05:26", getString(R.string.sun_details_sunrise), getString(R.string.sun_details_morning_description_sunrise), getResources().getColor(R.color.sun_sunrise));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("06:16", getString(R.string.sun_details_sunrise), getString(R.string.sun_details_morning_description_day), getResources().getColor(R.color.sun_day));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("13:24", getString(R.string.sun_details_noon), getString(R.string.sun_details_morning_description_noon), getResources().getColor(R.color.sun_noon));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("20:32", getString(R.string.sun_details_goldenhour), getString(R.string.sun_details_evening_description_goldenhour), getResources().getColor(R.color.sun_goldenhour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("21:23", getString(R.string.sun_details_sunset), getString(R.string.sun_details_evening_description_sunset), getResources().getColor(R.color.sun_sunset));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("21:48", getString(R.string.sun_details_bluehour), getString(R.string.sun_details_evening_description_bluehour), getResources().getColor(R.color.sun_bluehour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("22:05", getString(R.string.sun_details_nautical), getString(R.string.sun_details_evening_description_nautical), getResources().getColor(R.color.sun_nautical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("23:04", getString(R.string.sun_details_astronomical), getString(R.string.sun_details_evening_description_astronomical), getResources().getColor(R.color.sun_astronomical));
        sunDetailList.add(mSunDetail);

        mAdapter.notifyDataSetChanged();
    }

}
