package com.minhhop.easygolf.presentation.country

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.Country
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class CountryViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val countriesLiveData = MutableLiveData<List<Country>>()
    fun fetchCountries(keyword:String?){
        interactors.searchCountriesInLocal(keyword)?.let { countries->
            countriesLiveData.postValue(countries)
        }?: mScope.launch {
            interactors.fetchCountries({countries->
                countriesLiveData.postValue(countries)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}