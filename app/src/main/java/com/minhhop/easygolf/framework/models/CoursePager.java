package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CoursePager implements Serializable {

    @SerializedName("paginator")
    @Expose
    private Paginator paginator;

    @SerializedName("items")
    @Expose
    private List<Course> courses;

    public Paginator getPaginator() {
        return paginator;
    }

    public List<Course> getCourses() {
        return courses;
    }
}