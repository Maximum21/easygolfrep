package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.ResultNotification
import com.minhhop.core.domain.User

class PushNotificationToMemberInBattle(private val golfRepository: GolfRepository) {

    suspend operator fun invoke(roundId: String, members: List<User>, result: (ResultNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.pushNotificationToMemberInBattle(roundId, members, result, errorResponse)
}