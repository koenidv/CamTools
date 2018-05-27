package com.koenidv.camtools;
//  Created by koenidv on 27.05.2018.

public class sunDetail {
    private String mTime, mName, mDescription;
    private int mColor;

    public sunDetail() {
    }

    sunDetail(String time, String name, String description, int color) {
        this.mTime = time;
        this.mName = name;
        this.mDescription = description;
        this.mColor = color;
    }

    public String getTime() {
        return mTime;
    }
    public void setTime(String mTime) {
        this.mTime = mTime;
    }
    public String getName() {
        return mName;
    }
    public void setName(String mName) {
        this.mName = mName;
    }
    String getDescription() {
        return mDescription;
    }
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
    public int getColor() {
        return mColor;
    }
    public void setColor(int mColor) {
        this.mColor = mColor;
    }

}
