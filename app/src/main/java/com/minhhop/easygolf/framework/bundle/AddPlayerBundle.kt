package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.User

data class AddPlayerBundle(
        val limit:Int = -1,
        var result:String? = null,
        var blackList:String? = null,
        var title:String? = null
){
    companion object{
        fun toData(data:List<User>?):String?{
            return data?.let { clearData->
                Gson().toJson(clearData)
            }
        }
        fun toBlackList(black_list:List<User>?):String?{
            return black_list?.let { clearBlackList->
                Gson().toJson(clearBlackList)
            }
        }
    }

    fun getResult() = fetchData(result)
    fun getBlackList() = fetchData(blackList)
    private fun fetchData(data: String?): List<User>?{
        return data?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Array<User>::class.java).asList()
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}