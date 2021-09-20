package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundGolf

class StartExploreCourse(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(courseId: String, latitude: Double, longitude: Double, typeTee:String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.exploreCourse(courseId, latitude, longitude, typeTee, result, errorResponse)
}