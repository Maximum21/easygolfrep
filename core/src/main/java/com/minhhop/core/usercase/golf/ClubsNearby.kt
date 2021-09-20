package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.golf.ClubPager
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.course.RequestCourse
import com.minhhop.core.domain.course.RequestedCourse

class ClubsNearby(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(latitude: Double, longitude:Double, start:Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.nearby(latitude, longitude, start, keyword, result, errorResponse)
    suspend operator fun invoke(requestCourse: RequestCourse, result: (RequestedCourse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.requestCourse(requestCourse, result, errorResponse)
}