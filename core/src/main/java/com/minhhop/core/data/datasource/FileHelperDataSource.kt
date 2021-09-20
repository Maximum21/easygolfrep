package com.minhhop.core.data.datasource

import com.minhhop.core.domain.ErrorResponse
import java.io.*
import kotlin.math.roundToInt

interface FileHelperDataSource {
    @Throws(IOException::class)
    fun createImageFile(externalFilesDir:File) : File?
    @Throws(IOException::class)
    fun createImageFile(externalFilesDir:File,data: InputStream) : File?
    fun deleteFile(path:String)
    fun resizeImage(path: String, maxQualityScale:Float, errorResponse: (ErrorResponse) -> Unit)
    fun writeBytesToFile(data:ByteArrayOutputStream,file: File,errorResponse: (ErrorResponse) -> Unit)
}