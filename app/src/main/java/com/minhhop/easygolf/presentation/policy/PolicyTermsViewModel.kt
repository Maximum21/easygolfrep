package com.minhhop.easygolf.presentation.policy

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class PolicyTermsViewModel(private val interactors: Interactors) :  EasyGolfViewModel() {
    val policyTermLive = MutableLiveData<PolicyTerm>()

    fun fetchPolicyTerm(option: PolicyTerm.Option){
        mScope.launch {
            interactors.fetchPolicyTerm(option,{
                policyTermLive.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }

    }
}