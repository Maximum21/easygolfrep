package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.ClubPager

class FetchRecommendClub(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(latitude: Double, longitude:Double,  start: Int, result: (ClubPager) -> Unit,
                                   errorResponse: (ErrorResponse) -> Unit, callback: (Any)->Unit)
            = golfRepository.fetchRecommendClub(latitude, longitude, start, result, errorResponse, callback)
}