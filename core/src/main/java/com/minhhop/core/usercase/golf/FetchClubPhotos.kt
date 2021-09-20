package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.ClubPhoto

class FetchClubPhotos(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.fetchClubPhotos(clubId, result, errorResponse)
}