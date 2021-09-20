package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.PolicyTerm

class FetchPolicyTerm(private val generalRepository: GeneralRepository) {
    suspend operator fun invoke(option: PolicyTerm.Option, result:(PolicyTerm)->Unit, error: (ErrorResponse)->Unit)
            = generalRepository.fetchPolicyTerms(option,result, error)
}