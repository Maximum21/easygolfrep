package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundGolf

class GetRoundGolfByCourseInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(courseId: String, errorResponse: (ErrorResponse) -> Unit) : RoundGolf? = golfRepository.getRoundGolfByCourseInLocal(courseId)
}