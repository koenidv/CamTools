package com.koenidv.camtools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

import org.michaelbel.bottomsheet.BottomSheet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

//  Created by koenidv on 12.12.2018.
public class CalculateContrastActivity extends AppCompatActivity {

    private float DARKEN_TEXT = 0.5f;

    int color;
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
}
