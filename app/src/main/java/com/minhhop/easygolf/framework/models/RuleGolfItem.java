package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RuleGolfItem extends RealmObject implements Serializable{

    @SerializedName("index")
    @Expose
    private int index;

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("slug")
    @Expose
    private String slug;


    private int indexParent;
    private String titleParent;

    public int getIndexParent() {
        return indexParent;
    }

    public String getTitleParent() {
        return titleParent;
    }

    public void setIndexParent(int indexParent) {
        this.indexParent = indexParent;
    }

    public void setTitleParent(String titleParent) {
        this.titleParent = titleParent;
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getSlug() {
        return slug;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
