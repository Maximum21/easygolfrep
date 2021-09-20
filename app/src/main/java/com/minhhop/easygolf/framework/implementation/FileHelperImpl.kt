package com.minhhop.easygolf.framework.implementation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.minhhop.core.data.datasource.FileHelperDataSource
import com.minhhop.core.domain.ErrorResponse
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class FileHelperImpl : FileHelperDataSource {
    /**
     * create file with empty data
     * @param externalFilesDir like a path to save file, it must be a file get from provider
     * */
    @Throws(IOException::class)
    override fun createImageFile(externalFilesDir: File): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        if (!externalFilesDir.exists()) {
            externalFilesDir.mkdirs()
        } else {
            if (!externalFilesDir.isDirectory && externalFilesDir.canWrite()) {
                externalFilesDir.delete()
                externalFilesDir.mkdirs()
            }
        }
        return File.createTempFile(
                "EasyGolf_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                externalFilesDir /* directory */
        )
    }

    override fun createImageFile(externalFilesDir: File, data: InputStream): File? {
        val fileCreate = createImageFile(externalFilesDir)
        val outputStream = FileOutputStream(fileCreate)
        outputStream.use {
            outputStream.write(data.readBytes())
        }
        return fileCreate
    }

    override fun deleteFile(path: String) {
        val fileDelete = File(path)
        if (fileDelete.exists()) {
            fileDelete.delete()
        }
    }

    override fun resizeImage(path: String, maxQualityScale: Float, errorResponse: (ErrorResponse) -> Unit) {
        val destination = File(path)
        val realImage = checkRotation(destination)
        val bytes = ByteArrayOutputStream()

        val ratio = (maxQualityScale / realImage.width).coerceAtMost(maxQualityScale / realImage.height)

        val thumbnail = Bitmap.createScaledBitmap(realImage,
                (ratio * realImage.width.toDouble()).roundToInt(),
                (ratio * realImage.height.toDouble()).roundToInt(), true)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        try {
            destination.createNewFile()
            writeBytesToFile(bytes, destination, errorResponse)
        } catch (e: FileNotFoundException) {
            e.localizedMessage?.let { error ->
                errorResponse(ErrorResponse(message = error))
            }

        } catch (e: IOException) {
            e.localizedMessage?.let { error ->
                errorResponse(ErrorResponse(message = error))
            }
        }
    }

    override fun writeBytesToFile(data: ByteArrayOutputStream, file: File, errorResponse: (ErrorResponse) -> Unit) {
        try {
            FileOutputStream(file).use {
                it.write(data.toByteArray())
            }
        } catch (e: IOException) {
            e.localizedMessage?.let { error ->
                errorResponse(ErrorResponse(message = error))
            }
        }
    }

    private fun checkRotation(dataFile: File): Bitmap {
        val ei = ExifInterface(dataFile.absolutePath)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                rotateImage(BitmapFactory.decodeFile(dataFile.absolutePath), 90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                rotateImage(BitmapFactory.decodeFile(dataFile.absolutePath), 180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                rotateImage(BitmapFactory.decodeFile(dataFile.absolutePath), 270f)
            }
            ExifInterface.ORIENTATION_NORMAL -> {
                BitmapFactory.decodeFile(dataFile.absolutePath)
            }
            else -> BitmapFactory.decodeFile(dataFile.absolutePath)

        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}