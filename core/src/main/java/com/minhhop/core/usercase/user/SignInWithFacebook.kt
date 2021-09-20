package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse

class SignInWithFacebook(private val userRepository: UserRepository) {
    suspend operator fun invoke(firstName:String,lastName:String,brand:String,
                                result: (Authorization)->Unit, errorResponse: (ErrorResponse)->Unit)
            = userRepository.signInWithFacebook(firstName, lastName, brand, result, errorResponse)
}