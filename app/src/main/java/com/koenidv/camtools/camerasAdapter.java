package com.koenidv.camtools;
//  Created by koenidv on 01.10.2018.

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class camerasAdapter extends RecyclerView.Adapter<camerasAdapter.ViewHolder> {
    private List<cameraCard> mDataset;

    static class ViewHolder  extends RecyclerView.ViewHolder {
        TextView mNameTextView, mSensorSizeTextView, mResolutionTextView, mPixelpitchTextView, mConfusionTextView;

        ViewHolder(View view) {
            super(view);
            mNameTextView = view.findViewById(R.id.nameTextView);
            mSensorSizeTextView = view.findViewById(R.id.sensorsizeTextView);
            mResolutionTextView = view.findViewById(R.id.resolutionTextView);
            mPixelpitchTextView = view.findViewById(R.id.pixelpitchTextView);
            mConfusionTextView = view.findViewById(R.id.confusionTextView);
        }

    }

    camerasAdapter(List<cameraCard> dataset) {
        mDataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cameraCard mCameraCard = mDataset.get(position);
        holder.mNameTextView.setText(mCameraCard.getName());
        holder.mSensorSizeTextView.setText(mCameraCard.getSensorSize());
        holder.mResolutionTextView.setText(mCameraCard.getResolution());
        holder.mPixelpitchTextView.setText(mCameraCard.getPixelpitch());
        holder.mConfusionTextView.setText(mCameraCard.getConfusion());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
