package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        for (int i = 0; i <= prefs.getInt("cameras_amount", 0); i++) {
            cameraCard mCameraCard = new cameraCard(
                    prefs.getString("camera_" + i + "_name", getString(R.string.camera_default_name)),
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
                new SpeedDialActionItem.Builder(R.id.add_database, R.drawable.ic_search_white)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_database))
                        .create());
        mAddDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.add_custom, R.drawable.ic_edit_white)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_custom))
                        .create());


        mAddDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.add_database:
                        Toast.makeText(EditCamerasActivity.this, "The database will come soon", Toast.LENGTH_LONG).show();

                        /*XmlResourceParser databaseParser = getResources().getXml(R.xml.cameras);

                        try {
                            String eventType = databaseParser.getName();
                            int startTag = XmlResourceParser.START_TAG;
                            if (databaseParser.getEventType() == XmlResourceParser.START_TAG) {
                                String s = databaseParser.getName();

                                if (s.equals("manufacturer")) {
                                    databaseParser.next();   /// moving to the next node
                                    if(databaseParser.getName() != null && databaseParser.getName().equalsIgnoreCase("camera")){
                                        String name = databaseParser.getText();  ///to get value getText() method should be used
                                        databaseParser.next();   ///jumping on to the next node
                                        String name2 = databaseParser.getText();  ////similar to above

                                        Log.d("TAG", name);
                                        Log.d("TAG", name2);
                                    }
                                }
                            }
                        } catch (XmlPullParserException | IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/

                        break;
                    case R.id.add_custom:

                        final BottomSheetDialog mDialog = new BottomSheetDialog(EditCamerasActivity.this);
                        mDialog.setContentView(R.layout.dialog_camera_add_custom);
                        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        FrameLayout bottomSheet = (FrameLayout) mDialog.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

                        final EditText mNameEditText = mDialog.findViewById(R.id.nameEditText);
                        TextView mSizePresetTextView = mDialog.findViewById(R.id.sensorsizePresetTextView);
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

                        mSizePresetTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final List<String> presets = new ArrayList<>();
                                for (int preset = 0; preset <= getResources().getInteger(R.integer.preset_size_amount); preset++) {
                                    int resId = getResources().getIdentifier("preset_size_" + String.valueOf(preset), "string", getPackageName());
                                    Toast.makeText(EditCamerasActivity.this, String.valueOf(resId), Toast.LENGTH_SHORT).show();
                                    presets.add(getString(resId));
                                }

                                BottomSheet.Builder mBuilder = new BottomSheet.Builder(EditCamerasActivity.this);
                                mBuilder.setTitle(getString(R.string.calculate_camera_choose))
                                        .setItems(presets.toArray(new String[0]), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setDarkTheme(prefs.getBoolean("system_darkmode", false))
                                        .show();
                            }
                        });

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

                        mSaveButton.setEnabled(false);
                        mSaveButton.getBackground().setAlpha(0);
                        mSaveButton.setTextColor(getResources().getColor(R.color.gray));

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
            mSaveButton.getBackground().setAlpha(255);
            mSaveButton.setTextColor(Color.WHITE);
        } else {
            mSaveButton.setEnabled(false);
            mSaveButton.getBackground().setAlpha(0);
            mSaveButton.setTextColor(getResources().getColor(R.color.gray));
        }
    }
}
