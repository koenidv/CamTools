package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.rishabhkhanna.recyclerswipedrag.RecyclerHelper;

public class EditCamerasActivity extends AppCompatActivity {

    private final ArrayList<Camera> mCameraList = new ArrayList<>();
    private final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraList);
    private Snackbar undoSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cameras);
        final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        if (getResources().getBoolean(R.bool.darkmode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        RecyclerView mRecyclerView = findViewById(R.id.camerasRecyclerView);
        SpeedDialView mAddDial = findViewById(R.id.addSpeedDial);
        CardView mEmptyCardView = findViewById(R.id.emptyCard);

        mModuleManager.checkDarkmode(prefs);


        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerHelper touchHelper = new RecyclerHelper<Camera>(mCameraList, mAdapter);
        touchHelper.setRecyclerItemDragEnabled(true)
                .setRecyclerItemSwipeEnabled(false)
                .setOnDragItemListener((mFrom, mTo) -> {
                    mModuleManager.moveCamera(this, mFrom, mTo);
                });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);



        /*
         * Add from URL
         */

        Intent startIntent = getIntent();
        String intentAction = startIntent.getAction();
        Uri appLinkData = startIntent.getData();

        if (Objects.equals(intentAction, Intent.ACTION_VIEW)) {

            if (appLinkData != null) {
                //Add camera from URL (web database)
                try {
                    String data = appLinkData.toString()
                            .replace("https://camtools.koenidv.de/add/", "")
                            .replace("%20", " ")
                            .replace("%CE%B1", "α");

                    String name = data.substring(0, data.indexOf(";"));
                    data = data.substring(data.indexOf(";") + 1);
                    String resolution = data.substring(0, data.indexOf(";"));
                    data = data.substring(data.indexOf(";") + 1);
                    String sensorSize;
                    String imageUrl;
                    if (data.contains(";")) {
                        sensorSize = data.substring(0, data.indexOf(";"));
                        data = data.substring(data.indexOf(";") + 1);
                        imageUrl = data;
                    } else {
                        sensorSize = data;
                        imageUrl = null;
                    }
                    Camera addCamera = new Camera(name, R.drawable.camera_photo, resolution, sensorSize, imageUrl);
                    int index = prefs.getInt("cameras_amount", -1) + 1;
                    prefsEdit.putString("camera_" + String.valueOf(index), gson.toJson(addCamera))
                            .putInt("cameras_amount", index)
                            .apply();

                    Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_camera).replace("%s", addCamera.getName()), Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, v -> mModuleManager.deleteCamera(this, index, mCameraList, mAdapter)).show();
                } catch (Exception e) {
                    Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_error), Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        } else if (Objects.equals(intentAction, NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            //Add camera from Android Beam
            Parcelable[] rawMessage = startIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) rawMessage[0];
            String data = new String(message.getRecords()[0].getPayload());
            data = data.replace("%20", " ");

            try {
                String name = data.substring(0, data.indexOf(";"));
                data = data.substring(data.indexOf(";") + 1);
                String resolution = data.substring(0, data.indexOf(";"));
                data = data.substring(data.indexOf(";") + 1);
                String sensorsize = data;

                Camera addCamera = new Camera(name, R.drawable.camera_photo, resolution, sensorsize);
                int index = prefs.getInt("cameras_amount", -1) + 1;

                prefsEdit.putString("camera_" + String.valueOf(index), gson.toJson(addCamera))
                        .putInt("cameras_amount", index)
                        .apply();

                Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_camera).replace("%s", addCamera.getName()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> mModuleManager.deleteCamera(this, index, mCameraList, mAdapter)).show();
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_error), Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            //Show NFC tip snackbar if opened from settings and it hasn't been shown before
            if (!prefs.getBoolean("has_seen_nfc_tip", false) && prefs.getInt("cameras_amount", -1) != -1) {
                Snackbar.make(findViewById(R.id.rootView), getString(R.string.tip_share_beam), Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.okay, v -> {
                        })
                        .show();
                prefsEdit.putBoolean("has_seen_nfc_tip", true).apply();
            }
        }

        if (prefs.getInt("cameras_amount", -1) == -1) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyCardView.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i <= prefs.getInt("cameras_amount", -1); i++) {
                Camera mCamera = gson.fromJson(prefs.getString("camera_" + i, getString(R.string.camera_default)), Camera.class);

                mCameraList.add(mCamera);
            }
            mAdapter.notifyDataSetChanged();
        }

        mAddDial.setMainFabClosedBackgroundColor(getResources().getColor(R.color.colorAccentDark));
        mAddDial.setMainFabOpenedBackgroundColor(getResources().getColor(R.color.colorAccent));
        mAddDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.add_database, R.drawable.ic_search_white)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_database))
                        .create());
        mAddDial.addActionItem(
                new SpeedDialActionItem.Builder(R.id.add_custom, R.drawable.ic_edit)
                        .setFabImageTintColor(getResources().getColor(R.color.colorAccent))
                        .setLabel(getString(R.string.setting_cameras_new_custom))
                        .create());


        mAddDial.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.add_database:
                    CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                    intent.launchUrl(this, Uri.parse("https://camtools.koenidv.de/cams"));
                    break;
                case R.id.add_custom:
                    if (mEmptyCardView.getVisibility() == View.VISIBLE) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
                        mEmptyCardView.setVisibility(View.GONE);
                    }
                    if (undoSnackbar != null && undoSnackbar.isShown()) {
                        undoSnackbar.dismiss();
                        (new Handler()).postDelayed(() ->
                                        mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", -1) + 1, mCameraList, mAdapter),
                                250);
                    } else {
                        mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", -1) + 1, mCameraList, mAdapter);
                    }
                    break;
            }

            return false;
        });

        View.OnClickListener emptyAddCardListener = v -> {
            // Open database if there is a network connection, otherwise let the user add a custom camera

            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
                intent.launchUrl(this, Uri.parse("https://camtools.koenidv.de/cams"));
            } else {
                mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", -1) + 1, mCameraList, mAdapter);

                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
                mEmptyCardView.setVisibility(View.GONE);
            }
        };

        mEmptyCardView.setOnClickListener(emptyAddCardListener);
        findViewById(R.id.noCamerasAddButton).setOnClickListener(emptyAddCardListener);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            Camera mCamera = gson.fromJson(prefs.getString("camera_" + prefs.getInt("cameras_last", 0), getString(R.string.camera_default)), Camera.class);
            String data =
                    mCamera.getName().replace(" ", "%20")
                            + ";" + mCamera.getResolutionX()
                            + ":" + mCamera.getResolutionY()
                            + ";" + mCamera.getSensorSizeX()
                            + ":" + mCamera.getSensorSizeY();
            NdefMessage message = new NdefMessage(NdefRecord.createMime("application/com.koenidv.camtools", data.getBytes()), NdefRecord.createApplicationRecord("com.koenidv.camtools"));
            nfcAdapter.setNdefPushMessage(message, this);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void cardClicked(final View view) {
        if (undoSnackbar != null && undoSnackbar.isShown()) {
            undoSnackbar.dismiss();
        }

        final RecyclerView rv = findViewById(R.id.camerasRecyclerView);
        final int position = rv.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = rv.getAdapter();


        CameraDetailsSheet sheet = new CameraDetailsSheet();
        sheet.which = position;
        sheet.mActivity = EditCamerasActivity.this;
        sheet.mCameraList = mCameraList;
        sheet.mRecyclerView = rv;
        sheet.mAdapter = adapter;
        sheet.mUndoSnackbar = undoSnackbar;
        sheet.show(getSupportFragmentManager(), "camera_details_sheet");
    }

}