package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse

class ChangeTeeTypeForRoundGolfInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId: String,teeType: String?,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.changeTeeTypeForRoundGolfInLocal(roundId, teeType, onComplete, errorResponse)
}