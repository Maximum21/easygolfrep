package com.minhhop.core.domain.golf

data class Green(
        val id:String,
        val red: EasyGolfLocation?,
        val white: EasyGolfLocation?,
        val blue: EasyGolfLocation?,
        val coordinates:List<EasyGolfLocation>?
){

    enum class FLAG {
        BLUE,
        WHITE,
        RED
    }

    fun getDefaultGreen(flag: FLAG? = null): EasyGolfLocation? {
        if (flag == null) {
            return this.white
        }
        return when (flag) {
            FLAG.RED -> this.red
            FLAG.BLUE -> this.blue
            FLAG.WHITE -> this.white
        }
    }
}