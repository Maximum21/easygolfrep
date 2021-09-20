package com.minhhop.easygolf.framework.models.common

class Battle(
        val round_id:String = "",
        val club_id:String = "",
        @field:JvmField
        val is_host:Boolean = false
    )