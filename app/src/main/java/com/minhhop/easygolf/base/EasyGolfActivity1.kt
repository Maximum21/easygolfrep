package com.minhhop.easygolf.base

import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.NoNetworkDialog

@Deprecated("do not implementation")
abstract class EasyGolfActivity1<out T: EasyGolfViewModel>: AppCompatActivity(),View.OnClickListener{
    abstract val mViewModel : T

    private var mViewLoading:View? = null

    protected abstract fun setLayoutView():Int
    protected abstract fun initView()
    protected abstract fun loadData()

    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * set orientation activity
         * */
        requestedOrientation = setRequestOrientation()
        super.onCreate(savedInstanceState)

        /**
         * set layout
         * */
        setContentView(setLayoutView())
        mViewLoading = findViewById(R.id.layoutLoading)
        initView()
        loadData()
    }

    fun showMessage(message:String, onCancel: DialogInterface.OnCancelListener?){
        AlertDialogIOS(this,message,onCancel).show()
    }

    private fun noNetworkFound( onCancel: DialogInterface.OnCancelListener?){
        NoNetworkDialog(this,onCancel).show()
    }

    fun errorResponseMessage(errorResponse: ErrorResponse, onCancel: DialogInterface.OnCancelListener? = null){
        if(errorResponse.noNetwork){
            noNetworkFound(onCancel)
        }else{
            showMessage(errorResponse.message,onCancel)
        }
    }

    open fun setRequestOrientation():Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    open fun isHideSoftKeyBoardTouchOutSide() = true
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            if(isHideSoftKeyBoardTouchOutSide()){
                val viewFocus = window.currentFocus
                viewFocus?.apply {
                    if(viewFocus is EditText){
                        val screenLocation = IntArray(2)
                        /**
                         * get position view focus
                         * */
                        viewFocus.getLocationOnScreen(screenLocation)
                        val x = rawX + left - screenLocation[0]
                        val y = rawY + top - screenLocation[1]

                        if(x  < left || x  >= right || y < top || y >= bottom){
                            hideSoftKeyboard()
                        }
                    }
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    fun hideSoftKeyboard() {

        val viewFocus = currentFocus
        val iMM = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        iMM?.apply {
            if(viewFocus != null){
                hideSoftInputFromWindow(viewFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

    }


    open fun showLoading(){
        mViewLoading?.apply {
            visibility = View.VISIBLE
        }
    }

    open fun hideLoading(){
        mViewLoading?.apply {
            visibility = View.GONE
        }
    }


    override fun onDestroy() {
        mViewModel.cancelAllRequest()
        super.onDestroy()
    }

    override fun onClick(v: View) {

    }
}