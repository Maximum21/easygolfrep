package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class GetProfileUserFromFirebase (private val firebaseRepository: FirebaseRepository){
    operator fun invoke(id:String, onComplete:(User?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.getProfileUser(id, onComplete, errorResponse)
}