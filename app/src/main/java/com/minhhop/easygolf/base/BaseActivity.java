package com.minhhop.easygolf.base;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.core.CoreActivity;
import com.minhhop.easygolf.views.activities.SearchActivity;
/**
 * Created by pdypham on 4/3/18.
 */

public abstract class BaseActivity extends CoreActivity implements SwipeRefreshLayout.OnRefreshListener{


    private View layoutLoading;
    private SwipeRefreshLayout mViewRefresh;

//    private SwipeRefreshLayout layoutRefresh;

    protected enum STATUS{
        FIRST_LOADING,
        READY_LOADING,
        LOAD_MORE,
        REFRESH
    }

    protected STATUS statusLoading = STATUS.FIRST_LOADING;


    protected boolean onRegisterEventBus(){
        return false;
    }

    @Override
    protected void addView() {
        layoutLoading = findViewById(R.id.layoutLoading);

//
        if(layoutLoading != null){
            layoutLoading.setOnClickListener(this);
        }

        mViewRefresh = findViewById(R.id.viewRefresh);
        if(mViewRefresh != null){
            Resources dataResources = getResources();
            mViewRefresh.setColorSchemeColors(dataResources.getColor(R.color.colorAccent),dataResources.getColor(R.color.colorRed),
                    dataResources.getColor(R.color.colorBlack));

            mViewRefresh.setOnRefreshListener(this::refreshData);
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


    @Override
    protected int setIconToolbar() {
        return R.drawable.ic_icon_back_button;
    }


    @Override
    protected void loadData() {
        if(statusLoading == STATUS.FIRST_LOADING) {
            showLoading();
        }
    }

    protected void hideLoading(){

        if(layoutLoading != null){
            if(layoutLoading.getVisibility() == View.VISIBLE){
                layoutLoading.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.layoutLoading) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        DatabaseService.getInstance().closeRealm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(setMenuRes() != 0) {
            MenuInflater layoutInflater = getMenuInflater();
            layoutInflater.inflate(setMenuRes(), menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionSearch) {
            startActivity(new Intent(BaseActivity.this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected int setMenuRes(){
        return 0;
    }


    @Override
    public void onRefresh() {
        statusLoading = STATUS.REFRESH;
        loadData();
    }

    protected void markStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
    }
}
