package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.DataScoreGolf

class GetAllDataScoreGolfByRoundInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId: String, errorResponse: (ErrorResponse) -> Unit): List<DataScoreGolf>?
            = golfRepository.getDataScoreGolfOfRound(roundId, errorResponse)
}