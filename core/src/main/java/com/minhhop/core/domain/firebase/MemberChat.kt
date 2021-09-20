package com.minhhop.core.domain.firebase

class MemberChat {
    lateinit var user_id:String
    var active:Boolean = false
    var unread_count:Int = 0
    var typing:Boolean = false
    var hidden:Boolean = false
}