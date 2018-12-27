package com.koenidv.camtools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.google.android.material.snackbar.Snackbar;

import org.michaelbel.bottomsheet.BottomSheet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

//  Created by koenidv on 12.12.2018.
public class CalculateContrastActivity extends AppCompatActivity {

    private final static String TAG = "Contrast Calculator";
    private float DARKEN_TEXT = 0.5f;

    private int color;
    int contrast;
    int contrastMode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_contrast);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();

        ConstraintLayout resultLayout = findViewById(R.id.resultLayout);
        TextView resultTextView = findViewById(R.id.resultTextView);
        LinearLayout colorLayout = findViewById(R.id.colorLayout);
        LinearLayout modeLayout = findViewById(R.id.modeLayout);
        TextView modeTextView = findViewById(R.id.modeTextView);

        (new ModuleManager()).checkDarkmode(prefs);


        color = prefs.getInt("last_color", Color.parseColor("#20ffaa"));
        contrastMode = prefs.getInt("contrast_mode", 0);

        String mode;
        switch (contrastMode) {
            case 0:
                mode = getString(R.string.calculate_color_mode_complementary);
                contrast = calculateComplementary(color);
                break;
            case 1:
                mode = getString(R.string.calculate_color_mode_triadic);
                contrast = color;
                break;
            case 2:
                mode = getString(R.string.calculate_color_mode_warmth);
                contrast = color;
                break;
            case 3:
                mode = getString(R.string.calculate_color_mode_brightness);
                contrast = color;
                break;
            default:
                mode = getString(R.string.calculate_color_mode_complementary);
                contrast = calculateComplementary(color);
                break;
        }
        modeTextView.setText(String.format(getString(R.string.calculate_color_mode), mode));


        GradientDrawable gradient = new GradientDrawable();
        gradient.setColors(new int[]{color, contrast});
        gradient.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        resultLayout.setBackground(gradient);
        if (getSupportActionBar() != null) getSupportActionBar().setBackgroundDrawable(new ColorDrawable(contrast));
        resultTextView.setText(String.format("#%06X", (0xFFFFFF & contrast)));
        resultTextView.setTextColor(manipulateColor(color, DARKEN_TEXT));


        colorLayout.setOnClickListener(v -> {
            ColorPickerDialog colorDialog = ColorPickerDialog.createColorPickerDialog(CalculateContrastActivity.this);
            if (prefs.getBoolean("system_darkmode", false)) {
                colorDialog = ColorPickerDialog.createColorPickerDialog(CalculateContrastActivity.this, ColorPickerDialog.DARK_THEME);
            }
            colorDialog.setLastColor(prefs.getInt("last_color", Color.parseColor("#20ffaa")));
            colorDialog.hideOpacityBar();
            colorDialog.setOnColorPickedListener((color, hexVal) -> {
                prefsEdit.putInt("last_color", color).apply();
                this.color = color;

                contrast = calculate(color, contrastMode);
                gradient.setColors(new int[]{Color.parseColor(hexVal), contrast});
                gradient.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                resultLayout.setBackground(gradient);
                if (getSupportActionBar() != null) getSupportActionBar().setBackgroundDrawable(new ColorDrawable(contrast));
                resultTextView.setText(String.format("#%06X", (0xFFFFFF & contrast)));
                resultTextView.setTextColor(manipulateColor(color, DARKEN_TEXT));
            });
            colorDialog.show();
        });

        modeLayout.setOnClickListener(v -> {
            String[] entries = {
                    getString(R.string.calculate_color_mode_complementary),
                    getString(R.string.calculate_color_mode_triadic),
                    getString(R.string.calculate_color_mode_warmth),
                    getString(R.string.calculate_color_mode_brightness)
            };

            BottomSheet.Builder sheetBuilder = new BottomSheet.Builder(CalculateContrastActivity.this);
            sheetBuilder.setTitle(R.string.calculate_color_mode_choose);
            sheetBuilder.setItems(entries, (dialog, which) -> {
                prefsEdit.putInt("contrast_mode", which);
                contrastMode = which;
                String modeName;
                switch (which) {
                    case 0:
                        modeName = getString(R.string.calculate_color_mode_complementary);
                        break;
                    case 1:
                        modeName = getString(R.string.calculate_color_mode_triadic);
                        Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        modeName = getString(R.string.calculate_color_mode_warmth);
                        Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        modeName = getString(R.string.calculate_color_mode_brightness);
                        Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        modeName = getString(R.string.calculate_color_mode_complementary);
                        break;
                }
                modeTextView.setText(String.format(getString(R.string.calculate_color_mode), modeName));

                contrast = calculate(color, contrastMode);
                gradient.setColors(new int[]{color, contrast});
                gradient.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
                resultLayout.setBackground(gradient);
                if (getSupportActionBar() != null) getSupportActionBar().setBackgroundDrawable(new ColorDrawable(contrast));
                resultTextView.setText(String.format("#%06X", (0xFFFFFF & contrast)));
                resultTextView.setTextColor(manipulateColor(color, DARKEN_TEXT));
            });
            sheetBuilder.show();
        });

    }

    private int calculate(int color, int mode) {
        switch (mode) {
            case 0: //Complementary color
                return calculateComplementary(color);
            case 1: //Triadic colors
                return color;
            case 2: //Warm/Cold color
                return color;
            case 3: //Bright/Dark color
                return color;
            default:
                return calculateComplementary(color);
        }
    }

    private int calculateComplementary(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        return Color.rgb(255 - red, 255 - green, 255 - blue);
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
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
                                new ShortcutInfo.Builder(CalculateContrastActivity.this, "tools_contrast")
                                        .setShortLabel(getString(R.string.shortcut_tools_contrast))
                                        .setIcon(Icon.createWithResource(getBaseContext(), R.mipmap.shortcut_contrast))
                                        .setIntent(new Intent().setAction(Intent.ACTION_VIEW).setClass(getApplicationContext(), CalculateContrastActivity.class))
                                        .build();

                        mShortcutManager.requestPinShortcut(pinShortcutInfo, null);
                    }
                }
                break;
            case R.id.action_settings:
                startActivity(new Intent(CalculateContrastActivity.this, SettingsActivity.class));
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
            mModuleManager.showHistory(CalculateContrastActivity.this);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(TimerService.ACTION));
        Log.i(TAG, "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.i(TAG, "Unregistered broacast receiver");
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

    Snackbar mTimerSnackBar;
    TextView mSnackBarText;

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
                Snackbar.make(findViewById(R.id.resultLayout), String.format(getString(R.string.timer_finished), name), Snackbar.LENGTH_LONG)
                        .setAction(R.string.okay, v -> mTimerSnackBar.dismiss())
                        .show();
            } else {
                if (mTimerSnackBar == null || !mTimerSnackBar.isShown()) {
                    mTimerSnackBar = Snackbar.make(findViewById(R.id.resultLayout),
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
