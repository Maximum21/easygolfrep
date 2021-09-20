package com.minhhop.easygolf.services

import android.content.SharedPreferences
import com.minhhop.easygolf.framework.common.Contains

class PreferenceService {

    /**
     * Secret value
     */
    companion object{
        const val PREFS_NAME = "prefs_easy_golf_tamp"
//        private const val secretValue = "easy_golf_pref"

        private var instance: PreferenceService? = null
        fun getInstance(): PreferenceService {
            if (instance == null) {
                instance = PreferenceService()
            }
            return instance!!
        }
    }

    var mPreferences: SharedPreferences? = null

    val tutorial: Boolean
        get() = false


    fun getToken(): String? {
        return mPreferences?.getString(Contains.VALUE_AUTHORIZATION,null)
    }


    fun setToken(token: String): Boolean {
        return mPreferences?.edit()?.putString(Contains.VALUE_AUTHORIZATION,token)?.commit() ?: false
    }

    fun removeToken(): Boolean {
        return mPreferences?.edit()?.clear()?.commit() ?: return false
    }
    fun setAppTheme(tag:Int){
        mPreferences?.edit()?.putInt(Contains.VALUE_THEME_TAG,tag)?.apply()
    }
    fun getAppTheme():Int{
        return mPreferences?.getInt(Contains.VALUE_THEME_TAG,0)?:0
    }
}