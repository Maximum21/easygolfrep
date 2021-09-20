package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse

class SignIn(private val userRepository: UserRepository) {
    suspend operator fun invoke(userName:String,password:String,result: (Authorization)->Unit,errorResponse: (ErrorResponse)->Unit)
            = userRepository.signIn(userName, password,result, errorResponse)
}