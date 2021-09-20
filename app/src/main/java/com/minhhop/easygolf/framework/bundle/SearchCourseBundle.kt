package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Club
import com.minhhop.core.domain.golf.Course

data class SearchCourseBundle(
        val club: String
){
    companion object{

        fun toData(data: Club):String?{
            return data?.let { clearData->
                Gson().toJson(clearData)
            }
        }

        fun toBlackList(black_list:Club):String?{
            return black_list?.let { clearBlackList->
                Gson().toJson(clearBlackList)
            }
        }
    }
    fun getResult() = fetchData(club)
    fun getBlackList() = fetchData(club)
    private fun fetchData(data: String): Club?{
        return data?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Club::class.java)
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}