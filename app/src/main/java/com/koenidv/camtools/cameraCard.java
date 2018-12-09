package com.koenidv.camtools;
//  Created by koenidv on 01.10.2018.

import android.content.Context;
import android.content.SharedPreferences;

public class cameraCard {
    private String mName, mInfo;
    private boolean mIsLastUsed;

    public cameraCard() {
    }

    cameraCard(String mName, String mInfo, boolean mIsLastUsed) {
        this.mName = mName;
        this.mInfo = mInfo;
        this.mIsLastUsed = mIsLastUsed;
    }

    cameraCard(camera mCamera, int mIndex, Context mContext) {
        @SuppressWarnings("ConstantConditions") final SharedPreferences prefs = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);

        this.mName = mCamera.getName();
        this.mInfo = String.valueOf(Math.round(mCamera.getResolutionX() * mCamera.getResolutionY() / (float) 1000000))
                + mContext.getString(R.string.megapixel)
                + mContext.getString(R.string.resolution_size_seperator)
                + ModuleManager.truncateNumber(mCamera.getSensorSizeX()) + "x" + ModuleManager.truncateNumber(mCamera.getSensorSizeY())
                + mContext.getString(R.string.millimeter);
        this.mIsLastUsed = mIndex == prefs.getInt("cameras_last", -1);

    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    public String getInfo() {
        return mInfo;
    }
    public void setInfo(String info) {
        this.mInfo = info;
    }
    public boolean getIsLastUsed() {
        return  mIsLastUsed;
    }
    public void setIsLastUsed(boolean isLastUsed) {
        this.mIsLastUsed = isLastUsed;
    }
}
