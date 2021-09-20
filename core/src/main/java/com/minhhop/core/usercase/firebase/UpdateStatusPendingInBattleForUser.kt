package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class UpdateStatusPendingInBattleForUser(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(isPending: Boolean,roundId: String, user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.updateStatusPendingInBattle(isPending, roundId,user, onComplete, errorResponse)
}