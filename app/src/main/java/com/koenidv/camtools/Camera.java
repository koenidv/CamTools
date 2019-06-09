package com.koenidv.camtools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

//  Created by koenidv on 06.12.2018.
public class Camera {
    private String mName;
    private int iconId = R.drawable.camera_photo, mResolutionX, mResolutionY;
    private float mSensorSizeX, mSensorSizeY, mCropfactor, mPixelpitch, mConfusion;
    private URL mImage = null;

    Camera() {
    }

    Camera(String mName, int mIconId, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY, float mConfusion) {
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

    Camera(String mName, int mIconId, int mResolutionX, int mResolutionY, float mSensorSizeX, float mSensorSizeY) {
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
        this.mConfusion = Math.round((float) Math.sqrt(mSensorSizeX * mSensorSizeX + mSensorSizeY * mSensorSizeY) / 1.5) / 1000f;
    }

    /**
     * @param mName       The name
     * @param mIconId     Resource id of the Camera's icon
     * @param mResolution Maximum resolution, should be formatted as "x:y"
     * @param mSize       Sensor size, should be formatted as "x:y"
     * @param mImageUrl   Image to be displayed in the details, has to be a url
     */
    Camera(String mName, int mIconId, String mResolution, String mSize, String mImageUrl) {
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
        this.mCropfactor = cropfactor;
        this.mPixelpitch = pixelpitch;
        this.mConfusion = Math.round((float) Math.sqrt(mSensorSizeX * mSensorSizeX + mSensorSizeY * mSensorSizeY) / 1.5) / 1000f;
        if (mImageUrl != null) {
            try {
                this.mImage = new URL(URLDecoder.decode(mImageUrl, "UTF-8"));
            } catch (NullPointerException | MalformedURLException | UnsupportedEncodingException mE) {
                mE.printStackTrace();
            }
        }
    }

    /**
     * Same as above but without image.
     * Adding images by the user is unsupported anyways as of June '19.
     *
     * @param mName       The name
     * @param mIconId     Resource id of the Camera's icon
     * @param mResolution Maximum resolution, should be formatted as "x:y"
     * @param mSize       Sensor size, should be formatted as "x:y"
     */
    Camera(String mName, int mIconId, String mResolution, String mSize) {
        new Camera(mName, mIconId, mResolution, mSize, null);
    }

    public String getName() {
        return mName;
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

    public void setImageUrl(String mImageUrl) {
        try {
            this.mImage = new URL(mImageUrl);
        } catch (MalformedURLException mE) {
            mE.printStackTrace();
        }
    }

    URL getImageUrl() {
        return mImage;
    }

    static class loadImagesTask extends AsyncTask<URL, Integer, Bitmap> {
        ImageView view;
        ProgressBar progressBar;
        Context context;

        loadImagesTask(ImageView mView, ProgressBar mProgressBar, Context mContext) {
            view = mView;
            progressBar = mProgressBar;
            context = mContext;
        }

        @Override
        protected Bitmap doInBackground(URL... mURLS) {
            for (URL mURL : mURLS) {
                try {
                    return BitmapFactory.decodeStream(mURL.openConnection().getInputStream());
                } catch (IOException mE) {
                    mE.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap mBitmap) {
            view.setImageBitmap(mBitmap);
            progressBar.setVisibility(View.GONE);
        }
    }
}
