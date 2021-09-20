package com.minhhop.easygolf.presentation.gallery

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.GalleryMedia
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.common.AppUtils
import java.io.File
import java.io.InputStream

class EasyGolfGalleryViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val listGalleryLiveData = MutableLiveData<List<GalleryMedia>>()
    private var galleryCursor:Cursor? = null
    private var mCountItemInPage = 0
    fun getExternalGallery(context: Context){
        /**
         * @MediaStore.Images.Media.Data was Deprecated
         * */
        if(galleryCursor == null) {
            val imageProjection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATE_ADDED
            )
            val imageSortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageProjection,
                    null,
                    null,
                    imageSortOrder
            )
            galleryCursor = cursor
            mCountItemInPage = 0
        }

        val listData = ArrayList<GalleryMedia>()
        galleryCursor?.let { cursorSafe->
            val idColumn = cursorSafe.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateColumn = cursorSafe.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursorSafe.moveToNext() && mCountItemInPage < 20) {
                val id = cursorSafe.getLong(idColumn)
                val date = cursorSafe.getLong(dateColumn)
                val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                )
                listData.add(GalleryMedia(id.toString(),contentUri.toString(),date))
                mCountItemInPage++
            }
        }
        listGalleryLiveData.postValue(listData)
    }

    fun clearCursor(){
        galleryCursor?.moveToFirst()
        galleryCursor = null
        mCountItemInPage = 0
    }

    override fun cancelAllRequest() {
        super.cancelAllRequest()
        galleryCursor?.close()

    }

    fun createImageFileHolder(externalFilesDir: File) = interactors.createImageFile(externalFilesDir)
    fun deleteFileImage(path: String) = interactors.deleteFileInLocal(path)
    fun resizeFileImage(path: String) = interactors.resizeImageInLocal(path, AppUtils.MAX_QUALITY_IMAGE_SCALE) { mCommonErrorLive.postValue(it) }
}