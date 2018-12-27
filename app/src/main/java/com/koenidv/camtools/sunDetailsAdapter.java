package com.koenidv.camtools;

/* Well this doesn't quite work yet

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import pl.hypeapp.materialtimelineview.MaterialTimelineView;

//  Created by koenidv on 27.05.2018.

public class sunDetailsAdapter extends RecyclerView.Adapter<sunDetailsAdapter.ViewHolder> {
    private List<sunDetail> mDataset;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTime, mName, mDescription;
        MaterialTimelineView mTimelineView;

        ViewHolder(View view) {
            super(view);
            mTime = view.findViewById(R.id.astroTwiTime);
            mName = view.findViewById(R.id.astroTwiName);
            mDescription = view.findViewById(R.id.astroTwiDescription);
            mTimelineView = view.findViewById(R.id.sunDetailsTimelineCard);
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
        if (mSunDetail.getIsLine()) {
            holder.mTimelineView.setTimelineType(MaterialTimelineView.Companion.getTIMELINE_TYPE_LINE());
            holder.mName.setVisibility(View.GONE);
            holder.mDescription.setVisibility(View.GONE);
            holder.mTime.setVisibility(View.GONE);
            holder.mTimelineView.setMinHeight(28);
            holder.setIsRecyclable(false);
        }
        holder.mTime.setText(mSunDetail.getTime());
        holder.mTime.setTextColor(mSunDetail.getTextColor());
        holder.mName.setText(mSunDetail.getName());
        holder.mName.setTextColor(mSunDetail.getTextColor());
        holder.mDescription.setText(Html.fromHtml(mSunDetail.getDescription()));
        holder.mDescription.setTextColor(mSunDetail.getTextColor());
        holder.mTimelineView.setBackgroundColor(mSunDetail.getColor());
        if (position == 0) {
            holder.mTimelineView.setPosition(MaterialTimelineView.Companion.getPOSITION_FIRST()); //First position
        } else if (position == mDataset.size() - 1) {
            holder.mTimelineView.setPosition(MaterialTimelineView.Companion.getPOSITION_LAST()); //Last position
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
*/
//}