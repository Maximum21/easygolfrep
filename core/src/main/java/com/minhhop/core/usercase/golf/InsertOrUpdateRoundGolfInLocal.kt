package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.RoundGolf

class InsertOrUpdateRoundGolfInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(roundGolf: RoundGolf,teeType: String?, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.insertOrUpdateRoundGolfInLocal(roundGolf, teeType, onComplete, errorResponse)
}