package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.golf.ClubPager
import com.minhhop.core.domain.ErrorResponse

class FetchSuggestedClubs(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(latitude: Double, longitude:Double, start:Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.fetchSuggestedClubs(latitude, longitude, start, keyword, result, errorResponse)
}