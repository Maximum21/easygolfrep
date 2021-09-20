package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class AddMembersToBattleInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String,courseId: String, users:List<User>, onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.addMembersToBattle(roundId,courseId, users, onComplete, errorResponse)
}