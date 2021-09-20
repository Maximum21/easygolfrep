package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository


class IsReadySignIn(private val userRepository: UserRepository) {
    operator fun invoke() = userRepository.isReadySignIn()
}