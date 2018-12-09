package com.koenidv.camtools;

//  Created by koenidv on 06.12.2018.
public class camera {
    private String mName;
    private int mResolutionX, mResolutionY;
    private float mSensorSizeX, mSensorSizeY, mCropfactor, mPixelpitch, mConfusion;

    camera(String mName, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY, float mCropfactor, float mPixelpitch, float mConfusion) {
        this.mName = mName;
        this.mResolutionX = mResolutionX;
        this.mResolutionY = mResolutionY;
        this.mSensorSizeX = mSensorSizeX;
        this.mSensorSizeY = mSensorSizeY;
        this.mCropfactor = mCropfactor;
        this.mPixelpitch = mPixelpitch;
        this.mConfusion = mConfusion;
    }

    camera(String mName, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY, float mConfusion) {
        float cropfactor = (float) (43.27 / Math.sqrt(mSensorSizeX * mSensorSizeX + mSensorSizeY * mSensorSizeY));
        float pixelpitch = mSensorSizeX / mResolutionX * 1000;

        this.mName = mName;
        this.mResolutionX = mResolutionX;
        this.mResolutionY = mResolutionY;
        this.mSensorSizeX = mSensorSizeX;
        this.mSensorSizeY = mSensorSizeY;
        this.mCropfactor = cropfactor;
        this.mPixelpitch = pixelpitch;
        this.mConfusion = mConfusion;
    }

    /**
     * @param mName The name
     * @param mResolution Maximum resolution, should be formatted as "x:y"
     * @param mSize Sensor size, should be formatted as "x:y"
     * @param mConfusion The circle of confusion
     */
    camera(String mName, String mResolution, String mSize, String mConfusion) {
        int resX = Integer.valueOf(mResolution.substring(0, mResolution.indexOf(":")));
        int resY = Integer.valueOf(mResolution.substring(mResolution.indexOf(":") + 1));
        float sizeX = Float.valueOf(mSize.substring(0, mSize.indexOf(":")));
        float sizeY = Float.valueOf(mSize.substring(mSize.indexOf(":") + 1));
        float cropfactor = (float) (43.27 / Math.sqrt(sizeX * sizeX + sizeY * sizeY));
        float pixelpitch = sizeX / resX * 1000;

        this.mName = mName;
        this.mResolutionX = resX;
        this.mResolutionY = resY;
        this.mSensorSizeX = sizeX;
        this.mSensorSizeY = sizeY;
        mCropfactor = cropfactor;
        mPixelpitch = pixelpitch;
        this.mConfusion = Float.valueOf(mConfusion);
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }

    int getResolutionX() {
        return mResolutionX;
    }
    public void setResolutionX(int resolution) {
        this.mResolutionX = resolution;
    }
    int getResolutionY() {
        return mResolutionY;
    }
    public void setResolutionY(int resolution) {
        this.mResolutionY = resolution;
    }

    float getSensorSizeX() {
        return mSensorSizeX;
    }
    public void setSensorSizeX(float sensorsize) {
        this.mSensorSizeX = sensorsize;
    }
    float getSensorSizeY() {
        return mSensorSizeY;
    }
    public void setSensorSizeY(float sensorsize) {
        this.mSensorSizeY = sensorsize;
    }

    float getCropfactor() {
        return mCropfactor;
    }
    public void setCropfactor(float cropfactor) {
        this.mPixelpitch = cropfactor;
    }
    float getPixelpitch() {
        return mPixelpitch;
    }
    public void setPixelpitch(float pixelpitch) {
        this.mPixelpitch = pixelpitch;
    }
    float getConfusion() {
        return mConfusion;
    }
    public void setConfusion(float confusion) {
        this.mConfusion = confusion;
    }
}
