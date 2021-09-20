package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse

class ChangeTeeTypeInTheBattle(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(teeType:String,roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.changeTeeTypeInBattle(teeType, roundId, onComplete, errorResponse)
}