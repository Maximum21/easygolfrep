package com.minhhop.core.data.datasource

import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse

@Deprecated("CountryDataSource", ReplaceWith(" use GeneralDataSource"))
interface CountryDataSource {
    suspend fun fetch(result: (List<Country>)->Unit,errorResponse: (ErrorResponse)->Unit)

    fun getsInLocal():List<Country>

    fun save(countries:List<Country>,onComplete:()->Unit)

    fun getDefault():Country?

    fun isExit():Boolean
}