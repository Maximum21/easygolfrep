package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.golf.DataScoreGolf

class GetScoreAtHole(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId: String,holeId: String) : DataScoreGolf? = golfRepository.getDataScoreGolf(roundId, holeId)
}