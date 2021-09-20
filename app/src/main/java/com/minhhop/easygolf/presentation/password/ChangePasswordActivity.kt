package com.minhhop.easygolf.presentation.password

import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import kotlinx.android.synthetic.main.activity_change_password.*
import org.koin.android.ext.android.inject
import java.util.*

class ChangePasswordActivity : EasyGolfActivity<ChangePasswordViewModel>(),View.OnClickListener {

    override val mViewModel: ChangePasswordViewModel by inject()
    

    private var mLayoutErrorPassword: TextInputLayout? = null

    override fun setLayout(): Int {
        return R.layout.activity_change_password
    }

    override fun initView() {
        btSubmit.setOnClickListener(this)
        mLayoutErrorPassword = findViewById(R.id.layoutErrorPassword)
        mViewModel.changePassword.observe(this, Observer {
            Toast.makeText(this@ChangePasswordActivity, "Password was changed", Toast.LENGTH_LONG).show()
            finish()
        })
    }

    override fun loadData() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btSubmit->{
                if (Objects.requireNonNull(editNewPassword!!.text).toString().length >= 6) {
                    if (editNewPassword!!.text.toString() == Objects.requireNonNull(editConfirmPassword!!.text).toString()) {
                        viewMask()
                        mViewModel.ChangePassword(ResetPassword(editNewPassword!!.text.toString()
                                , editConfirmPassword!!.text.toString()))
                    } else {
                        mLayoutErrorPassword!!.error = getString(R.string.error_confirm_password)
                    }
                } else {
                    mLayoutErrorPassword!!.error = getString(R.string.error_password)
                }
            }
        }
    }
}