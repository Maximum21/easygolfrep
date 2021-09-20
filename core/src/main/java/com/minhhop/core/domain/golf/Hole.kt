package com.minhhop.core.domain.golf

data class Hole(
        val hole_id:String,
        val name:String?,
        val latitude:Double,
        val longitude:Double,
        val number:Int,
        val index:Int,
        val par:Int,
        val yard:Int,
        val tees:List<Tee>?,
        val green: Green?,
        val image:String?
)