package com.minhhop.easygolf.base.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.NoNetworkDialog
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class WozBaseFragment : Fragment(), View.OnClickListener {

    enum class STATE{
        FIRST_LOAD,
        REFRESH,
        LOADING,
        LOAD_MORE,
        FINISH
    }

    private val mListResponseApi = ArrayList<Subscription>()

    /**
     * get current state fragment
     * */
    open var mState = STATE.FIRST_LOAD

    open lateinit var mViewRoot:View
    private var mLayoutLoading:View? = null
    private var mViewRefresh: SwipeRefreshLayout? = null

    abstract fun setLayout():Int
    abstract fun initView(viewRoot:View)
    abstract fun loadData()

    open fun refreshData() {
        mState = STATE.REFRESH
        loadData()
    }

    open fun hideLayoutRefresh(){
        mViewRefresh?.apply {
            isRefreshing = false
        }
    }

    open fun showLoading(){
        mLayoutLoading?.apply {
            if(visibility != View.VISIBLE){
                visibility = View.VISIBLE
            }
        }
    }

    open fun hideLoading(){
        mLayoutLoading?.apply {
            if(visibility != View.GONE){
                visibility = View.GONE
            }
        }
    }


    protected fun startActivity(cl: Class<*>, bundle: Bundle) {
        val intent = Intent(context, cl)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    protected fun startActivity(cl: Class<*>) {
        startActivity(Intent(context, cl))
    }

    protected fun startActivityForResult(cl: Class<*>, bundle: Bundle, requestCode: Int) {
        val intent = Intent(context, cl)
        intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    protected fun startActivityForResult(cl: Class<*>, requestCode: Int) {
        val intent = Intent(context, cl)
        startActivityForResult(intent, requestCode)
    }

    fun <T:Any> registerResponse(response:Observable<T>,handleResponse: HandleResponse<T>){
        val item = response
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result->
                    handleResponse.onSuccess(result)
                },{ throwable ->
                    errorResponse(throwable)
                })

        mListResponseApi.add(item)
    }

    open fun errorResponse(throwable: Throwable){
        showMessage(throwable.localizedMessage,null)
        hideLayoutRefresh()
        hideLoading()
    }


    fun errorResponseMessage(errorResponse: ErrorResponse, onCancel: DialogInterface.OnCancelListener? = null){
        if(errorResponse.noNetwork){
            noNetworkFound(onCancel)
        }else{
            showMessage(errorResponse.message,onCancel)
        }
    }


    private fun noNetworkFound( onCancel: DialogInterface.OnCancelListener?){
        activity?.let {
            NoNetworkDialog(it,onCancel).show()
        }
    }

    open fun showMessage(message:String,onCancelListener: DialogInterface.OnCancelListener?){
        context?.apply {
            AlertDialogIOS(this,message,onCancelListener).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewRoot = inflater.inflate(setLayout(),container,false)

        /**
         * Start layout loading
         * */
        mLayoutLoading = mViewRoot.findViewById(R.id.layoutLoading)
        mLayoutLoading?.apply {
            setOnClickListener(this@WozBaseFragment)
        }
        /**
         * Start layout refresh
         * */
        mViewRefresh = mViewRoot.findViewById(R.id.viewRefresh)
        mViewRefresh?.apply {
            setColorSchemeColors(ContextCompat.getColor(context,R.color.colorRed),ContextCompat.getColor(context,R.color.colorAccent),
                    ContextCompat.getColor(context,R.color.color_flag_yellow))

            this.setOnRefreshListener {
                refreshData()
            }
        }

        initView(mViewRoot)
        loadData()
        return mViewRoot
    }

    override fun onClick(v: View?) {
        v?.apply {
            if(id == R.id.layoutLoading){}
        }
    }

    override fun onDestroy() {
        for (item in mListResponseApi){
            if(!item.isUnsubscribed){
                item.unsubscribe()
            }
        }
        super.onDestroy()
    }
}