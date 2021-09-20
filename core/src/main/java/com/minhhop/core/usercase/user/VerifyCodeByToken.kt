package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse

class VerifyCodeByToken(private val userRepository: UserRepository) {
    suspend operator fun invoke(token:String,result: (Authorization)->Unit,errorResponse: (ErrorResponse)->Unit)
            = userRepository.verifyCode(token,result,errorResponse)
}