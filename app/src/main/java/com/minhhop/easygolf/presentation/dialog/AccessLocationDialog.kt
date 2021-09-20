package com.minhhop.easygolf.presentation.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfDialog
import com.minhhop.easygolf.framework.common.Contains

class AccessLocationDialog(private val mActivity: Activity, private val inFragment: Fragment?) : EasyGolfDialog(mActivity) {
    override fun initView() {
        findViewById<View>(R.id.btEnableLocation).setOnClickListener(this)
    }

    override fun resContentView(): Int = R.layout.dialog_access_location

    override fun onClick(v: View) {
        super.onClick(v)
        if (v.id == R.id.btEnableLocation) {
            onClickEnableLocation()
            dismiss()
        }
    }

    fun setEventDismiss(onDismissListener: DialogInterface.OnDismissListener?): AccessLocationDialog {
        setOnDismissListener(onDismissListener)
        return this
    }

    private fun onClickEnableLocation() {
        if (inFragment == null) {
            mActivity.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    Contains.REQUEST_ACTION_LOCATION_SOURCE_SETTINGS)
        } else {
            inFragment.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    Contains.REQUEST_ACTION_LOCATION_SOURCE_SETTINGS)
        }
    }


    override fun cancelTouchOutside(): Boolean = false

}