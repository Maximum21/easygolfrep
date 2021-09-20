package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.golf.RoundGolf

class GetRoundGolfExitInLocal(private val golfRepository: GolfRepository) {
    operator fun invoke(): RoundGolf? = golfRepository.getRoundGolfExitInLocal()
}