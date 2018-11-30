package com.koenidv.camtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.TextView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//  Created by koenidv on 30.11.2018.
class InputManager {
     public void selectCamera(final Context mContext, final TextView mCameraTextView, final float[] mValue, final String mType, final float mDefault) {
        //mContext = mView.getContext();
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor prefsEdit = prefs.edit();

        if (prefs.getInt("cameras_amount", 0) == 0) {
            mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
        } else {
            final List<String> cameras = new ArrayList<>();
            for (int camera = 0; camera <= prefs.getInt("cameras_amount", 0); camera++) {
                cameras.add(prefs.getString("camera_" + camera + "_name", mContext.getString(R.string.camera_default_name)));
            }
            cameras.add(mContext.getString(R.string.calculate_camera_manage));
            ArrayList<Integer> icons = new ArrayList<>();
            for (int icon = 0; icon < cameras.size() - 1; icon++) {
                icons.add(R.drawable.ic_camera);
            }
            icons.add(R.drawable.ic_settings);
            int[] iconlist = new int[icons.size()];
            Iterator<Integer> iterator = icons.iterator();
            for (int i = 0; i < iconlist.length; i++)
            {
                iconlist[i] = iterator.next();
            }

            BottomSheet.Builder mBuilder = new BottomSheet.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.calculate_camera_choose))
                    .setItems(cameras.toArray(new String[0]), iconlist, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == cameras.size() - 1) {
                                mContext.startActivity(new Intent(mContext, EditCamerasActivity.class));
                            } else {
                                prefsEdit.putInt("cameras_last", which).apply();
                                mValue[0] = prefs.getFloat("camera_" + which + "_" + mType, mDefault);
                                mCameraTextView.setText(mContext.getString(R.string.calculate_camera).replace("%s", prefs.getString("camera_" + which + "_name", mContext.getString(R.string.camera_default_name))));
                            }
                        }
                    })
                    .setDarkTheme(prefs.getBoolean("system_darkmode", false))
                    .show();
        }
    }
}
