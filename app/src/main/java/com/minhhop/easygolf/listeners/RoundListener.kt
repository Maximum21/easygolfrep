package com.minhhop.easygolf.listeners

import android.widget.ImageView
import android.widget.TextView

import com.minhhop.easygolf.framework.models.RoundMatch

interface RoundListener {
    fun onClick(roundMatch: RoundMatch, imgCourse: ImageView?, txtName: TextView)
}
