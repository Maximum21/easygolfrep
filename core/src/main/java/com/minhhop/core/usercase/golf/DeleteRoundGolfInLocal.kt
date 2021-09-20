package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse

class DeleteRoundGolfInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(roundId:String,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.deleteRoundGolfInLocal(roundId, onComplete, errorResponse)
}