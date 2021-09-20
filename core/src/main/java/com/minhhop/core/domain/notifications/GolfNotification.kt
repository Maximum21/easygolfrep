package com.minhhop.core.domain.notifications
import com.minhhop.core.domain.User

data class GolfNotification (var date:String?,
                             var deleted:Boolean,
                             var user_id:String,
                             var id:String,
                             var user: User?,
                             var body:String,
                             var heading:String?,
                             var title:String,
                             var object_id:String?,
                             var seen:Boolean
)