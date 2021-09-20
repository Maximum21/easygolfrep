package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse

class IsRoundExitInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String,onResult: (Boolean) -> Unit,errorResponse: (ErrorResponse) -> Unit) = firebaseRepository.isRoundExit(roundId, onResult, errorResponse)
}