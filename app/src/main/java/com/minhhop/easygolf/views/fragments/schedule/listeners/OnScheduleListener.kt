package com.minhhop.easygolf.views.fragments.schedule.listeners

import android.widget.ImageView
import android.widget.TextView
import com.minhhop.easygolf.framework.models.Tournament

interface OnScheduleListener {
    fun onClick(imgCourse: ImageView,txtName:TextView,tournament: Tournament)
}