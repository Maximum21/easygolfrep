package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class SignUp(private val userRepository: UserRepository) {
    suspend operator fun invoke(firstName: String, lastName: String, countryCode:String, email:String,
                                phoneNumber:String, password: String,
                                result: (VerificationMessage)->Unit, errorResponse: (ErrorResponse)->Unit)
            = userRepository.signUp(firstName, lastName, countryCode, email, phoneNumber, password, result, errorResponse)
}