package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.minhhop.core.domain.Country

class Club {

    @Expose
    @SerializedName("id")
    lateinit var id:String

    @Expose
    @SerializedName("name")
    lateinit var name:String

    @Expose
    @SerializedName("courses")
    var courses = ArrayList<CourseClub>()

    @Expose
    @SerializedName("rating")
    var rating:Int = 0

    @Expose
    @SerializedName("country")
    var country:Country? = null

    @Expose
    @SerializedName("contact_email")
    var contactEmail:String? = null

    @Expose
    @SerializedName("description")
    var description:String? = null

    @Expose
    @SerializedName("state")
    var state:String? = null

    @Expose
    @SerializedName("phone_number")
    var phoneNumber:String? = null

    @Expose
    @SerializedName("image")
    var image:String? = null

    @Expose
    @SerializedName("address")
    var address:String? = null

    @Expose
    @SerializedName("number_of_holes")
    var numberOfHole:Int? = null

    @Expose
    @SerializedName("coordinate")
    lateinit var coordinate: CoordinateClub
    @Expose
    @SerializedName("city")
    lateinit var city: CoordinateClub


    @Expose
    @SerializedName("latitude")
    var latitude: Double = 0.0

    @Expose
    @SerializedName("longitude")
    var longitude: Double = 0.0

}