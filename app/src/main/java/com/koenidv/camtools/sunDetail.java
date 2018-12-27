package com.koenidv.camtools;
//  Created by koenidv on 27.05.2018.

import android.graphics.Color;

class sunDetail {
    private boolean mIsLine;
    private String mTime, mName, mDescription;
    private int mColor, mTextColor;

    public sunDetail() {
        this.mIsLine = true;
        this.mTime = "";
        this.mName = "";
        this.mDescription = "";
        this.mColor = Color.TRANSPARENT;
    }

    sunDetail(String time, String name, String description, int color) {
        this.mTime = time;
        this.mName = name;
        this.mDescription = description;
        this.mColor = color;
        this.mTextColor = Color.WHITE;
    }

    sunDetail(String time, String name, String description, int color, int textColor) {
        this.mTime = time;
        this.mName = name;
        this.mDescription = description;
        this.mColor = color;
        this.mTextColor = textColor;
    }

    boolean getIsLine() {
        return mIsLine;
    }

    public String getTime() {
        return mTime;
    }

    public String getName() {
        return mName;
    }

    String getDescription() {
        return mDescription;
    }

    public int getColor() {
        return mColor;
    }

    int getTextColor() {
        return  mTextColor;
    }

}
