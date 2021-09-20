package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundHost
import com.minhhop.core.domain.profile.Round

class OnCompleteRoundGolf(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(roundId: String, roundHost: RoundHost, result: (Round) -> Unit, errorResponse: (ErrorResponse) -> Unit) = golfRepository.onCompleteRound(roundId, roundHost, result, errorResponse)
}