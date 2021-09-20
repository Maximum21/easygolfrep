package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.golf.Feedback

class SendFeedbackGolf(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(feedback: Feedback, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.sendFeedback(feedback, result, errorResponse)
}