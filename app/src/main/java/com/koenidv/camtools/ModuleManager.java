package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

//  Created by koenidv on 30.11.2018.
class ModuleManager {

    /*
     *  UI
     */

    void checkDarkmode(SharedPreferences mPrefs) {
        if (!mPrefs.getBoolean("darkmode_follow_system", false)) {
            if (mPrefs.getBoolean("darkmode", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    /**
     * Display a bottom sheet to let the user select a Camera
     *
     * @param mContext        The context to run in.
     * @param mCameraTextView The TextView which displays the currently selected Camera.
     * @param mValue          The list in which either the current cicle of confusion or pixel pitch is saved.
     * @param mType           Defines what should be saved to {@param mValue}.
     *                        Should be "coc" for circle of confusion, "pixelpitch" for pixel pitch or "cropfactor" for cropfactor.
     */
    void selectCamera(final Context mContext, @Nullable final TextView mCameraTextView, @Nullable final float[] mValue, @Nullable final String mType) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        Gson gson = new Gson();

        if (prefs.getInt("cameras_amount", -1) == -1) {
            mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
        } else {
            //Build a bottomsheetdialog
            BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);

            //Get 16dp as pixel size
            float scale = mContext.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);

            //Ripple effect ressource
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);

            //Create a LinearLayout
            LinearLayout linearLayout = new LinearLayout(dialog.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Create a TextView with a height of 48dp and add it to the LinearLayout
            TextView titleTextView = new TextView(dialog.getContext());
            titleTextView.setText(mContext.getString(R.string.calculate_camera_choose));
            titleTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Medium);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_secondary));
            titleTextView.setHeight((int) (48 * scale + 0.5f));
            titleTextView.setGravity(Gravity.CENTER_VERTICAL);
            titleTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            linearLayout.addView(titleTextView);

            //Add a TextView with an icon and an onClickListener for every camera
            for (int i = 0; i <= prefs.getInt("cameras_amount", 0); i++) {
                Camera thisCamera = gson.fromJson(prefs.getString("camera_" + i, mContext.getString(R.string.camera_default)), Camera.class);

                TextView optionTextView = new TextView(dialog.getContext());
                optionTextView.setText(thisCamera.getName());
                optionTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Menu);
                optionTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_normal));

                optionTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(thisCamera.getIcon(), 0, 0, 0);
                optionTextView.setCompoundDrawablePadding(dpAsPixels);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    optionTextView.setCompoundDrawableTintList(mContext.getResources().getColorStateList(R.color.textColor_secondary, mContext.getTheme()));
                }

                optionTextView.setHeight((int) (48 * scale + 0.5f));
                optionTextView.setGravity(Gravity.CENTER_VERTICAL);
                optionTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
                optionTextView.setBackgroundResource(outValue.resourceId);

                int which = i;
                optionTextView.setOnClickListener(v -> {
                    prefsEdit.putInt("cameras_last", which).apply();

                    if (mType != null && mValue != null) {
                        Camera mCamera = gson.fromJson(prefs.getString("camera_" + which, mContext.getString(R.string.camera_default)), Camera.class);
                        switch (mType) {
                            case "coc":
                                mValue[0] = mCamera.getConfusion();
                                break;
                            case "pixelpitch":
                                mValue[0] = mCamera.getPixelpitch();
                                break;
                            case "cropfactor":
                                mValue[0] = mCamera.getCropfactor();
                                break;
                        }
                    }
                    if (mCameraTextView != null) {
                        mCameraTextView.setText(mContext.getString(R.string.calculate_camera)
                                .replace("%s", gson.fromJson(prefs.getString("camera_" + which, mContext.getString(R.string.camera_default)), Camera.class).getName()));
                    }
                    dialog.dismiss();
                });

                linearLayout.addView(optionTextView);
            }

            //Add an option to manage the cameras
            TextView manageTextView = new TextView(dialog.getContext());
            manageTextView.setText(mContext.getString(R.string.calculate_camera_manage));
            manageTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Menu);
            manageTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_normal));
            manageTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_settings, 0, 0, 0);
            manageTextView.setCompoundDrawablePadding(dpAsPixels);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manageTextView.setCompoundDrawableTintList(mContext.getResources().getColorStateList(R.color.textColor_secondary, mContext.getTheme()));
            }
            manageTextView.setHeight((int) (48 * scale + 0.5f));
            manageTextView.setGravity(Gravity.CENTER_VERTICAL);
            manageTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            manageTextView.setBackgroundResource(outValue.resourceId);
            manageTextView.setOnClickListener(v -> {
                dialog.dismiss();
                mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
            });
            linearLayout.addView(manageTextView);

            //Set the LinearLayout as content view and display the BottomSheet
            dialog.setContentView(linearLayout);
            dialog.show();
        }
    }

    /**
     * Show a bottom sheet which lets the user manipulate a Camera
     *
     * @param mContext    The context to run in.
     * @param mIndex      The position in {@param mCameraList}.
     *                    Should be one larger than the size of {@param mCameraList} to create a new Camera
     *                    or withing its size to edit an existing Camera.
     * @param mCameraList A list in which all {@link Camera} are stored to display them in the RecyclerView.
     * @param mAdapter    The RecyclerView's adapter.
     * @see #editCameraCheckAllFilled(Context, Dialog)
     * @see EditCamerasActivity
     */
    void editCamera(final Context mContext, final int mIndex, @Nullable final List<Camera> mCameraList, @Nullable final RecyclerView.Adapter mAdapter) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        Gson gson = new Gson();

        final BottomSheetDialog mDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);
        mDialog.setContentView(R.layout.sheet_camera_add_custom);
        Objects.requireNonNull(mDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        FrameLayout bottomSheet = mDialog.findViewById(R.id.design_bottom_sheet);

        assert bottomSheet != null;
        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

        TextView mTitleTextView = mDialog.findViewById(R.id.titleTextView);
        ImageButton mIconButton = mDialog.findViewById(R.id.iconImageButton);
        final EditText mNameEditText = mDialog.findViewById(R.id.nameEditText);
        TextView mResolutionPresetTextView = mDialog.findViewById(R.id.resolutionPresetTextView);
        final EditText mResolutionXEditText = mDialog.findViewById(R.id.resolutionXEditText);
        final EditText mResolutionYEditText = mDialog.findViewById(R.id.resolutionYEditText);
        TextView mSizePresetTextView = mDialog.findViewById(R.id.sensorsizePresetTextView);
        final EditText mSizeXEditText = mDialog.findViewById(R.id.sensorsizeXEditText);
        final EditText mSizeYEditText = mDialog.findViewById(R.id.sensorsizeYEditText);
        final EditText mConfusionEditText = mDialog.findViewById(R.id.cocEditText);
        final Button mCancelButton = mDialog.findViewById(R.id.cancelButton);
        final Button mSaveButton = mDialog.findViewById(R.id.saveButton);

        assert mTitleTextView != null;
        assert mIconButton != null;
        assert mNameEditText != null;
        assert mResolutionXEditText != null;
        assert mResolutionYEditText != null;
        assert mSizeXEditText != null;
        assert mSizeYEditText != null;
        assert mConfusionEditText != null;
        assert mSaveButton != null;

        final String index = "camera_" + String.valueOf(mIndex);
        Camera thisCamera = gson.fromJson(prefs.getString(index, null), Camera.class);

        if (thisCamera != null) {
            mTitleTextView.setText(R.string.setting_cameras_edit_custom_title);
            mIconButton.setImageDrawable(mContext.getDrawable(thisCamera.getIcon()));
            mNameEditText.setText(thisCamera.getName());
            mResolutionXEditText.setText(String.valueOf(thisCamera.getResolutionX()));
            mResolutionYEditText.setText(String.valueOf(thisCamera.getResolutionY()));
            mSizeXEditText.setText(String.valueOf(thisCamera.getSensorSizeX()));
            mSizeYEditText.setText(String.valueOf(thisCamera.getSensorSizeY()));
            mConfusionEditText.setText(String.valueOf(thisCamera.getConfusion()));
        } else {
            thisCamera = new Camera();
            ((TextView) Objects.requireNonNull(mDialog.findViewById(R.id.cocLabelTextView))).setVisibility(View.GONE);
            Objects.requireNonNull(mConfusionEditText).setVisibility(View.GONE);
            mSaveButton.setEnabled(false);
            mSaveButton.getBackground().setAlpha(0);
            mSaveButton.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        final Camera thisCameraFinal = thisCamera;


        TextWatcher checkOnTextChanged = new TextWatcher() {
            //f:off
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            //f:on
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editCameraCheckAllFilled(mContext, mDialog);
            }
        };

        mNameEditText.addTextChangedListener(checkOnTextChanged);
        mSizeXEditText.addTextChangedListener(checkOnTextChanged);
        mSizeYEditText.addTextChangedListener(checkOnTextChanged);
        mResolutionXEditText.addTextChangedListener(checkOnTextChanged);
        mResolutionYEditText.addTextChangedListener(checkOnTextChanged);
        mConfusionEditText.addTextChangedListener(checkOnTextChanged);

        //Show a bottomsheetdialog to select an icon
        mIconButton.setOnClickListener(v -> {
            BottomSheetDialog iconDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);
            iconDialog.setContentView(R.layout.sheet_camera_choose_icon);

            ((View) Objects.requireNonNull(iconDialog.findViewById(R.id.photoTextView))).setOnClickListener(optionview -> {
                iconDialog.dismiss();
                thisCameraFinal.setIcon(R.drawable.camera_photo);
                mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_photo));
            });
            ((View) Objects.requireNonNull(iconDialog.findViewById(R.id.shutterTextView))).setOnClickListener(optionview -> {
                iconDialog.dismiss();
                thisCameraFinal.setIcon(R.drawable.camera_shutter);
                mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_shutter));
            });
            ((View) Objects.requireNonNull(iconDialog.findViewById(R.id.videoTextView))).setOnClickListener(optionview -> {
                iconDialog.dismiss();
                thisCameraFinal.setIcon(R.drawable.camera_video);
                mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_video));
            });
            ((View) Objects.requireNonNull(iconDialog.findViewById(R.id.phoneTextView))).setOnClickListener(optionview -> {
                iconDialog.dismiss();
                thisCameraFinal.setIcon(R.drawable.camera_phone);
                mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_phone));
            });

            iconDialog.show();
        });

        assert mResolutionPresetTextView != null;
        mResolutionPresetTextView.setOnClickListener(v -> {
            //Build a bottomsheetdialog
            BottomSheetDialog presetDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);

            //Get 16dp as pixel size
            float scale = mContext.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);

            //Ripple effect ressource
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);

            //Create a LinearLayout
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Create a TextView with a height of 48dp and add it to the LinearLayout
            TextView titleTextView = new TextView(mContext);
            titleTextView.setText(mContext.getString(R.string.preset_choose));
            titleTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Medium);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_secondary));
            titleTextView.setHeight((int) (48 * scale + 0.5f));
            titleTextView.setGravity(Gravity.CENTER_VERTICAL);
            titleTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            linearLayout.addView(titleTextView);

            for (int preset = 0; preset <= mContext.getResources().getInteger(R.integer.preset_res_amount); preset++) {
                int resId = mContext.getResources().getIdentifier("preset_res_" + String.valueOf(preset), "string", mContext.getPackageName());

                TextView presetTextView = new TextView(mContext);
                presetTextView.setText(mContext.getString(resId));
                presetTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Menu);
                presetTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_normal));
                presetTextView.setHeight((int) (48 * scale + 0.5f));
                presetTextView.setGravity(Gravity.CENTER_VERTICAL);
                presetTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);

                final String which = String.valueOf(preset);
                presetTextView.setOnClickListener(optionview -> {
                    int resXid = mContext.getResources().getIdentifier("preset_res_" + which + "_x", "string", mContext.getPackageName());
                    int resYid = mContext.getResources().getIdentifier("preset_res_" + which + "_y", "string", mContext.getPackageName());

                    mResolutionXEditText.setText(mContext.getString(resXid));
                    mResolutionYEditText.setText(mContext.getString(resYid));
                });
                linearLayout.addView(presetTextView);
            }

            presetDialog.setContentView(linearLayout);
            presetDialog.show();
        });

        assert mSizePresetTextView != null;
        mSizePresetTextView.setOnClickListener(v -> {
            //Build a bottomsheetdialog
            BottomSheetDialog presetDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);

            //Get 16dp as pixel size
            float scale = mContext.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);

            //Ripple effect ressource
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);

            //Create a LinearLayout
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Create a TextView with a height of 48dp and add it to the LinearLayout
            TextView titleTextView = new TextView(mContext);
            titleTextView.setText(mContext.getString(R.string.preset_choose));
            titleTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Medium);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_secondary));
            titleTextView.setHeight((int) (48 * scale + 0.5f));
            titleTextView.setGravity(Gravity.CENTER_VERTICAL);
            titleTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            linearLayout.addView(titleTextView);

            for (int preset = 0; preset <= mContext.getResources().getInteger(R.integer.preset_size_amount); preset++) {
                int resId = mContext.getResources().getIdentifier("preset_size_" + String.valueOf(preset), "string", mContext.getPackageName());

                TextView presetTextView = new TextView(mContext);
                presetTextView.setText(mContext.getString(resId));
                presetTextView.setTextAppearance(mContext, android.R.style.TextAppearance_Material_Menu);
                presetTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_normal));
                presetTextView.setHeight((int) (48 * scale + 0.5f));
                presetTextView.setGravity(Gravity.CENTER_VERTICAL);
                presetTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);

                final String which = String.valueOf(preset);
                presetTextView.setOnClickListener(optionview -> {
                    int sizeXid = mContext.getResources().getIdentifier("preset_size_" + which + "_x", "string", mContext.getPackageName());
                    int sizeYid = mContext.getResources().getIdentifier("preset_size_" + which + "_y", "string", mContext.getPackageName());
                    int cocid = mContext.getResources().getIdentifier("preset_size_" + which + "_coc", "string", mContext.getPackageName());

                    mSizeXEditText.setText(mContext.getString(sizeXid));
                    mSizeYEditText.setText(mContext.getString(sizeYid));
                    mConfusionEditText.setText(mContext.getString(cocid));
                });
                linearLayout.addView(presetTextView);
            }

            presetDialog.setContentView(linearLayout);
            presetDialog.show();
        });

        assert mCancelButton != null;
        mCancelButton.setOnClickListener(v -> mDialog.dismiss());

        mSaveButton.setOnClickListener(v -> {
            Camera mCamera;

            if (!mConfusionEditText.getText().toString().equals("") && !mConfusionEditText.getText().toString().equals(".")) {
                mCamera = new Camera(
                        mNameEditText.getText().toString(),
                        thisCameraFinal.getIcon(),
                        Integer.valueOf(mResolutionXEditText.getText().toString()),
                        Integer.valueOf(mResolutionYEditText.getText().toString()),
                        Float.valueOf(mSizeXEditText.getText().toString()),
                        Float.valueOf(mSizeYEditText.getText().toString()),
                        Float.valueOf(mConfusionEditText.getText().toString())
                );
            } else {
                mCamera = new Camera(
                        mNameEditText.getText().toString(),
                        thisCameraFinal.getIcon(),
                        Integer.valueOf(mResolutionXEditText.getText().toString()),
                        Integer.valueOf(mResolutionYEditText.getText().toString()),
                        Float.valueOf(mSizeXEditText.getText().toString()),
                        Float.valueOf(mSizeYEditText.getText().toString())
                );
            }

            prefsEdit.putString(index, gson.toJson(mCamera))
                    .apply();

            if (mCameraList != null && mAdapter != null) {

                if (mIndex > prefs.getInt("cameras_amount", -1)) {
                    mCameraList.add(mCamera);
                    mAdapter.notifyItemInserted(mIndex);
                } else {
                    mCameraList.set(mIndex, mCamera);
                    mAdapter.notifyItemChanged(mIndex);
                }
            }

            if (mIndex > prefs.getInt("cameras_amount", -1)) {
                prefsEdit.putInt("cameras_amount", mIndex).apply();
            }

            mDialog.dismiss();
        });

        mDialog.show();
    }

    /**
     * Checks whether all EditTexts in {@param mDialog} are filled
     * and enables / disables its save button
     *
     * @param mContext The context to run in.
     * @param mDialog  The dialog in which to check the views.
     */
    private void editCameraCheckAllFilled(Context mContext, Dialog mDialog) {
        EditText mNameEditText = mDialog.findViewById(R.id.nameEditText);
        EditText mSizeXEditText = mDialog.findViewById(R.id.sensorsizeXEditText);
        EditText mSizeYEditText = mDialog.findViewById(R.id.sensorsizeYEditText);
        EditText mResolutionXEditText = mDialog.findViewById(R.id.resolutionXEditText);
        EditText mResolutionYEditText = mDialog.findViewById(R.id.resolutionYEditText);
        Button mSaveButton = mDialog.findViewById(R.id.saveButton);

        if (!mNameEditText.getText().toString().isEmpty()
                && !mSizeXEditText.getText().toString().isEmpty()
                && !mSizeYEditText.getText().toString().isEmpty()
                && !mResolutionXEditText.getText().toString().isEmpty()
                && !mResolutionXEditText.getText().toString().equals(".")
                && !mResolutionYEditText.getText().toString().isEmpty()
                && !mResolutionYEditText.getText().toString().equals(".")) {
            mSaveButton.setEnabled(true);
            mSaveButton.getBackground().setAlpha(255);
            mSaveButton.setTextColor(Color.WHITE);
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.getBackground().setAlpha(0);
            mSaveButton.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
    }

    /**
     * Moves a camera to a specific position
     *
     * @param mContext   The context to run in.
     * @param mFromIndex The position of the Camera to move.
     * @param mToIndex   The position to move the Camera to.
     * @see EditCamerasActivity
     */
    void moveCamera(final Context mContext, final int mFromIndex, final int mToIndex) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        String moving = prefs.getString("camera_" + mFromIndex, mContext.getString(R.string.camera_default));

        if (mFromIndex < mToIndex) {
            //Move every camera between from and to one down
            for (int movethis = mToIndex; movethis > mFromIndex; movethis--) {
                prefsEdit.putString("camera_" + String.valueOf(movethis - 1), prefs.getString("camera_" + movethis, mContext.getString(R.string.camera_default)));
                if (prefs.getInt("cameras_last", -1) == movethis) {
                    prefsEdit.putInt("cameras_last", movethis - 1);
                }
            }
        } else if (mFromIndex > mToIndex) {
            //As above, but one up
            for (int movethis = mToIndex; movethis < mFromIndex; movethis++) {
                prefsEdit.putString("camera_" + String.valueOf(movethis + 1), prefs.getString("camera_" + movethis, mContext.getString(R.string.camera_default)));
                if (prefs.getInt("cameras_last", -1) == movethis) {
                    prefsEdit.putInt("cameras_last", movethis + 1);
                }
            }
        }

        //Set the last used camera if it was the moved
        if (prefs.getInt("cameras_last", -1) == mFromIndex) {
            prefsEdit.putInt("cameras_last", mToIndex - 1);
        }

        //Put the moved Camera in the right place
        prefsEdit.putString("camera_" + mToIndex, moving).apply();

    }

    /**
     * Delete a Camera
     * Show a bottom sheet which lets the user manipulate a Camera
     *
     * @param mContext    The context to run in.
     * @param mIndex      The position in {@param mCameraList}.
     * @param mCameraList A list in which all {@link Camera} are stored to display them in the RecyclerView.
     * @param mAdapter    The RecyclerView's adapter.
     * @see EditCamerasActivity
     */
    void deleteCamera(final Context mContext, final int mIndex, final List<Camera> mCameraList, final RecyclerView.Adapter mAdapter) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        // Move every Camera with a higher index one down
        for (int move = mIndex + 1; move <= prefs.getInt("cameras_amount", 0); move++) {
            String indexLast = "camera_" + String.valueOf(move - 1);
            String indexThis = "camera_" + move;

            prefsEdit.putString(indexLast, prefs.getString(indexThis, mContext.getString(R.string.camera_default))).apply();
        }

        // Use the standard Camera if the deleted Camera was used last
        if (prefs.getInt("cameras_last", 0) == mIndex) {
            prefsEdit.putInt("cameras_last", 0);
        }

        // Remove the last entry (which is now the same as the second-to-last entry
        String index = "camera_" + prefs.getInt("cameras_amount", 0);
        prefsEdit.remove(index);

        // The amount of cameras is now one smaller
        prefsEdit.putInt("cameras_amount", prefs.getInt("cameras_amount", 0) - 1).commit();

        if (mCameraList != null && mAdapter != null) {
            mCameraList.remove(mIndex);
            mAdapter.notifyItemRemoved(mIndex);
        }
    }

    /**
     * Show a bottom sheet with equations
     *
     * @param mContext The context to run in.
     * @param mName    The name of the equation as saved in strings.xml
     */
    void showEquations(Context mContext, String mName) {
        final BottomSheetDialog mDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);
        mDialog.setContentView(R.layout.sheet_equation);

        int mathId = mContext.getResources().getIdentifier("equation_" + mName, "string", mContext.getPackageName());
        int descrId = mContext.getResources().getIdentifier("equation_" + mName + "_description", "string", mContext.getPackageName());

        io.github.kexanie.library.MathView mMathView = mDialog.findViewById(R.id.math_view);
        TextView mDescriptionTextView = mDialog.findViewById(R.id.descriptionTextView);
        assert mMathView != null;
        assert mDescriptionTextView != null;

        mMathView.setText(mContext.getString(mathId));
        mDescriptionTextView.setText(descrId);

        mDialog.show();
    }

    void showHistory(Context mContext) {
        if (Build.VERSION.SDK_INT >= 24) {
            @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
            Gson gson = new Gson();

            //Build a bottomsheetdialog
            BottomSheetDialog historyDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);

            //Get 16dp as pixel size
            float scale = mContext.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);

            //Ripple effect ressource
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);

            //Create a LinearLayout
            LinearLayout linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            //Create a TextView with a height of 48dp and add it to the LinearLayout
            TextView titleTextView = new TextView(mContext);
            titleTextView.setText(mContext.getString(R.string.history));
            titleTextView.setTextAppearance(android.R.style.TextAppearance_Material_Medium);
            titleTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_secondary, mContext.getTheme()));
            titleTextView.setHeight((int) (48 * scale + 0.5f));
            titleTextView.setGravity(Gravity.CENTER_VERTICAL);
            titleTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            linearLayout.addView(titleTextView);

            @SuppressWarnings("unchecked") ArrayList<String> historyActivities = gson.fromJson(prefs.getString("history", gson.toJson(new ArrayList<String>())), ArrayList.class);

            for (String historyActivity : historyActivities) {
                String label = historyActivity;
                try {
                    label = mContext.getResources().getString(
                            mContext.getPackageManager().getActivityInfo(
                                    new ComponentName(mContext.getPackageName(), mContext.getPackageName() + "." + historyActivity), 0
                            ).labelRes);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                TextView entryTextView = new TextView(mContext);
                entryTextView.setText(label);
                entryTextView.setTextAppearance(android.R.style.TextAppearance_Material_Menu);
                entryTextView.setTextColor(mContext.getResources().getColor(R.color.textColor_normal, mContext.getTheme()));
                entryTextView.setHeight((int) (48 * scale + 0.5f));
                entryTextView.setGravity(Gravity.CENTER_VERTICAL);
                entryTextView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
                entryTextView.setOnClickListener(optionview -> {
                    try {
                        Class<?> c = Class.forName(mContext.getPackageName() + "." + historyActivity);
                        Intent intent = new Intent(mContext, c);
                        mContext.startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
                linearLayout.addView(entryTextView);
            }

            historyDialog.setContentView(linearLayout);

            if (historyActivities.size() > 0)
                historyDialog.show();
        }
    }

    /**
     * Opens an app to measure distance,
     * com.google.tango.measure
     * or com.grymala.ruler
     *
     * @param mContext The apps context
     */
    void openArMeasure(Context mContext) {
        PackageManager manager = mContext.getPackageManager();

        try {
            Intent intent = manager.getLaunchIntentForPackage("com.google.tango.measure");
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException | NullPointerException anfe) {
            try {
                Intent intent = manager.getLaunchIntentForPackage("com.grymala.ruler");
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException | NullPointerException anfet) {
                /* TODO
                new BottomDialog.Builder(mContext)
                        .setTitle(R.string.no_ar_measure_app_installed)
                        .setContent(R.string.no_ar_measure_app_installed_description)
                        .setPositiveText(R.string.install_measure_by_google)
                        .setNegativeText(R.string.install_aruler_by_grymala)
                        .setPositiveTextColor(R.color.colorAccent)
                        .setNegativeTextColor(R.color.colorAccent)
                        .onPositive(mBottomDialog -> {
                            try {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.tango.measure")));
                            } catch (android.content.ActivityNotFoundException anfei) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.tango.measure")));
                            }
                        })
                        .onNegative(mBottomDialog -> {
                            try {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.grymala.aruler")));
                            } catch (android.content.ActivityNotFoundException anfei) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.grymala.aruler")));
                            }
                        })
                        .show();
                        */
            }
        }
    }

    /*
     * Input
     */

    String focalLength(int mProgress) {
        int out = (int) Math.round(mProgress * mProgress * 0.2);
        return String.valueOf(out);
    }

    int focalLength(String mText) {
        double in = Double.valueOf(mText);
        return (int) Math.round(Math.sqrt(5 * in));
    }

    /**
     * Returns the aperture for a seekbar progress
     *
     * @param mProgress: Seekbar progress
     * @param mPart:     2 for full stops, 4 for halfs, 6 for thirds
     */
    String aperture(int mProgress, int mPart) {
        double aperture = Math.pow(2.0, 1.0 / (double) mPart * (double) mProgress);

        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(aperture >= 10 ? "#" : "#.#");

        return df.format(aperture);
    }

    /**
     * Returns the seekbar progress for an aperture
     *
     * @param mText: Aperture (e.g. "3.5")
     * @param mPart: 2 for full stops, 4 for halfs, 6 for thirds
     */
    int aperture(String mText, int mPart) {
        double in = Double.valueOf(mText);
        double out = (Math.log(in) / Math.log(2)) / (1.0 / (double) mPart);
        return (int) Math.round(out);
    }

    String distance(int mProgress, boolean mEmpirical) {
        mProgress++;
        double distance = mProgress * mProgress / 10d;

        if (mEmpirical) {
            distance *= 3.281;
        }

        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(distance >= 8 ? "#" : "#.#");

        return df.format(distance);
    }

    /*
     *  Tools
     */

    int distance(String mText, boolean mEmpirical) {
        double in = Double.valueOf(mText);

        if (mEmpirical) {
            in /= 3.281;
        }

        double out = Math.sqrt(in * 10) - 1;

        return (int) Math.round(out);
    }

    void addToHistory(Context mContext, final String mName) {
        if (Build.VERSION.SDK_INT >= 24) {
            @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
            final SharedPreferences.Editor prefsEdit = prefs.edit();
            Gson gson = new Gson();

            ArrayList<String> currentHistory = new ArrayList<>();
            currentHistory.add("");
            //noinspection unchecked
            currentHistory.addAll(1, gson.fromJson(prefs.getString("history", gson.toJson(new ArrayList<String>())), ArrayList.class));
            currentHistory.removeIf(element -> element.equals(mName));
            if (currentHistory.size() == 5) {
                currentHistory.remove(4);
            }
            currentHistory.set(0, mName);

            prefsEdit.putString("history", gson.toJson(currentHistory)).apply();
        }
    }

    /**
     * Returns the same number as String with a maximum of two decimals
     *
     * @param mNumber The number to truncate.
     * @return {@param mNumber} as String.
     * @see DecimalFormat
     */
    static String truncateNumber(Float mNumber) {
        return new DecimalFormat("#.##").format(mNumber);
    }

    /**
     * Shorthand
     * Converts a number of seconds to a Spanned containing hours, minutes, seconds
     *
     * @param mContext    The context to run in.
     * @param mCalculated The number of seconds to calculate with.
     * @return A Spanned formatted as "DD days, HH hours, MM minutes, SS seconds.
     * @see #convertTime(Context, float, boolean)
     */
    @Deprecated
    Spanned convertTime(Context mContext, float mCalculated) {
        return convertTime(mContext, mCalculated, true);
    }

    /**
     * Converts a number of seconds to a Spanned containing hours, minutes, seconds
     *
     * @param mContext    The context to run in.
     * @param mCalculated The number of seconds to calculate with.
     * @param mShortened  Whether to show decimals over 10 seconds
     * @return A Spanned formatted as "DD days, HH hours, MM minutes, SS seconds.
     */
    Spanned convertTime(Context mContext, float mCalculated, boolean mShortened) {
        int days = 0;
        int hours = 0;
        int minutes = 0;

        while (mCalculated > 86400) {
            days++;
            mCalculated -= 86400;
        }
        while (mCalculated > 3600) {
            hours++;
            mCalculated -= 3600;
        }
        while (mCalculated > 60) {
            minutes++;
            mCalculated -= 60;
        }

        StringBuilder mResult = new StringBuilder();
        if (days != 0) {
            mResult.append(String.valueOf(days)).append("<small>").append(mContext.getString(R.string.time_days)).append("</small>").append(" ");
        }
        if (hours != 0) {
            mResult.append(String.valueOf(hours)).append("<small>").append(mContext.getString(R.string.time_hours)).append("</small>").append(" ");
        }
        if (minutes != 0) {
            mResult.append(String.valueOf(minutes)).append("<small>").append(mContext.getString(R.string.time_minutes)).append("</small>").append(" ");
        }
        if (Float.compare(mCalculated, 0) != 0) {
            if (mShortened) {
                mResult.append(new DecimalFormat(mCalculated > 10 ? "#" : "#.#").format(mCalculated)).append("<small>").append(mContext.getString(R.string.time_seconds)).append("</small>");
            } else {
                mResult.append(new DecimalFormat("#0.0#").format(mCalculated)).append("<small>").append(mContext.getString(R.string.time_seconds)).append("</small>");
            }
        } else {
            mResult.append("0<small>").append(mContext.getString(R.string.time_seconds)).append("</small>");
        }

        return Html.fromHtml(mResult.toString());
    }

    String convertMilliseconds(long milliseconds) {
        return String.format(Locale.getDefault(),
                "%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    /**
     * Truncates the input to a number of decimals depending on its size
     * and converts to the empirical system if neccessary
     *
     * @param mContext    The context to run in.
     * @param mCalculated The number of meters to calculate with.
     * @return A Spanned as # if over 20 meters, else #.#, converted to feet if needed so
     */
    Spanned convertDistance(Context mContext, float mCalculated) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);

        if (prefs.getBoolean("empirical", false)) {
            mCalculated *= 3.281;
            return Html.fromHtml(new DecimalFormat(mCalculated > 20 ? "#" : "#.#").format(mCalculated) + "<small>" + mContext.getString(R.string.feet) + "</small>");
        } else {
            return Html.fromHtml(new DecimalFormat(mCalculated > 20 ? "#" : "#.#").format(mCalculated) + "<small>" + mContext.getString(R.string.meter) + "</small>");
        }
    }
}
