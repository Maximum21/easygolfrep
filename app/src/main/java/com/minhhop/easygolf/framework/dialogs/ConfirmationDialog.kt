package com.minhhop.easygolf.framework.dialogs

import android.content.Context
import android.content.DialogInterface
import android.view.KeyboardShortcutGroup
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog

class ConfirmationDialog(context: Context, private val content: String,private val positiveText : String, private val negativeText : String,
                     private val onCancelListener: DialogInterface.OnCancelListener?,
                     private val onDoneListener: DialogInterface.OnCancelListener?) : BaseDialog(context), View.OnClickListener {
    var btAccept: TextView? = null
    var btRefuse: TextView? = null
    var dialog_message_etv: TextView? = null
    var mLayoutView: LinearLayout? = null
    override fun initView() {
        btAccept = findViewById(R.id.btAccept)
        btRefuse = findViewById(R.id.btRefuse)
        dialog_message_etv = findViewById(R.id.dialog_message_etv)
        mLayoutView = findViewById(R.id.layout_view)
        dialog_message_etv!!.text = content
        btAccept!!.text = positiveText
        btRefuse!!.text = negativeText
        btAccept!!.setOnClickListener(this)
        btRefuse!!.setOnClickListener(this)
    }

    override fun resContentView(): Int {
        return R.layout.dialog_confirm_data
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v.id){
            R.id.btAccept->{
                if(onDoneListener!=null) {
                    onDoneListener.onCancel(this)
                    dismiss()
                }
            }
            R.id.btRefuse->{
                if (onCancelListener != null) {
                    onCancelListener.onCancel(this)
                    dismiss()
                }
            }
        }
    }

    override fun onProvideKeyboardShortcuts(data: List<KeyboardShortcutGroup>, menu: Menu?, deviceId: Int) {}
    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

}