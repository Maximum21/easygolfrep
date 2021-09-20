package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class ResendCodeOTP(private val userRepository: UserRepository) {
    suspend operator fun invoke(phone:String, ios:String, onComplete: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse)->Unit) = userRepository.resendCode(phone, ios, onComplete, errorResponse)
}