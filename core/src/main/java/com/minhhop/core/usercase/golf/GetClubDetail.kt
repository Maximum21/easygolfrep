package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.Club

class GetClubDetail(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String, result: (Club) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getClubDetail(clubId, result, errorResponse)
}