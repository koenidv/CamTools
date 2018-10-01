package com.koenidv.camtools;
//  Created by koenidv on 01.10.2018.

public class cameraCard {
    private String mName, mSensorSize, mResolution, mPixelpitch, mConfusion;

    public cameraCard() {
    }

    cameraCard(String mName, String mSensorSize, String mResolution, String mPixelpitch, String mConfusion) {
        this.mName = mName;
        this.mSensorSize = mSensorSize;
        this.mResolution = mResolution;
        this.mPixelpitch = mPixelpitch;
        this.mConfusion = mConfusion;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    public String getSensorSize() {
        return mSensorSize;
    }
    public void setSensorSize(String sensorsize) {
        this.mSensorSize = sensorsize;
    }
    public String getResolution() {
        return mResolution;
    }
    public void setResolution(String resolution) {
        this.mResolution = resolution;
    }
    public String getPixelpitch() {
        return mPixelpitch;
    }
    public void setPixelpitch(String pixelpitch) {
        this.mPixelpitch = pixelpitch;
    }
    public String getConfusion() {
        return mConfusion;
    }
    public void setConfusion(String confusion) {
        this.mConfusion = confusion;
    }
}
