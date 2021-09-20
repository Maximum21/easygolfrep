package com.minhhop.easygolf.framework.common

import android.content.Context
import android.os.Environment
import com.minhhop.easygolf.BuildConfig
import java.io.File

object Contains {
    private external fun urlApiDevelopment(): String
    private external fun urlApiProduct(): String

    external fun encryptionKeyRealm():String

    fun fileDirClubPhoto(context: Context?) = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"club")
    fun fileDirGolfPhoto(context: Context?) = File(context?.filesDir?.path,"golf")
    const val REQUEST_CODE_COUNTRY = 0x0002
    const val REQUEST_CODE_PHOTO_GALLERY = 0x0003
    const val REQUEST_CODE_SCORECARD_PHOTO_GALLERY = 0x0013
    const val UPDATE_REQEUST_CODE = 0x00135
    const val REQUEST_CODE_CAMERA = 0x0004
    const val REQUEST_CODE_CAMERA_PERMISSION = 0x0014
    const val REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION = 0x0024
    const val REQUEST_CODE_PHONE_CALL_PERMISSION = 0x0034
    const val REQUEST_LOCATION_PERMISSION = 0x0044

    const val REQUEST_ACTION_LOCATION_SOURCE_SETTINGS = 0x0006
    const val REQUEST_SCORE_MORE = 0x0007
    const val REQUEST_SCORE_MORE_TO_ANOTHER_PLAYER = 0x0008
    const val REQUEST_CODE_ADD_MEMBER_CHAT = 0x0009
    const val REQUEST_CODE_COURSE = 0x0010
    const val FORMAT_IMAGE = "yyyyMMdd_HHmmss"
    const val VALUE_AUTHORIZATION = "VALUE_AUTHORIZATION"
    const val VALUE_THEME_TAG = "VALUE_THEME_TAG"
    const val EXTRA_IMAGE_COURSE = "EXTRA_IMAGE_COURSE"
    const val EXTRA_ID_COURSE = "EXTRA_ID_COURSE"
    const val EXTRA_PLAY_WITH_BATTLE = "EXTRA_PLAY_WITH_BATTLE"
    const val EXTRA_ID_ROUND_BATTLE = "EXTRA_ID_ROUND_BATTLE"
    const val EXTRA_ID_CLUB = "EXTRA_ID_CLUB"
    const val EXTRA_SCORECARD = "EXTRA_SCORECARD"
    const val EXTRA_NAME_COURSE = "EXTRA_NAME_COURSE"
    const val EXTRA_NAME_CLUB = "EXTRA_NAME_CLUB"
    const val EXTRA_IS_SHOW_LIKE_HISTORY = "EXTRA_IS_SHOW_LIKE_HISTORY"
    const val EXTRA_NAME_COUNTRY = "EXTRA_IMAGE_COURSE"
    const val EXTRA_ISO_COUNTRY = "EXTRA_NAME_COURSE"
    const val EXTRA_VIDEO = "EXTRA_VIDEO"
    const val EXTRA_NEWS = "EXTRA_NEWS"
    const val EXTRA_RULE = "EXTRA_RULE"
    const val EXTRA_TITLE_RULE = "EXTRA_TITLE_RULE"
    const val EXTRA_TITLE_RULE_CHILD = "EXTRA_TITLE_RULE_CHILD"
    const val EXTRA_POLICY = "EXTRA_POLICY"
    const val EXTRA_KEY_SEARCH = "EXTRA_KEY_SEARCH"
    const val EXTRA_KEY_NAME_COURSE = "EXTRA_KEY_NAME_COURSE"
    const val EXTRA_ID_ROUND = "EXTRA_ID_ROUND"
    const val EXTRA_URL_WEB_VIEW = "EXTRA_URL_WEB_VIEW"
    const val EXTRA_KEY_WORD_SEARCH = "EXTRA_KEY_WORD_SEARCH"
    const val EXTRA_VALUE_PLACE = "EXTRA_VALUE_PLACE"
    const val EXTRA_VALUE_DATE = "EXTRA_VALUE_DATE"
    const val EXTRA_PAR = "EXTRA_PAR"
    const val EXTRA_TYPE_NOTIFICATION = "EXTRA_TYPE_NOTIFICATION"
    const val EXTRA_CHANEL_CHAT = "EXTRA_CHANEL_CHAT"
    const val EXTRA_IS_RETURN = "EXTRA_IS_RETURN"
    const val EXTRA_BLACK_LIST = "EXTRA_BLACK_LIST"
    const val EXTRA_EXIT_GAME_BATTLE = "EXTRA_EXIT_GAME_BATTLE"
    const val EXTRA_LIMIT = "EXTRA_LIMIT"
    const val EXTRA_ID_TOURNAMENT = "EXTRA_ID_TOURNAMENT"
    const val EXTRA_FOR_RETURN_DATA = "EXTRA_FOR_RETURN_DATA"

    /**
     * For chat
     */
    const val TRANSITION_PHOTO_NAME = "TRANSITION_PHOTO_NAME"
    const val EXTRA_PHOTO_URL_KEY = "EXTRA_PHOTO_URL_KEY"
    const val EXTRA_ID_CHANNEL = "EXTRA_NAME_CHANNEL"
    const val EXTRA_IS_GROUP_CHAT = "EXTRA_IS_GROUP_CHAT"
    const val EXTRA_ID_USER_RECEIVE = "EXTRA_ID_USER_RECEIVE"
    val urlApi: String
        get() {
            return if ("development".equals(BuildConfig.FLAVOR, ignoreCase = true)) {
                urlApiDevelopment()
            } else {
                if ("product".equals(BuildConfig.FLAVOR, ignoreCase = true)) {
                    urlApiProduct()
                }else urlApiDevelopment()
            }
        }

    init {
        System.loadLibrary("native-lib")
    }
}