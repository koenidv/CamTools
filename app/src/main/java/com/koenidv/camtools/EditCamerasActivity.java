package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditCamerasActivity extends AppCompatActivity {

    final List<cameraCard> mCameraCardList = new ArrayList<>();

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


        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraCardList);
        mRecyclerView.setAdapter(mAdapter);


        /*
         * Add from URL
         */

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null && !appLinkData.toString().startsWith("%7B")) {

            String data = appLinkData.toString().replace("https://addcam.koenidv.de/", "").replace("%20", " ");

            String name = data.substring(0, data.indexOf(";"));
            data = data.substring(data.indexOf(";") + 1);
            String resolution = data.substring(0, data.indexOf(";"));
            data = data.substring(data.indexOf(";") + 1);
            String sensorsize = data.substring(0, data.indexOf(";"));
            data = data.substring(data.indexOf(";") + 1);
            String confusion = data;

            camera addCamera = new camera(name, resolution, sensorsize, confusion);
            prefsEdit.putString("camera_" + String.valueOf(prefs.getInt("cameras_amount", 0) + 1), gson.toJson(addCamera))
                    .putInt("cameras_amount", prefs.getInt("cameras_amount", 0) + 1)
                    .apply();


        } else if (appLinkData != null) {
            String data = appLinkData.toString().replace("https://cam.koenidv.de/add/", "").replace("%7B", "{").replace("%7D", "}").replace("%20", " ");
            data = Html.fromHtml(data).toString();
            camera addCamera = gson.fromJson(data, camera.class);

            prefsEdit.putString("camera_" + String.valueOf(prefs.getInt("cameras_amount", 0) + 1), gson.toJson(addCamera))
                    .putInt("cameras_amount", prefs.getInt("cameras_amount", 0) + 1)
                    .apply();

            Snackbar.make(findViewById(R.id.rootView), "Added camera \"" + addCamera.getName() + "\"", Snackbar.LENGTH_LONG).show();
        }


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

                    Snackbar.make(mRecyclerView, "The database will come soon...", Snackbar.LENGTH_LONG).show();

                    break;
                case R.id.add_custom:
                    mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", 0) + 1, mCameraCardList, mAdapter);
                    break;
            }

            return false;
        });
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
                Intent intent = new Intent(EditCamerasActivity.this, CameraDetailsActivity.class)
                        .putExtra("which", position);
                startActivity(intent);
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
                mModuleManager.editCamera(EditCamerasActivity.this, rv.getChildAdapterPosition(view), EditCamerasActivity.this.mCameraCardList, adapter);
            } else if (which == finalPosition_delete) {
                mModuleManager.deleteCamera(EditCamerasActivity.this, rv.getChildAdapterPosition(view), EditCamerasActivity.this.mCameraCardList, adapter);
            }
        });
        mBuilder.setDarkTheme(prefs.getBoolean("system_darkmode", false))
                .show();
    }

}