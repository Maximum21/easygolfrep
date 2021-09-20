package com.minhhop.core.domain.golf

data class PassScore(
        val course_id:String,
        val gross_score:Double,
        val date:Long,
        val adjusted_score:Double,
        val tee:String
)