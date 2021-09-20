package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataLocation extends DataLocationMissing implements Serializable {

    @SerializedName("tee")
    @Expose
    private String tee;

    public String getTee() {
        return tee;
    }

    public void setTee(String tee) {
        this.tee = tee;
    }
}
