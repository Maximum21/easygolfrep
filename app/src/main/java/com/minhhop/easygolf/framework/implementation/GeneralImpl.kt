package com.minhhop.easygolf.framework.implementation

import com.minhhop.core.data.datasource.GeneralDataSource
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.network.remote.GeneralService

class GeneralImpl(private val generalService: GeneralService,private val realmManager: RealmManager) : GeneralDataSource {
    override suspend fun fetchPolicyTerms(option: PolicyTerm.Option, result: (PolicyTerm) -> Unit, error: (ErrorResponse) -> Unit) {
        generalService.getTermsPolicy(option.value).run({
            result(it)
        },{
            error(it)
        })
    }

    override suspend fun fetchCountries(result: (List<Country>) -> Unit, error: (ErrorResponse) -> Unit) {
        realmManager.getCountries()?.let {
            result(it)
        }?:generalService.allCountries().run({
            realmManager.saveCountries(it){
                //ignore error
                result(it)
            }
        },{
            error(it)
        })
    }

    override fun searchCountries(keywords: String?): List<Country>? = realmManager.getCountries(keywords)

    override fun getDefaultCountry(iso:String?,result: (Country?) -> Unit, error: (ErrorResponse) -> Unit) {
        result(realmManager.getDefaultCountry(iso))
    }

    override fun getEasyGolfAppData(): EasyGolfApp? = realmManager.getEasyGolfAppData()

    override fun updateEasyGolfAppData(data: EasyGolfApp) {
        realmManager.updateEasyGolfAppData(data)
    }

    override fun deleteEasyGolfAppData(roundId: String?) {
        realmManager.deleteEasyGolfAppDataByRoundId(roundId)
    }
}