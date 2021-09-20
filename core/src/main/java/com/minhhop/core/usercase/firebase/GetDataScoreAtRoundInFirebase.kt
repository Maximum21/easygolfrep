package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.ScorecardModel

class GetDataScoreAtRoundInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String, onComplete:(List<ScorecardModel>?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.getDataScoreAtRound(roundId, onComplete, errorResponse)
}