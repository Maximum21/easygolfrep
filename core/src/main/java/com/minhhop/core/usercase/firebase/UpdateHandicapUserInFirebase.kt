package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class UpdateHandicapUserInFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.updateHandicapUser(user, onComplete, errorResponse)
}