package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.Course;
import com.minhhop.easygolf.services.image.RoundedTransformationBuilder;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.views.activities.WebViewActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class SearchBookingAdapter extends BaseRecyclerViewAdapter<Course> {


    public SearchBookingAdapter(Context context) {
        super(context);
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_search_booking;
    }

    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new SearchBookingHolder(viewRoot);
    }




    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof SearchBookingHolder){


            Course eData = mDataList.get(position);

            SearchBookingHolder target = (SearchBookingHolder) holder;
            target.viewDiver.setVisibility(position == 0 ? View.GONE: View.VISIBLE);


            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(15)
                    .oval(false)
                    .build();
            Picasso.get().load(eData.getImage())
                    .fit()
                    .placeholder(R.drawable.img_holder_golf_radius)
                    .error(R.drawable.img_holder_golf_radius)
                    .transform(transformation)
                    .into(target.imgCourse);

            target.txtName.setText(eData.getName());
            target.txtAddress.setText(eData.getAddress());

            target.btBook.setOnClickListener(v -> {
                String url = mDataList.get(position).getWebsite();
                if(!TextUtils.isEmpty(url)){
                    Bundle bundleWeb = new Bundle();
                    bundleWeb.putString(Contains.EXTRA_URL_WEB_VIEW,url);
                    Intent intent = new Intent(getContext(),WebViewActivity.class);
                    intent.putExtras(bundleWeb);
                    getContext().startActivity(intent);
                }
            });

        }
    }


    private class SearchBookingHolder extends CoreRecyclerViewHolder{
        private View viewDiver;
        private ImageView imgCourse;
        private TextView txtName;
        private TextView txtAddress;
        private View btBook;

        private SearchBookingHolder(View itemView) {
            super(itemView);
            viewDiver = itemView.findViewById(R.id.viewDiver);
            imgCourse = itemView.findViewById(R.id.imgCourse);

            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.txtAddress);

            btBook = itemView.findViewById(R.id.btBook);

        }
    }
}
