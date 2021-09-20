package com.minhhop.easygolf.framework.implementation

import com.minhhop.core.data.datasource.CountryDataSource
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.network.ConnectActivity
import com.minhhop.easygolf.framework.network.remote.GeneralService

class CountryImpl(private val generalService: GeneralService,
                  private val realmManager: RealmManager,
                  private val connectActivity: ConnectActivity): CountryDataSource {

    override fun save(countries: List<Country>, onComplete: () -> Unit) = realmManager.saveCountries(countries,onComplete)

    override fun getDefault(): Country? = realmManager.getDefaultCountry(null)

    override fun getsInLocal(): List<Country> = ArrayList()

    override fun isExit(): Boolean = false

    override suspend fun fetch(result: (List<Country>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        if(connectActivity.isHaveNetworkConnected()) {
            generalService.allCountries().run({countries->
                result(countries)
            },{
                errorResponse(it)
            })
        }else{
            errorResponse(ErrorResponse.noHaveNetwork())
        }
    }
}