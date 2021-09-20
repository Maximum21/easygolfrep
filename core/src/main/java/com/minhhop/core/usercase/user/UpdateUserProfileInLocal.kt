package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class UpdateUserProfileInLocal(private val userRepository: UserRepository) {
    operator fun invoke(user: User, onComplete: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userRepository.updateProfileUserInLocal(user, onComplete, errorResponse)
}