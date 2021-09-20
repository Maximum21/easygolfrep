package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.Review

class FetchClubReview(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String, result: (List<Review>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.fetchClubReview(clubId, result, errorResponse)
}