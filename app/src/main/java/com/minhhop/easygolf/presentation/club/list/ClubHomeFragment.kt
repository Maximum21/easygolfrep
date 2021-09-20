package com.minhhop.easygolf.presentation.club.list

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.golf.LocationHelper
import com.minhhop.easygolf.presentation.club.ClubAdapter
import com.minhhop.easygolf.presentation.dialog.AccessLocationDialog
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import kotlinx.android.synthetic.main.fragment_course_home.view.*
import org.koin.android.ext.android.inject
import kotlin.math.abs
import kotlin.math.sign

class ClubHomeFragment : EasyGolfFragment<ClubHomeViewModel>(),ClubAdapter.CourseEvent {
    companion object{
        private const val MAX_VELOCITY_X = 3200
    }
    override val mViewModel: ClubHomeViewModel by inject()
    private var mRecycleNearbyClubAdapter:ClubAdapter? = null
    private var mRecycleRecommendClubAdapter:ClubAdapter? = null
    private var mCurrentLocation:LatLng? = null
    private var mLayoutManagerHome:LinearLayoutManager? = null
    override fun setLayout(): Int = R.layout.fragment_course_home

    override fun initView(viewRoot: View) {
        context?.let { context->
            mRecycleNearbyClubAdapter = ClubAdapter(context,true,this)
            viewRoot.listClubNearby.layoutManager = object: LinearLayoutManager(context,HORIZONTAL,false){
                override fun isAutoMeasureEnabled(): Boolean = true
            }

            viewRoot.listClubNearby.adapter = mRecycleNearbyClubAdapter
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(viewRoot.listClubNearby)
            registerBecomeRecyclerViewInClubHome(viewRoot.listClubNearby)
            /**
             * recommend list
             * */
            registerBecomeRecyclerViewInClubHome(viewRoot.listHome)
            mRecycleRecommendClubAdapter = ClubAdapter(context,false,this)
            mLayoutManagerHome = LinearLayoutManager(context)
            viewRoot.listHome.layoutManager = mLayoutManagerHome
            viewRoot.listHome.setHasFixedSize(true)
            viewRoot.listHome.adapter = mRecycleRecommendClubAdapter
            mRecycleRecommendClubAdapter?.registerLoadMore(mLayoutManagerHome!!,viewRoot.listHome){}
        }
        /**
         *Start handle register load more
         * */
        viewRoot.nestedScrollHomeView.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if(v.getChildAt(v.childCount - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                        scrollY > oldScrollY) {

                    val visibleItemCount = mLayoutManagerHome?.childCount ?:0
                    val totalItemCount = mLayoutManagerHome?.itemCount ?:0
                    val pastVisibleItems = mLayoutManagerHome?.findFirstVisibleItemPosition()?:0

                    if (mRecycleRecommendClubAdapter?.isReachEnd() == false && mStatusLoading == StatusLoading.FINISH_LOAD) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            mCurrentLocation?.let { currentLocation->
                                mViewModel.fetchByHomePage(currentLocation.latitude,currentLocation.longitude,onLoadMore = true)
                            }?:mViewModel.fetchByHomePage(0.0,0.0,onLoadMore = true)
                        }
                    }
                }
            }
        }

        /**
         *end handle register load more
         * */

        mViewModel.clubRecommendPagerLive.observe(viewLifecycleOwner, Observer {
            if(mViewModel.clubNearbyPagerLive.value != null){
                hideMask()
            }
            if(it.items.isNotEmpty()){
                if( mStatusLoading == StatusLoading.REFRESH_LOAD || mStatusLoading == StatusLoading.FIRST_LOAD){
                    mRecycleRecommendClubAdapter?.setDataList(it.items)
                    if(mViewModel.clubNearbyPagerLive.value != null){
                        hideRefreshLoading()
                    }
                }else{
                    mRecycleRecommendClubAdapter?.addListItem(it.items)
                }
                if (it.paginator.start <= -1){
                    mRecycleRecommendClubAdapter?.onReachEnd()
                }
            }else{
                hideRefreshLoading()
                mRecycleRecommendClubAdapter?.onReachEnd()
                if(mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD){
                    mRecycleRecommendClubAdapter?.clearAll()
                    mRecycleRecommendClubAdapter?.onReachEnd()
                }
            }

            if(mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD){
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
            hideRefreshLoading()
        })
        mViewModel.clubNearbyPagerLive.observe(viewLifecycleOwner, Observer {
            if(mViewModel.clubRecommendPagerLive.value != null){
                hideMask()
                hideRefreshLoading()
            }
            mRecycleNearbyClubAdapter?.setDataList(it.items)
        })

        viewRoot.toolbarSearch.setOnClickListener {
            searchClubs()
        }
    }

    override fun viewMask() {
        super.viewMask()
        viewRoot.viewMask.startBurnViewAnimation()
    }

    override fun hideMask() {
        super.hideMask()
        viewRoot.viewMask.stopBurnViewAnimation()
        if(viewRoot.listHome.visibility != View.VISIBLE){
            viewRoot.listHome.visibility = View.VISIBLE
        }
    }
    override fun loadData() {
        viewMask()
        viewRoot.refreshLayout.isEnabled = false
        LocationHelper().startLocationUpdatesForFragment(this,false,object : LocationHelper.OnLocationCatch{
            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
                mCurrentLocation =  mLatLng
                mCurrentLocation?.let { currentLocation->
                    mViewModel.fetchByHomePage(currentLocation.latitude,currentLocation.longitude)
                }?:mViewModel.fetchByHomePage(0.0,0.0)

                viewRoot.refreshLayout.isEnabled = true
            }

            override fun locationNotAvailable() {
                mViewModel.fetchByHomePage(0.0,0.0)
                viewRoot.refreshLayout.isEnabled = true
            }

            override fun onPermissionDenied() {
                activity?.let { activity->
                    AccessLocationDialog(activity,null)
                            .show()
                }
            }
        })
    }

    override fun onRefreshData() {
        super.onRefreshData()
        mRecycleRecommendClubAdapter?.openReachEnd()
        mCurrentLocation?.let { currentLocation->
            mViewModel.fetchByHomePage(currentLocation.latitude,currentLocation.longitude,onRefresh = true)
        }?:mViewModel.fetchByHomePage(0.0,0.0,onRefresh = true)
    }

    override fun onClickListener(club: Club) {
        context?.let { context ->
            EasyGolfNavigation.easyGolfCourseDetailDirection(context,club.id,club.name,club.image)
        }
    }

    override fun searchClubs() {
        EasyGolfNavigation.addCourseDirection(this, CreatePostActivity.REQUEST_CODE_COURSE,-1,"",getString(R.string.search_courses))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if(requestCode == CreatePostActivity.REQUEST_CODE_COURSE && resultCode == Activity.RESULT_OK && intent != null){
            val selectedCourseBundle = EasyGolfNavigation.SearchCourseBundle(intent)
            selectedCourseBundle?.getResult()?.let { users->
                activity?.let { EasyGolfNavigation.easyGolfCourseDetailDirection(it,users.id,users.name,users.image) }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Contains.REQUEST_LOCATION_PERMISSION && permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)){
            loadData()
        }
    }

    private fun registerBecomeRecyclerViewInClubHome(recyclerView: RecyclerView){
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        ViewCompat.setNestedScrollingEnabled(recyclerView, false)
        registerFling(recyclerView)
    }

    private fun registerFling(recyclerView: RecyclerView){
        recyclerView.onFlingListener = object: RecyclerView.OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                return if (abs(velocityX) > MAX_VELOCITY_X) {
                    val velocityXResult = (MAX_VELOCITY_X * sign(velocityX.toDouble())).toInt()
                    recyclerView.fling(velocityXResult, velocityY)
                    true
                }else{
                    false
                }
            }
        }
    }
}