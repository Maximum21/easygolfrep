package com.minhhop.core.usercase.country

import com.minhhop.core.data.repository.CountryRepository
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse

class GetCountries(private val countryRepository: CountryRepository) {
    suspend operator fun invoke(result: (List<Country>)->Unit,errorResponse: (ErrorResponse)->Unit) = countryRepository.gets(result,errorResponse)
}