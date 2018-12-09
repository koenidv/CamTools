package com.koenidv.camtools;
//  Created by koenidv on 01.10.2018.

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class camerasAdapter extends RecyclerView.Adapter<camerasAdapter.ViewHolder> {
    private List<cameraCard> mDataset;

    static class ViewHolder  extends RecyclerView.ViewHolder {
        CardView mCardView;
        TextView mNameTextView, mInfoTextView;

        ViewHolder(View view) {
            super(view);
            mCardView = view.findViewById(R.id.cameraCardView);
            mNameTextView = view.findViewById(R.id.nameTextView);
            mInfoTextView = view.findViewById(R.id.infoTextView);
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
        holder.mInfoTextView.setText(mCameraCard.getInfo());
        if (mCameraCard.getIsLastUsed()) {
            holder.mNameTextView.setTextColor(holder.mNameTextView.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            holder.mNameTextView.setTextColor(holder.mNameTextView.getContext().getResources().getColor(R.color.textColor_normal));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public cameraCard getItem(int position) {
        return mDataset.get(position);
    }
}
