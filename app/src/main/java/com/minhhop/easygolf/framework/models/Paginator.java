package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Paginator implements Serializable {

    @SerializedName("start")
    @Expose
    private int start;

    @SerializedName("total")
    @Expose
    private int total;

    public int getStart() {
        return start;
    }

    public int getTotal() {
        return total;
    }
}
