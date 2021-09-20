package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.UserRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.rule.GolfRule

class GetGolfRulesFromLocal(private val userRepository: UserRepository) {
    operator fun invoke(key:String,errorResponse: (ErrorResponse)->Unit) : List<GolfRule>? = userRepository.getRulesFromLocal(key,errorResponse)
    fun save(rules:List<GolfRule>, onComplete:()->Unit) = userRepository.save(rules,onComplete)
}