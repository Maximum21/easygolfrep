package com.minhhop.core.domain

data class PolicyTerm(
        val title:String,
        val content:String
){
    enum class Option(val value:String){
        TERMS("terms"),
        PRIVACY("privacy")
    }
}