package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.GeneralDataSource
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.PolicyTerm

class GeneralRepository(private val generalDataSource: GeneralDataSource) {
    suspend fun fetchPolicyTerms(option: PolicyTerm.Option, result:(PolicyTerm)->Unit, error: (ErrorResponse)->Unit)
            = generalDataSource.fetchPolicyTerms(option,result, error)
    suspend fun fetchCountries(result: (List<Country>) -> Unit, error: (ErrorResponse) -> Unit) = generalDataSource.fetchCountries(result, error)
    fun searchCountries(keywords: String?) : List<Country>? = generalDataSource.searchCountries(keywords)
    fun getDefaultCountry(iso:String?,result: (Country?) -> Unit, error: (ErrorResponse) -> Unit) = generalDataSource.getDefaultCountry(iso,result, error)
    fun getEasyGolfAppData(): EasyGolfApp? = generalDataSource.getEasyGolfAppData()
    fun updateEasyGolfAppData(data:EasyGolfApp) = generalDataSource.updateEasyGolfAppData(data)
    fun deleteEasyGolfAppData(roundId:String?) = generalDataSource.deleteEasyGolfAppData(roundId)
}