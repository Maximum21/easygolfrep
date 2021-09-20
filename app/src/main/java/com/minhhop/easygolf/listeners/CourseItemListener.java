package com.minhhop.easygolf.listeners;

import android.widget.ImageView;
import android.widget.TextView;

import com.minhhop.easygolf.framework.models.Course;

public interface CourseItemListener {
    void onClick(Course course, ImageView imgCourse, TextView txtName);
}
