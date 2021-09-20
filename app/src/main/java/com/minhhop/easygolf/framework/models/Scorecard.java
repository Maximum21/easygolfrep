package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Scorecard {

    @SerializedName("scorecard")
    @Expose
    private List<Scorecard> scorecards;

    @SerializedName("index")
    @Expose
    private int index;

    @SerializedName("par")
    @Expose
    private int par;

    @SerializedName("score")
    @Expose
    private int score;

    @SerializedName("net")
    @Expose
    private int net;

    @SerializedName("putts")
    @Expose
    private int putts;

    @SerializedName("hole")
    @Expose
    private int hole;

    @SerializedName("color")
    @Expose
    private String color;

    public List<Scorecard> getScorecards() {
        return scorecards;
    }

    public int getIndex() {
        return index;
    }

    public int getPar() {
        return par;
    }

    public int getScore() {
        return score;
    }

    public int getNet() {
        return net;
    }

    public int getPutts() {
        return putts;
    }

    public int getHole() {
        return hole;
    }

    public String getColor() {
        return color;
    }
}
