package com.minhhop.core.domain.golf

data class Club(
        val id:String,
        val image:String?,
        val name:String?,
        val address:String?,
        val website:String?,
        val distance:Double,
        val rating:Double,
        val courses:List<Course>?,
        val description:String?,
        val phone_number:String?,
        val coordinate:ShortLocation?,
        val is_following:Boolean?
)