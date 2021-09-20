package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.Hole

class UpdateMathForRoundGolfInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId: String, hole: Hole, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.updateMathForRoundGolfInLocal(roundId, hole, onComplete, errorResponse)
}