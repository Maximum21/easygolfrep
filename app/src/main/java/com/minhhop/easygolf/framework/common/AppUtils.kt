package com.minhhop.easygolf.framework.common

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import com.minhhop.core.domain.User
import com.minhhop.easygolf.presentation.custom.bottomnavigation.ColorRGBHolder
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt


class AppUtils {
    companion object {
        const val MAX_QUALITY_IMAGE_SCALE = 1280f
        var mColorPrimaryStart = ColorRGBHolder(131, 193, 37)
        var mColorPrimaryEnd = ColorRGBHolder(37, 123, 17)

        var mColorBezierStart = ColorRGBHolder(84, 175, 71)
        var mColorBezierEnd = ColorRGBHolder(102, 179, 47)

        fun isTextEmpty(vararg values: String): Boolean {
            for (item in values) {
                if (TextUtils.isEmpty(item)) {
                    return true
                }
            }
            return false
        }

        fun getDescription(key: String, text: String): RequestBody {
            val jsonObject = JSONObject()
            jsonObject.put(key, text)
            val body = text.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            return body
        }

        fun pickMultipleImageGallery(createPostActivity: CreatePostActivity) {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            createPostActivity.startActivityForResult(intent, Contains.REQUEST_CODE_PHOTO_GALLERY);
        }

        fun setDimentions(img: ImageView, layoutWidth: Int?, layoutHeight: Int) {
            if (layoutWidth != null) {
                img.layoutParams.width = layoutWidth
            }
            img.layoutParams.height = layoutHeight
            img.requestLayout()
        }

        fun getPathFromURI(uri: Uri, createPostActivity: CreatePostActivity): String {
            var path: String = uri.path!! // uri = any content Uri
            var imagePath: String? = null
            val databaseUri: Uri
            val selection: String?
            val selectionArgs: Array<String>?
            if (path.contains("/document/image:")) { // files selected from "Documents"
                databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                selection = "_id=?"
                selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
            } else { // files selected from all other sources, especially on Samsung devices
                databaseUri = uri
                selection = null
                selectionArgs = null
            }
            try {
                val projection = arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.ORIENTATION,
                        MediaStore.Images.Media.DATE_TAKEN
                ) // some example data you can query
                val cursor = createPostActivity.contentResolver.query(
                        databaseUri,
                        projection, selection, selectionArgs, null
                )
                if (cursor!!.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(projection[0])
                    imagePath = cursor.getString(columnIndex)
                    // Log.e("path", imagePath);
                }
                cursor.close()
                return imagePath!!
            } catch (e: Exception) {
                Log.e("WOW", e.message, e)
            }
            return ""
        }

        fun getFriendsList(key: String, uriList: List<User>): ArrayList<RequestBody> {
            var tempList: ArrayList<RequestBody> = ArrayList()
            for (temp in uriList) {
                tempList.add(getDescription(key, temp.id))
            }

            return tempList
        }

        fun getFiles(key: String, uriList: ArrayList<String>): ArrayList<MultipartBody.Part> {
            var tempList: ArrayList<MultipartBody.Part> = ArrayList()
            if (uriList != null && uriList.isNotEmpty()) {
                for (x in 0 until uriList.size) {
                    val file = File(uriList[x])

                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                    if (requestFile != null) {
                        val image: MultipartBody.Part = MultipartBody.Part.createFormData(key, file.name, requestFile)
                        tempList.add(image)
                    }
                }
            }

            return tempList
        }

        fun setFlagDrawableAssets(context: Context, iso: String, imgView: ImageView) {
            val lowerIso = iso.toLowerCase(Locale.getDefault())
            setImageDrawableAssets(context, "$lowerIso.png", imgView)
        }

        private fun setImageDrawableAssets(context: Context, nameFile: String, imgView: ImageView) {
            try {
                val ims = context.assets.open(nameFile)
                val d = Drawable.createFromStream(ims, null)
                imgView.setImageDrawable(d)
                ims.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        //Method to check if given url contains video or image
        fun isImage(url: String): Boolean {
            try {
                val connection: URLConnection = URL(url).openConnection()
                val contentType: String = connection.getHeaderField("Content-Type")
                return contentType.startsWith("image/")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false;
        }

        fun generatePublishTime(dateCreated: Long, context: Context? = null): CharSequence {
            //TODO with context
            val calender = Calendar.getInstance()
            calender.timeZone = TimeZone.getTimeZone("UTC")
            val currentTime = calender.time
            val outputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            outputFmt.timeZone = TimeZone.getTimeZone("UTC")
            val dateBefore = Date(dateCreated)
            outputFmt.format(dateBefore)
            val mills: Long = abs(currentTime.time - dateBefore.time)
            val days: Long = (mills / (1000 * 60 * 60 * 24) % 30)
            val hours: Long = (mills / (1000 * 60 * 60) % 24)
            val minutes = (mills / (1000 * 60) % 60)
            val seconds = (mills / (1000) % 60)
            val years: Long = (days / 365)
            val months: Long = (days / 30 * 12) % 12
            return when {
                years > 0 -> {
                    "${years}y ago"
                }
                months > 0 -> {
                    "$months months ago"
                }
                days > 0 -> {
                    "${days}d ago"
                }
                hours > 0 -> {
                    "${hours}h ago"
                }
                minutes > 0 -> {
                    "${minutes}m ago"
                }
                seconds > 0 -> {
                    "${seconds}s ago"
                }
                else -> {
                    "just now"
                }
            }
        }

        fun isWeek(dateCreated: Long): Int {
            val calender = Calendar.getInstance()
            calender.timeZone = TimeZone.getTimeZone("UTC")
            val currentTime = calender.time
            val outputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            outputFmt.timeZone = TimeZone.getTimeZone("UTC")
            val dateBefore = Date(dateCreated)
            outputFmt.format(dateBefore)
            val mills: Long = abs(currentTime.time - dateBefore.time)
            val days: Long = (mills / (1000 * 60 * 60 * 24) % 30)
            if (days < 6) {
                return 0
            } else if (days < 30) {
                return 1
            } else {
                return 2
            }
        }

        fun convertISOToDate(date: String?, context: Context): String? {
            return date?.let { dateSafe->
                val simFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                simFormat.timeZone = TimeZone.getTimeZone("UTC")
                try {
                    simFormat.parse(dateSafe)?.let { result ->
                        formatDateTime(result.time)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        fun isSameMonth(firstDate: Long, lastDate: Long): Boolean {
            val calendarTarget = Calendar.getInstance()
            calendarTarget.timeInMillis = firstDate

            val calendarNow = Calendar.getInstance()
            calendarNow.timeInMillis = lastDate
            return calendarNow.get(Calendar.MONTH) == calendarTarget.get(Calendar.MONTH) && calendarNow.get(Calendar.YEAR) == calendarTarget.get(Calendar.YEAR)
        }

        fun convertDpToPixel(value: Int, context: Context): Int {
            val density = context.resources.displayMetrics.density
            return (value * density).roundToInt()
        }

        fun formatDateTime(date: Long): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
            return formatter.format(Date(date))
        }

        fun isValidEmail(target: CharSequence): Boolean = Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}