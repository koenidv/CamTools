package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EditCamerasActivity extends AppCompatActivity {

    final List<cameraCard> mCameraCardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cameras);
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = EditCamerasActivity.this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();
        final ModuleManager mModuleManager = new ModuleManager();

        RecyclerView mRecyclerView = findViewById(R.id.camerasRecyclerView);
        SpeedDialView mAddDial = findViewById(R.id.addSpeedDial);


        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final RecyclerView.Adapter mAdapter = new camerasAdapter(mCameraCardList);
        mRecyclerView.setAdapter(mAdapter);


        for (int i = 0; i <= prefs.getInt("cameras_amount", 0); i++) {
            cameraCard mCameraCard = new cameraCard(
                    prefs.getString("camera_" + i + "_name", getString(R.string.camera_default_name)),
                    getString(R.string.sensorsize_indicator) + ModuleManager.truncateNumber(prefs.getFloat("camera_" + i + "_sensorsize_x", 36)) + "x" + ModuleManager.truncateNumber(prefs.getFloat("camera_" + i + "_sensorsize_y", 24)) + getString(R.string.millimeter),
                    getString(R.string.resolution_indicator) + String.valueOf(Math.round(prefs.getInt("camera_" + i + "_resolution_x", 5472) * prefs.getInt("camera_" + i + "_resolution_y", 3648) / (float) 1000000)) + getString(R.string.megapixel)
                            + " (" + String.valueOf(prefs.getInt("camera_" + i + "_resolution_x", 5472)) + "x" + String.valueOf(prefs.getInt("camera_" + i + "_resolution_y", 3648)) + getString(R.string.pixel) + ")",
                    getString(R.string.pixelpitch_indicator) + ModuleManager.truncateNumber(prefs.getFloat("camera_" + i + "_pixelpitch", 6.6f)) + getString(R.string.micrometer),
                    getString(R.string.coc_indicator) + ModuleManager.truncateNumber(prefs.getFloat("camera_" + i + "_coc", 0.03f)) + getString(R.string.millimeter),
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


        mAddDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.add_database:

                        //SQLiteDatabase mDatabase = new DataBaseHelper(EditCamerasActivity.this).getReadableDatabase();

                        //mDatabase.rawQuery("SELECT * FROM Canon", new String[]{}).close();


                        //Toast.makeText(EditCamerasActivity.this, "The database will come soon", Toast.LENGTH_LONG).show();

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
                        mModuleManager.editCamera(EditCamerasActivity.this, prefs.getInt("cameras_amount", 0) + 1, mCameraCardList, mAdapter);
                        break;
                }

                return false;
            }
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
        int position_up = -1;
        int position_down = -1;
        int position_use = 0;
        final int position_edit;
        int position_delete = -1;

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
        mBuilder.setItems(options.toArray(new String[0]), iconlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (which == finalPosition_up) {
                    mModuleManager.moveCamera(EditCamerasActivity.this, position, position - 1, EditCamerasActivity.this.mCameraCardList, adapter);
                    Snackbar.make(rv, "Move up", Snackbar.LENGTH_SHORT).show();
                } else if (which == finalPosition_down) {
                    mModuleManager.moveCamera(EditCamerasActivity.this, position, position + 1, EditCamerasActivity.this.mCameraCardList, adapter);
                    Snackbar.make(rv, "Move down", Snackbar.LENGTH_SHORT).show();
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
            }
        });
        mBuilder.setDarkTheme(prefs.getBoolean("system_darkmode", false))
                .show();
    }

}