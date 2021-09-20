package com.minhhop.core.usercase.country

import com.minhhop.core.data.repository.CountryRepository

class IsCountriesExitInLocal(private val countryRepository: CountryRepository) {
    operator fun invoke() = countryRepository.isExit()
}