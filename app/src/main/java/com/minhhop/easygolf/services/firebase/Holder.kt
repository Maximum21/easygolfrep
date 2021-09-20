package com.minhhop.easygolf.services.firebase

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Holder (
        @Expose
        @SerializedName("data")
        val list: List<ObjectUserProfile>
)