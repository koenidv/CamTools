package com.koenidv.camtools;


import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import androidx.annotation.Nullable;

public class CameraDetailsSheet extends BottomSheetDialogFragment {

    int which = 0;

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
        ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        TextView mNameTextView = view.findViewById(R.id.nameTextView);
        TextView mPixelCountTextView = view.findViewById(R.id.pixelcountTextView);
        TextView mResolutionTextView = view.findViewById(R.id.resolutionTextView);
        TextView mSensorSizeTextView = view.findViewById(R.id.sensorsizeTextView);
        TextView mCropFactorTextView = view.findViewById(R.id.cropfactorTextView);
        TextView mPixelPitchTextView = view.findViewById(R.id.pixelpitchTextView);
        TextView mCocTextView = view.findViewById(R.id.confusionTextView);


        camera mCamera = gson.fromJson(prefs.getString("camera_" + which, getString(R.string.camera_default)), camera.class);


        mNameTextView.setText(mCamera.getName());
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


        /*
         * Transfer camera with android beam
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
            //NdefMessage message = new NdefMessage(NdefRecord.createUri(url));
            NdefMessage message = new NdefMessage(NdefRecord.createMime("application/com.koenidv.camtools", data.getBytes()), NdefRecord.createApplicationRecord("com.koenidv.camtools"));
            nfcAdapter.setNdefPushMessage(message, getActivity());
        }

        return view;
    }

}
