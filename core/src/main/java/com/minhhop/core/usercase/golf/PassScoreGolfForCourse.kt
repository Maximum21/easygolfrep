package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.golf.PassScore

class PassScoreGolfForCourse (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(passScore: PassScore, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.passScoreGolf(passScore, result, errorResponse)
}