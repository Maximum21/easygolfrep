package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.Review

class AddReviewForClub(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String,rate:Int, content:String?, reviewId: String?, result: (Review) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.addReviewForClub(clubId, rate, content, reviewId, result, errorResponse)
}