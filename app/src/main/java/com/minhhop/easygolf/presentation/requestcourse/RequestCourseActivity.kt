package com.minhhop.easygolf.presentation.requestcourse

import android.content.DialogInterface
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.minhhop.core.domain.course.RequestCourse
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.ConfirmationDialog
import com.minhhop.easygolf.framework.golf.LocationHelper
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedFragment
import kotlinx.android.synthetic.main.activity_request_course.*
import org.koin.android.ext.android.inject


class RequestCourseActivity : EasyGolfActivity<RequestCourseViewModel>() , OnMapReadyCallback, View.OnClickListener{

    private var mSelectedLocation: LatLng? = null
    private var mCurrentLocation: LatLng? = null
    private var mMap: GoogleMap? = null
    override val mViewModel : RequestCourseViewModel by inject()
    override fun setLayout(): Int {
        return R.layout.activity_request_course
    }

    override fun initView() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        enter_course_name_iv_end.setOnClickListener(this)
        currentLocationTiltIv.setOnClickListener(this)
        back_btn.setOnClickListener(this)
        currentLocationIv.setOnClickListener(this)
        currentLocationTypeIv.setOnClickListener(this)
        mViewModel.requestedCourse.observe(this, Observer {
            hideMask()
            Toast.makeText(this@RequestCourseActivity, getString(R.string.course_added), Toast.LENGTH_LONG).show()
            finish()
        })

        val cfWhite: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.MULTIPLY)
        val cfBlack: ColorFilter = PorterDuffColorFilter(ContextCompat.getColor(this,R.color.colorBlack), PorterDuff.Mode.MULTIPLY)
        currentLocationIv.colorFilter = cfWhite
        currentLocationTiltIv.colorFilter = cfBlack
    }

    override fun loadData() {
        LocationHelper().startLocationUpdatesForActivity(this, false, object : LocationHelper.OnLocationCatch {
            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
                mCurrentLocation = mLatLng
                mCurrentLocation?.let{
                    selectedLocation(it)
                }
            }

            override fun locationNotAvailable() {
            }

            override fun onPermissionDenied() {

            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnMapClickListener {
            selectedLocation(it)
        }
        mCurrentLocation?.let{
            selectedLocation(it)
        }

    }

    private fun selectedLocation(location: LatLng){
       location?.let{
           mSelectedLocation = it
           mMap?.clear()
           mMap?.addMarker(MarkerOptions()
                   .position(it)
                   .title(getString(R.string.selected_location)))
           val cameraPosition = CameraPosition.Builder().target(it).zoom(17.0f).build()
           val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
           mMap?.animateCamera(cameraUpdate)
       }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.enter_course_name_iv_end->{
                if(courseNameEtv.text.toString().trim().isNotEmpty() && mSelectedLocation !=null){
                    viewMask()
                    mViewModel.reqeustCourse(RequestCourse(courseNameEtv.text.toString().trim(),
                            mSelectedLocation?.longitude?:0.0,
                            mSelectedLocation?.latitude?:0.0))
                }else if(courseNameEtv.text.toString().trim().isNotEmpty()){
                    AlertDialogIOS(this, getString(R.string.enter_course_name_error), null).show()
                }else{
                    AlertDialogIOS(this, getString(R.string.enter_course_location_error), null).show()
                }
            }
            R.id.currentLocationIv->{
                mCurrentLocation?.let{
                    selectedLocation(it)
                }
            }
            R.id.back_btn->{
                finish()
            }
            R.id.currentLocationTypeIv->{
                showPopup(v)
            }
        }
    }

    private fun showPopup(itemView: View) {
        //creating a popup menu
        val popup = PopupMenu(this@RequestCourseActivity, itemView)
        //inflating menu from xml resource
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_map_options)
        //adding click listener
        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.normal -> {
                    mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
                R.id.satellite -> {
                    mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                }
                R.id.hybrid -> {
                    mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
                }
                R.id.terrain -> {
                    mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                }
            }
            false
        }
        //displaying the popup
        //displaying the popup
        popup.show()
    }
}