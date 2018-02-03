package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

public class CustomColorsActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    String colorEditing = "";

    final String LIGHT_LIGHTER = "custom_light_lighter";
    final String LIGHT_NORMAL = "custom_light_normal";
    final String LIGHT_DARKER = "custom_light_darker";
    final String DARK_LIGHTER = "custom_dark_lighter";
    final String DARK_NORMAL = "custom_dark_normal";
    final String DARK_DARKER = "custom_dark_darker";
    final String TAB_SUN = "custom_tab_sun";
    final String TAB_STARS = "custom_tab_stars";
    final String TAB_FOCUS = "custom_tab_focus";
    final String TAB_ND = "custom_tab_nd";
    final String TAB_SETTINGS = "custom_tab_settings";
    final String TAB_TIMELAPSE = "custom_tab_timelapse";
    final String TEXT_MAIN_LIGHT = "custom_text_main_light";
    final String TEXT_SUB_LIGHT = "custom_text_sub_light";
    final String TEXT_MAIN_DARK = "custom_text_main_dark";
    final String TEXT_SUB_DARK = "custom_text_sub_dark";

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor prefsEdit = prefs.edit();

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        prefsEdit.putString(colorEditing, hexColor).apply();
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_colors);

        FloatingActionButton resetFAB = findViewById(R.id.customColorsResetFAB);
        Button lightLighterButton = findViewById(R.id.custom_light_lighter_Button);
        Button lightNormalButton = findViewById(R.id.custom_light_normal_Button);
        Button lightDarkerButton = findViewById(R.id.custom_light_darker_Button);
        Button darkLighterButton = findViewById(R.id.custom_dark_lighter_Button);
        Button darkNormalButton = findViewById(R.id.custom_dark_normal_Button);
        Button darkDarkerButton = findViewById(R.id.custom_dark_darker_Button);
        Button tabSunButton = findViewById(R.id.custom_tab_sun_Button);
        Button tabStarsButton = findViewById(R.id.custom_tab_stars_Button);
        Button tabFocusButton = findViewById(R.id.custom_tab_focus_Button);
        Button tabNdButton = findViewById(R.id.custom_tab_nd_Button);
        Button tabSettingsButton = findViewById(R.id.custom_tab_settings_Button);
        Button tabTimelapseButton = findViewById(R.id.custom_tab_timelapse_Button);
        Button textMainLightButton = findViewById(R.id.custom_text_main_light_Button);
        Button textSubLightButton = findViewById(R.id.custom_text_sub_light_Button);
        Button textMainDarkButton = findViewById(R.id.custom_text_main_dark_Button);
        Button textSubDarkButton = findViewById(R.id.custom_text_sub_dark_Button);


        resetFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(CustomColorsActivity.this);
                dialog.setTitle(getString(R.string.warning_reset_colors_title))
                        .setMessage(getString(R.string.warning_reset_colors_message))
                        .setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor prefsEdit = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
                                prefsEdit.remove(LIGHT_LIGHTER)
                                        .remove(LIGHT_NORMAL)
                                        .remove(LIGHT_DARKER)
                                        .remove(DARK_LIGHTER)
                                        .remove(DARK_NORMAL)
                                        .remove(DARK_DARKER)
                                        .remove(TAB_SUN)
                                        .remove(TAB_STARS)
                                        .remove(TAB_FOCUS)
                                        .remove(TAB_ND)
                                        .remove(TAB_SETTINGS)
                                        .remove(TAB_TIMELAPSE)
                                        .remove(TEXT_MAIN_LIGHT)
                                        .remove(TEXT_SUB_LIGHT)
                                        .remove(TEXT_MAIN_DARK)
                                        .remove(TEXT_SUB_DARK)
                                        .apply();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mDialogInterface, int mI) {
                            }
                        })
                        .show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(CustomColorsActivity.this, R.color.colorWarning));
            }
        });
        lightLighterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = LIGHT_LIGHTER;
                openDialog();
            }
        });
        lightNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = LIGHT_NORMAL;
                openDialog();
            }
        });
        lightDarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = LIGHT_DARKER;
                openDialog();
            }
        });
        darkLighterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = DARK_LIGHTER;
                openDialog();
            }
        });
        darkNormalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = DARK_NORMAL;
                openDialog();
            }
        });
        darkDarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = DARK_DARKER;
                openDialog();
            }
        });
        tabSunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_SUN;
                openDialog();
            }
        });
        tabStarsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_STARS;
                openDialog();
            }
        });
        tabFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_FOCUS;
                openDialog();
            }
        });
        tabNdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_ND;
                openDialog();
            }
        });
        tabSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_SETTINGS;
                openDialog();
            }
        });
        tabTimelapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TAB_TIMELAPSE;
                openDialog();
            }
        });
        textMainLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TEXT_MAIN_LIGHT;
                openDialog();
            }
        });
        textSubLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TEXT_SUB_LIGHT;
                openDialog();
            }
        });
        textMainDarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TEXT_MAIN_DARK;
                openDialog();
            }
        });
        textSubDarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                colorEditing = TEXT_SUB_DARK;
                openDialog();
            }
        });

    }

    private void openDialog() {
        // Pass a context, along with the title of the dialog
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .titleSub(R.string.colors)  // title of dialog when viewing shades of a color
                .accentMode(false)  // when true, will display accent palette instead of primary palette
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                //.preselect(SelectMe)  // optionally preselects a color
                .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .show(this); // an AppCompatActivity which implements ColorCallback
    }


}
