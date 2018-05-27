package com.koenidv.camtools;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import pl.hypeapp.materialtimelineview.MaterialTimelineView;

//  Created by koenidv on 27.05.2018.

public class sunDetailsAdapter extends RecyclerView.Adapter<sunDetailsAdapter.ViewHolder> {
    private List<sunDetail> mDataset;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTime, mName, mDescription;
        MaterialTimelineView mTimelineView;
        CardView mHolderCardView;

        ViewHolder(View view) {
            super(view);
            mTime = view.findViewById(R.id.sunDetailTime);
            mName = view.findViewById(R.id.sunDetailName);
            mDescription = view.findViewById(R.id.sunDetailDescription);
            mTimelineView = view.findViewById(R.id.sunDetailsTimelineCard);
            mHolderCardView = view.findViewById(R.id.sunDetailsHolderCardView);
        }
    }

    sunDetailsAdapter(List<sunDetail> detailsDataset) {
        mDataset = detailsDataset;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sun_details_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        sunDetail mSunDetail = mDataset.get(position);
        holder.mTime.setText(mSunDetail.getTime());
        holder.mName.setText(mSunDetail.getName());
        holder.mDescription.setText(Html.fromHtml(mSunDetail.getDescription()));
        holder.mTimelineView.setBackgroundColor(mSunDetail.getColor());
        //if (position == 0) {
        //    holder.mTimelineView.setPosition(0); //First position
        //} else if (position == mDataset.size() - 1) {
        //    holder.mTimelineView.setPosition(2); //Last position
        //}
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
