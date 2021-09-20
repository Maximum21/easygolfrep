package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.News;
import com.minhhop.easygolf.listeners.EventItemNewsListener;
import com.minhhop.easygolf.utils.AppUtil;
import com.squareup.picasso.Picasso;

public class NewsAdapter extends BaseRecyclerViewAdapter<News>{

    private EventItemNewsListener mEventItemNewsListener;

    public NewsAdapter(Context context, EventItemNewsListener eventItemNewsListener) {
        super(context);
        this.mEventItemNewsListener = eventItemNewsListener;
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_news;
    }



    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new NewsHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof NewsHolder){
            NewsHolder target = (NewsHolder) holder;
            News e = mDataList.get(position);
            Picasso.get().load(e.getPhoto())
                    .placeholder(R.drawable.img_holder_golf_radius)
                    .error(R.drawable.img_holder_golf_radius)
                    .into(target.mImgView);
//            Picasso.get().load(e.getPhoto()).into(target.mImgView);
            target.mTxtTitle.setText(e.getTitle());
            target.mTxtContent.setText(Html.fromHtml(e.getContent()));
            target.mTxtAuthor.setText(e.getAuthor());
            target.mTxtDate.setText(AppUtil.fromISO8601UTC(e.getDate(),"MMMM dd,yyyy"));
        }
    }

    private class NewsHolder extends CoreRecyclerViewHolder{

        private TextView mTxtTitle;
        private TextView mTxtContent;
        private TextView mTxtAuthor;
        private TextView mTxtDate;
        private ImageView mImgView;

        private NewsHolder(View itemView) {
            super(itemView);

            mTxtTitle = itemView.findViewById(R.id.txtTitle);
            mTxtContent = itemView.findViewById(R.id.txtContent);
            mTxtAuthor = itemView.findViewById(R.id.txtAuthor);
            mTxtDate = itemView.findViewById(R.id.txtDate);
            mImgView = itemView.findViewById(R.id.imgView);


            itemView.setOnClickListener(v -> mEventItemNewsListener.onClick(mDataList.get(getAdapterPosition())));
        }
    }
}
