package com.minhhop.easygolf.presentation.round

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.round.RoundHistoryPager

class RoundHistoryRespository (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(start:Int,id:String,result: (RoundHistoryPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getRoundHistory(start,id,result, errorResponse)
}