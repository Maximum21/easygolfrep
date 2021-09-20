package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.models.Course;
import com.minhhop.easygolf.listeners.ItemDataSearchListener;

public class DataSearchAdapter extends BaseRecyclerViewAdapter<Course> {
    private ItemDataSearchListener mItemDataSearchListener;

    public DataSearchAdapter(Context context,ItemDataSearchListener itemDataSearchListener) {
        super(context);
        this.mItemDataSearchListener = itemDataSearchListener;
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_search;
    }

    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof DataSearchHolder){
            DataSearchHolder target = (DataSearchHolder) holder;
            target.mTxtName.setText(mDataList.get(position).getName());
        }
    }

    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new DataSearchHolder(viewRoot);
    }

    private class DataSearchHolder extends CoreRecyclerViewHolder{
        private TextView mTxtName;

        private DataSearchHolder(View itemView) {
            super(itemView);
            mTxtName = itemView.findViewById(R.id.txtName);
            itemView.setOnClickListener(v -> mItemDataSearchListener.onClick(mDataList.get(getAdapterPosition())));
        }
    }
}
