package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoPager {

    @SerializedName("paginator")
    @Expose
    private Paginator paginator;

    @SerializedName("items")
    @Expose
    private List<Video> videos;

    public Paginator getPaginator() {
        return paginator;
    }

    public List<Video> getVideos() {
        return videos;
    }
}