package com.minhhop.easygolf.framework.database

import android.content.Context
import com.minhhop.easygolf.framework.common.Contains

class PreferenceManager(context: Context) {

    companion object{
        private const val PREFS_NAME = "prefs_easy_golf"
    }

    private val mPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    fun getToken(): String? {
        return mPreferences.getString(Contains.VALUE_AUTHORIZATION,null)
    }

    fun setToken(token: String): Boolean {
        return mPreferences.edit()?.putString(Contains.VALUE_AUTHORIZATION,token)?.commit() ?: false
    }

    fun removeToken(): Boolean {
        return mPreferences.edit()?.clear()?.commit() ?: return false
    }
    fun setAppTheme(tag:Int){
        mPreferences.edit()?.putInt(Contains.VALUE_THEME_TAG,tag)?.apply()
    }
    fun getAppTheme():Int{
        return mPreferences.getInt(Contains.VALUE_THEME_TAG,0)
    }
}