package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResetPassword {

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("repassword")
    @Expose
    private String repassword;


    public ResetPassword(String password, String repassword) {
        this.password = password;
        this.repassword = repassword;
    }
}
