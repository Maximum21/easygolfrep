package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse

class FetchCountries(private val generalRepository: GeneralRepository) {
    suspend operator fun invoke(result: (List<Country>) -> Unit, error: (ErrorResponse) -> Unit) = generalRepository.fetchCountries(result, error)
}