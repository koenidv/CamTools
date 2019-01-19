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
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.michaelbel.bottomsheet.BottomSheet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
    void selectCamera(final Context mContext, final TextView mCameraTextView, final float[] mValue, final String mType) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        Gson gson = new Gson();

        if (prefs.getInt("cameras_amount", -1) == -1) {
            mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
        } else {
            final List<String> cameras = new ArrayList<>();
            final List<Integer> icons = new ArrayList<>();
            for (int camera = 0; camera <= prefs.getInt("cameras_amount", 0); camera++) {
                Camera thisCamera = gson.fromJson(prefs.getString("camera_" + camera, mContext.getString(R.string.camera_default)), Camera.class);
                cameras.add(thisCamera.getName());
                icons.add(thisCamera.getIcon());
            }
            cameras.add(mContext.getString(R.string.calculate_camera_manage));

            icons.add(R.drawable.ic_settings);
            int[] iconlist = new int[icons.size()];
            Iterator<Integer> iterator = icons.iterator();
            for (int i = 0; i < iconlist.length; i++) {
                iconlist[i] = iterator.next();
            }

            BottomSheet.Builder mBuilder = new BottomSheet.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.calculate_camera_choose))
                    .setItems(cameras.toArray(new String[0]), iconlist, (dialog, which) -> {
                        if (which == cameras.size() - 1) {
                            mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
                        } else {
                            prefsEdit.putInt("cameras_last", which).apply();
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
                            mCameraTextView.setText(mContext.getString(R.string.calculate_camera)
                                    .replace("%s", gson.fromJson(prefs.getString("camera_" + which, mContext.getString(R.string.camera_default)), Camera.class).getName()));
                        }
                    })
                    .setDarkTheme(mContext.getResources().getBoolean(R.bool.darkmode))
                    .show();
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
            mDialog.findViewById(R.id.cocLabelTextView).setVisibility(View.GONE);
            mConfusionEditText.setVisibility(View.GONE);
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

        mIconButton.setOnClickListener(v -> {
            BottomSheet.Builder sheet = new BottomSheet.Builder(mContext);
            int[] items = {
                    R.string.camera_icon_photo,
                    R.string.camera_icon_shutter,
                    R.string.camera_icon_video,
                    R.string.camera_icon_phone
            };

            int[] icons = {
                    R.drawable.camera_photo,
                    R.drawable.camera_shutter,
                    R.drawable.camera_video,
                    R.drawable.camera_phone
            };

            sheet.setItems(items, icons, (dialog, which) -> {
                switch (which) {
                    case 0:
                        thisCameraFinal.setIcon(R.drawable.camera_photo);
                        mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_photo));
                        break;
                    case 1:
                        thisCameraFinal.setIcon(R.drawable.camera_shutter);
                        mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_shutter));
                        break;
                    case 2:
                        thisCameraFinal.setIcon(R.drawable.camera_video);
                        mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_video));
                        break;
                    case 3:
                        thisCameraFinal.setIcon(R.drawable.camera_phone);
                        mIconButton.setImageDrawable(mContext.getDrawable(R.drawable.camera_phone));
                        break;
                }
            });

            sheet.setDarkTheme(mContext.getResources().getBoolean(R.bool.darkmode))
                    .show();
        });

        mResolutionPresetTextView.setOnClickListener(v -> {
            final List<String> presets = new ArrayList<>();
            for (int preset = 0; preset <= mContext.getResources().getInteger(R.integer.preset_res_amount); preset++) {
                int resId = mContext.getResources().getIdentifier("preset_res_" + String.valueOf(preset), "string", mContext.getPackageName());
                presets.add(mContext.getString(resId));
            }

            BottomSheet.Builder mBuilder = new BottomSheet.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.preset_choose))
                    .setItems(presets.toArray(new String[0]), (dialog, which) -> {
                        int resXid = mContext.getResources().getIdentifier("preset_res_" + String.valueOf(which) + "_x", "string", mContext.getPackageName());
                        int resYid = mContext.getResources().getIdentifier("preset_res_" + String.valueOf(which) + "_y", "string", mContext.getPackageName());

                        mResolutionXEditText.setText(mContext.getString(resXid));
                        mResolutionYEditText.setText(mContext.getString(resYid));
                    })
                    .setDarkTheme(mContext.getResources().getBoolean(R.bool.darkmode))
                    .show();
        });

        mSizePresetTextView.setOnClickListener(v -> {
            final List<String> presets = new ArrayList<>();
            for (int preset = 0; preset <= mContext.getResources().getInteger(R.integer.preset_size_amount); preset++) {
                int resId = mContext.getResources().getIdentifier("preset_size_" + String.valueOf(preset), "string", mContext.getPackageName());
                presets.add(mContext.getString(resId));
            }

            BottomSheet.Builder mBuilder = new BottomSheet.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.preset_choose))
                    .setItems(presets.toArray(new String[0]), (dialog, which) -> {
                        int sizeXid = mContext.getResources().getIdentifier("preset_size_" + String.valueOf(which) + "_x", "string", mContext.getPackageName());
                        int sizeYid = mContext.getResources().getIdentifier("preset_size_" + String.valueOf(which) + "_y", "string", mContext.getPackageName());
                        int cocid = mContext.getResources().getIdentifier("preset_size_" + String.valueOf(which) + "_coc", "string", mContext.getPackageName());

                        mSizeXEditText.setText(mContext.getString(sizeXid));
                        mSizeYEditText.setText(mContext.getString(sizeYid));
                        mConfusionEditText.setText(mContext.getString(cocid));
                    })
                    .setDarkTheme(mContext.getResources().getBoolean(R.bool.darkmode))
                    .show();
        });

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
     * @param mContext    The context to run in.
     * @param mFromIndex  The position of the Camera to move.
     * @param mToIndex    The position to move the Camera to.
     * @param mCameraList A list in which all {@link Camera} are stored to display them in the RecyclerView.
     * @param mAdapter    The RecyclerView's adapter.
     * @see EditCamerasActivity
     */
    void moveCamera(final Context mContext, final int mFromIndex, final int mToIndex, @Nullable final List<Camera> mCameraList, @Nullable final RecyclerView.Adapter mAdapter) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        String moving = prefs.getString("camera_" + mFromIndex, mContext.getString(R.string.camera_default));

        if (mFromIndex < mToIndex) {
            //Move every camera between from and to one down
            for (int movethis = mToIndex; movethis > mFromIndex; movethis--) {
                prefsEdit.putString("camera_" + String.valueOf(movethis - 1), prefs.getString("camera_" + movethis, mContext.getString(R.string.camera_default)));
            }
        } else if (mFromIndex > mToIndex) {
            //As above, but one up
            for (int movethis = mToIndex; movethis < mFromIndex; movethis++) {
                prefsEdit.putString("camera_" + String.valueOf(movethis + 1), prefs.getString("camera_" + movethis, mContext.getString(R.string.camera_default)));
            }
        }

        //Put the moved Camera in the right place
        prefsEdit.putString("camera_" + mToIndex, moving).apply();

        if (mCameraList != null && mAdapter != null) {
            Camera toCard = mCameraList.get(mToIndex);
            mCameraList.set(mToIndex, mCameraList.get(mFromIndex));
            mCameraList.set(mFromIndex, toCard);
            mAdapter.notifyItemMoved(mFromIndex, mToIndex);
        }

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
        mMathView.setText(mContext.getString(mathId));
        mDescriptionTextView.setText(descrId);

        mDialog.show();
    }

    void showHistory(Context mContext) {
        if (Build.VERSION.SDK_INT >= 24) {
            @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
            Gson gson = new Gson();

            @SuppressWarnings("unchecked") ArrayList<String> historyClasses = gson.fromJson(prefs.getString("history", gson.toJson(new ArrayList<String>())), ArrayList.class);
            ArrayList<String> historyNames = new ArrayList<>();
            for (int i = 0; i < historyClasses.size(); i++) {
                // Get activity label for each activity
                String label = historyClasses.get(i);
                try {
                    label = mContext.getResources().getString(
                            mContext.getPackageManager().getActivityInfo(
                                    new ComponentName(mContext.getPackageName(), mContext.getPackageName() + "." + historyClasses.get(i)), 0
                            ).labelRes);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                historyNames.add(label);
            }

            BottomSheet.Builder mBuilder = new BottomSheet.Builder(mContext);
            mBuilder.setItems(historyNames.toArray(new String[0]), (dialog, which) -> {
                try {
                    Class<?> c = Class.forName(mContext.getPackageName() + "." + historyClasses.get(which));
                    Intent intent = new Intent(mContext, c);
                    mContext.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            if (historyClasses.size() > 0) {
                mBuilder.show();
            }
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
