package com.minhhop.easygolf.framework.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUser{
    @SerializedName("email")
    @Expose
    protected String email;

    @SerializedName("first_name")
    @Expose
    protected String firstName;

    @SerializedName("last_name")
    @Expose
    protected String lastName;

    @SerializedName("gender")
    @Expose
    protected String gender;

    @SerializedName("country_code")
    @Expose
    protected String countryCode;

    @SerializedName("phone_notification")
    @Expose
    protected boolean phoneNotification;

    @SerializedName("email_notification")
    @Expose
    protected boolean emailNotification;

    @SerializedName("birthday")
    @Expose
    protected String birthday;

    @SerializedName("phone_number")
    @Expose
    protected String phoneNumber;


    @SerializedName("language")
    @Expose
    protected String language;
    @SerializedName("handicap")
    @Expose
    protected int  handicap = 0;
    @SerializedName("friends")
    @Expose
    protected int  friends = 0;
    @SerializedName("following")
    @Expose
    protected int  following = 0;

    public UpdateUser() {

    }

    public UpdateUser(String email, String firstName, String lastName, String birthday, String phoneNumber,
                      String gender, String countryCode, boolean phoneNotification, boolean emailNotification) {

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.countryCode = countryCode;
        this.phoneNotification = phoneNotification;
        this.emailNotification = emailNotification;
    }


    public void setLanguage(String language) {
        this.language = language;
    }
}
