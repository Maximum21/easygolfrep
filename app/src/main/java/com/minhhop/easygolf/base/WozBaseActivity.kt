package com.minhhop.easygolf.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.NoNetworkDialog
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
@Deprecated("do not implementation")
abstract class WozBaseActivity : AppCompatActivity(),View.OnClickListener  {

    enum class STATUS{
        REFRESH,
        FIRST_LOAD,
        LOAD_MORE,
        FINISH
    }

    var mStatusLoading = STATUS.FIRST_LOAD

    /**
     * Handle when someone invite me
     * */
    private var mDbReferenceUser:DatabaseReference? = null
    private var mChildEvent:ChildEventListener? = null

    private var mViewLoading:View? = null
    private var mViewRefresh: SwipeRefreshLayout? = null
    private val mListResponse = ArrayList<Subscription>()

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
        mViewLoading = findViewById(com.minhhop.easygolf.R.id.layoutLoading)
        mViewLoading?.apply {
            setOnClickListener(this@WozBaseActivity)
        }

        mViewRefresh = findViewById(com.minhhop.easygolf.R.id.viewRefresh)
        mViewRefresh?.apply {

            if (mViewRefresh != null) {
                setColorSchemeColors(ContextCompat.getColor(context,com.minhhop.easygolf.R.color.colorAccent),
                        ContextCompat.getColor(context,com.minhhop.easygolf.R.color.colorRed),
                        ContextCompat.getColor(context,com.minhhop.easygolf.R.color.colorBlack))

                setOnRefreshListener { this@WozBaseActivity.refreshData() }
            }
        }

        setToolBar()
        initView()
        loadData()
    }

    open fun refreshData() {
        mStatusLoading = STATUS.REFRESH
        loadData()
    }

    open fun hideRefreshLoading() {
        mViewRefresh?.apply {
            isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(setMenuRes() != 0){
            val layoutInflater = menuInflater
            layoutInflater.inflate(setMenuRes(),menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun <T:Any> registerResponse(response:Observable<T>,handleResponse: HandleResponse<T>){
        val item = response
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ result ->
                    handleResponse.onSuccess(result)
                },{ throwable ->
                    errorResponse(throwable)
                })

        mListResponse.add(item)

    }


    fun <T:Any> registerResponseNotShowError(response:Observable<T>,handleResponse: HandleResponse<T>){
                response
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    handleResponse.onSuccess(it)
                },{ throwable ->
                   val classNameTag = localClassName
                    Log.e(classNameTag,throwable.localizedMessage)
                })
    }

    open fun errorResponse(throwable: Throwable){
        showMessage(throwable.localizedMessage,null)
        hideLoading()
    }

    override fun onClick(v: View?) {

    }

    override fun onDestroy() {
        for (item in mListResponse){
            if(!item.isUnsubscribed){
                item.unsubscribe()
            }
        }
        super.onDestroy()
    }


    fun showMessage(message:String, onCancel:DialogInterface.OnCancelListener?){
        AlertDialogIOS(this,message,onCancel).show()
    }

    fun noNetworkFound( onCancel:DialogInterface.OnCancelListener?){
        NoNetworkDialog(this,onCancel).show()
    }

    fun errorResponseMessage(errorResponse: ErrorResponse, onCancel:DialogInterface.OnCancelListener? = null){
        if(errorResponse.noNetwork){
            noNetworkFound(onCancel)
        }else{
            showMessage(errorResponse.message,onCancel)
        }
    }

    private fun setToolBar(){
        /**
         * set icon back
         * */

        val toolbarHome: Toolbar? = findViewById(com.minhhop.easygolf.R.id.toolBarHome)
        toolbarHome?.apply {
            setSupportActionBar(this)
            supportActionBar?.let {
                actionBar ->
                actionBar.setDisplayShowHomeEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.title = null
            }

        }

        val toolBarBack: Toolbar? = findViewById(com.minhhop.easygolf.R.id.toolBarBack)
        toolBarBack?.apply {
            title = null
            setSupportActionBar(this)
            val actionBar = supportActionBar
            actionBar?.also {
                it.setDisplayHomeAsUpEnabled(true)

                if(setIconToolbar() != 0){
                    it.setHomeAsUpIndicator(setIconToolbar())
                }else {
                    it.setHomeAsUpIndicator(com.minhhop.easygolf.R.drawable.ic_icon_back_button)
                }
                it.title = null

                getTitleToolBar()?.let { myTitle->
                    title = myTitle
                }
            }

            setNavigationOnClickListener {
                this@WozBaseActivity.onBackPressed()
            }
        }

        getTitleToolBar()?.let{
//            val titleToolbar: TextView? = findViewById(com.minhhop.easygolf.R.id.titleToolBar)
//            titleToolbar?.apply { text = it }
        }
    }

    open fun getTitleToolBar():String? = null
    open fun setIconToolbar(): Int = 0


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

    open fun setMenuRes(): Int = 0

    open fun setRequestOrientation():Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    open fun isHideSoftKeyBoardTouchOutSide() = true

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

        val intent = Intent(this@WozBaseActivity, cl)
        intent.putExtras(bundle)

        startActivityForResult(intent, requestCode)
    }

}