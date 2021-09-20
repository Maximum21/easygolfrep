package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse

class GetDefaultCountryByLocale(private val generalRepository: GeneralRepository) {
    operator fun invoke(iso:String?,result: (Country?) -> Unit, error: (ErrorResponse) -> Unit) = generalRepository.getDefaultCountry(iso,result, error)
}