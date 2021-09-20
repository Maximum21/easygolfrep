package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.FileHelperDataSource
import com.minhhop.core.domain.ErrorResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class FileHelperRepository(private val fileHelperDataSource: FileHelperDataSource) {
    @Throws(IOException::class)
    fun createImageFile(externalFilesDir:File) : File? = fileHelperDataSource.createImageFile(externalFilesDir)
    @Throws(IOException::class)
    fun createImageFile(externalFilesDir:File,data: InputStream) : File? = fileHelperDataSource.createImageFile(externalFilesDir, data)
    fun deleteFile(path:String) = fileHelperDataSource.deleteFile(path)
    fun resizeImage(path: String, maxQualityScale:Float, errorResponse: (ErrorResponse) -> Unit) = fileHelperDataSource.resizeImage(path, maxQualityScale, errorResponse)
    fun writeBytesToFile(data: ByteArrayOutputStream, file: File, errorResponse: (ErrorResponse) -> Unit)
            = fileHelperDataSource.writeBytesToFile(data, file, errorResponse)
}