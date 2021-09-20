package com.minhhop.easygolf.services.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.minhhop.easygolf.base.WozBaseActivity
import java.io.IOException
import java.nio.charset.Charset

class ImportJsonToFirebase {
    fun loadDataFromAsset(activity: WozBaseActivity){
        val gson = Gson()

        loadJSONFromAsset(activity)?.apply {
            val result = gson.fromJson<Holder>(this, Holder::class.java)
            val dbFirebase = FirebaseDatabase.getInstance().getReference("users")
            for (item in result.list){
                val data = ClearProfile(item.first_name,item.last_name,item.online,item.avatar)
                dbFirebase.child(item.id).child("profile")
                        .setValue(data)
            }
        }
    }

    fun loadJSONFromAsset(activity: WozBaseActivity): String? {
        var json: String? = null
        try {
            val jsonFile = activity.assets.open("test_user.json")
            val size = jsonFile.available()
            val buffer = ByteArray(size)
            jsonFile.read(buffer)
            jsonFile.close()
            String()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}