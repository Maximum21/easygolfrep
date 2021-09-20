package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.DataScoreGolf

class InsertOrUpdateScoreHole(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId: String, holeId:String, data: DataScoreGolf, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.insertOrUpdateDataScoreGolf(roundId, holeId,data, onComplete, errorResponse)
}