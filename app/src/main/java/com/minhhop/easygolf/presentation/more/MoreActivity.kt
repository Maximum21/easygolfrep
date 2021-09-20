package com.minhhop.easygolf.presentation.more

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.dialogs.ConfirmDialog
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.presentation.policy.PolicyTermsActivity
import com.minhhop.easygolf.presentation.signin.SignInActivity
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.views.activities.AboutActivity
import com.minhhop.easygolf.views.activities.AccountSettingActivity
import com.minhhop.easygolf.views.activities.ChangePasswordActivity
import com.minhhop.easygolf.views.activities.RuleGolfActivity
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject

class MoreActivity : WozBaseActivity() {

    private val moreViewModel: MoreViewModel by inject()

    private var mImgAvatar: ImageView? = null
    private var mTxtName: TextView? = null


    override fun setLayoutView(): Int {
        return R.layout.fragment_more
    }

    override fun initView() {
        findViewById<View>(R.id.actionChangePassword).setOnClickListener(this)
        findViewById<View>(R.id.viewTerms).setOnClickListener(this)
        findViewById<View>(R.id.viewPolicy).setOnClickListener(this)
        findViewById<View>(R.id.viewAboutUs).setOnClickListener(this)
        findViewById<View>(R.id.viewAccountSetting).setOnClickListener(this)
        findViewById<View>(R.id.viewRuleGolf).setOnClickListener(this)

        mImgAvatar = findViewById(R.id.imgAvatar)
        mTxtName = findViewById(R.id.txtName)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.viewAccountSetting -> if (DatabaseService.getInstance().currentUserEntity != null) {
                startActivity(AccountSettingActivity::class.java)
            }
            R.id.viewAboutUs -> startActivity(AboutActivity::class.java)

            R.id.actionChangePassword -> startActivity(ChangePasswordActivity::class.java)
            R.id.viewTerms -> startActivity(PolicyTermsActivity::class.java)
            R.id.viewPolicy -> goPolicy()
            R.id.viewRuleGolf -> startActivity(RuleGolfActivity::class.java)
        }
    }

    private fun goPolicy() {
        val bundle = Bundle()
        bundle.putBoolean(Contains.EXTRA_POLICY, true)
        startActivity(PolicyTermsActivity::class.java, bundle)
    }

    //    @Override
    //    protected int setIconToolbar() {
    //        return R.drawable.ic_icon_back_button_black;
    //    }

    override fun getTitleToolBar(): String? {
        return getString(R.string.back)
    }

    override fun loadData() {

    }

    override fun onStart() {
        super.onStart()

        registerResponse(ApiService.getInstance().userService.profile(),object: HandleResponse<UserEntity> {
            override fun onSuccess(result: UserEntity) {
                setValueUser(result)
            }

        })
    }


    private fun setValueUser(userEntity: UserEntity) {
        if (!TextUtils.isEmpty(userEntity.avatar)) {
            Picasso.get().load(userEntity.avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar)
        } else {
            Picasso.get().load(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar)
        }

        mTxtName!!.text = userEntity.fullName
    }

    override fun setMenuRes(): Int {
        return R.menu.menu_more
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionLogout) {
            ConfirmDialog(this)
                    .setOnConfirm { this.logout() }
                    .show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        DatabaseService.getInstance()
                .removeToken{
                    startActivity(SignInActivity::class.java,
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    moreViewModel.logout()
                    finish()
                }
    }
}
