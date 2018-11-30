package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalculateSunOverviewActivity extends AppCompatActivity {

    List<sunDetail> sunDetailList = new ArrayList<>();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_sun_overview);

        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        LinearLayout mSearchLayout = findViewById(R.id.searchLayout);
        RecyclerView mRecyclerView = findViewById(R.id.detailsRecyclerView);


        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AutocompleteFilter citiesFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(citiesFilter)
                            .build(CalculateSunOverviewActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

            }
        });


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CalculateSunOverviewActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new sunDetailsAdapter(sunDetailList);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);


        Location location = new Location("39.9522222", "-75.1641667");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "America/New_York");

        String mAstronomicalSunrise = calculator.getAstronomicalSunriseForDate(Calendar.getInstance());
        String mNauticalSunrise = calculator.getNauticalSunriseForDate(Calendar.getInstance());
        String mCivilSunrise = calculator.getCivilSunriseForDate(Calendar.getInstance());
        String mSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String mSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        String mCivilSunset = calculator.getCivilSunsetForDate(Calendar.getInstance());
        String mNauticalSunset = calculator.getNauticalSunsetForDate(Calendar.getInstance());
        String mAstronomicalSunset = calculator.getAstronomicalSunsetForDate(Calendar.getInstance());

        sunDetail mSunDetail = new sunDetail("00:42", getString(R.string.sun_details_night), getString(R.string.sun_details_evening_description_night), getResources().getColor(R.color.sun_night));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mAstronomicalSunrise, getString(R.string.sun_details_astronomical), getString(R.string.sun_details_morning_description_astronomical), getResources().getColor(R.color.sun_astronomical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mNauticalSunrise, getString(R.string.sun_details_nautical), getString(R.string.sun_details_morning_description_nautical), getResources().getColor(R.color.sun_nautical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mCivilSunrise, getString(R.string.sun_details_bluehour), getString(R.string.sun_details_morning_description_bluehour), getResources().getColor(R.color.sun_bluehour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("???", getString(R.string.sun_details_goldenhour), getString(R.string.sun_details_morning_description_goldenhour), getResources().getColor(R.color.sun_goldenhour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("???", "Zivile Dämmerung endet", getString(R.string.sun_details_morning_description_sunrise), getResources().getColor(R.color.sun_sunrise), Color.DKGRAY);
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mSunrise, getString(R.string.sun_details_sunrise), getString(R.string.sun_details_morning_description_day), getResources().getColor(R.color.sun_day), Color.DKGRAY);
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("???", getString(R.string.sun_details_noon), getString(R.string.sun_details_morning_description_noon), getResources().getColor(R.color.sun_noon), Color.DKGRAY);
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("???", getString(R.string.sun_details_goldenhour), getString(R.string.sun_details_evening_description_goldenhour), getResources().getColor(R.color.sun_goldenhour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mCivilSunset, getString(R.string.sun_details_sunset), getString(R.string.sun_details_evening_description_sunset), getResources().getColor(R.color.sun_sunset));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail("???", getString(R.string.sun_details_bluehour), getString(R.string.sun_details_evening_description_bluehour), getResources().getColor(R.color.sun_bluehour));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mNauticalSunset, getString(R.string.sun_details_nautical), getString(R.string.sun_details_evening_description_nautical), getResources().getColor(R.color.sun_nautical));
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail();
        sunDetailList.add(mSunDetail);
        mSunDetail = new sunDetail(mAstronomicalSunset, getString(R.string.sun_details_astronomical), getString(R.string.sun_details_evening_description_astronomical), getResources().getColor(R.color.sun_astronomical));
        sunDetailList.add(mSunDetail);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("TAG", "Place: " + place.getName());
                Log.i("TAG", "Coordinates: " + place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_shortcut:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
                    assert mShortcutManager != null;
                    if (mShortcutManager.isRequestPinShortcutSupported()) {
                        ShortcutInfo pinShortcutInfo =
                                new ShortcutInfo.Builder(CalculateSunOverviewActivity.this, "sky_overview").build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                    startActivity(new Intent(CalculateSunOverviewActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
