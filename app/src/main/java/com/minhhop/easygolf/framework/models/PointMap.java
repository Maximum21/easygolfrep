package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.minhhop.easygolf.utils.AppUtil;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PointMap extends RealmObject implements Serializable {

    @PrimaryKey
    private String id;

    @SerializedName("idClub")
    @Expose
    private String idClub;


    @SerializedName("latitude")
    @Expose
    protected double latitude;

    @SerializedName("longitude")
    @Expose
    protected double longitude;


    public PointMap(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        id = AppUtil.getRandomString(10);
    }


    public PointMap() {
        id = AppUtil.getRandomString(10);
    }

    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
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
