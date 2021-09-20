package com.minhhop.easygolf.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.DataSearchAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.framework.models.Course
import com.minhhop.easygolf.framework.models.MessageBooking
import com.minhhop.easygolf.listeners.ItemDataSearchListener
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SearchActivity : WozBaseActivity(), ItemDataSearchListener {
    private var mDataSearchAdapter: DataSearchAdapter? = null

    private var mEditSearch: EditText? = null
    private var mStart = 0
    internal val handler = Handler()
    private lateinit var  mLatLng: LatLng

    private var subApi: Subscription? = null
    private var mIsReturnData:Boolean = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun setLayoutView(): Int {
        EventBus.getDefault().register(this)
        return R.layout.activity_search
    }

    override fun onDestroy() {
        if (subApi != null) {
            subApi!!.unsubscribe()
        }
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun getTitleToolBar(): String? {
        return ""
    }

    override fun initView() {
        mStatusLoading = STATUS.FIRST_LOAD

        mIsReturnData = intent.getBooleanExtra(Contains.EXTRA_FOR_RETURN_DATA,false)

        mDataSearchAdapter = DataSearchAdapter(this, this)
        val listResult = findViewById<RecyclerView>(R.id.listResult)

        val layoutManager = LinearLayoutManager(this)

        listResult.layoutManager = layoutManager
        mDataSearchAdapter!!.registerLoadMore(layoutManager, listResult) {
            if (mStatusLoading === STATUS.LOAD_MORE) {
                callApiSearch()
            }
        }

        listResult.adapter = mDataSearchAdapter


        mEditSearch = findViewById(R.id.editSearch)

        mEditSearch!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val term = s.toString()
                Log.d("onTextChanged", term)
                if (term.length >= 2) {
                    handler.removeMessages(0)
                    handler.postDelayed({
                        mStatusLoading = STATUS.FIRST_LOAD
                        mDataSearchAdapter!!.openReachEnd()
                        mStart = 0
                        doSearch()

                    }, 350)
                } else if (term.isEmpty()) {
                    mStatusLoading = STATUS.FIRST_LOAD
                    mDataSearchAdapter!!.openReachEnd()
                    mStart = 0
                    doSearch()
                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        doSearch()

    }


    override fun loadData() {

    }

    fun doSearch() {
        Log.e("WOW", "do search")
        //        mDataSearchAdapter.clearAll();
//   TODO @quipham     Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(object : PermissionListener {
//                    @SuppressLint("MissingPermission")
//                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                        Log.e("WOW", "onPermissionGranted: ")
//                        fusedLocationClient.lastLocation
//                                .addOnSuccessListener { location : Location? ->
//                                    location?.apply {
//                                        mLatLng = LatLng(this.latitude,this.longitude)
//                                        callApiSearch()
//                                    }
//
//                                }
//                    }
//
//                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                        mLatLng = LatLng(0.0,0.0)
//                        callApiSearch()
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
//                        mLatLng = LatLng(0.0,0.0)
//                        callApiSearch()
//                    }
//                }).check()

    }

    private fun callApiSearch() {
        Log.e("WOW", "Call Api")
        if (subApi != null) {
            subApi!!.unsubscribe()
        }
        subApi = ApiService.getInstance().golfService.searchCourses(mLatLng.latitude,
                mLatLng.longitude, mEditSearch!!.text.toString(), mStart, "debug")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    if (mStatusLoading === STATUS.FIRST_LOAD || mStatusLoading === STATUS.REFRESH) {
                        mDataSearchAdapter!!.setDataList(result.courses)
                        mStatusLoading = STATUS.LOAD_MORE
                    } else {
                        mDataSearchAdapter!!.addListItem(result.courses)
                    }
                    mStart = result.paginator.start
                    if (mStart < 0) {
                        mDataSearchAdapter!!.onReachEnd()
                    }

                    hideLoading()
                }, { hideLoading() })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageBooking) {
        finish()
    }

    override fun onClick(course: Course) {
        val bundle = Bundle()
        bundle.putString(Contains.EXTRA_ID_COURSE, course.id)
        bundle.putString(Contains.EXTRA_NAME_COURSE, course.name)

        if (!mIsReturnData) {
            bundle.putString(Contains.EXTRA_IMAGE_COURSE, course.image)
            startActivity(EasyGolfClubDetailActivity::class.java, bundle)
        }else{
            val intent = Intent()
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
}
