package com.minhhop.easygolf.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.SearchCourseAdapter
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import com.minhhop.easygolf.framework.bundle.SearchCourseBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.golf.LocationHelper
import kotlinx.android.synthetic.main.activity_add_player.toolbarBack
import kotlinx.android.synthetic.main.activity_search_clubs.*
import org.koin.android.ext.android.inject

class SelectCourseActivity : EasyGolfActivity<GolfClubViewModel>(), SearchCourseAdapter.SearchCoursesView {
    private var coursesAdapter: SearchCourseAdapter? = null
    override val mViewModel: GolfClubViewModel by inject()
    lateinit var selectCourseBundle: SearchCourseBundle
    internal val mHandlerSearch = Handler()
    private val mBlackList = HashMap<String, Club>()
    private var mCurrentLocation: LatLng? = null
    override fun setLayout(): Int = R.layout.activity_search_clubs

    override fun initView() {
        toolbarBack.title = getString(R.string.search_courses);
        selectCourseBundle = EasyGolfNavigation.SearchCourseBundle(intent)!!
        if (selectCourseBundle.getBlackList() != null) {
            mBlackList[selectCourseBundle.getBlackList()!!.id] = selectCourseBundle.getBlackList()!!
        }
        coursesAdapter = SearchCourseAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)
        search_courses_rv.layoutManager = layoutManager

        coursesAdapter!!.registerLoadMore(layoutManager, search_courses_rv) {
            if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                fetchNearbyClubs()
            }
        }
        search_courses_rv.adapter = coursesAdapter
        mViewModel.suggestedClubsPagerLive.observe(this, Observer {
            hideMask()
            if (it.items.isNotEmpty()) {
                if (mStatusLoading == StatusLoading.REFRESH_LOAD
                        || mStatusLoading == StatusLoading.FIRST_LOAD) {
                    coursesAdapter?.setDataList(showData(it.items))
                    hideRefreshLoading()
                } else {
                    coursesAdapter?.addListItem(showData(it.items))
                }
                if (it.paginator.start <= -1) {
                    coursesAdapter?.onReachEnd()
                }
            } else {
                hideRefreshLoading()
                coursesAdapter?.onReachEnd()
                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    coursesAdapter?.clearAll()
                    coursesAdapter?.onReachEnd()
                }
            }

            if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
            hideRefreshLoading()
        })
        toolbarSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mHandlerSearch.removeCallbacksAndMessages(null)
                if(s.toString().trim().isEmpty()){
                    coursesAdapter?.openReachEnd()
                }
                mHandlerSearch.postDelayed({
                    coursesAdapter?.clearAll()
                    mCurrentLocation?.let { currentLocation ->
                        mViewModel.searchDebounced(latitude = currentLocation.latitude, longitude = currentLocation.longitude, keyword = s.toString().trim())
                    } ?: mViewModel.searchDebounced(0.0, 0.0, s.toString().trim())
                }, 500)
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showData(data: List<Club>): List<Club> {
        return if (mBlackList.isEmpty()) {
            data
        } else {
            val result = ArrayList<Club>()
            data.map { club ->
                if (!mBlackList.containsKey(club.id)) {
                    result.add(club)
                }
            }
            result
        }
    }

    override fun loadData() {
        LocationHelper().startLocationUpdatesForActivity(this, false, object : LocationHelper.OnLocationCatch {
            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
                mCurrentLocation = mLatLng
                fetchNearbyClubs();
            }

            override fun locationNotAvailable() {
                fetchNearbyClubs();
            }

            override fun onPermissionDenied() {
                // todo: what should we do??
            }
        })
    }

    private fun fetchNearbyClubs(keyword: String = "",onRefresh:Boolean = false) {
        mCurrentLocation?.let { currentLocation ->
            mViewModel.fetchSuggestedClubs(latitude = currentLocation.latitude, longitude = currentLocation.longitude, keyword = keyword, onRefresh = false)
        } ?: mViewModel.fetchSuggestedClubs(0.0, 0.0, keyword, onRefresh)
    }

    override fun onClickListener(images: Club) {
        val dataIntent = Intent()
        val bundle = Bundle()
        bundle.putString(SearchCourseBundle::club.name, SearchCourseBundle.toData(images))
        dataIntent.putExtras(bundle)
        setResult(Activity.RESULT_OK, dataIntent)
        finish()
    }
}