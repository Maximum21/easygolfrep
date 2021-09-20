package com.minhhop.easygolf.views.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minhhop.easygolf.R;
import com.minhhop.easygolf.adapter.VideoAdapter;
import com.minhhop.easygolf.base.fragment.WozBaseFragment;
import com.minhhop.easygolf.framework.models.Video;
import com.minhhop.easygolf.listeners.EventItemVideoListener;
import com.minhhop.easygolf.listeners.EventLoadMore;
import com.minhhop.easygolf.services.ApiService;
import com.minhhop.easygolf.framework.common.Contains;

import org.jetbrains.annotations.NotNull;


public class VideoRecentFragment extends WozBaseFragment implements EventLoadMore,EventItemVideoListener{

    private VideoAdapter mVideoAdapter;
    private int start = 0;

    @Override
    public void initView(@NotNull View viewRoot) {
        RecyclerView listVideo = viewRoot.findViewById(R.id.listVideo);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listVideo.setLayoutManager(linearLayoutManager);
        mVideoAdapter = new VideoAdapter(getContext(),this);
        mVideoAdapter.registerLoadMore(linearLayoutManager,listVideo,this);
        listVideo.setAdapter(mVideoAdapter);

    }

    @Override
    public int setLayout() {
        return R.layout.fragment_recent_video;
    }


    @Override
    public void loadData() {

        registerResponse(ApiService.Companion.getInstance().getGolfService().getVideoPager(start), result -> {
            if(getMState() == STATE.FIRST_LOAD || getMState() == STATE.REFRESH){
                mVideoAdapter.setDataList(result.getVideos());
                setMState(STATE.LOAD_MORE);
                hideLayoutRefresh();
            }else {
                mVideoAdapter.addListItem(result.getVideos());
            }

            start = result.getPaginator().getStart();
            if(start < 0){
                mVideoAdapter.onReachEnd();
            }
        });

    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    @Override
    public void refreshData() {
        start = 0;
        super.refreshData();
    }

    @Override
    public void onClick(Video item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contains.EXTRA_VIDEO,item);
//        startActivity(VideoDetailActivity.class,bundle);
    }


}
