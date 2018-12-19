package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import org.michaelbel.bottomsheet.BottomSheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = SettingsActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();


        final LinearLayout mDarkmodeLayout = findViewById(R.id.darkmodeLayout);
        final Button mDarkmodeButton = findViewById(R.id.settingsDarkmodeButton);
        final TextView mCamerasTextView = findViewById(R.id.settingsCamerasDescription);
        final Button mCamerasButton = findViewById(R.id.settingsCamerasButton);
        final TextView mApertureTextView = findViewById(R.id.settingsUnitsApertureTitle);
        final Button mApertureButton = findViewById(R.id.settingsUnitsApertureButton);
        final TextView mDistancesTextView = findViewById(R.id.settingsUnitsDistanceTitle);
        final Button mDistancesButton = findViewById(R.id.settingsUnitsDistanceButton);
        final TextView mNdTextView = findViewById(R.id.settingsUnitsNdTitle);
        final Button mNdButton = findViewById(R.id.settingsUnitsNdButton);
        final Button mFeedbackButton = findViewById(R.id.settingsFeedbackButton);


        if (prefs.getBoolean("system_darkmode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if (prefs.getBoolean("system_darkmode", false)) {
            mDarkmodeButton.setText(getString(R.string.setting_disable));
        }

        if (!prefs.getBoolean("used_darkmode", false)) {
            mDarkmodeLayout.setBackgroundColor(getResources().getColor(R.color.settings_darkmodeBackground));
            ((TextView) findViewById(R.id.settingsDarkmodeTitle)).setTextColor(getResources().getColor(R.color.background_light_lighter));
            ((TextView) findViewById(R.id.settingsDarkmodeDescription)).setTextColor(getResources().getColor(R.color.background_light_normal));
        }

        // Check for old version
        if (!prefs.getString("camera_1_name", "").equals("")) {
            prefsEdit.clear().apply();
            Snackbar.make(findViewById(R.id.rootView), "Es wurde zuvor eine inkompatible Version genutzt. Der Speicher wurde geleert.", Snackbar.LENGTH_INDEFINITE);
        }

        mCamerasTextView.setText(getString(R.string.setting_cameras_description).replace("%s",
                getResources().getQuantityString(R.plurals.cameras, prefs.getInt("cameras_amount", -1) + 1, prefs.getInt("cameras_amount", -1) + 1)));

        String apertureText = getString(R.string.setting_units_aperture_short) + ": ";
        switch (prefs.getInt("aperture_stops", 2)) {
            case 0:
                apertureText += getString(R.string.setting_units_aperture_full_short);
                break;
            case 1:
                apertureText += getString(R.string.setting_units_aperture_half_short);
                break;
            case 2:
                apertureText += getString(R.string.setting_units_aperture_third_short);
        }
        mApertureTextView.setText(apertureText);

        String distanceText = getString(R.string.setting_units_distance) + ": ";
        if (prefs.getBoolean("empirical", false)) {
            distanceText += getString(R.string.setting_units_distance_empirical);
        } else {
            distanceText += getString(R.string.setting_units_distance_metrical);
        }
        mDistancesTextView.setText(distanceText);

        String ndText = getString(R.string.setting_units_nd) + ": ";
        if (prefs.getBoolean("ndstops", false)) {
            ndText += getString(R.string.setting_units_nd_stops);
        } else {
            ndText += getString(R.string.setting_units_nd_times);
        }
        mNdTextView.setText(ndText);

        /*
         * Listeners
         */

        mDarkmodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentNightMode = getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    prefsEdit.putBoolean("system_darkmode", false).apply();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    prefsEdit.putBoolean("system_darkmode", true).apply();
                    if (!prefs.getBoolean("used_darkmode", false)) {
                        prefsEdit.putBoolean("used_darkmode", true).apply();
                    }
                }
                prefsEdit.putBoolean("theme_changed", true).apply();
                SettingsActivity.this.recreate();
            }
        });

        View.OnClickListener camerasClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, EditCamerasActivity.class));
            }
        };
        mCamerasButton.setOnClickListener(camerasClickListener);
        findViewById(R.id.settingsCamerasCard).setOnClickListener(camerasClickListener);

        mApertureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] items = new int[]{
                        R.string.setting_units_aperture_full,
                        R.string.setting_units_aperture_half,
                        R.string.setting_units_aperture_third
                };
                BottomSheet.Builder mBuilder = new BottomSheet.Builder(SettingsActivity.this);
                mBuilder.setTitle(getString(R.string.setting_units_aperture))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefsEdit.putInt("aperture_stops", which).apply();
                                String apertureText = getString(R.string.setting_units_aperture_short) + ": ";
                                switch (which) {
                                    case 0:
                                        apertureText += getString(R.string.setting_units_aperture_full_short);
                                        break;
                                    case 1:
                                        apertureText += getString(R.string.setting_units_aperture_half_short);
                                        break;
                                    case 2:
                                        apertureText += getString(R.string.setting_units_aperture_third_short);
                                }
                                mApertureTextView.setText(apertureText);
                            }
                        })
                        .setDarkTheme(prefs.getBoolean("system_darkmode", false))
                        .show();
            }
        });

        mDistancesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] items = new int[]{
                        R.string.setting_units_distance_metrical,
                        R.string.setting_units_distance_empirical
                };
                BottomSheet.Builder mBuilder = new BottomSheet.Builder(SettingsActivity.this);
                mBuilder.setTitle(getString(R.string.setting_units_distance))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefsEdit.putBoolean("empirical", which != 0).apply();
                                String distanceText = getString(R.string.setting_units_distance) + ": ";
                                if (which == 1) {
                                    distanceText += getString(R.string.setting_units_distance_empirical);
                                } else {
                                    distanceText += getString(R.string.setting_units_distance_metrical);
                                }
                                mDistancesTextView.setText(distanceText);
                            }
                        })
                        .setDarkTheme(prefs.getBoolean("system_darkmode", false))
                        .show();
            }
        });

        mNdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] items = new int[]{
                        R.string.setting_units_nd_stops,
                        R.string.setting_units_nd_times
                };
                BottomSheet.Builder mBuilder = new BottomSheet.Builder(SettingsActivity.this);
                mBuilder.setTitle(getString(R.string.setting_units_nd))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                prefsEdit.putBoolean("ndstops", which == 0).apply();
                                String ndText = getString(R.string.setting_units_nd) + ": ";
                                if (which == 1) {
                                    ndText += getString(R.string.setting_units_nd_times);
                                } else {
                                    ndText += getString(R.string.setting_units_nd_stops);
                                }
                                mNdTextView.setText(ndText);
                            }
                        })
                        .setDarkTheme(prefs.getBoolean("system_darkmode", false))
                        .show();
            }
        });

        View.OnClickListener feedbackClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = SettingsActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        };
        mFeedbackButton.setOnClickListener(feedbackClickListener);
        findViewById(R.id.settingsFeedbackCard).setOnClickListener(feedbackClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = SettingsActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        switch (id) {
            case R.id.action_delete_history:
                prefsEdit.remove("history").apply();
                break;
            case R.id.action_reset:
                prefsEdit.clear().apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.action_help:
                //startActivity(new Intent(MainActivity.this, FragmentActivity.class));
                break;
            case R.id.action_libraries:
                new LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .start(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = SettingsActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        if (prefs.getBoolean("theme_changed", false)) {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            prefsEdit.putBoolean("theme_changed", false).apply();
        }
        super.onBackPressed();
    }
}
