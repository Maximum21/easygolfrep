package com.minhhop.core.usercase.general

import com.minhhop.core.data.repository.GeneralRepository

class SearchCountriesInLocal(private val generalRepository: GeneralRepository) {
    operator fun invoke(keywords: String?) = generalRepository.searchCountries(keywords)
}