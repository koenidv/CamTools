package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
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

    final List<cameraCard> mCameraCardList = new ArrayList<>();
    final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraCardList);

    @Override
    protected void onResume() {
        super.onResume();

        //Toast.makeText(EditCamerasActivity.this, "OnResume", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cameras);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();
        Gson gson = new Gson();

        if (prefs.getBoolean("system_darkmode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        RecyclerView mRecyclerView = findViewById(R.id.camerasRecyclerView);
        SpeedDialView mAddDial = findViewById(R.id.addSpeedDial);
        CardView mEmptyCardView = findViewById(R.id.emptyCard);


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

            if (appLinkData != null && !appLinkData.toString().startsWith("%7B")) {
                try {

                    String data = appLinkData.toString().replace("http://addcam.koenidv.de/", "").replace("%20", " ");

                    String name = data.substring(0, data.indexOf(";"));
                    data = data.substring(data.indexOf(";") + 1);
                    String resolution = data.substring(0, data.indexOf(";"));
                    data = data.substring(data.indexOf(";") + 1);
                    String sensorsize = data.substring(0, data.indexOf(";"));
                    data = data.substring(data.indexOf(";") + 1);
                    String confusion = data;

                    camera addCamera = new camera(name, resolution, sensorsize, confusion);
                    int index = prefs.getInt("cameras_amount", 0) + 1;
                    prefsEdit.putString("camera_" + String.valueOf(index), gson.toJson(addCamera))
                            .putInt("cameras_amount", index)
                            .apply();

                    Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_camera).replace("%s", addCamera.getName()), Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, v -> mModuleManager.deleteCamera(this, index, mCameraCardList, mAdapter)).show();
                } catch (IndexOutOfBoundsException ie) {
                    Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_error), Snackbar.LENGTH_LONG).show();
                }
            }

        } else if (Objects.equals(intentAction, NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] rawMessage = startIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage message = (NdefMessage) rawMessage[0];
            String data = new String(message.getRecords()[0].getPayload());
            data = data.replace("%20", " ");

            try {
                String name = data.substring(0, data.indexOf(";"));
                data = data.substring(data.indexOf(";") + 1);
                String resolution = data.substring(0, data.indexOf(";"));
                data = data.substring(data.indexOf(";") + 1);
                String sensorsize = data.substring(0, data.indexOf(";"));
                data = data.substring(data.indexOf(";") + 1);
                String confusion = data;

                camera addCamera = new camera(name, resolution, sensorsize, confusion);
                int index = prefs.getInt("cameras_amount", 0) + 1;

                prefsEdit.putString("camera_" + String.valueOf(index), gson.toJson(addCamera))
                        .putInt("cameras_amount", index)
                        .apply();

                Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_camera).replace("%s", addCamera.getName()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> mModuleManager.deleteCamera(this, index, mCameraCardList, mAdapter)).show();
            } catch (IndexOutOfBoundsException ie) {
                Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_added_error), Snackbar.LENGTH_LONG).show();
            }
        }

        if (prefs.getInt("cameras_amount", 0) == 0 && prefs.getString("camera_0", "").equals("")) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyCardView.setVisibility(View.VISIBLE);

        } else {
            for (int i = 0; i <= prefs.getInt("cameras_amount", 0); i++) {
                camera mCamera = gson.fromJson(prefs.getString("camera_" + i, getString(R.string.camera_default)), camera.class);

                cameraCard mCameraCard = new cameraCard(
                        mCamera.getName(),
                        String.valueOf(Math.round(mCamera.getResolutionX() * mCamera.getResolutionY() / (float) 1000000))
                                + getString(R.string.megapixel)
                                + getString(R.string.resolution_size_seperator)
                                + ModuleManager.truncateNumber(mCamera.getSensorSizeX()) + "x" + ModuleManager.truncateNumber(mCamera.getSensorSizeY())
                                + getString(R.string.millimeter),
                        (prefs.getInt("cameras_last", -1) == i));
                mCameraCardList.add(mCameraCard);
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
                    mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", 0) + 1, mCameraCardList, mAdapter);
                    break;
            }

            return false;
        });
        if (!prefs.getBoolean("has_seen_nfc_tip", false)) {
            Snackbar.make(findViewById(R.id.rootView), getString(R.string.tip_share_beam), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.okay, v -> {
                    })
                    .show();
            prefsEdit.putBoolean("has_seen_nfc_tip", true).apply();
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
                mModuleManager.moveCamera(EditCamerasActivity.this, position, position - 1, EditCamerasActivity.this.mCameraCardList, adapter);
            } else if (which == finalPosition_down) {
                mModuleManager.moveCamera(EditCamerasActivity.this, position, position + 1, EditCamerasActivity.this.mCameraCardList, adapter);
            } else if (which == finalPosition_use) {
                mCameraCardList.get(prefs.getInt("cameras_last", 0)).setIsLastUsed(false);
                mCameraCardList.get(position).setIsLastUsed(true);
                prefsEdit.putInt("cameras_last", position).apply();
                adapter.notifyDataSetChanged();
            } else if (which == position_edit) {
                mModuleManager.editCamera(EditCamerasActivity.this, position, EditCamerasActivity.this.mCameraCardList, adapter);
            } else if (which == finalPosition_delete) {
                cameraCard card = mCameraCardList.get(position);
                mCameraCardList.remove(position);
                mAdapter.notifyItemRemoved(position);

                Snackbar undoSnackbar = Snackbar.make(findViewById(R.id.rootView), getString(R.string.setting_cameras_deleted).replace("%s", card.getName()), Snackbar.LENGTH_LONG);
                undoSnackbar.setAction(R.string.undo, v -> {
                    mCameraCardList.add(position, card);
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
        mBuilder.setDarkTheme(prefs.getBoolean("system_darkmode", false))
                .show();
    }

    public void addCard(View v) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        final ModuleManager mModuleManager = new ModuleManager();
        mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", 0) + 1, mCameraCardList, mAdapter);

        RecyclerView mRecyclerView = findViewById(R.id.camerasRecyclerView);
        CardView mEmptyCardView = findViewById(R.id.emptyCard);

        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyCardView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        mEmptyCardView.setVisibility(View.GONE);
    }
}