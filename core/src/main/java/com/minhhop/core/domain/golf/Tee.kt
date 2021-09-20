package com.minhhop.core.domain.golf

class Tee (
        val id:String,
        val latitude:Double,
        val longitude:Double,
        type:String,
        yard:Double
) : BaseTee(yard, type)