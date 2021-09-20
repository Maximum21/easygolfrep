package com.minhhop.core.domain.golf

data class DataScoreGolf(
        var id: String?,
        var score: Int?,
        /**
         *@fairway left : 0
         *@fairway center : 1
         *@fairway right : 2
         * */
        var fairwayHit: Int?,
        /**
         *@greenInRegulation false : 0 - miss
         *@greenInRegulation true: 1 - hit
         * */
        var greenInRegulation: Int?,
        /**
         *@putt 0...4
         * */
        var putts: Int?,
        var roundId: String?,
        var number: Int?
) {
    enum class FairwayHit {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }
    enum class GreenInRegulation {
        MISS,
        HIT,
        NONE
    }

    enum class PUTT {
        EXIT,
        NONE
    }

    fun getFairwayHit():FairwayHit {
        return when (this.fairwayHit) {
            0 -> {
                FairwayHit.LEFT
            }
            1 -> {
                FairwayHit.CENTER
            }
            2 -> {
                FairwayHit.RIGHT
            }
            else->{
                FairwayHit.NONE
            }
        }
    }

    fun getGreenInRegulation():GreenInRegulation{
        return when (this.greenInRegulation) {
            0 -> {
                GreenInRegulation.MISS
            }
            1 -> {
                GreenInRegulation.HIT
            }
            else->{
                GreenInRegulation.NONE
            }
        }
    }

    fun getPutt():PUTT{
        return if((this.putts?:-1) <= -1){
            PUTT.NONE
        }else PUTT.EXIT
    }

    fun isComplete() = score != null && (score ?: 0) > 0
}