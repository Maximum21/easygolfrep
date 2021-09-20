package com.minhhop.easygolf.framework.models

import com.minhhop.core.domain.firebase.EasyGolfMessenger

open class LastMessengerModel(easyGolfMessenger: EasyGolfMessenger) : EasyGolfMessenger() {
    var need_push = true
    init {
        this.id = easyGolfMessenger.id
        this.time = easyGolfMessenger.time
        this.body = easyGolfMessenger.body
        this.type = easyGolfMessenger.type
        this.sender_id = easyGolfMessenger.sender_id
    }
}