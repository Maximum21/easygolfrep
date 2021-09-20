package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoundPager {
    @SerializedName("paginator")
    @Expose
    private Paginator paginator;

    @SerializedName("items")
    @Expose
    private List<RoundMatch> rounds;

    public Paginator getPaginator() {
        return paginator;
    }

    public List<RoundMatch> getRounds() {
        return rounds;
    }
}
