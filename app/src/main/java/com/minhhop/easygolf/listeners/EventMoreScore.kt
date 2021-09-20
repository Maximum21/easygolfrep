package com.minhhop.easygolf.listeners

import androidx.annotation.DrawableRes

interface EventMoreScore {
    fun callMe()
    fun saveMe(score: Int, fairway: Int, green: Int, putt: Int, @DrawableRes resWhite: Int, @DrawableRes res: Int)
    fun close()
}