package com.minhhop.core.usercase.country

import com.minhhop.core.data.repository.CountryRepository
import com.minhhop.core.domain.Country

class SaveCountries(private val countryRepository: CountryRepository) {
    operator fun invoke(countries:List<Country>,onComplete:()->Unit) = countryRepository.save(countries,onComplete)
}