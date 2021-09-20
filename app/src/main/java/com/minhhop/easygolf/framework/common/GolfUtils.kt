package com.minhhop.easygolf.framework.common

import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Scorecard
import java.math.RoundingMode
import java.text.DecimalFormat

object GolfUtils {
    fun calculateUserHandicapByCourse(scorecard: Scorecard,user: User):Double? {
        return if(canCalculateHandicapByCourse(scorecard)) {
            ((user.handicap?:0.0) * ((scorecard.sr?:0.0) / 113)) + ((scorecard.cr?:0.0) - scorecard.par)
        }else null
    }

    fun canCalculateHandicapByCourse(scorecard: Scorecard):Boolean = ((scorecard.sr?:0.0) > 0.0
            || (scorecard.cr?:0.0) > 0.0)

    fun roundHandicap(handicap:Double) : String {
        return if((handicap - handicap.toInt()) <= 0) handicap.toInt().toString()
        else {
            val df = DecimalFormat("#")
            df.roundingMode = RoundingMode.HALF_UP
            df.format(handicap)
        }
    }
}