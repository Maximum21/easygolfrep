package com.minhhop.easygolf.presentation.rules

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.rule.GolfRule

class GolfRulesRepository (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(result: (List<GolfRule>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getRules(result, errorResponse)
}