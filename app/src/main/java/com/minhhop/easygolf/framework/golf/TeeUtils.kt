package com.minhhop.easygolf.framework.golf

import android.graphics.Color
import com.minhhop.core.domain.golf.BaseTee
import com.minhhop.easygolf.R

class TeeUtils{
    companion object{
        const val DEFAULT_TEE_TYPE = "BLUE"
        fun <T: BaseTee> getDefault(tees: List<T>?): T? {
            return getTeeByType(tees, DEFAULT_TEE_TYPE)
        }

        fun <T: BaseTee> getTeeByType(tees: List<T>?, type: String?): T? {
            tees?.forEach { tee->
                if(tee.type == type?:DEFAULT_TEE_TYPE){
                    return tee
                }
            }
            return if(tees?.isNotEmpty() == true) tees.first() else null
        }

        fun getColorByType(type: String? = DEFAULT_TEE_TYPE): Int {
            return when (type) {
                "RED" -> Color.parseColor("#c6342d")
                "WHITE" -> Color.parseColor("#FFF1F1F1")
                "GREEN" -> Color.parseColor("#4c8e06")
                "BLUE" -> Color.parseColor("#FF0D5C93")
                "YELLOW" -> Color.parseColor("#f6dc03")
                "BLACK" -> Color.parseColor("#000000")
                "PLATINUM" -> Color.parseColor("#d4d4d4")
                "PURPLE" -> Color.parseColor("#dea0dd")
                "SEPIA" -> Color.parseColor("#704214")
                "GREY" -> Color.parseColor("#666666")
                "GRAY" -> Color.parseColor("#a9a9a9")
                "SHARK" -> Color.parseColor("#006272")
                "BROWN" -> Color.parseColor("#793802")
                "LIME" -> Color.parseColor("#c8e260")
                "PINK" -> Color.parseColor("#cf5b85")
                "ORANGE" -> Color.parseColor("#e8722e")
                "JADE" -> Color.parseColor("#00a86b")
                "COPPER" -> Color.parseColor("#cf8d6d")
                "SLIVER" -> Color.parseColor("#bbc2c2")
                "GOLD" -> Color.parseColor("#F5DA31")
                else -> Color.parseColor("#FF0D5C93")
            }

        }

        fun getResIcon(tee: BaseTee): Int {
            return when (tee.type) {
                "RED" -> R.drawable.ic_red_tee_icon
                "WHITE" -> R.drawable.ic_white_tee_icon
                "GREEN" -> R.drawable.ic_tee_green_icon
                "BLUE" -> R.drawable.ic_icon_blue_tee
                "YELLOW" -> R.drawable.ic_yelow_tee_icon
                "BLACK" -> R.drawable.ic_icon_black_tee
                "PLATINUM" -> R.drawable.ic_icon_platinum_tee
                "PURPLE" -> R.drawable.ic_icon_purple_tee
                "SEPIA" -> R.drawable.ic_icon_sepia_tee
                "GREY" -> R.drawable.ic_icon_grey_tee
                "GRAY" -> R.drawable.ic_icon_gray_tee
                "SHARK" -> R.drawable.ic_icon_shark_tee
                "BROWN" -> R.drawable.ic_icon_brown_tee
                "LIME" -> R.drawable.ic_icon_lime_tee
                "PINK" -> R.drawable.ic_icon_pink_tee_icon
                "ORANGE" -> R.drawable.ic_orange_tee_icon
                "JADE" -> R.drawable.ic_icon_jade_tee
                "COPPER" -> R.drawable.ic_icon_copper_tee
                "SLIVER" -> R.drawable.ic_icon_silver_tee
                "GOLD" -> R.drawable.ic_yelow_tee_icon
                else -> R.drawable.ic_orange_tee_icon
            }
        }
    }
}