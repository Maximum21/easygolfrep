package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundGolf

class StartRoundGolf(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(courseId:String, latitude: Double, longitude:Double, teeType:String, result: (RoundGolf)->Unit, errorResponse: (ErrorResponse)->Unit)
            = golfRepository.startRound(courseId, latitude, longitude, teeType, result, errorResponse)

}