package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class FetchFriends (private val userRepository: UserRepository) {
    suspend operator fun invoke(result: (List<User>) -> Unit, errorResponse: (ErrorResponse)->Unit) = userRepository.fetchFriends(result, errorResponse)
}