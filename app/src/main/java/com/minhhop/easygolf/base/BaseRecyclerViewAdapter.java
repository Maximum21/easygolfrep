package com.minhhop.easygolf.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreRecyclerViewAdapter;
import com.minhhop.easygolf.core.CoreRecyclerViewHolder;
import com.minhhop.easygolf.framework.base.EndlessRecyclerViewScrollListener;
import com.minhhop.easygolf.listeners.EventLoadMore;

/**
 * Created by woz copyright thanhqui on 4/5/18.
 */

public abstract class BaseRecyclerViewAdapter<T> extends CoreRecyclerViewAdapter<T> {

    protected static final int TYPE_PROGRESS_LOAD_MORE = 0x0001;
    protected static final int TYPE_ITEM = 0x0002;
    protected boolean isRegisterLoadMore;

    protected boolean mIsReachEnd = false;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected EventLoadMore mEventLoadMore;
    private EndlessRecyclerViewScrollListener mEndlessRecyclerViewScrollListener;

    public BaseRecyclerViewAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public CoreRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRoot;
        if(viewType == TYPE_PROGRESS_LOAD_MORE){
            viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_load_more,parent,false);
            return new BottomViewHolder(viewRoot);
        }
        viewRoot = LayoutInflater.from(getContext()).inflate(setLayout(viewType),parent,false);
        return setViewHolder(viewRoot,viewType);
    }



    @Override
    public int getItemViewType(int position) {
        if(position == getBottomItemPosition() && isRegisterLoadMore){
            return TYPE_PROGRESS_LOAD_MORE;
        }
        return getCustomItemViewType(position);
    }


    public void registerLoadMore(RecyclerView.LayoutManager layoutManager,
                                 RecyclerView recyclerView, EventLoadMore eventLoadMore){

        isRegisterLoadMore = true;
        this.mLayoutManager = layoutManager;

        this.mEventLoadMore = eventLoadMore;

        if(layoutManager instanceof GridLayoutManager){


            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (getItemViewType(position)){
                        case TYPE_PROGRESS_LOAD_MORE:
                            return ((GridLayoutManager) mLayoutManager).getSpanCount();
                        case TYPE_ITEM:
                            return 1;
                    }
                    return 0;
                }
            });
        }

        this.mEndlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                if(!mIsReachEnd) {
                    mEventLoadMore.onLoadMore();
                }
            }
        };

        recyclerView.addOnScrollListener(this.mEndlessRecyclerViewScrollListener);

    }


/**
     if need to use adapter delegate custom override this in child ->
*/

    protected int getCustomItemViewType(int position){
        return TYPE_ITEM;
    }

    protected abstract int setLayout(int viewType);
    protected abstract CoreRecyclerViewHolder setViewHolder(View viewRoot, int viewType);


    public void onReachEnd(){
        mIsReachEnd = true;
        notifyItemChanged(getBottomItemPosition());
    }

    public void openReachEnd(){
        mEndlessRecyclerViewScrollListener.resetState();
        mIsReachEnd = false;
        notifyItemChanged(getBottomItemPosition());
    }


    @Override
    public void onBindViewHolder(@NonNull CoreRecyclerViewHolder holder, int position) {
        if(holder instanceof BottomViewHolder){
            ((BottomViewHolder) holder).progress.setVisibility(
                    mIsReachEnd ? View.GONE : View.VISIBLE
            );
            if(!mIsReachEnd){
                initLoading();
            }
        }
    }

    protected void initLoading(){

    }

    @Override
    public int getItemCount() {
        return isRegisterLoadMore?(super.getItemCount() + 1):super.getItemCount();
    }

    protected int getBottomItemPosition() {
        return getItemCount() - 1;
    }

    private static class BottomViewHolder extends CoreRecyclerViewHolder{
        private View progress;

        private BottomViewHolder(View itemView) {
            super(itemView);

            progress = itemView.findViewById(R.id.progress);
        }
    }


}
