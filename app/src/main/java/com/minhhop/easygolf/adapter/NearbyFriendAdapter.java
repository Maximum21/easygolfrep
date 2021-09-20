package com.minhhop.easygolf.adapter;

import android.content.Context;
import android.view.View;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;

public class NearbyFriendAdapter extends BaseRecyclerViewAdapter<String> {

    public NearbyFriendAdapter(Context context) {
        super(context);
    }

    @Override
    protected int setLayout(int viewType) {
        return R.layout.item_nearby_friend;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    protected CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType) {
        return new NearbyFriendHolder(viewRoot);
    }

    private class NearbyFriendHolder extends CoreRecyclerViewHolder{

        private NearbyFriendHolder(View itemView) {
            super(itemView);
        }
    }
}
