package com.minhhop.core.domain.golf

data class MatchGolf(
        val hole_id: String,
        val score: Int?,
        var putts: Int?,
        var fairway_hit: Int?,
        var green_in_regulation: Int?,
        val hole: Hole?,
        val tee: Tee?
) {
    companion object {
        fun fromDataScoreGolf(data: DataScoreGolf, holeId: String): MatchGolf {
            return MatchGolf(
                    holeId,
                    data.score,
                    data.putts,
                    data.fairwayHit,
                    data.greenInRegulation,
                    null,
                    null
            )
        }
    }

    fun toDataScoreGolf(roundId: String): DataScoreGolf = DataScoreGolf(
            null,
            this.score,
            this.fairway_hit,
            this.green_in_regulation,
            this.putts,
            roundId,
            hole?.number
    )
}