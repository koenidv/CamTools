package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CalculateSpotStarsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_spotstars);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();

        final boolean changing[] = {false};

        final LinearLayout mCameraLayout = findViewById(R.id.cameraLayout);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        final ExpandableLinearLayout mResultExpandLayout = findViewById(R.id.resultExpandableLayout);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mApertureSeekbar = findViewById(R.id.apertureSeekbar);
        final EditText mApertureEditText = findViewById(R.id.apertureEditText);
        final Button mStartTimerButton = findViewById(R.id.startTimerButton);
        final Button mExpandButton = findViewById(R.id.expandButton);
        final Button mEquationsButton = findViewById(R.id.equationsButton);

        int mLastCamera = prefs.getInt("cameras_last", 0);
        final float[] mPixelpitch = {prefs.getFloat("camera_" + mLastCamera + "_pitch", 6.6f)};

        mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + mLastCamera + "_name", getString(R.string.camera_default_name))));
        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mApertureEditText.setText(prefs.getString("aperture", "3.5"));
        mApertureSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("aperture", "3.5"))));
        calculate(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));

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
                    startActivity(new Intent(CalculateSpotStarsActivity.this, EditCamerasActivity.class));
                } else {
                    AlertDialog.Builder mDialog;
                    if (prefs.getBoolean("darkmode", false)) {
                        mDialog = new AlertDialog.Builder(CalculateSpotStarsActivity.this, R.style.darkDialog);
                    } else {
                        mDialog = new AlertDialog.Builder(CalculateSpotStarsActivity.this);
                    }
                    List<String> cameras = new ArrayList<>();

                    for (int camera = 0; camera <= prefs.getInt("cameras_amount", 0); camera++) {
                        cameras.add(prefs.getString("camera_" + camera + "_name", getString(R.string.camera_default_name)));
                    }

                    mDialog.setSingleChoiceItems(cameras.toArray(new String[cameras.size()]), prefs.getInt("cameras_last", 0), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefsEditor.putInt("cameras_last", which).apply();
                            mPixelpitch[0] = prefs.getFloat("camera_" + which + "_pixelpitch", 6.6f);
                            mCameraTextView.setText(getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + which + "_name", getString(R.string.camera_default_name))));
                            calculate(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));
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
                            startActivity(new Intent(CalculateSpotStarsActivity.this, EditCamerasActivity.class));
                        }
                    });

                    mDialog.show();
                }
            }
        });

        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultExpandLayout.toggle();
                mExpandButton.setCompoundDrawables(null, null, null, null);
                if (mStartTimerButton.getVisibility() == View.VISIBLE) {
                    mStartTimerButton.setVisibility(View.GONE);
                    mExpandButton.setText(getString(R.string.stars_collapse));
                } else {
                    mStartTimerButton.setVisibility(View.VISIBLE);
                    mExpandButton.setText(getString(R.string.stars_expand));
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
                    calculate(mPixelpitch[0], Float.valueOf(s.toString()), Float.valueOf(mApertureEditText.getText().toString()));
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
                    calculate(mPixelpitch[0], Float.valueOf(mLengthEditText.getText().toString()), Float.valueOf(s.toString()));
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

    private void calculate(float mPixelpitch, float mLength, float mAperture) {
        float mNpf = (35 * mAperture + 30 * mPixelpitch) / mLength;
        float m500 = 500 / mLength;
        float m600 = 600 / mLength;

        Spanned mNpfSpanned = Html.fromHtml(new DecimalFormat(mNpf > 10 ? "#" : "#.#").format(mNpf) + "<small>" + getString(R.string.time_seconds) + "</small>");
        Spanned m500Spanned = Html.fromHtml(new DecimalFormat(m500 > 10 ? "#" : "#.#").format(m500) + "<small>" + getString(R.string.time_seconds) + "</small>");
        Spanned m600Spanned = Html.fromHtml(new DecimalFormat(m600 > 10 ? "#" : "#.#").format(m600) + "<small>" + getString(R.string.time_seconds) + "</small>");

        final TextView mNpfTextView = findViewById(R.id.resultTextView);
        final TextView m500TextView = findViewById(R.id.result500TextView);
        final TextView m600TextView = findViewById(R.id.result600TextView);

        mNpfTextView.setText(mNpfSpanned);
        m500TextView.setText(m500Spanned);
        m600TextView.setText(m600Spanned);
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
                                new ShortcutInfo.Builder(CalculateSpotStarsActivity.this, "exposure_spotstars").build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateSpotStarsActivity.this, SettingsActivity.class));
                break;
            case R.id.action_help:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
