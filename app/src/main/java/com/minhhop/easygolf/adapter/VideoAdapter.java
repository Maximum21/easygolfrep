package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.Video;
import com.minhhop.easygolf.listeners.EventItemVideoListener;
import com.minhhop.easygolf.utils.AppUtil;

public class VideoAdapter extends BaseRecyclerViewAdapter<Video>{

    private EventItemVideoListener mEventItemVideoListener;
    private boolean mIsRecommended = false;


    public VideoAdapter(Context context,EventItemVideoListener eventItemVideoListener) {
        super(context);
        this.mEventItemVideoListener = eventItemVideoListener;
    }

    public VideoAdapter(Context context,boolean isRecommended,EventItemVideoListener eventItemVideoListener) {
        super(context);
        this.mEventItemVideoListener = eventItemVideoListener;
        this.mIsRecommended = isRecommended;
    }





    @Override
    protected int setLayout(int viewType) {
        return !mIsRecommended ? R.layout.item_video: R.layout.item_video_recommended;
    }

    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new VideoHolder(viewRoot);
    }


    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof VideoHolder){
            VideoHolder target = (VideoHolder) holder;
            Video e = mDataList.get(position);

//            Picasso.get().load(e.getPhoto()).into(target.mImgView);
            Glide.with(getContext()).load(e.getPhoto()).into(target.mImgView);

            target.mTxtName.setText(e.getName());
            target.mTxtDate.setText(AppUtil.fromISO8601UTC(e.getDate()));
            target.mTxtTag.setText(AppUtil.formatTagVideo(e.getTags()));
        }
    }

    private class VideoHolder extends CoreRecyclerViewHolder{

        private ImageView mImgView;
        private TextView mTxtName;
        private TextView mTxtDate;
        private TextView mTxtTag;

        private VideoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> mEventItemVideoListener.onClick(mDataList.get(getAdapterPosition())));

            mImgView = itemView.findViewById(R.id.imgView);
            mTxtName = itemView.findViewById(R.id.txtName);
            mTxtDate = itemView.findViewById(R.id.txtDate);
            mTxtTag = itemView.findViewById(R.id.txtTag);
        }
    }
}
