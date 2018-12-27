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
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

public class CalculateHyperFocalActivity extends AppCompatActivity {

    private final static String TAG = "Hyperfocal Calculator";
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

    /*
     *  Calculations
     */
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
            mModuleManager.showHistory(CalculateHyperFocalActivity.this);
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
                                new ShortcutInfo.Builder(CalculateHyperFocalActivity.this, "focus_hyper")
                                        .setShortLabel(getString(R.string.shortcut_focus_hyper))
                                        .setIcon(Icon.createWithResource(getBaseContext(), R.mipmap.shortcut_hyperfocal))
                                        .setIntent(new Intent().setAction(Intent.ACTION_VIEW).setClass(getApplicationContext(), CalculateHyperFocalActivity.class))
                                        .build();

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
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_hyperfocal);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        final boolean changing[] = {false};

        final View mResultLayout = findViewById(R.id.resultLayout);
        final LinearLayout mCameraLayout = findViewById(R.id.cameraLayout);
        final TextView mCameraTextView = findViewById(R.id.cameraTextView);
        final SeekBar mLengthSeekbar = findViewById(R.id.focallengthSeekbar);
        final EditText mLengthEditText = findViewById(R.id.focallengthEditText);
        final SeekBar mApertureSeekbar = findViewById(R.id.apertureSeekbar);
        final EditText mApertureEditText = findViewById(R.id.apertureEditText);
        final LinearLayout mEquationsLayout = findViewById(R.id.equationsLayout);

        mModuleManager.checkDarkmode(prefs);

        Camera lastCamera = gson.fromJson(prefs.getString("camera_" + prefs.getInt("cameras_last", 0), getString(R.string.camera_default)), Camera.class);
        coc[0] = lastCamera.getConfusion();

        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(mModuleManager.focalLength(prefs.getString("focallength", "24")));
        mApertureEditText.setText(prefs.getString("aperture", "3.5"));
        mApertureSeekbar.setProgress(mModuleManager.aperture(prefs.getString("aperture", "3.5"), prefs.getInt("aperture_stops", 6)));
        calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));

        switch (prefs.getInt("aperture_stops", 6)) {
            case 2:
                mApertureSeekbar.setMax(10);
                break;
            case 4:
                mApertureSeekbar.setMax(20);
                break;
            //6 (Thirds) is the default. No change needed here.
        }


        /*
         *  Listeners
         */

        findViewById(R.id.nearlimitTextView).setOnClickListener(v -> mModuleManager.openArMeasure(this));
        findViewById(R.id.farlimitTextView).setOnClickListener(v -> mModuleManager.openArMeasure(this));
        findViewById(R.id.hyperfocalTextView).setOnClickListener(v -> mModuleManager.openArMeasure(this));

        mCameraLayout.setOnClickListener(v -> mModuleManager.selectCamera(CalculateHyperFocalActivity.this, mCameraTextView, coc, "coc"));

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
                    prefsEdit.putString("focallength", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(s.toString()), Float.valueOf(prefs.getString("aperture", "3.5")));
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
                    mApertureEditText.setText(mModuleManager.aperture(progress, prefs.getInt("aperture_stops", 6)));
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
                if (!s.toString().isEmpty() && !s.toString().equals(".")) {
                    if (!changing[0]) {
                        changing[0] = true;
                        mApertureSeekbar.setProgress(mModuleManager.aperture(s.toString(), prefs.getInt("aperture_stops", 6)));
                        changing[0] = false;
                    }
                    prefsEdit.putString("aperture", s.toString()).apply();
                    calculate(coc[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(s.toString()));
                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mEquationsLayout.setOnClickListener(v -> mModuleManager.showEquations(CalculateHyperFocalActivity.this, "hyperfocal"));
    }

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
