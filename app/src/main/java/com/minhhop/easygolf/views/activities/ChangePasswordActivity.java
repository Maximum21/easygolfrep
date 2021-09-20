package com.minhhop.easygolf.views.activities;


import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS;
import com.minhhop.easygolf.framework.models.ResetPassword;
import com.minhhop.easygolf.services.ApiService;

import java.util.Objects;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePasswordActivity extends WozBaseActivity {

    private AppCompatEditText mEditNewPassword;
    private AppCompatEditText mEditConfirmPassword;

    private TextInputLayout mLayoutErrorPassword;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btSubmit).setOnClickListener(this);

        mEditNewPassword = findViewById(R.id.editNewPassword);
        mEditConfirmPassword = findViewById(R.id.editConfirmPassword);
        mLayoutErrorPassword = findViewById(R.id.layoutErrorPassword);
    }


    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.btSubmit){

            if(Objects.requireNonNull(mEditNewPassword.getText()).toString().length() >= 6) {
                if (mEditNewPassword.getText().toString().equals(Objects.requireNonNull(mEditConfirmPassword.getText()).toString())) {

                    showLoading();
                    ResetPassword e = new ResetPassword(mEditNewPassword.getText().toString()
                            , mEditConfirmPassword.getText().toString());

                    ApiService.Companion.getInstance().getUserService().updatePassword(e)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                Toast.makeText(ChangePasswordActivity.this, "Password was changed", Toast.LENGTH_LONG).show();
                                finish();
                            }, throwable -> {

                                // show back button
                                hideLoading();
                                new AlertDialogIOS(ChangePasswordActivity.this, throwable.getLocalizedMessage(), null);
                            });


                } else {
                    mLayoutErrorPassword.setError(getString(R.string.error_confirm_password));
                }
            }else {
                mLayoutErrorPassword.setError(getString(R.string.error_password));
            }
        }
    }
}
