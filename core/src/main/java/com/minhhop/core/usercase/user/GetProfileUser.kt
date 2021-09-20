package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.password.ResetPassword
import sun.security.util.Password

class GetProfileUser(private val userRepository: UserRepository) {
    suspend operator fun invoke(result: (User)->Unit, errorResponse: (ErrorResponse)->Unit) = userRepository.getProfile(result,errorResponse)
    suspend operator fun invoke(resetPassword: ResetPassword,tag:Int,result: (User)->Unit, errorResponse: (ErrorResponse)->Unit) = userRepository.resetPasswrod(resetPassword,result,errorResponse)
}