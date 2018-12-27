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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

public class CalculateSpotStarsActivity extends AppCompatActivity {

    private final static String TAG = "Spot Stars Calculator";
    private final float[] mPixelpitch = {0};

    private boolean timerRunning;
    private float timerTime;
    private String timerName;
    private Snackbar mTimerSnackBar;
    private TextView mSnackBarText;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ModuleManager mModuleManager = new ModuleManager();
            mModuleManager.showHistory(CalculateSpotStarsActivity.this);
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
                                new ShortcutInfo.Builder(CalculateSpotStarsActivity.this, "exposure_spotstars")
                                        .setShortLabel(getString(R.string.shortcut_exposure_spotstars))
                                        .setIcon(Icon.createWithResource(getBaseContext(), R.mipmap.shortcut_spotstars))
                                        .setIntent(new Intent().setAction(Intent.ACTION_VIEW).setClass(getApplicationContext(), CalculateSpotStarsActivity.class))
                                        .build();

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

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
    }

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
        mPixelpitch[0] = lastCamera.getPixelpitch();
        calculate(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));
        registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
        super.onResume();
    }

    private void calculate(float mPixelpitch, float mLength, float mAperture) {
        if (mLength == 0) return;

        final ModuleManager mModuleManager = new ModuleManager();

        float mNpf = calculateNpf(mPixelpitch, mLength, mAperture);
        float m500 = calculate500(mLength);
        float m600 = calculate600(mLength);

        final TextView mNpfTextView = findViewById(R.id.resultTextView);
        final TextView m500TextView = findViewById(R.id.result500TextView);
        final TextView m600TextView = findViewById(R.id.result600TextView);

        mNpfTextView.setText(mModuleManager.convertTime(CalculateSpotStarsActivity.this, mNpf, true));
        m500TextView.setText(mModuleManager.convertTime(CalculateSpotStarsActivity.this, m500, true));
        m600TextView.setText(mModuleManager.convertTime(CalculateSpotStarsActivity.this, m600, true));
    }

    private float calculateNpf(float mPixelpitch, float mLength, float mAperture) {
        return (35 * mAperture + 30 * mPixelpitch) / mLength;
    }

    private float calculate500(float mLength) {
        return 500 / mLength;
    }

    private float calculate600(float mLength) {
        return 600 / mLength;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_spotstars);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEditor = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();

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
        final LinearLayout mEquationsLayout = findViewById(R.id.equationsLayout);

        mModuleManager.checkDarkmode(prefs);

        mLengthEditText.setText(prefs.getString("focallength", "24"));
        mLengthSeekbar.setProgress(Math.round(Float.valueOf(prefs.getString("focallength", "24"))));
        mApertureEditText.setText(prefs.getString("aperture", "3.5"));
        mApertureSeekbar.setProgress(mModuleManager.aperture(prefs.getString("aperture", "3.5"), prefs.getInt("aperture_stops", 6)));

        switch (prefs.getInt("aperture_stops", 6)) {
            case 2:
                mApertureSeekbar.setMax(10);
                break;
            case 4:
                mApertureSeekbar.setMax(20);
                break;
            //6 (Thirds) is the default. No change needed here.
        }

        View.OnClickListener timerClickListener = v -> {
            float calculated;

            if (v == findViewById(R.id.result500TextView)) {
                calculated = Math.round(calculate500(Float.valueOf(prefs.getString("focallength", "24"))) * 10) / 10f;
            } else if (v == findViewById(R.id.result600TextView)) {
                calculated = Math.round(calculate600(Float.valueOf(prefs.getString("focallength", "24"))) * 10) / 10f;
            } else {
                calculated = Math.round(calculateNpf(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5"))) * 10) / 10f;
            }

            if (!timerRunning || (Float.compare(calculated, timerTime) == 0)) {
                TimerSheet sheet = new TimerSheet();
                sheet.startTime = calculated;
                sheet.startService = true;
                sheet.tagName = getString(R.string.select_nd);
                sheet.show(getSupportFragmentManager(), "timer");
            } else {
                Toast.makeText(this, String.valueOf(timerTime), Toast.LENGTH_LONG).show();

                BottomSheetDialog mSheet = new BottomSheetDialog(CalculateSpotStarsActivity.this, R.style.AppBottomSheetDialogTheme);
                mSheet.setContentView(R.layout.sheet_timer_confirm);

                Button confirmButton = mSheet.findViewById(R.id.confirmButton);

                confirmButton.setText(String.format(getString(R.string.timer_override_confirm), timerName));
                confirmButton.setOnClickListener(v1 -> {
                    mSheet.dismiss();
                    TimerSheet sheet = new TimerSheet();
                    sheet.startTime = calculated;
                    sheet.startService = true;
                    sheet.tagName = getString(R.string.select_nd);
                    sheet.show(getSupportFragmentManager(), "timer");
                });

                mSheet.show();
            }
        };

        mStartTimerButton.setOnClickListener(timerClickListener);
        findViewById(R.id.resultTextView).setOnClickListener(timerClickListener);
        findViewById(R.id.result500TextView).setOnClickListener(timerClickListener);
        findViewById(R.id.result600TextView).setOnClickListener(timerClickListener);

        mCameraLayout.setOnClickListener(v -> mModuleManager.selectCamera(CalculateSpotStarsActivity.this, mCameraTextView, mPixelpitch, "pixelpitch"));

        mCameraTextView.addTextChangedListener(new TextWatcher() {
            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            //f:on
            @Override
            public void afterTextChanged(Editable s) {
                calculate(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(prefs.getString("aperture", "3.5")));
            }
        });

        mExpandButton.setOnClickListener(v -> {
            mResultExpandLayout.toggle();
            mExpandButton.setCompoundDrawables(null, null, null, null);
            if (mStartTimerButton.getVisibility() == View.VISIBLE) {
                mStartTimerButton.setVisibility(View.GONE);
                mExpandButton.setText(getString(R.string.stars_collapse));
            } else {
                mStartTimerButton.setVisibility(View.VISIBLE);
                mExpandButton.setText(getString(R.string.stars_expand));
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
                    calculate(mPixelpitch[0], Float.valueOf(s.toString()), Float.valueOf(prefs.getString("aperture", "3.5")));
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
                    prefsEditor.putString("aperture", s.toString()).apply();
                    calculate(mPixelpitch[0], Float.valueOf(prefs.getString("focallength", "24")), Float.valueOf(s.toString()));
                }
            }

            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
        });

        mEquationsLayout.setOnClickListener(v -> mModuleManager.showEquations(CalculateSpotStarsActivity.this, "spotstars"));
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
                timerRunning = false;
            } else if (millisUntilFinished == 0) {
                mTimerSnackBar.dismiss();
                timerRunning = false;
                Snackbar.make(findViewById(R.id.rootView), String.format(getString(R.string.timer_finished), name), Snackbar.LENGTH_LONG)
                        .setAction(R.string.okay, v -> mTimerSnackBar.dismiss())
                        .show();
            } else {
                timerRunning = true;
                timerTime = maxTime / 1000f;
                timerName = name;
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
