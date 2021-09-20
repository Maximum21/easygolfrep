package com.minhhop.easygolf.core;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by woz on 3/25/18.
 */

public abstract class CoreRecyclerViewAdapter<T> extends RecyclerView.Adapter<CoreRecyclerViewHolder>{

    protected ArrayList<T> mDataList;
    private Context mContext;

    public CoreRecyclerViewAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }



    public void addListItem(List<T> itemList){
        mDataList.addAll(itemList);
        this.notifyDataSetChanged();
    }

    public void addItem(T item){
        mDataList.add(item);
        this.notifyItemInserted(mDataList.size());
    }

    public void addItem(T item,int position){
        mDataList.add(position,item);
        this.notifyItemInserted(position);
    }

    public void resetData(){
        mDataList = new ArrayList<>();
        this.notifyDataSetChanged();
    }


    public void clearAll(){
        if(mDataList != null) {
            mDataList.clear();
            this.notifyDataSetChanged();
        }
    }


    public void removeItem(int position){
        if(mDataList != null) {
           if(position < mDataList.size() && position >= 0){
               mDataList.remove(position);
               this.notifyItemRemoved(position);
           }
        }
    }

    public void setDataList(List<T> itemList){
        List<T> clone = new ArrayList<>(itemList);
        if(mDataList != null) {
            mDataList.clear();
        }else {
            mDataList = new ArrayList<>();
        }
        mDataList.addAll(clone);
        this.notifyDataSetChanged();

    }



    public List<T> getData(){
        return mDataList;
    }


    protected Context getContext(){
        return mContext;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


}
