package com.minhhop.core.domain.firebase

import com.minhhop.core.domain.firebase.NotificationModel.Type.*

data class NotificationModel(
        val id:String?,
        val title:String?,
        val body:String?,
        val type:String?,
        /**
         * For chat
         * */
        var channel_id:String? = null,
        var group:Boolean = false,

        /**
         * For invite to the battle
         * */
        var object_id:String? = null,
        var club_id:String? = null,
        var course_id:String? = null
)  {
    companion object{
        fun getType(type:String?):Type{
            return when(type){
                GENERAL.value -> {
                    GENERAL
                }
                ADD_FRIEND.value -> {
                    ADD_FRIEND
                }
                FOLLOW_USER.value -> {
                    FOLLOW_USER
                }
                ACCEPT_FRIEND_REQUEST.value -> {
                    ACCEPT_FRIEND_REQUEST
                }
                DECLINE_FRIEND_REQUEST.value -> {
                    DECLINE_FRIEND_REQUEST
                }
                SYNC_CONTACT.value -> {
                    SYNC_CONTACT
                }
                ROUND_INVITATION.value -> {
                    ROUND_INVITATION
                }
                COMPLETE_ROUND.value -> {
                    COMPLETE_ROUND
                }
                APPROVED_ROUND.value -> {
                    APPROVED_ROUND
                }else->{
                    GENERAL
                }
            }
        }
    }
    fun toType():Type{
        return getType(this.type)
    }
    enum class Type(val value:String){
        GENERAL("GENERAL"),
        ADD_FRIEND("ADD_FRIEND"),
        FOLLOW_USER("FOLLOW_USER"),
        ACCEPT_FRIEND_REQUEST("ACCEPT_FRIEND_REQUEST"),
        DECLINE_FRIEND_REQUEST("DECLINE_FRIEND_REQUEST"),
        SYNC_CONTACT("SYNC_CONTACT"),
        ROUND_INVITATION("ROUND_INVITATION"),
        COMPLETE_ROUND("COMPLETE_ROUND"),
        APPROVED_ROUND("APPROVED_ROUND")
    }
}