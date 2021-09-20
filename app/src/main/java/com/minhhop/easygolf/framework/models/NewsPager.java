package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsPager {

    @SerializedName("paginator")
    @Expose
    private Paginator paginator;

    @SerializedName("items")
    @Expose
    private List<News> news;

    public Paginator getPaginator() {
        return paginator;
    }

    public List<News> getNews() {
        return news;
    }
}