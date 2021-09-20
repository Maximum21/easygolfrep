package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.firebase.BattleRound

class GetBattleRoundFindFirstInFirebaseForCurrentUser(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(onComplete:(BattleRound?)->Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseRepository.getBattleRoundFindFirst(onComplete, errorResponse)
}