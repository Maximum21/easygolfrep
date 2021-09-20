package com.minhhop.easygolf.framework.dialogs

import android.app.Activity
import android.content.DialogInterface
import android.view.*
import android.widget.Button
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog
import com.minhhop.easygolf.utils.AppUtil

class NoNetworkDialog(private val mActivity: Activity, private val onCancelListener: DialogInterface.OnCancelListener?) :
        BaseDialog(mActivity), View.OnClickListener {

    private lateinit var mButtonTryAgain:Button

    override fun initView() {

       mButtonTryAgain = findViewById(R.id.bt_try_again)
        mButtonTryAgain.setOnClickListener {
            onCancelListener?.onCancel(this)
            dismiss()
        }
    }

    override fun config(window: Window?) {
        super.config(window)
        window?.let { _window->
            val wlp = _window.attributes
            wlp.gravity = Gravity.CENTER
            _window.setLayout((AppUtil.getWidthScreen(mActivity) * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            _window.attributes = wlp
        }
    }

    override fun resContentView(): Int {
        return R.layout.dialog_no_network
    }


    override fun onProvideKeyboardShortcuts(data: List<KeyboardShortcutGroup>, menu: Menu?, deviceId: Int) {

    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }
}