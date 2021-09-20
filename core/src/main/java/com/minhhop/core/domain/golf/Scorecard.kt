package com.minhhop.core.domain.golf

class Scorecard(
        val par:Int,
        yard:Double,
        val distance:Double,
        type:String,
        val cr:Double?,
        val sr:Double?
) : BaseTee(yard, type)