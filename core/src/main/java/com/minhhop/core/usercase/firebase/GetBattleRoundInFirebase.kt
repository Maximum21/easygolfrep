package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.firebase.BattleRound

class GetBattleRoundInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId:String, userId:String, onComplete:(BattleRound?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.getBattleRound(roundId, userId, onComplete, errorResponse)
}