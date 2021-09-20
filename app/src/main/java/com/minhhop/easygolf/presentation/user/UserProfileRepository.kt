package com.minhhop.easygolf.presentation.user

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.profile.PeopleResponse

class UserProfileRepository (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(id: String, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getUserProfile(id,result, errorResponse)
    suspend operator fun invoke(id: String, message:String, tag:Int, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.requestAction(id,message,tag,result, errorResponse)
}