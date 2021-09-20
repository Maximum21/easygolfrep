package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.Country
import java.lang.Exception

data class CountryBundle(
        val country: String
) {
    companion object {
        fun toData(data: Country):String{
            return Gson().toJson(data)
        }
    }

    fun toCountry(): Country? {
        return try {
            Gson().fromJson(this.country, Country::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}