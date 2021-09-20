package com.minhhop.easygolf.presentation.custom.bottomnavigation

import android.graphics.Color

data class ColorRGBHolder(
        val red:Int,
        val green:Int,
        val blue:Int
){
    fun toColor() = Color.rgb(red, green, blue)
}