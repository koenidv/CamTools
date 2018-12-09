package com.koenidv.camtools;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;

public class CameraDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_details);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        int which = 0;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                which = 0;
            } else {
                which = extras.getInt("which");
            }
        } else {
            try {
                which = (int) savedInstanceState.getSerializable("which");
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }

        TextView mNameTextView = findViewById(R.id.nameTextView);
        TextView mPixelCountTextView = findViewById(R.id.pixelcountTextView);
        TextView mResolutionTextView = findViewById(R.id.resolutionTextView);
        TextView mSensorSizeTextView = findViewById(R.id.sensorsizeTextView);
        TextView mCropFactorTextView = findViewById(R.id.cropfactorTextView);
        TextView mPixelPitchTextView = findViewById(R.id.pixelpitchTextView);
        TextView mCocTextView = findViewById(R.id.confusionTextView);
        FloatingActionButton mEditFAB = findViewById(R.id.editFAB);


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

        int finalWhich = which;
        mEditFAB.setOnClickListener(v -> mModuleManager.editCamera(CameraDetailsActivity.this, finalWhich, null, null));


        /*
         * Transfer camera with android beam
         */
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(CameraDetailsActivity.this);
        if (nfcAdapter != null) {
            NdefMessage message = null;
            try {
                message = new NdefMessage(mCamera.getName().getBytes());
            } catch (FormatException mE) {
                mE.printStackTrace();
            }
            nfcAdapter.setNdefPushMessage(message, CameraDetailsActivity.this);
        }


    }
}

