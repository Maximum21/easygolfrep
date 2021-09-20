package com.minhhop.easygolf.presentation.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import androidx.lifecycle.Observer
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.models.UpdateUser
import com.minhhop.easygolf.framework.models.UserFirebase
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import com.minhhop.easygolf.services.ApiService.Companion.getInstance
import com.minhhop.easygolf.utils.AppUtil
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.android.ext.android.inject
import java.util.*

class SignUpActivity : EasyGolfActivity<SignUpViewModel>() {

    override val mViewModel: SignUpViewModel
            by inject()

    override fun setLayout(): Int = R.layout.activity_sign_up

    override fun initView() {

        mViewModel.currentCountryLiveData.observe(this, Observer {
            valueNameCountry.text = getString(R.string.format_result_name_country, it.nice_name, it.phone_code.toString())
            AppUtil.setFlagDrawableAssets(this, it.iso, imgFlag)
        })

        mViewModel.registerSuccess.observe(this, Observer {
            EasyGolfNavigation.verifyCodeDirection(this,it)
        })

        termPolicyLink.paintFlags = termPolicyLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        termPolicyLink.setOnClickListener { EasyGolfNavigation.policyTermDirection(this, PolicyTerm.Option.TERMS) }

        pickerCountry.setOnClickListener {
            EasyGolfNavigation.countryDirection(this)
        }

        btRegister.setOnClickListener {
            if (btRegister.getStatus() == EasyGolfButton.Status.IDLE) {

                editFirstName.text?.toString().let { valueFirstName ->

                    editLastName.text?.toString().let { valueLastName ->
                        editEmail.text?.toString()?.trim { it <= ' ' }.let { valueEmail ->
                            editPhone.text?.toString().let { valuePhone ->
                                editPassword.text?.toString().let { valuePassword ->
                                    editConfirmPassword.text?.toString().let { valueConfirmPassword ->
                                        if (!valueFirstName.isNullOrEmpty()) {
                                            if (!valueLastName.isNullOrEmpty()) {
                                                if (!valueEmail.isNullOrEmpty() && AppUtils.isValidEmail(valueEmail)) {
                                                    if (!valuePhone.isNullOrEmpty()) {
                                                        if (!valuePassword.isNullOrEmpty()) {
                                                            if (!valueConfirmPassword.isNullOrEmpty()) {
                                                                if (valuePassword == valueConfirmPassword) {
                                                                    btRegister.setStatus(EasyGolfButton.Status.PROCESS)
                                                                    viewMask()
                                                                    mViewModel.fetchApiRegister(valueFirstName,
                                                                            valueLastName, valueEmail, valuePhone, valuePassword)
                                                                } else {
                                                                    showCommonMessage(getString(R.string.error_confirm_password))
                                                                }
                                                            } else {
                                                                showCommonMessage(getString(R.string.error_confirm_password))
                                                            }
                                                        } else {
                                                            showCommonMessage(getString(R.string.error_password))
                                                        }
                                                    } else {
                                                        showCommonMessage(getString(R.string.error_phone))
                                                    }
                                                } else {
                                                    showCommonMessage(getString(R.string.error_email))
                                                }
                                            } else {
                                                showCommonMessage(getString(R.string.error_last_name))
                                            }
                                        } else {
                                            showCommonMessage(getString(R.string.error_first_name))
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun commonError() {
        super.commonError()
        btRegister.setStatus(EasyGolfButton.Status.IDLE)
    }

    override fun loadData() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Contains.REQUEST_CODE_COUNTRY && resultCode == Activity.RESULT_OK) {
            EasyGolfNavigation.countryBundle(data)?.toCountry()?.let { countrySelected ->
                mViewModel.setCountry(countrySelected)
            }
        }
    }
}