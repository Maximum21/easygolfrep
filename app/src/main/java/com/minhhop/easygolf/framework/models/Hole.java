package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Hole extends RealmObject {


    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("idClub")
    @Expose
    private String idClub;

    @PrimaryKey
    @SerializedName("hole_id")
    @Expose
    private String holeId;

    @SerializedName("par")
    @Expose
    private int par;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("yard")
    @Expose
    private int yard;

    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;

    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    @SerializedName("index")
    @Expose
    private int index;

    @SerializedName("tees")
    @Expose
    private RealmList<Tee> tees;

    @SerializedName("green")
    @Expose
    private Green greens;

    @SerializedName("number")
    @Expose
    private int number;

    @SerializedName("longitude")
    @Expose
    private double longitude;


    @SerializedName("latitude")
    @Expose
    private double latitude;


    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
    }

    public int getId() {
        return id;
    }

    public String getHoleId() {
        return holeId;
    }

    public int getPar() {
        return par;
    }

    public String getImage() {
        return image;
    }

    public int getYard() {
        return yard;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getIndex() {
        return index;
    }

    public List<Tee> getTees() {
        return tees;
    }

    public Green getGreens() {
        return greens;
    }

    public int getNumber() {
        return number;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

}
