package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class CalculateReverseFocusActivity extends AppCompatActivity {

    private final static String TAG = "Aperture Calculator";
    private final float[] coc = {0};

    @Override
    protected void onResume() {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        Camera lastCamera = (new Gson()).fromJson(prefs.getString("camera_" + prefs.getInt("cameras_last", 0), getString(R.string.camera_default)), Camera.class);
        if (prefs.getInt("cameras_amount", -1) == -1) {
            mCameraTextView.setText(R.string.calculate_camera_add);
        } else {
            mCameraTextView.setText(String.format(getString(R.string.calculate_camera), lastCamera.getName()));
        }
        coc[0] = lastCamera.getConfusion();
        registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
        super.onResume();
    }

    private Snackbar mTimerSnackBar;
    private TextView mSnackBarText;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(CalculateReverseFocusActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
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
                                new ShortcutInfo.Builder(CalculateReverseFocusActivity.this, "focus_reverse")
                                        .setShortLabel(getString(R.string.shortcut_focus_reverse))
                                        .setIcon(Icon.createWithResource(getBaseContext(), R.mipmap.shortcut_reversefocus))
                                        .setIntent(new Intent().setAction(Intent.ACTION_VIEW).setClass(getApplicationContext(), CalculateReverseFocusActivity.class))
                                        .build();

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
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_reversefocus);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

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

        mModuleManager.checkDarkmode(prefs);

        Camera lastCamera = gson.fromJson(prefs.getString("camera_" + prefs.getInt("cameras_last", 0), getString(R.string.camera_default)), Camera.class);
        coc[0] = lastCamera.getConfusion();

        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mNearEditText.setText(prefs.getString("nearfocus", "4"));
        mNearSeekbar.setProgress(mModuleManager.distance(prefs.getString("nearfocus", "4"), prefs.getBoolean("empirical", false)));
        mFarEditText.setText(prefs.getString("farfocus", "12"));
        mFarSeekbar.setProgress(mModuleManager.distance(
                String.valueOf(Float.valueOf(prefs.getString("farfocus", "12"))
                        - Float.valueOf(prefs.getString("nearfocus", "4"))),
                prefs.getBoolean("empirical", false)));
        calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));


        if (prefs.getBoolean("empirical", false)) {
            mNearIndicatorTextView.setText(R.string.feet);
            mFarIndicatorTextView.setText(R.string.feet);
        }


        /*
         *  Listeners
         */

        findViewById(R.id.distanceTextView).setOnClickListener(v -> mModuleManager.openArMeasure(this));

        mCameraLayout.setOnClickListener(v -> mModuleManager.selectCamera(CalculateReverseFocusActivity.this, mCameraTextView, coc, "coc"));

        mCameraTextView.addTextChangedListener(new TextWatcher() {
            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            //f:on
            @Override
            public void afterTextChanged(Editable s) {
                calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));
            }
        });

        mLengthSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!changing[0]) {
                    changing[0] = true;
                    mLengthEditText.setText(mModuleManager.focalLength(progress));
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
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mLengthSeekbar.setProgress(mModuleManager.focalLength(s.toString()));
                        changing[0] = false;
                    }
                    prefsEditor.putString("focallength", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(s.toString()), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(prefs.getString("farfocus", "12")));
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
                    mNearEditText.setText(mModuleManager.distance(progress, prefs.getBoolean("empirical", false)));
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
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mNearSeekbar.setProgress(mModuleManager.distance(s.toString(), prefs.getBoolean("empirical", false)));
                        changing[0] = false;
                    }
                    prefsEditor.putString("nearfocus", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(s.toString()), Float.valueOf(prefs.getString("farfocus", "12")));
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
                    double distance = Float.valueOf(mModuleManager.distance(progress, prefs.getBoolean("empirical", false)))
                            + Float.valueOf(prefs.getString("nearfocus", "4"));

                    DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
                    df.applyPattern(distance >= 8 ? "#" : "#.#");

                    mFarEditText.setText(String.valueOf(df.format(distance)));
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
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mFarSeekbar.setProgress(mModuleManager.distance(
                                String.valueOf(
                                        Float.valueOf(s.toString())
                                                - Float.valueOf(prefs.getString("nearfocus", "4"))),
                                prefs.getBoolean("empirical", false)));
                        changing[0] = false;
                    }
                    prefsEditor.putString("farfocus", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("nearfocus", "4")), Float.valueOf(s.toString()));

                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mEquationsLayout.setOnClickListener(v -> mModuleManager.showEquations(CalculateReverseFocusActivity.this, "reversefocus"));

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
    public void onStop() {
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            ModuleManager mModuleManager = new ModuleManager();
            long millisUntilFinished = intent.getLongExtra("remaining", 0);
            int maxTime = intent.getIntExtra("max", 0);
            String name = intent.getStringExtra("name");

            if (intent.getBooleanExtra("dismiss", false)) {
                mTimerSnackBar.dismiss();
            } else if (millisUntilFinished == 0) {
                mTimerSnackBar.dismiss();
                Snackbar.make(findViewById(R.id.rootView), String.format(getString(R.string.timer_finished), name), Snackbar.LENGTH_LONG)
                        .setAction(R.string.okay, v -> mTimerSnackBar.dismiss())
                        .show();
            } else {
                if (mTimerSnackBar == null || !mTimerSnackBar.isShown()) {
                    mTimerSnackBar = Snackbar.make(findViewById(R.id.rootView),
                            String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)),
                            Snackbar.LENGTH_INDEFINITE);
                    mTimerSnackBar
                            .setAction(R.string.show, v -> {
                                TimerSheet sheet = new TimerSheet();
                                sheet.startTime = maxTime / 1000;
                                sheet.tagName = name;
                                sheet.show(getSupportFragmentManager(), "timer");
                            })
                            .show();
                    mSnackBarText = mTimerSnackBar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                } else {
                    mSnackBarText.setText(String.format(getString(R.string.timer_text), name, mModuleManager.convertMilliseconds(millisUntilFinished)));
                }
            }
        }
    }
}
