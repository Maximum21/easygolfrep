package com.minhhop.easygolf.framework.dialogs

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import com.minhhop.easygolf.utils.AppUtil
import kotlinx.android.synthetic.main.dialog_host_end_game.*

class HostEndGameDialog(private val mActivity: Activity,
                        private val mListener: ListenerConfirmData?) : BaseDialog(mActivity) {

    override fun initView() {
        findViewById<View>(R.id.btAccept).setOnClickListener(this)
    }

    override fun config(window: Window?) {
        super.config(window)
        window?.let { _window ->
            val wlp = _window.attributes
            wlp.gravity = Gravity.CENTER
            _window.setLayout((AppUtil.getWidthScreen(mActivity) * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            _window.attributes = wlp
        }

    }

    override fun resContentView(): Int = R.layout.dialog_host_end_game

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.let { _v ->
            if (_v.id == R.id.btAccept) {
                btAccept.setStatus(EasyGolfButton.Status.PROCESS)
                mListener?.onConfirm(this)
            }
        }
    }

    interface ListenerConfirmData {
        fun onConfirm(dialog: HostEndGameDialog)
    }
}