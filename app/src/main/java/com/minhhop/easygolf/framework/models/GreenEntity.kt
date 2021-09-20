package com.minhhop.easygolf.framework.models

import com.minhhop.easygolf.R

class GreenEntity{
    enum class FLAG {
        BLUE,
        WHITE,
        RED
    }

     companion object{
         fun getDrawFlagGreen(flag: FLAG): Int {
             return when (flag) {
                FLAG.BLUE ->  R.drawable.ic_icon_flag_blue
                 FLAG.WHITE -> R.drawable.ic_icon_flag_white
                 FLAG.RED ->  R.drawable.ic_icon_flag_red
             }
         }

     }
}