package com.minhhop.easygolf.framework.models.request

data class StartRoundRequest(
        val latitude : Double,
        val longitude : Double,
        val tee: String
)