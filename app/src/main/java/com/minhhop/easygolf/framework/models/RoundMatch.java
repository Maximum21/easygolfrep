package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class RoundMatch extends RealmObject {

    private String idClub;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("course")
    @Expose
    private Course course;

    @Ignore
    @SerializedName("match")
    @Expose
    private Match match;

    @Ignore
    @SerializedName("handicap")
    @Expose
    private boolean handicap;

    @Ignore
    @SerializedName("hdc")
    @Expose
    private double hdc;

    @Ignore
    @SerializedName("hcp")
    @Expose
    private int hcp;

    @Ignore
    @SerializedName("course_id")
    @Expose
    private String course_id;


    @SerializedName("club_name")
    @Expose
    private String clubName;


    @Ignore
    @SerializedName("matches")
    @Expose
    private List<Match> matches;

    @SerializedName("holes")
    @Expose
    private RealmList<Hole> holes;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("unit")
    @Expose
    private String unit;

    @Ignore
    @SerializedName("friends")
    @Expose
    private List<Friend> friends;


    @Ignore
    @SerializedName("tee")
    @Expose
    private String tee;

    @Ignore
    @SerializedName("eagle")
    @Expose
    private int eagle;

    @Ignore
    @SerializedName("birdie")
    @Expose
    private int birdie;

    @Ignore
    @SerializedName("par")
    @Expose
    private int par;

    @Ignore
    @SerializedName("bogies")
    @Expose
    private int bogey;

    @Ignore
    @SerializedName("dbogies")
    @Expose
    private int doubleBogeys;

    @Ignore
    @SerializedName("albatross")
    @Expose
    private int albatross;


    @Ignore
    @SerializedName("others")
    @Expose
    private int others;

    @Ignore
    @SerializedName("scores")
    @Expose
    private int score;

    @Ignore
    @SerializedName("over")
    @Expose
    private int over;

    public int getOver() {
        return over;
    }

    public int getScore() {
        return score;
    }

    public String getTee() {
        return tee;
    }

    public int getEagle() {
        return eagle;
    }

    public int getBirdie() {
        return birdie;
    }

    public int getPar() {
        return par;
    }

    public int getBogey() {
        return bogey;
    }

    public int getDoubleBogeys() {
        return doubleBogeys;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public String getClubName() {
        return clubName;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
    }

    public Course getCourse() {
        return course;
    }

    public Match getMatch() {
        return match;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public String getStatus() {
        return status;
    }

    public String getUnit() {
        return unit;
    }


    public double getHdc() {
        return hdc;
    }

    public int getHcp() {
        return hcp;
    }

    public int getAlbatross() {
        return albatross;
    }

    public int getOthers() {
        return others;
    }

    public String getCourse_id() {
        return course_id;
    }

    public RoundMatch(){
        matches = new RealmList<>();
    }
}
