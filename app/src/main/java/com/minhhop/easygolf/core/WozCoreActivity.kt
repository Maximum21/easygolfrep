package com.minhhop.easygolf.core

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.minhhop.easygolf.R

abstract class WozCoreActivity : AppCompatActivity(),View.OnClickListener {
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
        mViewLoading?.apply {
            setOnClickListener(this@WozCoreActivity)
        }

        setToolBar()
        initView()
        loadData()
    }

    override fun onClick(v: View?) {

    }

    private fun setToolBar(){
        /**
         * set icon back
         * */
        val toolBarBack: Toolbar? = findViewById(R.id.toolBarBack)
        toolBarBack?.apply {
            title = null
            setSupportActionBar(this)
            val actionBar = supportActionBar
            actionBar?.also {
                it.setDisplayHomeAsUpEnabled(true)
                if(setIconToolBar() != 0){
                    it.setHomeAsUpIndicator(setIconToolBar())
                }
            }

            setNavigationOnClickListener {
                this@WozCoreActivity.onBackPressed()
            }
        }

        getTitleToolBar()?.let{
//            val titleToolbar:TextView? = findViewById(R.id.titleToolBar)
//            titleToolbar?.apply { text = it }
        }
    }

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
                return false
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun hideSoftKeyboard() {

        val viewFocus = currentFocus
        val iMM = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        iMM?.apply {
            if(viewFocus != null){
                hideSoftInputFromWindow(viewFocus.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
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

    open fun setRequestOrientation():Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    open fun isHideSoftKeyBoardTouchOutSide() = true
    open fun setIconToolBar(): Int  = 0
    open fun getTitleToolBar(): String? = null

    open fun startActivity(cl:Class<*>){
        startActivity(Intent(this,cl))
    }

    open fun startActivity(cl:Class<*>,flag:Int){
        val intent = Intent(this,cl)
        intent.addFlags(flag)
        startActivity(intent)
    }

    open fun startActivity(cl:Class<*>,bundle: Bundle){
        val intent = Intent(this,cl)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    open fun startActivityForResult(cl:Class<*>,requestCode: Int){
        val intent = Intent(this,cl)
        startActivityForResult(intent,requestCode)
    }

    protected fun startActivityForResult(cl: Class<*>, requestCode: Int, bundle: Bundle) {

        val intent = Intent(this@WozCoreActivity, cl)
        intent.putExtras(bundle)

        startActivityForResult(intent, requestCode)
    }
}