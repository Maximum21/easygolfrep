package com.minhhop.core.data.datasource

import com.minhhop.core.domain.Country
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.PolicyTerm
import com.sun.org.apache.xpath.internal.compiler.Keywords

interface GeneralDataSource {
    suspend fun fetchPolicyTerms(option: PolicyTerm.Option, result: (PolicyTerm) -> Unit, error: (ErrorResponse) -> Unit)
    suspend fun fetchCountries(result: (List<Country>) -> Unit, error: (ErrorResponse) -> Unit)
    fun searchCountries(keywords: String?) : List<Country>?
    fun getDefaultCountry(iso:String?,result: (Country?) -> Unit, error: (ErrorResponse) -> Unit)
    fun getEasyGolfAppData(): EasyGolfApp?
    fun updateEasyGolfAppData(data:EasyGolfApp)
    fun deleteEasyGolfAppData(roundId:String?)
}