package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf

class GetScoreUserInBattleFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String, numberHole:String, user: User, onComplete:(DataScoreGolf?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.getScoreUser(roundId, numberHole, user, onComplete, errorResponse)
}