package com.minhhop.easygolf.base;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.minhhop.easygolf.R;

@Deprecated
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private View mViewRoot;
    private View layoutLoading;
    protected SwipeRefreshLayout mViewRefresh;

    protected enum STATUS{
        FIRST_LOADING,
        LOAD_MORE,
        REFRESH
    }

    protected STATUS statusLoading = STATUS.FIRST_LOADING;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewRoot = inflater.inflate(setLayout(),container,false);

        layoutLoading = mViewRoot.findViewById(R.id.layoutLoading);

        if(layoutLoading != null) {
            layoutLoading.setOnClickListener(this);
        }

        initView(mViewRoot);
        mViewRefresh = mViewRoot.findViewById(R.id.viewRefresh);
        if(mViewRefresh != null){
            Resources dataResources = getResources();
            mViewRefresh.setColorSchemeColors(dataResources.getColor(R.color.colorAccent),dataResources.getColor(R.color.colorRed),
                    dataResources.getColor(R.color.colorBlack));

            mViewRefresh.setOnRefreshListener(this::refreshData);
        }

        loadData();
        return mViewRoot;
    }


    protected View getViewRoot(){
        return mViewRoot;
    }

    protected abstract void initView(View viewRoot);


    protected void loadData(){
        if(statusLoading == STATUS.FIRST_LOADING) {
            showLoading();
        }
    }


    protected void refreshData(){
        statusLoading = STATUS.REFRESH;
        loadData();
    }

    protected void showLoading(){

        if(layoutLoading != null){
            if(layoutLoading.getVisibility() != View.VISIBLE){
                layoutLoading.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void hideRefreshLoading(){

        if(mViewRefresh != null){
            mViewRefresh.setRefreshing(false);
        }

    }

    protected void hideLoading(){

        if(layoutLoading != null){
            if(layoutLoading.getVisibility() == View.VISIBLE){
                layoutLoading.setVisibility(View.GONE);
            }
        }

    }


    protected abstract int setLayout();


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.layoutLoading){

        }
    }

    protected void startActivity(Class cl, Bundle bundle){

        Intent intent = new Intent(getContext(),cl);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void startActivity(Class cl){
        startActivity(new Intent(getContext(),cl));
    }

    protected void startActivityForResult(Class cl, Bundle bundle, int requestCode){

        Intent intent = new Intent(getContext(),cl);
        intent.putExtras(bundle);

        startActivityForResult(intent,requestCode);
    }
}
