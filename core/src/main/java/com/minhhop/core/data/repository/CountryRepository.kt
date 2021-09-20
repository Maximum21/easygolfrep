package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.CountryDataSource
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse

class CountryRepository(private val countryDataSource: CountryDataSource) {
    suspend fun gets(result: (List<Country>)->Unit,errorResponse: (ErrorResponse)->Unit) = countryDataSource.fetch(result,errorResponse)

    fun getsInLocal() = countryDataSource.getsInLocal()

    fun save(countries:List<Country>,onComplete:()->Unit) = countryDataSource.save(countries,onComplete)

    fun getDefault():Country? = countryDataSource.getDefault()

    fun isExit():Boolean = countryDataSource.isExit()

}