package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.minhhop.easygolf.utils.AppUtil;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Match extends RealmObject {

    @PrimaryKey
    private String id;

    @SerializedName("idClub")
    @Expose
    private String idClub;

    @SerializedName("hole")
    @Expose
    private Hole hole;

    public Hole getHole() {
        return hole;
    }

    @SerializedName("score")
    @Expose
    private int score;

    @SerializedName("putts")
    @Expose
    private int putts;

    @SerializedName("shots")
    @Expose
    private int shots;

    @SerializedName("tee")
    @Expose
    private Tee tee;

    @SerializedName("flag")
    @Expose
    private PointMap flag;


    @Ignore
    @SerializedName("fairway_hit")
    @Expose
    private int fairway_hit;

    @Ignore
    @SerializedName("green_in_regulation")
    @Expose
    private int greenInRegulation;


    public int getFairway_hit() {
        return fairway_hit;
    }

    public int getGreenInRegulation() {
        return greenInRegulation;
    }

    public Tee getTee() {
        return tee;
    }

    public PointMap getFlag() {
        return flag;
    }

    public void setHole(Hole hole) {
        this.hole = hole;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPutts() {
        return putts;
    }

    public void setPutts(int putts) {
        this.putts = putts;
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

    public void setId(String id) {
        this.id = id;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public void setTee(Tee tee) {
        this.tee = tee;
    }

    public void setFlag(PointMap flag) {
        this.flag = flag;
    }

    public Match() {
        id = AppUtil.getRandomString(10);
    }
}
