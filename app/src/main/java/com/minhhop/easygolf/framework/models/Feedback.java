package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("subject")
    @Expose
    private String subject;

    @SerializedName("content")
    @Expose
    private String content;

    public Feedback(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
