package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.GalleryMedia

data class GalleryBundle(
        /**
         * @param @result is content uri path, to get it @contentResolver
         * */
        var result:String? = null,
        val maxPhotoSelect:Int = 1
){
    companion object{
        fun toData(data:List<GalleryMedia>?):String?{
            return data?.let { clearData->
                Gson().toJson(clearData)
            }
        }
    }

    fun getResult() = fetchData(result)
    private fun fetchData(data: String?): List<GalleryMedia>?{
        return data?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Array<GalleryMedia>::class.java).asList()
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}