package com.koenidv.camtools;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CameraDetailsSheet extends BottomSheetDialogFragment {

    int which = 0;
    Activity mActivity;
    ArrayList<Camera> mCameraList;
    RecyclerView.Adapter mAdapter;
    RecyclerView mRecyclerView;
    Snackbar mUndoSnackbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sheet_camera_details, container, false);

        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        TextView mNameTextView = view.findViewById(R.id.nameTextView);
        TextView mPixelCountTextView = view.findViewById(R.id.pixelcountTextView);
        TextView mResolutionTextView = view.findViewById(R.id.resolutionTextView);
        TextView mSensorSizeTextView = view.findViewById(R.id.sensorsizeTextView);
        TextView mCropFactorTextView = view.findViewById(R.id.cropfactorTextView);
        TextView mPixelPitchTextView = view.findViewById(R.id.pixelpitchTextView);
        TextView mCocTextView = view.findViewById(R.id.confusionTextView);
        ImageView mImageView = view.findViewById(R.id.cameraImageView);

        Camera mCamera = gson.fromJson(prefs.getString("camera_" + which, getString(R.string.camera_default)), Camera.class);


        mNameTextView.setText(mCamera.getName());
        mNameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(mCamera.getIcon(), 0, 0, 0);
        mPixelCountTextView.setText(getString(R.string.pixelcount_text)
                .replace("%s", String.valueOf(Math.round(mCamera.getResolutionX() * mCamera.getResolutionY() / (float) 1000000))));
        mResolutionTextView.setText(getString(R.string.resolution_text)
                .replace("%x", String.valueOf(mCamera.getResolutionX()))
                .replace("%y", String.valueOf(mCamera.getResolutionY())));
        mSensorSizeTextView.setText(getString(R.string.sensorsize_text)
                .replace("%x", ModuleManager.truncateNumber(mCamera.getSensorSizeX()))
                .replace("%y", ModuleManager.truncateNumber(mCamera.getSensorSizeY())));
        mCropFactorTextView.setText(getString(R.string.cropfactor_text)
                .replace("%s", ModuleManager.truncateNumber(mCamera.getCropfactor())));
        mPixelPitchTextView.setText(getString(R.string.pixelpitch_text)
                .replace("%s", ModuleManager.truncateNumber(mCamera.getPixelpitch())));
        mCocTextView.setText(getString(R.string.coc_text)
                .replace("%s", ModuleManager.truncateNumber(mCamera.getConfusion())));
        if (mCamera.getImageUrl() != null) {
            new Camera.loadImagesTask(mImageView, view.findViewById(R.id.imageProgressBar), this.getContext()).execute(mCamera.getImageUrl());
        } else {
            view.findViewById(R.id.imageCard).setVisibility(View.GONE);
        }

        ((TextView) view.findViewById(R.id.sensorXTextView)).setText(
                getString(R.string.sensor_description_x)
                        .replace("%p", String.valueOf(mCamera.getResolutionX()))
                        .replace("%s", ModuleManager.truncateNumber(mCamera.getSensorSizeX()))
        );
        ((TextView) view.findViewById(R.id.sensorYTextView)).setText(
                getString(R.string.sensor_description_y)
                        .replace("%p", String.valueOf(mCamera.getResolutionY()))
                        .replace("%s", ModuleManager.truncateNumber(mCamera.getSensorSizeY()))
        );
        ((TextView) view.findViewById(R.id.sensorGeneralTextView)).setText(
                getString(R.string.sensor_description_general)
                        .replace("%r", String.valueOf(Math.round(mCamera.getResolutionX() * mCamera.getResolutionY() / (float) 1000000)))
                        .replace("%f", ModuleManager.truncateNumber(mCamera.getCropfactor()))
                        .replace("%p", ModuleManager.truncateNumber(mCamera.getPixelpitch()))
                        .replace("%c", ModuleManager.truncateNumber(mCamera.getConfusion()))
        );

        if (which == prefs.getInt("cameras_last", -1)) {
            view.findViewById(R.id.useTextView).setVisibility(View.GONE);
        }

        view.findViewById(R.id.useTextView).setOnClickListener(v -> {
            //Set the camera as the currently active one
            this.dismiss();
            mAdapter.notifyItemChanged(prefs.getInt("cameras_last", 0), mRecyclerView.getChildAt(prefs.getInt("cameras_last", 0)));
            mAdapter.notifyItemChanged(which);
            prefsEdit.putInt("cameras_last", which).apply();
        });

        view.findViewById(R.id.editTextView).setOnClickListener(v -> {
            //Open a bottomsheet which lets the user edit the camera
            this.dismiss();
            mModuleManager.editCamera(mActivity, which, mCameraList, mAdapter);
        });

        view.findViewById(R.id.deleteTextView).setOnClickListener(v -> {
            //Show a snackbar, delete the camera after it has been dismissed
            mCameraList.remove(which);
            mAdapter.notifyItemRemoved(which);

            mUndoSnackbar = Snackbar.make(mActivity.findViewById(R.id.rootView), getString(R.string.setting_cameras_deleted).replace("%s", mCamera.getName()), Snackbar.LENGTH_LONG);
            mUndoSnackbar.setAction(R.string.undo, ignored -> {
                mCameraList.add(which, mCamera);
                mAdapter.notifyItemInserted(which);
            });
            mUndoSnackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar mSnackbar, int event) {
                    if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                        mModuleManager.deleteCamera(mActivity, which, null, null);
                    }
                }
            });
            this.dismiss();
            mUndoSnackbar.show();
        });


        /*
         * Transfer Camera with android beam
         */
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (nfcAdapter != null) {
            String data =
                    mCamera.getName().replace(" ", "%20")
                            + ";"
                            + mCamera.getResolutionX()
                            + ":"
                            + mCamera.getResolutionY()
                            + ";"
                            + mCamera.getSensorSizeX()
                            + ":"
                            + mCamera.getSensorSizeY()
                            + ";"
                            + mCamera.getConfusion();
            NdefMessage message = new NdefMessage(NdefRecord.createMime("application/com.koenidv.camtools", data.getBytes()), NdefRecord.createApplicationRecord("com.koenidv.camtools"));
            nfcAdapter.setNdefPushMessage(message, getActivity());
        }

        return view;
    }

}
