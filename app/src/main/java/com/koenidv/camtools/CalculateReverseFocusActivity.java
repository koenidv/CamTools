package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CalculateReverseFocusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_reversefocus);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        final boolean changing[] = {false};

        final LinearLayout mCameraLayout = findViewById(R.id.cameraLayout);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mNearSeekbar = findViewById(R.id.nearfocusSeekbar);
        final EditText mNearEditText = findViewById(R.id.nearfocusEditText);
        final SeekBar mFarSeekbar = findViewById(R.id.farfocusSeekbar);
        final EditText mFarEditText = findViewById(R.id.farfocusEditText);
        final Button mEquationsButton = findViewById(R.id.equationsButton);

        int lastCamera = prefs.getInt("cameras_last", 0);
        final float[] coc = {prefs.getFloat("camera_" + lastCamera + "_coc", 0.03f)};

        mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + lastCamera + "_name", getString(R.string.camera_default_name))));
        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mNearEditText.setText(prefs.getString("nearfocus", "4"));
        mNearSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("nearfocus", "4"))));
        mFarEditText.setText(prefs.getString("farfocus", "12"));
        mFarSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("farfocus", "12"))));
        calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));

        if (prefs.getInt("cameras_amount", 0) == 0) {
            mCameraTextView.setText(getString(R.string.calculate_camera_add));
        }


        /*
         *  Listeners
         */

        mCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getInt("cameras_amount", 0) == 0) {
                    startActivity(new Intent(CalculateReverseFocusActivity.this, EditCamerasActivity.class));
                } else {
                    AlertDialog.Builder mDialog;
                    if (prefs.getBoolean("darkmode", false)) {
                        mDialog = new AlertDialog.Builder(CalculateReverseFocusActivity.this, R.style.darkDialog);
                    } else {
                        mDialog = new AlertDialog.Builder(CalculateReverseFocusActivity.this);
                    }
                    List<String> cameras = new ArrayList<>();

                    for (int camera = 0; camera <= prefs.getInt("cameras_amount", 0); camera++) {
                        cameras.add(prefs.getString("camera_" + camera + "_name", getString(R.string.camera_default_name)));
                    }

                    mDialog.setSingleChoiceItems(cameras.toArray(new String[cameras.size()]), prefs.getInt("cameras_last", 0), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefsEditor.putInt("cameras_last", which).apply();
                            coc[0] = prefs.getFloat("camera_" + which + "_coc", 0.03f);
                            mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + which + "_name", getString(R.string.camera_default_name))));
                            calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));
                            dialog.dismiss();
                        }
                    });
                    mDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    mDialog.setNeutralButton(getString(R.string.calculate_camera_manage), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(CalculateReverseFocusActivity.this, EditCamerasActivity.class));
                        }
                    });

                    mDialog.show();
                }
            }
        });

        mLengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mLengthEditText.setText(String.valueOf(progress));
                    changing[0] = false;
                }
            }

            //f:off
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            //f:on
        });

        mLengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mLengthSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                        changing[0] = false;
                    }
                    prefsEditor.putString("focallength", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(s.toString()), Float.valueOf(mNearEditText.getText().toString()), Float.valueOf(mFarEditText.getText().toString()));
                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mNearSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mNearEditText.setText(String.valueOf(progress));
                    changing[0] = false;
                }
            }

            //f:off
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            //f:on
        });

        mNearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mNearSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                        changing[0] = false;
                    }
                    prefsEditor.putString("nearfocus", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()), Float.valueOf(mFarEditText.getText().toString()));
                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mFarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mFarEditText.setText(String.valueOf(progress));
                    changing[0] = false;
                }
            }

            //f:off
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            //f:on
        });

        mFarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mFarSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                        changing[0] = false;
                    }
                    prefsEditor.putString("farfocus", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(mNearEditText.getText().toString()), Float.valueOf(s.toString()));

                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mEquationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Equations as bottom sheet
            }
        });

    }

    private void calculate(float mConfusion, float mLength, float mNear, float mFar) {
        mNear *= 10000;
        mFar *= 10000;

        float mCalc = (mLength / mNear + mLength / mFar - 2) / (1 / mFar - 1 / mNear);
        float mDistance = (mCalc - mLength) / (mCalc / mNear - 1);
        float mAperture = mLength * mLength / (mCalc * mConfusion);
        //float mDepth = mFar - mNear;

        Spanned mDistanceSpanned = Html.fromHtml(new DecimalFormat(mDistance > 200000 ? "#" : "#.#").format(mDistance / 10000) + "<small>" + getString(R.string.meter) + "</small>");
        Spanned mApertureSpanned = Html.fromHtml("<small>" + getString(R.string.aperture) + "</small>" + new DecimalFormat(mAperture > 10 ? "#" : "#.#").format(mAperture));
        //Spanned mDepthSpanned = Html.fromHtml(getString(R.string.focus_depth_indicator) + " <b>" + String.format("%.2f", mDepth / 1000) + "</b>");

        TextView mApertureTextView = findViewById(R.id.apertureTextView);
        TextView mDistanceTextView = findViewById(R.id.distanceTextView);

        mApertureTextView.setText(mApertureSpanned);
        mDistanceTextView.setText(mDistanceSpanned);
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
                                new ShortcutInfo.Builder(CalculateReverseFocusActivity.this, "focus_reverse").build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateReverseFocusActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
