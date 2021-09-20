package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Video implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("tags")
    @Expose
    private List<String> tags;

    @SerializedName("video_id")
    @Expose
    private String videoId;

    @SerializedName("description")
    @Expose
    private String description;


    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() {
        return photo;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getDescription() {
        return description;
    }
}
