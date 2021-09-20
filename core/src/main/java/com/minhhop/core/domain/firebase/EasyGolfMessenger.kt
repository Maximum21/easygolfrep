package com.minhhop.core.domain.firebase

open class EasyGolfMessenger {
    enum class TypeMessage{
        STARTING,
        TEXT,
        IMAGE,
        ADD,
        LEAVE,
        NAME
    }

    companion object{

        fun getType(type:String): TypeMessage {
            if(type.equals("starting",true)){
                return TypeMessage.STARTING
            }
            if(type.equals("text",true)){
                return TypeMessage.TEXT
            }
            if(type.equals("image",true)){
                return TypeMessage.IMAGE
            }
            if(type.equals("add",true)){
                return TypeMessage.ADD
            }
            if(type.equals("leave",true)){
                return TypeMessage.LEAVE
            }
            if(type.equals("name",true)){
                return TypeMessage.NAME
            }
            return TypeMessage.TEXT
        }
    }

    var id:String? = null
    var time:Long = 0
    lateinit var type:String
    lateinit var body:String
    lateinit var sender_id:String
}
