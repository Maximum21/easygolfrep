package com.minhhop.easygolf.views.activities;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.adapter.SearchBookingAdapter;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.models.PointMap;
import com.minhhop.easygolf.services.ApiService;
import com.minhhop.easygolf.framework.common.Contains;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SearchBookingActivity extends WozBaseActivity {

    private SearchBookingAdapter mSearchBookingAdapter;

    private long mDate;
    private PointMap mPointMap;

    private int mStart = 0;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_search_booking;
    }

    @Override
    protected void initView() {

        Intent mIntent = getIntent();
        mDate = mIntent.getLongExtra(Contains.EXTRA_VALUE_DATE,-1);
        mPointMap = (PointMap) mIntent.getSerializableExtra(Contains.EXTRA_VALUE_PLACE);
        if(mPointMap == null){
            mPointMap = new PointMap();
        }

        RecyclerView listBook = findViewById(R.id.listBook);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listBook.setLayoutManager(layoutManager);
        mSearchBookingAdapter = new SearchBookingAdapter(this);


        listBook.setAdapter(mSearchBookingAdapter);
        mSearchBookingAdapter.registerLoadMore(layoutManager, listBook, () -> {
            if(mStart > 0) {
                loadData();
            }
        });


    }


    @Override
    protected void loadData() {
        showLoading();
        ApiService.Companion.getInstance().getGolfService().findTeeTimes(mPointMap.getLatitude(),mPointMap.getLongitude(),mDate,mStart)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if(getMStatusLoading() == STATUS.FIRST_LOAD || getMStatusLoading() == STATUS.REFRESH){
                        mSearchBookingAdapter.setDataList(result.getCourses());
                        setMStatusLoading(STATUS.LOAD_MORE);
                        hideRefreshLoading();
                    }else {
                        mSearchBookingAdapter.addListItem(result.getCourses());
                    }

                    mStart = result.getPaginator().getStart();
                    if (mStart < 0) {
                        mSearchBookingAdapter.onReachEnd();
                    }
                    hideLoading();

                }, throwable -> {
                    // show back button
                    hideLoading();
                });
    }


    @Override
    public void refreshData() {
        mStart = 0;
        mSearchBookingAdapter.openReachEnd();
        setMStatusLoading(STATUS.REFRESH);
        loadData();
    }

}
