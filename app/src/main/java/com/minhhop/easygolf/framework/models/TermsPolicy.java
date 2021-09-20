package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsPolicy {

    @SerializedName("title")
    @Expose
    private String title;


    @SerializedName("content")
    @Expose
    private String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
