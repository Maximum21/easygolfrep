package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class UserForgotPassword(private val userRepository: UserRepository) {
    suspend operator fun invoke(phoneNumber: String, countryCode: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userRepository.forgotPassword(phoneNumber, countryCode, result, errorResponse)
}