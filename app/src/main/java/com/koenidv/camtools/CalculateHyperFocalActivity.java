package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.net.Uri;
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

import androidx.appcompat.app.AppCompatActivity;

public class CalculateHyperFocalActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_calculate_hyperfocal);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();


        final boolean changing[] = {false};

        final LinearLayout mCameraLayout = findViewById(R.id.cameraLayout);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mApertureSeekbar = findViewById(R.id.apertureSeekbar);
        final EditText mApertureEditText = findViewById(R.id.apertureEditText);
        final LinearLayout mEquationsLayout = findViewById(R.id.equationsLayout);

        int lastCamera = prefs.getInt("cameras_last", 0);
        coc[0] = prefs.getFloat("camera_" + lastCamera + "_coc", 0.03f);

        mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + lastCamera + "_name", getString(R.string.camera_default_name))));
        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mApertureEditText.setText(prefs.getString("aperture", "3.5"));
        mApertureSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("aperture", "3.5"))));
        calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));

        if (prefs.getInt("cameras_amount", 0) == 0) {
            mCameraTextView.setText(getString(R.string.calculate_camera_add));
        }


        /*
         *  Listeners
         */

        mCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModuleManager.selectCamera(CalculateHyperFocalActivity.this, mCameraTextView, coc, "coc", 0.03f);
            }
        });

        mCameraTextView.addTextChangedListener(new TextWatcher() {
            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            //f:on
            @Override
            public void afterTextChanged(Editable s) {
                calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));
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
                    prefsEdit.putString("focallength", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(s.toString()), Float.valueOf(mApertureEditText.getText().toString()));
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
                    prefsEdit.putString("aperture", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()));
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
                mModuleManager.showEquations(CalculateHyperFocalActivity.this, "hyperfocal");
            }
        });
    }

    /*
     *  Calculations
     */

    @SuppressLint("DefaultLocale")
    private void calculate(float mConfusion, float mLength, float mAperture) {
        ModuleManager mModuleManager = new ModuleManager();
        float mHyper = mLength * mLength / (mAperture * mConfusion) + mLength;
        float mNear = mHyper * mHyper / (mHyper + mHyper - mLength);

        if (String.valueOf(mNear).equals("NaN")) {
            mNear = 0;
        }

        Spanned mHyperSpanned = mModuleManager.convertDistance(CalculateHyperFocalActivity.this, mHyper / 10000);
        Spanned mNearSpanned = mModuleManager.convertDistance(CalculateHyperFocalActivity.this, mNear / 10000);
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
                                new ShortcutInfo.Builder(CalculateHyperFocalActivity.this, "focus_hyper").build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateHyperFocalActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:
                Intent fcBrowserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_focus_more_calculate)));
                startActivity(fcBrowserIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(CalculateHyperFocalActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

}
