package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class RemoveMemberFromBattleInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String, user: User, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.removeMemberFromBattle(roundId, user, onComplete, errorResponse)
}