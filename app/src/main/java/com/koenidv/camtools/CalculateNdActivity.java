package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalculateNdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_nd);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();



        final boolean[] changing = {false};

        final TextView mResultTextView = findViewById(R.id.resultTextView);
        final Button mStartTimerButton = findViewById(R.id.startTimerButton);
        final SeekBar mTimeSeekbar = findViewById(R.id.exposuretimeSeekbar);
        final EditText mTimeEditText = findViewById(R.id.exposuretimeEditText);
        final SeekBar mStrengthSeekbar = findViewById(R.id.densitySeekbar);
        final EditText mStrengthEditText = findViewById(R.id.densityEditText);
        final Button mEquationsButton = findViewById(R.id.equationsButton);


        mTimeEditText.setText(prefs.getString("ndTime", "4"));
        mTimeSeekbar.setProgress(Math.round(Float.valueOf(mTimeEditText.getText().toString())));
        mStrengthEditText.setText(prefs.getString("ndStrength", "10"));
        if (prefs.getBoolean("ndstops", false)) {
            mStrengthSeekbar.setProgress(Math.round(Float.valueOf(mStrengthEditText.getText().toString())));
        } else {
            int counter = 0;
            for (float two = Float.valueOf(mStrengthEditText.getText().toString()); two > 2; two /= 2) {
                counter++;
            }
            mStrengthSeekbar.setProgress(counter);
        }
        if (Float.valueOf(mTimeEditText.getText().toString()) < 36000 && Float.valueOf(mStrengthEditText.getText().toString()) < 60000) {
            mResultTextView.setText(mModuleManager.convertTime(CalculateNdActivity.this, calculate(Float.valueOf(mTimeEditText.getText().toString()), Float.valueOf(mStrengthEditText.getText().toString()), prefs.getBoolean("ndstops", false))));
        }


        mStartTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CalculateNdActivity.this, "This will come later", Toast.LENGTH_LONG).show();
            }
        });

        mTimeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mTimeEditText.setText(String.valueOf(progress), TextView.BufferType.EDITABLE);
                    changing[0] = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !s.toString().equals(".") && !changing[0]) {
                    changing[0] = true;
                    mTimeSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                    changing[0] = false;
                }
                if (s.length() > 0 && !s.toString().equals(".")) {
                    prefsEditor.putString("ndTime", s.toString()).apply();
                    mResultTextView.setText(mModuleManager.convertTime(CalculateNdActivity.this, calculate(Float.valueOf(s.toString()), Float.valueOf(mStrengthEditText.getText().toString()), prefs.getBoolean("ndstops", false))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mStrengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    if (prefs.getBoolean("ndstops", false)) {
                        mStrengthEditText.setText(String.valueOf(progress));
                    } else {
                        mStrengthEditText.setText(String.valueOf(Math.round(Math.pow(2, progress))));
                    }
                    changing[0] = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mStrengthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !s.toString().equals(".") && !changing[0]) {
                    changing[0] = true;
                    if (prefs.getBoolean("ndstops", false)) {
                        mStrengthSeekbar.setProgress(Math.round(Float.valueOf(s.toString())));
                    } else {
                        int counter = 0;
                        for (float two = Float.valueOf(s.toString()); two > 2; two /= 2) {
                            counter++;
                        }
                        mStrengthSeekbar.setProgress(counter);
                    }
                    changing[0] = false;
                }
                if (s.length() > 0 && !s.toString().equals(".")) {
                    prefsEditor.putString("ndStrength", s.toString()).apply();
                    mResultTextView.setText(mModuleManager.convertTime(CalculateNdActivity.this, calculate(Float.valueOf(mTimeEditText.getText().toString()), Float.valueOf(s.toString()), prefs.getBoolean("ndstops", false))));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEquationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
                modalBottomSheet.show(getSupportFragmentManager(), "bottom sheet");
            }
        });
    }

    private float calculate(float mTime, float mStrength, boolean mStops) {
        if (mStops) {
            return (mTime * (float) Math.pow(2, mStrength));
        } else {
            return (mTime * mStrength);
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
                                new ShortcutInfo.Builder(CalculateNdActivity.this, "exposure_nd").build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateNdActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
