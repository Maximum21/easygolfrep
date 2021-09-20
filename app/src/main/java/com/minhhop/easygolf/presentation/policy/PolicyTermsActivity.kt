package com.minhhop.easygolf.presentation.policy

import android.text.method.ScrollingMovementMethod
import androidx.lifecycle.Observer
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_policy_terms.*
import org.koin.android.ext.android.inject

class PolicyTermsActivity : EasyGolfActivity<PolicyTermsViewModel>() {

    override val mViewModel: PolicyTermsViewModel
            by inject()

    override fun setLayout(): Int = R.layout.activity_policy_terms

    override fun initView() {
        val valueOption = when(EasyGolfNavigation.policyTermBundle(intent)?: PolicyTerm.Option.PRIVACY.value){
            PolicyTerm.Option.PRIVACY.value->{
                PolicyTerm.Option.PRIVACY
            }
            PolicyTerm.Option.TERMS.value->{
                PolicyTerm.Option.TERMS
            }else->{
                PolicyTerm.Option.PRIVACY
            }
        }
        mViewModel.fetchPolicyTerm(valueOption)
        txtContent.movementMethod = ScrollingMovementMethod()
    }

    override fun loadData() {
        viewMask()
        mViewModel.policyTermLive.observe(this, Observer { result->
            txtContent.text = result.content
            hideMask()
        })
    }


}