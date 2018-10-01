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

public class CalculateHyperFocalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_hyperfocal);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        final boolean changing[] = {false};

        final TextView mNearTextView = findViewById(R.id.nearlimitTextView);
        final TextView mHyperTextView = findViewById(R.id.hyperfocalTextView);
        final TextView mFarTextView = findViewById(R.id.farlimitTextView);
        final Spinner mSensorSpinner = findViewById(R.id.sensorsizeSpinner);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mApertureSeekbar = findViewById(R.id.apertureSeekbar);
        final EditText mApertureEditText = findViewById(R.id.apertureEditText);
        final Button mEquationsButton = findViewById(R.id.equationsButton);

        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mApertureEditText.setText(prefs.getString("aperture", "3.5"));
        mApertureSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("aperture", "3.5"))));
        calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));


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
                    calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(s.toString()), Float.valueOf(mApertureEditText.getText().toString()));
                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mApertureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mApertureEditText.setText(String.valueOf(progress));
                    changing[0] = false;
                }
            }

            //f:off
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            //f:on
        });

        mApertureEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && !s.equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mApertureSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                        changing[0] = false;
                    }
                    prefsEditor.putString("aperture", s.toString()).apply();
                    calculate(prefs.getFloat("coc", 0.0029f), Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()));
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

    /*
     *  Calculations
     */

    @SuppressLint("DefaultLocale")
    private void calculate(float mConfusion, float mLength, float mAperture) {
        float mHyper = mLength * mLength / (mAperture * mConfusion) + mLength;
        float mNear = mHyper * mHyper / (mHyper + mHyper - mLength);

        if (String.valueOf(mNear).equals("NaN")) {
            mNear = 0;
        }

        Spanned mHyperSpanned = Html.fromHtml(new DecimalFormat(mHyper > 200000 ? "#" : "#.#").format(mHyper / 10000) + "<small>" + getString(R.string.meter) + "</small>");
        Spanned mNearSpanned = Html.fromHtml(new DecimalFormat(mNear > 200000 ? "#" : "#.#").format(mNear / 10000) + "<small>" + getString(R.string.meter) + "</small>");
        String mFarString = "∞";

        if (mHyper == Double.POSITIVE_INFINITY || String.valueOf(mHyper).equals("NaN")) {
            mNearSpanned = Html.fromHtml("∞");
            mHyperSpanned = Html.fromHtml("∞");
        }

        final TextView mNearTextView = findViewById(R.id.nearlimitTextView);
        final TextView mHyperTextView = findViewById(R.id.hyperfocalTextView);
        final TextView mFarTextView = findViewById(R.id.farlimitTextView);

        mNearTextView.setText(mNearSpanned);
        mHyperTextView.setText(mHyperSpanned);
        mFarTextView.setText(mFarString);
    }
}
