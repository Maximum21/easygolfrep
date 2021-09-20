package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataLocationMissing implements Serializable {


    @SerializedName("hdc")
    @Expose
    private double hdc;

    @SerializedName("hcp")
    @Expose
    private double hcp;

    @SerializedName("unit")
    @Expose
    private String unit;

    @SerializedName("latitude")
    @Expose
    protected double latitude;

    @SerializedName("longitude")
    @Expose
    protected double longitude;


    public void setHdc(double hdc) {
        this.hdc = hdc;
    }

    public void setHcp(double hcp) {
        this.hcp = hcp;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public double getHdc() {
        return hdc;
    }

    public double getHcp() {
        return hcp;
    }

    public String getUnit() {
        return unit;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
