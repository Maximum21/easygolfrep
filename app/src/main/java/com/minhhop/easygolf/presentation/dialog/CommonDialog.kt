package com.minhhop.easygolf.presentation.dialog

import android.content.Context
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfDialog

class CommonDialog(context: Context) : EasyGolfDialog(context) {
    override fun initView() {
    }

    override fun resContentView(): Int = R.layout.dialog_common

    fun setContent(message:String?) = this

    fun addOnListener(commonDialogListener:CommonDialogListener?) = this

    interface CommonDialogListener{

    }
}