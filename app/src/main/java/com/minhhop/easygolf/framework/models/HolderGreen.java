package com.minhhop.easygolf.framework.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.minhhop.easygolf.utils.AppUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class HolderGreen extends RealmObject {

    public enum TYPE_FLAG{
        BLUE,
        WHITE,
        RED
    }

    private String idClub;

    @PrimaryKey
    private String id;


    @SerializedName("latitude")
    @Expose
    protected double latitude;

    @SerializedName("longitude")
    @Expose
    protected double longitude;

    @Ignore
    public TYPE_FLAG typeFlag = TYPE_FLAG.WHITE;

    public HolderGreen(){
        id = AppUtil.getRandomString(10);
    }

    public String getId() {
        return id;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
