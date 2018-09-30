package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = SettingsActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();


        final Button mDarkmodeButton = findViewById(R.id.settingsDarkmodeButton);
        final LinearLayout mUnitsLayout = findViewById(R.id.settingsUnitsCardLayout);
        final TextView mDistancesTextView = findViewById(R.id.settingsUnitsDistanceTitle);
        final Button mDistancesButton = findViewById(R.id.settingsUnitsDistanceButton);
        final TextView mNdTextView = findViewById(R.id.settingsUnitsNdTitle);
        final Button mNdButton = findViewById(R.id.settingsUnitsNdButton);

        if (prefs.getBoolean("darkmode", true)) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_dark_darker)));
            findViewById(R.id.settingsLinearLayout).setBackgroundColor(getResources().getColor(R.color.background_dark_normal));
            mDarkmodeButton.setText(getString(R.string.setting_disable));
            mUnitsLayout.setBackgroundColor(getResources().getColor(R.color.background_dark_darker));
            ((TextView) findViewById(R.id.settingsUnitsTitle)).setTextColor(getResources().getColor(R.color.text_main_dark));
            mDistancesTextView.setTextColor(getResources().getColor(R.color.text_sub_dark));
            mNdTextView.setTextColor(getResources().getColor(R.color.text_sub_dark));
        }

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


        mDarkmodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsEdit.putBoolean("darkmode", !prefs.getBoolean("darkmode", false)).apply();
                SettingsActivity.this.recreate();
            }
        });

        mDistancesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(SettingsActivity.this);
                }
                dialog.setTitle(getString(R.string.setting_units_distance))
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(new CharSequence[]{getString(R.string.setting_units_distance_metrical), getString(R.string.setting_units_distance_empirical)}, prefs.getBoolean("empirical", false) ? 1 : 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putBoolean("empirical", mChecked != 0).apply();
                                String distanceText = getString(R.string.setting_units_distance) + ": ";
                                if (mChecked == 1) {
                                    distanceText += getString(R.string.setting_units_distance_empirical);
                                } else {
                                    distanceText += getString(R.string.setting_units_distance_metrical);
                                }
                                mDistancesTextView.setText(distanceText);
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 100);
                            }
                        })
                        .show();
            }
        });

        mNdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(SettingsActivity.this);
                }
                dialog.setTitle(getString(R.string.setting_units_nd))
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(new CharSequence[]{getString(R.string.setting_units_nd_stops), getString(R.string.setting_units_nd_times)}, prefs.getBoolean("ndstops", false) ? 0 : 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putBoolean("ndstops", mChecked == 0).apply();
                                String ndText = getString(R.string.setting_units_nd) + ": ";
                                if (mChecked == 1) {
                                    ndText += getString(R.string.setting_units_nd_times);
                                } else {
                                    ndText += getString(R.string.setting_units_nd_stops);
                                }
                                mNdTextView.setText(ndText);
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 100);
                            }
                        })
                        .show();
            }
        });


        /*mChooseCamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(SettingsActivity.this);
                }
                dialog.setTitle(getString(R.string.settings_cam_choose))
                        .setNeutralButton(getString(R.string.offerHelp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                                //TODO: Help for sensor size
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(getResources().getStringArray(R.array.sensorFormats), prefs.getInt("chosenCamera", 0), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putInt("chosenCamera", mChecked);
                                prefsEdit.putFloat("coc", Float.parseFloat(getResources().getStringArray(R.array.sensorFormatValues)[mChecked])).apply();
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 1);
                                Toast.makeText(SettingsActivity.this, String.valueOf(prefs.getFloat("coc", 0.0029f)), Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
            }
        });*/
        /*mNdStrengthunitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final AlertDialog.Builder dialog;
                if (prefs.getBoolean("darkmode", false)) {
                    dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.darkDialog);
                } else {
                    dialog = new AlertDialog.Builder(SettingsActivity.this);
                }
                dialog.setTitle(getString(R.string.settings_nd_strengthunit_dialog))
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {

                            }
                        })
                        .setSingleChoiceItems(new CharSequence[]{getString(R.string.settings_nd_strengthunit_stops), getString(R.string.settings_nd_strengthunit_times)}, prefs.getBoolean("times_instead_stops", false) ? 1 : 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface mDialogInterface, int mChecked) {
                                prefsEdit.putBoolean("times_instead_stops", mChecked != 0).apply();
                                new Handler().postDelayed(new Runnable() { //Post delayed so the user can see what he selected
                                    @Override
                                    public void run() {
                                        mDialogInterface.dismiss();
                                    }
                                }, 100);
                            }
                        })
                        .show();
            }
        });*/
        /*mDarkmodeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton mCompoundButton, boolean mB) {
                prefsEdit.putBoolean("darkmode", mDarkmodeSwitch.isChecked()).apply();
                //Snackbar.make(SettingsActivity.this.findViewById(R.id.navigation), "Not ready yet", Snackbar.LENGTH_SHORT).show();
                try {
                    //new MainActivity().recreate();
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            Intent intent = SettingsActivity.this.getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            SettingsActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            SettingsActivity.this.finish();

                            SettingsActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent);
                        }
                    });
                } catch (NullPointerException npe) {
                    Snackbar.make(SettingsActivity.this.findViewById(R.id.navigation), "ERROR.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });*/
        /*mRateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final String appPackageName = SettingsActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        mAboutmeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                startActivity(new Intent(SettingsActivity.this, FragmentActivity.class));
            }
        });
        mAboutappLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                //Intent i = new Intent(MainActivity.this, FragmentActivity.class);
                //startActivity(i);
                new LibsBuilder()
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT)
                        //start the activity
                        .start(SettingsActivity.this);
            }
        });*/

    }
}