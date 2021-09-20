package com.minhhop.easygolf.presentation.rules

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import kotlinx.coroutines.launch

class GolfRulesViewModel (override val interactors: Interactors) : GolfClubViewModel(interactors) {


    var rulesModel = MutableLiveData<List<GolfRule>>()

    fun getRulesFromServer() {
        mScope.launch {
            interactors.golfRulesRepository.invoke({ result->
                rulesModel.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun saveRules(rules: List<GolfRule>){
        mScope.launch {
            interactors.getRules.save(rules){

            }
        }
    }
    fun getRules(key:String) = interactors.getRules.invoke(key){
        mCommonErrorLive.postValue(it)
    }
}