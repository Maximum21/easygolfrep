package com.minhhop.easygolf.framework.bundle

import android.os.Bundle
import com.minhhop.core.domain.firebase.NotificationModel
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.endgame.EndGameActivity
import com.minhhop.easygolf.presentation.home.EasyGolfHomeActivity

data class NotificationBundle(
        val type: NotificationModel.Type
) {
    companion object {
        fun extraBundle(bundle: Bundle?): NotificationBundle? {
            return bundle?.getString(NotificationBundle::type.name)?.let { type ->
                val notificationBundle = NotificationBundle(NotificationModel.getType(type))
                notificationBundle.mBundle = bundle
                notificationBundle
            }
        }

        fun extraBundle(notificationModel: NotificationModel?): NotificationBundle? {
            val bundle = Bundle()
            bundle.putString(NotificationModel::id.name, notificationModel?.id)
            bundle.putString(NotificationModel::title.name, notificationModel?.title)
            bundle.putString(NotificationModel::body.name, notificationModel?.body)
            bundle.putString(NotificationModel::type.name, notificationModel?.type)
            bundle.putString(NotificationModel::channel_id.name, notificationModel?.channel_id)
            bundle.putBoolean(NotificationModel::group.name, notificationModel?.group ?: false)
            bundle.putString(NotificationModel::object_id.name, notificationModel?.object_id)
            bundle.putString(NotificationModel::club_id.name, notificationModel?.club_id)
            bundle.putString(NotificationModel::course_id.name, notificationModel?.course_id)
            return extraBundle(bundle)
        }
    }

    fun getClassTarget(): Class<*> {
        return when (type) {
            NotificationModel.Type.GENERAL -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.ADD_FRIEND -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.FOLLOW_USER -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.ACCEPT_FRIEND_REQUEST -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.DECLINE_FRIEND_REQUEST -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.SYNC_CONTACT -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.ROUND_INVITATION -> {
                EasyGolfHomeActivity::class.java
            }
            NotificationModel.Type.COMPLETE_ROUND -> {
                EndGameActivity::class.java
            }
            NotificationModel.Type.APPROVED_ROUND -> {
                EasyGolfHomeActivity::class.java
            }
        }
    }

    var mBundle: Bundle? = null
//    fun toCompleteRoundBundle(notificationModel: NotificationModel) {
//        notificationModel.object_id?.let { roundId->
//            EndGameBundle(
//                    roundId,null,null,PlayGolfBundle.TypeGame.BATTLE_GAME
//            )
//        }?.let { endGameBundle ->
//            mBundle = EasyGolfNavigation.buildEndGameBundle(endGameBundle)
//        }
//    }

    fun toBundle() = mBundle
    fun parseBundle(): Bundle? {
        return when (type) {
            NotificationModel.Type.GENERAL -> {
                Bundle()
            }
            NotificationModel.Type.ADD_FRIEND -> {
                Bundle()
            }
            NotificationModel.Type.FOLLOW_USER -> {
                Bundle()
            }
            NotificationModel.Type.ACCEPT_FRIEND_REQUEST -> {
                Bundle()
            }
            NotificationModel.Type.DECLINE_FRIEND_REQUEST -> {
                Bundle()
            }
            NotificationModel.Type.SYNC_CONTACT -> {
                Bundle()
            }
            NotificationModel.Type.ROUND_INVITATION -> {
                val roundInvitationBundle = Bundle()
                mBundle?.getString(NotificationModel::object_id.name)?.let { roundId ->
                    roundInvitationBundle.putString(PlayGolfBundle::roundId.name,roundId)
                }
                roundInvitationBundle
            }
            NotificationModel.Type.COMPLETE_ROUND -> {
                mBundle?.getString(NotificationModel::object_id.name)?.let { roundId ->
                    EasyGolfNavigation.buildEndGameBundle(
                            EndGameBundle(roundId, null, null, PlayGolfBundle.TypeGame.UNKNOWN)
                    )
                }
            }
            NotificationModel.Type.APPROVED_ROUND -> {
                Bundle()
            }
        }
//        return if(mBundle != null){
//            mBundle?.putString(NotificationBundle::type.name, type.value)
//            mBundle!!
//        }else{
//            val bundle = Bundle()
//            bundle.putString(NotificationBundle::type.name, type.value)
//            bundle
//        }
    }
}