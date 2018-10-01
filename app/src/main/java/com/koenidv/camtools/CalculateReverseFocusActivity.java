package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CalculateReverseFocusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_reversefocus);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        final boolean changing[] = {false};

        final TextView mApertureTextView = findViewById(R.id.apertureTextView);
        final TextView mDistanceTextView = findViewById(R.id.distanceTextView);
        final Spinner mSensorSpinner = findViewById(R.id.sensorsizeSpinner);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mNearSeekbar = findViewById(R.id.nearfocusSeekbar);
        final EditText mNearEditText = findViewById(R.id.nearfocusEditText);
        final SeekBar mFarSeekbar = findViewById(R.id.farfocusSeekbar);
        final EditText mFarEditText = findViewById(R.id.farfocusEditText);
        final Button mEquationsButton = findViewById(R.id.equationsButton);

        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mNearEditText.setText(prefs.getString("nearfocus", "4"));
        mNearSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("nearfocus", "4"))));
        mFarEditText.setText(prefs.getString("farfocus", "12"));
        mFarSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("farfocus", "12"))));
        calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));


        /*
         *  Listeners
         */

        //ToDo: Sensor size

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
                    calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(s.toString()), Float.valueOf(mNearEditText.getText().toString()), Float.valueOf(mFarEditText.getText().toString()));
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
                    calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()), Float.valueOf(mFarEditText.getText().toString()));
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
                    calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(mNearEditText.getText().toString()), Float.valueOf(s.toString()));

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
}
