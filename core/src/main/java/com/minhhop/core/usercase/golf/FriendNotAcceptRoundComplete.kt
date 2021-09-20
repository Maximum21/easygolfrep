package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class FriendNotAcceptRoundComplete(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.friendNotAcceptRoundComplete(roundId, result, errorResponse)
}