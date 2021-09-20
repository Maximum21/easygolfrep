package com.minhhop.core.domain.course

import com.minhhop.core.domain.User

class RequestedCourse (
        var id : String,
        var name :String,
        var created_by : User,
        var editor : List<User>,
        var lastUpdated : String,
        var longitude : Double,
        var latitude : String,
        var status : String
)