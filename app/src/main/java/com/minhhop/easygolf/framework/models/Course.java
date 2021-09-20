package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Course extends RealmObject implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("idClub")
    @Expose
    private String idClub;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("rating")
    @Expose
    private int rating;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("distance")
    @Expose
    private double distance;


    @SerializedName("holes")
    @Expose
    private int holes;

    @SerializedName("type")
    @Expose
    private String type;


    @SerializedName("length")
    @Expose
    private int length;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    @SerializedName("fee")
    @Expose
    private String fee;


    @SerializedName("slope")
    @Expose
    private String slope;

    @SerializedName("par")
    @Expose
    private int par;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("description")
    @Expose
    private String description;

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getPar() {
        return par;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    public double getDistance() {
        return distance;
    }

    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
    }

    public int getHoles() {
        return holes;
    }

    public int getRating() {
        return rating;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFee() {
        if (this.fee != null && this.currency != null) {
            return this.fee + this.currency;
        }
        return fee;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getSlope() {
        return slope;
    }
}