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

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditCamerasActivity extends AppCompatActivity {

    private final List<Camera> mCameraList = new ArrayList<>();
    private final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraList);
    private Snackbar undoSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cameras);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
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
                    String data = appLinkData.toString().replace("https://camtools.koenidv.de/add/", "").replace("%20", " ");

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

    public void cardClicked(final View view) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();

        final RecyclerView rv = findViewById(R.id.camerasRecyclerView);
        final int position = rv.getChildAdapterPosition(view);

        List<String> options = new ArrayList<>();
        List<Integer> icons = new ArrayList<>();
        int counter = -1;
        final int position_details;
        int position_up = -1;
        int position_down = -1;
        int position_use = 0;
        final int position_edit;
        int position_delete = -1;

        options.add(getString(R.string.details));
        icons.add(R.drawable.ic_info);
        position_details = counter + 1;
        counter++;
        if (position != 0) {
            options.add(getString(R.string.move_up));
            icons.add(R.drawable.ic_up);
            position_up = counter + 1;
            counter++;
        }
        if (position != rv.getChildCount() - 1) {
            options.add(getString(R.string.move_down));
            icons.add(R.drawable.ic_down);
            position_down = counter + 1;
            counter++;
        }
        if (rv.getChildCount() > 1 && position != prefs.getInt("cameras_last", 0)) {
            options.add(getString(R.string.use_next));
            icons.add(R.drawable.ic_favorite);
            position_use = counter + 1;
            counter++;
        }
        options.add(getString(R.string.edit));
        icons.add(R.drawable.ic_edit);
        position_edit = counter + 1;
        counter++;
        if (rv.getChildCount() > 1) {
            options.add(getString(R.string.delete));
            icons.add(R.drawable.ic_delete);
            position_delete = counter + 1;
        }

        int[] iconlist = new int[icons.size()];
        Iterator<Integer> iterator = icons.iterator();
        for (int i = 0; i < iconlist.length; i++) {
            iconlist[i] = iterator.next();
        }

        final int finalPosition_up = position_up;
        final int finalPosition_down = position_down;
        final int finalPosition_use = position_use;
        final int finalPosition_delete = position_delete;
        BottomSheet.Builder mBuilder = new BottomSheet.Builder(EditCamerasActivity.this);
        mBuilder.setItems(options.toArray(new String[0]), iconlist, (dialog, which) -> {
            RecyclerView.Adapter adapter = rv.getAdapter();

            if (which == position_details) {
                CameraDetailsSheet sheet = new CameraDetailsSheet();
                sheet.which = position;
                sheet.show(getSupportFragmentManager(), "camera_details_sheet");
            } else if (which == finalPosition_up) {
                mModuleManager.moveCamera(EditCamerasActivity.this, position, position - 1, EditCamerasActivity.this.mCameraList, adapter);
            } else if (which == finalPosition_down) {
                mModuleManager.moveCamera(EditCamerasActivity.this, position, position + 1, EditCamerasActivity.this.mCameraList, adapter);
            } else if (which == finalPosition_use) {
                adapter.notifyItemChanged(prefs.getInt("cameras_last", 0), rv.getChildAt(prefs.getInt("cameras_last", 0)));
                adapter.notifyItemChanged(position);
                prefsEdit.putInt("cameras_last", position).apply();

                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
                if (nfcAdapter != null) {
                    Camera mCamera = (new Gson()).fromJson(prefs.getString("camera_" + prefs.getInt("cameras_last", 0), getString(R.string.camera_default)), Camera.class);
                    String data =
                            mCamera.getName().replace(" ", "%20")
                                    + ";" + mCamera.getResolutionX()
                                    + ":" + mCamera.getResolutionY()
                                    + ";" + mCamera.getSensorSizeX()
                                    + ":" + mCamera.getSensorSizeY();
                    NdefMessage message = new NdefMessage(NdefRecord.createMime("application/com.koenidv.camtools", data.getBytes()), NdefRecord.createApplicationRecord("com.koenidv.camtools"));
                    nfcAdapter.setNdefPushMessage(message, this);
                }
            } else if (which == position_edit) {
                mModuleManager.editCamera(EditCamerasActivity.this, position, EditCamerasActivity.this.mCameraList, adapter);
            } else if (which == finalPosition_delete) {
                Camera deletedCamera = mCameraList.get(position);
                mCameraList.remove(position);
                mAdapter.notifyItemRemoved(position);

                undoSnackbar = Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_deleted).replace("%s", deletedCamera.getName()), Snackbar.LENGTH_LONG);
                undoSnackbar.setAction(R.string.undo, v -> {
                    mCameraList.add(position, deletedCamera);
                    mAdapter.notifyItemInserted(position);
                });
                undoSnackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar mSnackbar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            mModuleManager.deleteCamera(EditCamerasActivity.this, position, null, null);
                        }
                    }
                });
                undoSnackbar.show();

            }
        });
        mBuilder.setDarkTheme(getResources().getBoolean(R.bool.darkmode))
                .show();
    }
}