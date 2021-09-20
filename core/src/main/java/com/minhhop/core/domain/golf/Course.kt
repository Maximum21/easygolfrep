package com.minhhop.core.domain.golf

data class Course(
        val id:String,
        val name:String?,
        val image:String?,
        val scorecard:List<Scorecard>?
)