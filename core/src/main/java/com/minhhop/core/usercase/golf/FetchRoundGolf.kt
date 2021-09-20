package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundGolf

class FetchRoundGolf(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(roundId: String, result: (RoundGolf)->Unit, errorResponse: (ErrorResponse)->Unit) = golfRepository.fetchRound(roundId, result, errorResponse)
}