package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository

class SaveAccessTokenInLocal(private val userRepository: UserRepository) {
    operator fun invoke(accessToken:String) = userRepository.saveAccessTokenInLocal(accessToken)
}