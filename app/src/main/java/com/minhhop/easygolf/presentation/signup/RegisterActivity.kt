package com.minhhop.easygolf.presentation.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.presentation.policy.PolicyTermsActivity
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.country.CountryActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.android.ext.android.inject

class RegisterActivity : EasyGolfActivity<SignUpViewModel>() {

//    private val registerViewModel:RegisterViewModel by inject()
//    private lateinit var mEditFirstName: EditText
//    private lateinit var mEditLastName: EditText
//    private lateinit var mEditEmail: EditText
//    private lateinit var mEditPhone: EditText
//    private lateinit var mEditPassword: EditText
//    private lateinit var mEditConfirmPassword: EditText
//    private lateinit var mErrorFirstName: TextInputLayout
//    private lateinit var mErrorLastName: TextInputLayout
//    private lateinit var mErrorEmail: TextInputLayout
//    private lateinit var mErrorPhone: TextInputLayout
//    private lateinit var mErrorPassword: TextInputLayout
//    private lateinit var mErrorConfirmPassword: TextInputLayout
//    private lateinit var mValueNameCountry: TextView
//    private lateinit var mImgFlag: ImageView
//
//    private var mIsoCode:String ? = null
//    override fun setLayoutView(): Int {
//        return R.layout.activity_register
//    }

    override fun initView() {


//        mEditFirstName = findViewById(R.id.editFirstName)
//        mEditLastName = findViewById(R.id.editLastName)
//        mEditEmail = findViewById(R.id.editEmail)
//        mEditPhone = findViewById(R.id.editPhone)
//        mEditPassword = findViewById(R.id.editPassword)
//        mEditConfirmPassword = findViewById(R.id.editConfirmPassword)
//        mValueNameCountry = findViewById(R.id.valueNameCountry)
//        findViewById<View>(R.id.btRegister).setOnClickListener(this)
//        findViewById<View>(R.id.pickerCountry).setOnClickListener(this)
//
//        mErrorFirstName = findViewById(R.id.errorFirstName)
//        mErrorLastName = findViewById(R.id.errorLastName)
//        mErrorEmail = findViewById(R.id.errorEmail)
//        mErrorPhone = findViewById(R.id.errorPhone)
//        mErrorPassword = findViewById(R.id.errorPassword)
//        mErrorConfirmPassword = findViewById(R.id.errorConfirmPassword)
//
//        mImgFlag = findViewById(R.id.imgFlag)
//        val termLink = findViewById<TextView>(R.id.termPolicyLink)
//        termLink.paintFlags = termLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
//
//        termLink.setOnClickListener { startActivity(PolicyTermsActivity::class.java) }
//
//
//        registerViewModel.getDefaultCountry()
//
//        registerViewModel.currentCountry.observe(this, Observer {
//            mIsoCode = it.iso
//            AppUtils.setFlagDrawableAssets(this, it.iso?:"", mImgFlag)
//            mValueNameCountry.text = getString(R.string.format_result_name_country,
//                    it.nice_name, it.phone_code.toString())
//        })
//
//        registerViewModel.missingField.observe(this, Observer {
//            removeError()
//            when(it){
//                R.string.error_first_name->{
//                    mErrorFirstName.isErrorEnabled = true
//                    mErrorFirstName.error = getString(R.string.error_first_name)
//                }
//                R.string.error_last_name->{
//                    mErrorLastName.isErrorEnabled = true
//                    mErrorLastName.error = getString(R.string.error_last_name)
//                }
//                R.string.error_email->{
//                    mErrorEmail.isErrorEnabled = true
//                    mErrorEmail.error = getString(R.string.error_email)
//                }
//                R.string.error_phone->{
//                    mErrorPhone.isErrorEnabled = true
//                    mErrorPhone.error = getString(R.string.error_phone)
//                }
//                R.string.error_password->{
//                    mErrorPassword.isErrorEnabled = true
//                    mErrorPassword.error = getString(R.string.error_password)
//                }
//                R.string.error_confirm_password->{
//                    mErrorConfirmPassword.isErrorEnabled = true
//                    mErrorConfirmPassword.error = getString(R.string.error_confirm_password)
//                }
//            }
//        })
//
//        registerViewModel.registerSuccess.observe(this, Observer {
//            EasyGolfNavigation1.verifyCodeDirection(this,it)
//            hideLoading()
//        })
//
//        registerViewModel.commonErrorLive.observe(this, Observer {
//            errorResponseMessage(it)
//        })
    }

    override val mViewModel: SignUpViewModel
        by inject()

    override fun setLayout(): Int = R.layout.activity_register

    override fun loadData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }
//    override fun onDestroy() {
//        registerViewModel.cancelAllRequest()
//        super.onDestroy()
//    }
//
//    override fun setIconToolbar(): Int {
//        return R.drawable.ic_icon_back_button_black
//    }
//
//    override fun getTitleToolBar(): String? {
//        return getString(R.string.back)
//    }
//
//
//    override fun onClick(v: View?) {
//        super.onClick(v)
//        when (v!!.id) {
//            R.id.btRegister -> actionRegister()
//            R.id.pickerCountry -> startActivityForResult(CountryActivity::class.java, Contains.REQUEST_CODE_COUNTRY)
//        }
//    }
//
//
//    override fun loadData() {}
//
//    private fun actionRegister() {
//
//        val valueFirstName = mEditFirstName.text.toString()
//        val valueLastName = mEditLastName.text.toString()
//        val valueEmail = mEditEmail.text.toString().trim { it <= ' ' }
//        val valuePhone = mEditPhone.text.toString()
//        val valuePassword = mEditPassword.text.toString()
//        val valueConfirmPassword = mEditConfirmPassword.text.toString()
//
//        registerViewModel.fetchRegister(valueFirstName,valueLastName,valueEmail,valuePhone,valuePassword,valueConfirmPassword)
//    }
//
//
//    private fun removeError(){
//
//        mErrorFirstName.isErrorEnabled = false
//        mErrorLastName.isErrorEnabled = false
//        mErrorEmail.isErrorEnabled = false
//        mErrorPhone.isErrorEnabled = false
//        mErrorPassword.isErrorEnabled = false
//        mErrorConfirmPassword.isErrorEnabled = false
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == Contains.REQUEST_CODE_COUNTRY && data != null) {
//                val valueName = data.getStringExtra(Contains.EXTRA_NAME_COUNTRY)
//                val iso = data.getStringExtra(Contains.EXTRA_ISO_COUNTRY)
//                registerViewModel.setCurrentCountry(valueName,iso)
//            }
//        }
//    }
}
