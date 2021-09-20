package com.minhhop.easygolf.framework.dialogs

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog
import com.minhhop.easygolf.listeners.EventSaveNameGroup
import com.minhhop.easygolf.utils.AppUtil

class EditNameGroupDialog(private val mActivity: Activity, val title:String, val name:String? = null) : BaseDialog(mActivity) {
    private lateinit var mTxtTitle:TextView
    private lateinit var mEditValue: AppCompatEditText
    private var mEventSaveNameGroup:EventSaveNameGroup? = null
    private lateinit var  mButton:TextView
    private var mNameButton:String? = null
    override fun initView() {
        mTxtTitle = findViewById(R.id.txtTitle)
        mEditValue = findViewById(R.id.edtValue)
        mTxtTitle.text = title
        name?.apply {
            mEditValue.text?.append(this)
        }

        mButton = findViewById(R.id.actionSave)
        mNameButton?.apply {
            mButton.text = this

        }

        mButton.setOnClickListener {
            mEventSaveNameGroup?.onSave(mEditValue.text.toString())
            this@EditNameGroupDialog.dismiss()
        }
    }

    fun setNameButton(name:String):EditNameGroupDialog{
        mNameButton = name
        return this
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


    override fun resContentView(): Int = R.layout.dialog_edit_name_group

    fun setEvent(event:EventSaveNameGroup):EditNameGroupDialog{
        this.mEventSaveNameGroup = event
        return this
    }
}