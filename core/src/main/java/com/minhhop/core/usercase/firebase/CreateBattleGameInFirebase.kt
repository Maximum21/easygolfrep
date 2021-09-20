package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class CreateBattleGameInFirebase(private val firebaseRepository: FirebaseRepository) {
    suspend operator fun invoke(roundId: String, courseId: String, friends: List<User>?, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.createBattleGame(roundId, courseId, friends, onComplete, errorResponse)
}