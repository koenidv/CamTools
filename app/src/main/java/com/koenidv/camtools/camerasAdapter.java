package com.koenidv.camtools;
//  Created by koenidv on 01.10.2018.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class camerasAdapter extends RecyclerView.Adapter<camerasAdapter.ViewHolder> {
    private List<Camera> mDataset;

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

    camerasAdapter(List<Camera> dataset) {
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
        Camera thisCamera = mDataset.get(position);
        Context context = holder.mCardView.getContext();

        String info = String.valueOf(Math.round(thisCamera.getResolutionX() * thisCamera.getResolutionY() / (float) 1000000))
                + context.getString(R.string.megapixel)
                + context.getString(R.string.resolution_size_seperator)
                + ModuleManager.truncateNumber(thisCamera.getSensorSizeX()) + "x" + ModuleManager.truncateNumber(thisCamera.getSensorSizeY())
                + context.getString(R.string.millimeter);
        boolean isLastUsed = (position == context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getInt("cameras_last", -1));

        holder.mNameTextView.setText(thisCamera.getName());
        // Display the camera's icon in the overview.
        // Disabled because it's confusing
        //holder.mNameTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(thisCamera.getIcon(), 0, 0, 0);
        holder.mInfoTextView.setText(info);

        if (isLastUsed) {
            holder.mNameTextView.setTextColor(holder.mNameTextView.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            holder.mNameTextView.setTextColor(holder.mNameTextView.getContext().getResources().getColor(R.color.textColor_normal));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
