package com.minhhop.easygolf.framework.bundle

import android.util.Log
import com.google.gson.Gson
import com.minhhop.core.domain.feed.NewsFeed

class PostDetailsBundle (
        val postId: String,
        val tag:Int,
        val newsFeed: String
){
    companion object{

        fun toData(data: NewsFeed):String?{
            return data?.let { clearData->
                Log.e("tesosnsns","1==${clearData}")
                Gson().toJson(clearData)
            }
        }

        fun toBlackList(black_list:NewsFeed):String?{
            return black_list?.let { clearBlackList->
                Gson().toJson(clearBlackList)
            }
        }
    }
    fun getResult() = fetchData(newsFeed)
    fun getBlackList() = fetchData(newsFeed)
    private fun fetchData(data: String): NewsFeed?{
        return data?.let { dataJson->
            try {
                Gson().fromJson(dataJson,NewsFeed::class.java)
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}