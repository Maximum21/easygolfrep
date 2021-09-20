package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf

class PostScoreInBattleToFirebase(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(roundId: String, numberHole:String, user: User, data: DataScoreGolf, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.postScore(roundId, numberHole, user, data, onComplete, errorResponse)
}