package com.minhhop.easygolf.framework.bundle
@Deprecated("remove v1",ReplaceWith("PlayGolfBundle"))
data class PlayGolfBundle1(
        val playWithBattle:Boolean = false,
        val round:String?,
        val clubId:String,
        val courseId:String,
        val exitGameBattle:Boolean = false
)