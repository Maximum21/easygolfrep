package com.minhhop.core.usercase.user

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class UpdateProfileUser(private val userRepository: UserRepository) {
    suspend operator fun invoke(firstName: String,lastName: String,email: String?,
                                  countryCode:String,phoneNumber: String?,birthday:String?,
                                  gender:String?,phoneNotification:Boolean,emailNotification:Boolean,
                                  result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userRepository.updateProfileUser(firstName, lastName, email, countryCode, phoneNumber, birthday, gender, phoneNotification, emailNotification, result, errorResponse)
}