package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage

class UnregisterForLoginBySocial(private val userRepository: UserRepository) {
    suspend operator fun invoke(type: String, accessToken: String, phoneNumber: String, countryCode: String, email: String?,
                                 result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userRepository.unregisterSocial(type, accessToken, phoneNumber, countryCode, email, result, errorResponse)
}