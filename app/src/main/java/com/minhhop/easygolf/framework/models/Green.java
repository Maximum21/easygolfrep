package com.minhhop.easygolf.framework.models;


import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.minhhop.easygolf.R;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Green extends RealmObject {

    public enum FLAG {
        BLUE,
        WHITE,
        RED
    }

    private String idClub;


    private HolderGreen red;


    private HolderGreen white;

    private HolderGreen blue;

    private String lastUpdate;


    private String dateCreated;


    private int id;

    @Ignore
    private List<Coordinate> coordinates;


    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
    }

    public HolderGreen getRed() {
        return red;
    }

    public HolderGreen getWhite() {
        return white;
    }

    public HolderGreen getBlue() {
        return blue;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getId() {
        return id;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }


    public HolderGreen getDefaultGreen(@Nullable FLAG flag){
        if(flag == null){
            return this.white;
        }
        HolderGreen target;
        switch (flag){
            case RED:
                target = this.red;
                break;
            case BLUE:
                target = this.blue;
                break;
            case WHITE:
                target = this.white;
                break;

            default:
                target = this.white;
        }
        return target;
    }


    public static int getDrawFlagGreen(HolderGreen.TYPE_FLAG flag){
        int resGreen = R.drawable.ic_icon_flag_white;

        switch (flag) {
            case BLUE:
                resGreen = R.drawable.ic_icon_flag_blue;
                break;
            case WHITE:
                break;
            case RED:
                resGreen = R.drawable.ic_icon_flag_red;
                break;
        }

        return resGreen;
    }

}
