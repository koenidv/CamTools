package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

public class EditCamerasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cameras);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        RecyclerView mRecyclerView = findViewById(R.id.camerasRecyclerView);
        SpeedDialView mAddDial = findViewById(R.id.addSpeedDial);


        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final List<cameraCard> mCameraCardList = new ArrayList<>();
        final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraCardList);
        mRecyclerView.setAdapter(mAdapter);

        //cameraCard mCameraCard = new cameraCard("Canon EOS 6D", "Vollformat (24x36mm)", "22MP", "Zahl", "0.003");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 5D Mark 4", "Vollformat (24x36mm)", "35MP", "Zahl", "0.003");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 30D", "APS-C (16x22mm)", "22MP", "Zahl", "0.01");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 6D", "Vollformat (24x36mm)", "22MP", "Zahl", "0.003");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 5D Mark 4", "Vollformat (24x36mm)", "35MP", "Zahl", "0.003");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 30D", "APS-C (16x22mm)", "22MP", "Zahl", "0.01");
        //mCameraCardList.add(mCameraCard);
        //mCameraCard = new cameraCard("Canon EOS 6D", "Vollformat (24x36mm)", "22MP", "Zahl", "0.003");
        //mCameraCardList.add(mCameraCard);
        //mCameraCardList.add(mCameraCard);
        //mAdapter.notifyDataSetChanged();

        for (int i = 0; i <= prefs.getInt("cameras_amount", 0); i++) {
            cameraCard mCameraCard = new cameraCard(
                    prefs.getString("camera_" + i + "_name", "Default camera"),
                    getString(R.string.sensorsize_indicator) + String.valueOf(prefs.getInt("camera_" + i + "_sensorsize_x", 36)) + "x" + String.valueOf(prefs.getInt("camera_" + i + "_sensorsize_y", 24)) + getString(R.string.millimeter),
                    getString(R.string.resolution_indicator) + String.valueOf(Math.round(prefs.getInt("camera_" + i + "_resolution_x", 5472) * prefs.getInt("camera_" + i + "_resolution_y", 3648) / (float) 1000000)) + getString(R.string.megapixel)
                    + " (" + String.valueOf(prefs.getInt("camera_" + i + "_resolution_x", 5472)) + "x" + String.valueOf(prefs.getInt("camera_" + i + "_resolution_y", 3648)) + getString(R.string.pixel) + ")",
                    getString(R.string.pixelpitch_indicator) + String.valueOf(prefs.getFloat("camera_" + i + "_pixelpitch", 6.6f)) + getString(R.string.micrometer),
                    getString(R.string.coc_indicator) + String.valueOf(prefs.getFloat("camera_" + i + "_coc", 0.03f)) + getString(R.string.millimeter));
            mCameraCardList.add(mCameraCard);
        }
        mAdapter.notifyDataSetChanged();


        mAddDial.setMainFabClosedBackgroundColor(getResources().getColor(R.color.colorAccentDark));
        mAddDial.setMainFabOpenedBackgroundColor(getResources().getColor(R.color.colorAccent));
        mAddDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.add_database, R.drawable.ic_search_white_24dp)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_database))
                        .create());
        mAddDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.add_custom, R.drawable.ic_custom_white_24px)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_custom))
                        .create());


        mAddDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.add_database:
                        Toast.makeText(EditCamerasActivity.this, "Add from database", Toast.LENGTH_LONG).show();


                        break;
                    case R.id.add_custom:
                        Toast.makeText(EditCamerasActivity.this, "Add custom", Toast.LENGTH_LONG).show();
                        final Dialog mDialog = new Dialog(EditCamerasActivity.this);
                        mDialog.setContentView(R.layout.dialog_camera_add_custom);

                        final EditText mNameEditText = mDialog.findViewById(R.id.nameEditText);
                        Spinner mSizeSpinner = mDialog.findViewById(R.id.sensorsizeSpinner);
                        final EditText mSizeXEditText = mDialog.findViewById(R.id.sensorsizeXEditText);
                        final EditText mSizeYEditText = mDialog.findViewById(R.id.sensorsizeYEditText);
                        Spinner mResolutionSpinner = mDialog.findViewById(R.id.resolutionSpinner);
                        final EditText mResolutionXEditText = mDialog.findViewById(R.id.resolutionXEditText);
                        final EditText mResolutionYEditText = mDialog.findViewById(R.id.resolutionYEditText);
                        final EditText mConfusionEditText = mDialog.findViewById(R.id.cocEditText);
                        Button mCancelButton = mDialog.findViewById(R.id.cancelButton);
                        final Button mSaveButton = mDialog.findViewById(R.id.saveButton);

                        TextWatcher checkOnTextChanged = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                checkAllFilled(mDialog);

                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        };

                        mNameEditText.addTextChangedListener(checkOnTextChanged);
                        mSizeXEditText.addTextChangedListener(checkOnTextChanged);
                        mSizeYEditText.addTextChangedListener(checkOnTextChanged);
                        mResolutionXEditText.addTextChangedListener(checkOnTextChanged);
                        mResolutionYEditText.addTextChangedListener(checkOnTextChanged);
                        mConfusionEditText.addTextChangedListener(checkOnTextChanged);

                        mCancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.cancel();
                            }
                        });

                        mSaveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String index = "camera_" + String.valueOf(prefs.getInt("cameras_amount", 0) + 1) + "_";
                                float pixelpitch = Float.valueOf(mSizeXEditText.getText().toString()) / Float.valueOf(mResolutionXEditText.getText().toString()) * 1000;
                                prefsEdit.putInt("cameras_amount", prefs.getInt("cameras_amount", 0) + 1)
                                        .putString(index + "name", mNameEditText.getText().toString())
                                        .putInt(index + "sensorsize_x", Integer.valueOf(mSizeXEditText.getText().toString()))
                                        .putInt(index + "sensorsize_y", Integer.valueOf(mSizeYEditText.getText().toString()))
                                        .putInt(index + "resolution_x", Integer.valueOf(mResolutionXEditText.getText().toString()))
                                        .putInt(index + "resolution_y", Integer.valueOf(mResolutionYEditText.getText().toString()))
                                        .putFloat(index + "coc", Float.valueOf(mConfusionEditText.getText().toString()))
                                        .putFloat(index + "pixelpitch", pixelpitch)
                                        .apply();

                                cameraCard mCameraCard = new cameraCard(
                                        mNameEditText.getText().toString(),
                                        getString(R.string.sensorsize_indicator) + mSizeXEditText.getText().toString() + "x" + mSizeYEditText.getText().toString() + getString(R.string.millimeter),
                                        getString(R.string.resolution_indicator) + String.valueOf(Integer.valueOf(mResolutionXEditText.getText().toString()) * Integer.valueOf(mResolutionYEditText.getText().toString()) / 1000000) + getString(R.string.megapixel),
                                        getString(R.string.pixelpitch_indicator) + String.valueOf(pixelpitch) + String.valueOf(R.string.micrometer),
                                        getString(R.string.coc_indicator) + mConfusionEditText.getText().toString() + getString(R.string.millimeter));
                                mCameraCardList.add(mCameraCard);
                                mAdapter.notifyDataSetChanged();

                                mDialog.dismiss();
                            }
                        });


                        mDialog.show();


                        break;
                }

                return false;
            }
        });
    }

    private void checkAllFilled(Dialog mDialog) {
        EditText mNameEditText = mDialog.findViewById(R.id.nameEditText);
        EditText mSizeXEditText = mDialog.findViewById(R.id.sensorsizeXEditText);
        EditText mSizeYEditText = mDialog.findViewById(R.id.sensorsizeYEditText);
        EditText mResolutionXEditText = mDialog.findViewById(R.id.resolutionXEditText);
        EditText mResolutionYEditText = mDialog.findViewById(R.id.resolutionYEditText);
        EditText mConfusionEditText = mDialog.findViewById(R.id.cocEditText);
        Button mSaveButton = mDialog.findViewById(R.id.saveButton);

        if (!mNameEditText.getText().toString().isEmpty()
                && !mSizeXEditText.getText().toString().isEmpty()
                && !mSizeYEditText.getText().toString().isEmpty()
                && !mResolutionXEditText.getText().toString().isEmpty()
                && !mResolutionYEditText.getText().toString().isEmpty()
                && !mConfusionEditText.getText().toString().isEmpty()
                && !mConfusionEditText.getText().toString().equals(".")) {
            mSaveButton.setEnabled(true);
            mSaveButton.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.setTextColor(getResources().getColor(R.color.gray));
        }
    }
}
