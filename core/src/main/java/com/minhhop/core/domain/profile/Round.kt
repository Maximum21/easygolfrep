package com.minhhop.core.domain.profile

import com.minhhop.core.domain.golf.Course
import com.minhhop.core.domain.golf.Hole

class Round(
        val id:String,
        val club_name:String?,
        val course_name:String?,
        val course_id:String?,
        val date:String?,
        val status:String?,
        val tee:String?,
        val unit:String?,
        val reason:String?,
        val holes:List<Hole>?,
        val course: Course?,
        val math: Hole?,
        val hdc:Double,
        val hcp:Double,
        val eagle:Double,
        val birdie:Double,
        val par:Double,
        val bogies:Double,
        val dbogies:Double,
        val albatross:Double,
        val others:Double,
        val over:Double,
        val scores:Double,
        val thru:Int,
        val differential:Double
)