package com.minhhop.core.domain.firebase

class ChannelChat{
    var id:String? = null

    lateinit var last_message: EasyGolfMessenger

    lateinit var members: HashMap<String, MemberChat>

    var group:Boolean = false

    var name:String? = null

    fun getMember(idUser:String):List<MemberChat>{
        val results = ArrayList<MemberChat>()
        var myKey = ""
        for ((key,value) in members){
            if (value.user_id != idUser) {
                results.add(value)
            }else{
                myKey = key
            }
        }
        if(results.size <= 1){
            results.add(members[myKey]!!)
        }
        return results
    }

    fun getMy(idUser:String): MemberChat {
        for ((_,value) in members){
            if (value.user_id == idUser){
                return value
            }
        }
        return members.values.toList()[0]
    }

}