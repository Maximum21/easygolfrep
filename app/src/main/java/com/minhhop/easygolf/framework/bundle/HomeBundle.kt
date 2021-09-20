package com.minhhop.easygolf.framework.bundle

class HomeBundle {
    companion object{
        fun whatType(type:String?): TYPE {
            type?.apply {
                return when(this) {
                    "CHAT" -> TYPE.CHAT
                    "ROUND_INVITE" -> TYPE.ROUND_INVITE
                    else -> TYPE.NIL
                }
            }
            return TYPE.NIL
        }
    }

    enum class TYPE{
        NIL,
        CHAT,
        ROUND_INVITE
    }
    var type = TYPE.NIL

    var channelId:String? = null
    var group:Boolean = false

    var playWithBattle:Boolean = false
    var round:String? = null
    var clubId:String? = null
    var courseId:String? = null

    constructor(channelId:String,group:Boolean){
        this.channelId = channelId
        this.group = group
        this.type = TYPE.CHAT
    }

    constructor(round:String,clubId:String,courseId:String,playWithBattle:Boolean){
        this.round = round
        this.clubId = clubId
        this.courseId = courseId
        this.playWithBattle = playWithBattle
        this.type = TYPE.ROUND_INVITE
    }
}