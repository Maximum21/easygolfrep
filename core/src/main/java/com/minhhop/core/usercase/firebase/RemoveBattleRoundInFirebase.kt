package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse

class RemoveBattleRoundInFirebase(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(roundId: String,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.removeBattleRound(roundId, onComplete, errorResponse)
}