package com.koenidv.camtools;

//  Created by koenidv on 06.12.2018.
public class camera {
    private String mName;
    private int iconId = R.drawable.camera_photo, mResolutionX, mResolutionY;
    private float mSensorSizeX, mSensorSizeY, mCropfactor, mPixelpitch, mConfusion;

    camera() {
    }

    camera(String mName, int mIconId, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY, float mCropfactor, float mPixelpitch, float mConfusion) {
        this.mName = mName;
        this.iconId = mIconId;
        this.mResolutionX = mResolutionX;
        this.mResolutionY = mResolutionY;
        this.mSensorSizeX = mSensorSizeX;
        this.mSensorSizeY = mSensorSizeY;
        this.mCropfactor = mCropfactor;
        this.mPixelpitch = mPixelpitch;
        this.mConfusion = mConfusion;
    }

    camera(String mName, int mIconId, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY, float mConfusion) {
        float cropfactor = (float) (43.27 / Math.sqrt(mSensorSizeX * mSensorSizeX + mSensorSizeY * mSensorSizeY));
        float pixelpitch = mSensorSizeX / mResolutionX * 1000;

        this.mName = mName;
        this.iconId = mIconId;
        this.mResolutionX = mResolutionX;
        this.mResolutionY = mResolutionY;
        this.mSensorSizeX = mSensorSizeX;
        this.mSensorSizeY = mSensorSizeY;
        this.mCropfactor = cropfactor;
        this.mPixelpitch = pixelpitch;
        this.mConfusion = mConfusion;
    }

    /**
     * @param mName       The name
     * @param mResolution Maximum resolution, should be formatted as "x:y"
     * @param mSize       Sensor size, should be formatted as "x:y"
     * @param mConfusion  The circle of confusion
     */
    @Deprecated
    camera(String mName, String mResolution, String mSize, String mConfusion) {
        int resX = Integer.valueOf(mResolution.substring(0, mResolution.indexOf(":")));
        int resY = Integer.valueOf(mResolution.substring(mResolution.indexOf(":") + 1));
        float sizeX = Float.valueOf(mSize.substring(0, mSize.indexOf(":")));
        float sizeY = Float.valueOf(mSize.substring(mSize.indexOf(":") + 1));
        float cropfactor = (float) (43.27 / Math.sqrt(sizeX * sizeX + sizeY * sizeY));
        float pixelpitch = sizeX / resX * 1000;

        this.mName = mName;
        this.iconId = R.drawable.camera_photo;
        this.mResolutionX = resX;
        this.mResolutionY = resY;
        this.mSensorSizeX = sizeX;
        this.mSensorSizeY = sizeY;
        mCropfactor = cropfactor;
        mPixelpitch = pixelpitch;
        this.mConfusion = Float.valueOf(mConfusion);
    }

    /**
     * @param mName       The name
     * @param mIconId     Resource id of the camera's icon
     * @param mResolution Maximum resolution, should be formatted as "x:y"
     * @param mSize       Sensor size, should be formatted as "x:y"
     * @param mConfusion  The circle of confusion
     */
    camera(String mName, int mIconId, String mResolution, String mSize, String mConfusion) {
        int resX = Integer.valueOf(mResolution.substring(0, mResolution.indexOf(":")));
        int resY = Integer.valueOf(mResolution.substring(mResolution.indexOf(":") + 1));
        float sizeX = Float.valueOf(mSize.substring(0, mSize.indexOf(":")));
        float sizeY = Float.valueOf(mSize.substring(mSize.indexOf(":") + 1));
        float cropfactor = (float) (43.27 / Math.sqrt(sizeX * sizeX + sizeY * sizeY));
        float pixelpitch = sizeX / resX * 1000;

        this.mName = mName;
        this.iconId = mIconId;
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

    public int getIcon() {
        return iconId;
    }

    public void setIcon(int iconId) {
        this.iconId = iconId;
    }

    int getResolutionX() {
        return mResolutionX;
    }

    int getResolutionY() {
        return mResolutionY;
    }

    float getSensorSizeX() {
        return mSensorSizeX;
    }

    float getSensorSizeY() {
        return mSensorSizeY;
    }

    float getCropfactor() {
        return mCropfactor;
    }

    float getPixelpitch() {
        return mPixelpitch;
    }

    float getConfusion() {
        return mConfusion;
    }
}
