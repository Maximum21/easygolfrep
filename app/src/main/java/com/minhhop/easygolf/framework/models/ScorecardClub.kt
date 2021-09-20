package com.minhhop.easygolf.framework.models

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ScorecardClub() : Parcelable {
    @Expose
    @SerializedName("par")
    var par:Int = 0

    @Expose
    @SerializedName("yard")
    var yard:Long = 0

    @Expose
    @SerializedName("distance")
    var distance:Long = 0

    @Expose
    @SerializedName("type")
    lateinit var type:String

    @Expose
    @SerializedName("cr")
    var cr:Double = 0.0

    @Expose
    @SerializedName("sr")
    var sr:Double = 0.0

    constructor(parcel: Parcel) : this() {
        par = parcel.readInt()
        yard = parcel.readLong()
        distance = parcel.readLong()
        parcel.readString()?.apply {
            type = this
        }
        cr = parcel.readDouble()
        sr = parcel.readDouble()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(par)
        parcel.writeLong(yard)
        parcel.writeLong(distance)
        parcel.writeString(type)
        parcel.writeDouble(cr)
        parcel.writeDouble(sr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScorecardClub> {
        override fun createFromParcel(parcel: Parcel): ScorecardClub {
            return ScorecardClub(parcel)
        }

        override fun newArray(size: Int): Array<ScorecardClub?> {
            return arrayOfNulls(size)
        }
    }


    fun getResOptionIcon(): Int {

        if (!TextUtils.isEmpty(this.type)) {
            val resIcon: Int
            when (this.type.toUpperCase()) {
                "RED" -> resIcon = Color.parseColor("#c6342d")
                "WHITE" -> resIcon = Color.parseColor("#FFF1F1F1")
                "GREEN" -> resIcon = Color.parseColor("#4c8e06")
                "BLUE" -> resIcon = Color.parseColor("#FF0D5C93")
                "YELLOW" -> resIcon = Color.parseColor("#f6dc03")
                "BLACK" -> resIcon = Color.parseColor("#000000")
                "PLATINUM" -> resIcon = Color.parseColor("#d4d4d4")

                "PURPLE" -> resIcon = Color.parseColor("#dea0dd")

                "SEPIA" -> resIcon = Color.parseColor("#704214")

                "GREY" -> resIcon = Color.parseColor("#666666")

                "GRAY" -> resIcon = Color.parseColor("#a9a9a9")

                "SHARK" -> resIcon = Color.parseColor("#006272")

                "BROWN" -> resIcon = Color.parseColor("#793802")

                "LIME" -> resIcon = Color.parseColor("#c8e260")

                "PINK" -> resIcon = Color.parseColor("#cf5b85")

                "ORANGE" -> resIcon = Color.parseColor("#e8722e")

                "GOLD" -> resIcon = Color.parseColor("#FFD700")

                "JADE" -> resIcon = Color.parseColor("#00a86b")

                "COPPER" -> resIcon = Color.parseColor("#cf8d6d")

                "SLIVER" -> resIcon = Color.parseColor("#bbc2c2")
                else -> resIcon = Color.parseColor("#FF0D5C93")
            }
            return resIcon
        }
        return 0

    }

}