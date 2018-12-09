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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    final float[] coc = {0};

    @Override
    protected void onResume() {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + prefs.getInt("cameras_last", 0) + "_name", getString(R.string.camera_default_name))));
        coc[0] = prefs.getFloat("camera_" + prefs.getInt("cameras_last", 0) + "_coc", 0.03f);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_reversefocus);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();


        final boolean changing[] = {false};

        final LinearLayout mCameraLayout = findViewById(R.id.cameraLayout);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mNearSeekbar = findViewById(R.id.nearfocusSeekbar);
        final EditText mNearEditText = findViewById(R.id.nearfocusEditText);
        TextView mNearIndicatorTextView = findViewById(R.id.nearfocusIndicator);
        final SeekBar mFarSeekbar = findViewById(R.id.farfocusSeekbar);
        final EditText mFarEditText = findViewById(R.id.farfocusEditText);
        TextView mFarIndicatorTextView = findViewById(R.id.farfocusIndicator);
        final LinearLayout mEquationsLayout = findViewById(R.id.equationsLayout);

        int lastCamera = prefs.getInt("cameras_last", 0);
        coc[0] = prefs.getFloat("camera_" + lastCamera + "_coc", 0.03f);

        mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + lastCamera + "_name", getString(R.string.camera_default_name))));
        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mNearEditText.setText(prefs.getString("nearfocus", "4"));
        mNearSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("nearfocus", "4"))));
        mFarEditText.setText(prefs.getString("farfocus", "12"));
        mFarSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("farfocus", "12")) - Float.valueOf(prefs.getString("nearfocus", "4"))));
        calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));

        if (prefs.getInt("cameras_amount", 0) == 0) {
            mCameraTextView.setText(getString(R.string.calculate_camera_add));
        }

        final float conversionfactor = prefs.getBoolean("empirical", false) ? 3.281f : 1f;
        if (prefs.getBoolean("empirical", false)) {
            mNearIndicatorTextView.setText(R.string.feet);
            mFarIndicatorTextView.setText(R.string.feet);
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
                            calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")) / conversionfactor, Float.valueOf(prefs.getString("farfocus", "12")) / conversionfactor);
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
        mCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.selectCamera(CalculateReverseFocusActivity.this, mCameraTextView, coc, "coc");
            }
        });

        mCameraTextView.addTextChangedListener(new TextWatcher() {
            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            //f:on
            @Override
            public void afterTextChanged(Editable s) {
                calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")) / conversionfactor, Float.valueOf(prefs.getString("farfocus", "12")) / conversionfactor);
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
                    calculate(coc[0], Float.valueOf(s.toString()), Float.valueOf(mNearEditText.getText().toString()) / conversionfactor, Float.valueOf(mFarEditText.getText().toString()) / conversionfactor);
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
                    calculate(coc[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()) / conversionfactor, Float.valueOf(mFarEditText.getText().toString()) / conversionfactor);
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
                    mFarEditText.setText(String.valueOf(Math.round(progress + Float.valueOf(mNearEditText.getText().toString()))));
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
                    calculate(coc[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(mNearEditText.getText().toString()) / conversionfactor, Float.valueOf(s.toString()) / conversionfactor);

                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mEquationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.showEquations(CalculateReverseFocusActivity.this, "reversefocus");
            }
        });

    }

    private void calculate(float mConfusion, float mLength, float mNear, float mFar) {
        ModuleManager mModuleManager = new ModuleManager();
        mNear *= 10000;
        mFar *= 10000;

        float mCalc = (mLength / mNear + mLength / mFar - 2) / (1 / mFar - 1 / mNear);
        float mDistance = (mCalc - mLength) / (mCalc / mNear - 1);
        float mAperture = mLength * mLength / (mCalc * mConfusion);

        Spanned mDistanceSpanned = mModuleManager.convertDistance(CalculateReverseFocusActivity.this, mDistance / 10000);
        Spanned mApertureSpanned = Html.fromHtml("<small>" + getString(R.string.aperture) + "</small>" + new DecimalFormat(mAperture > 10 ? "#" : "#.#").format(mAperture));

        if (mFar <= mNear) {
            mDistanceSpanned = null;
            mApertureSpanned = null;
        }

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

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(CalculateReverseFocusActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
