package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class GetProfileInLocal(private val userRepository: UserRepository) {
    operator fun invoke(errorResponse: (ErrorResponse)->Unit) : User? = userRepository.getProfileInLocal(errorResponse)
}