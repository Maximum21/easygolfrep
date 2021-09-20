package com.minhhop.easygolf.presentation.golf.play

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.minhhop.core.domain.golf.EasyGolfLocation
import com.minhhop.easygolf.R
import com.minhhop.easygolf.presentation.golf.GoogleMapsHelper
import com.minhhop.easygolf.presentation.golf.component.marker.EventAnimationMarker
import com.minhhop.easygolf.presentation.golf.component.marker.MarkerAnimation
import com.minhhop.easygolf.presentation.golf.play.EasyGolfMapFragment.TouchDown.*
import com.minhhop.easygolf.presentation.golf.play.EasyGolfMapFragment.ZoomStateGreen.*
import com.minhhop.easygolf.services.MapHelper
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.views.fragments.MapWrapperLayout
import kotlin.math.*

class EasyGolfMapFragment : SupportMapFragment(), OnMapReadyCallback, MapWrapperLayout.OnDragListener {

    companion object{
        const val TAG = "EasyGolfMapFragment"
        private const val MIN_ZOOM_IN_MAP = 16f
        private const val MAX_ZOOM_IN_MAP = 20f
        private const val IN_BOUNDS_DIAGONAL = 3.3
        private const val WORLD_PX = 256
        private const val MAX_ZOOM = 30.0
        private const val LN2 = 0.6931471805599453
    }
    enum class TouchDown{
        TEE,
        HOLDER,
        FLAG,
        MAP,
        NONE
    }
    enum class ZoomStateGreen{
        ZOOM_IN,
        ZOOMING,
        ZOOM_OUT
    }
    private var mZoomStateGreen = ZOOM_OUT
    private var mTouchDown = NONE

    private var mGoogleMapBehavior:GoogleMapBehavior? = null
    private var mOriginalView: View? = null
    private lateinit var mMapWrapperLayout: MapWrapperLayout
    private var mGoogleMap:GoogleMap? = null

    /**
     * value support for draw map
     * */
    private var mSizeHolderMarker:Int = 0
    private var mSizeHolderActive: Int = 0
    private var mSizeHolderActiveLine: Int = 0
    private var mSizeDotHolderMarker:Int = 0
    private var mHitHolderToFlag:Boolean = false
    /**
     * view for easy-golf
     * */
    private var mCanMoveTee:Boolean = true
    private var mTeeMarker:Marker? = null
    private var mPolylineTeeToHolder:Polyline? = null
    private var mPolylineGreen:Polyline? = null
    private var mFlagMarker:Marker? = null
    private var mPolylineFlagToHolder:Polyline? = null
    private var mHolderMarker: Marker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mOriginalView = super.onCreateView(inflater, container, savedInstanceState)
        mMapWrapperLayout = MapWrapperLayout(activity)
        mMapWrapperLayout.addView(mOriginalView)
        return mMapWrapperLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setup values
        mSizeHolderMarker = context?.resources?.getDimension(R.dimen.size_holder_map)?.toInt()?:0
        mSizeDotHolderMarker = context?.resources?.getDimension(R.dimen.size_dot_holder_map)?.toInt()?:0

        mSizeHolderActiveLine = resources.getDimension(R.dimen.size_holder_map_active_dis).toInt()
        mSizeHolderActive = resources.getDimension(R.dimen.size_holder_map_active).toInt()

        getMapAsync(this)
        mMapWrapperLayout.setOnDragListener(this)
    }

    override fun getView(): View? {
        return mOriginalView
    }

    fun registerGoogleMapBehavior(googleMapBehavior: GoogleMapBehavior?){
        this.mGoogleMapBehavior = googleMapBehavior
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let{ map->

            map.uiSettings.isCompassEnabled = false
            map.uiSettings.isMapToolbarEnabled = false
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE

            map.setMaxZoomPreference(MAX_ZOOM_IN_MAP)
            map.setMinZoomPreference(MIN_ZOOM_IN_MAP)
            mGoogleMap = map

            mGoogleMap?.setOnCameraMoveListener {
                if(map.cameraPosition.zoom < MAX_ZOOM_IN_MAP &&  mZoomStateGreen == ZOOM_IN){
                    drawGreen(null,true)
                    mZoomStateGreen = ZOOM_OUT
                    mHitHolderToFlag = false
                }

                mHolderMarker?.let { holderMarker->
                    mTeeMarker?.let { teeMarker->
                        mFlagMarker?.let { flagMarker->
                            mGoogleMapBehavior?.onCameraMoveListener(teeMarker.position,holderMarker.position,
                                    flagMarker.position,this,mZoomStateGreen)
                            updateLine()
                        }
                    }
                }
            }
        }
    }

    override fun onDrag(motionEvent: MotionEvent?) {
        mGoogleMap?.also { map->
            mHolderMarker?.let { holderMarker ->
                mTeeMarker?.let { teeMarker ->
                    mFlagMarker?.let { flagMarker ->
                        motionEvent?.apply {
                            val currentX = x.toInt()
                            val currentY = y.toInt()
                            when (this.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    if(mZoomStateGreen != ZOOMING) {
                                        val pointTouch = Point(currentX, currentY)
                                        var currentPointMapToScreen = map.projection.toScreenLocation(holderMarker.position)
                                        /**
                                         * When touch holder area
                                         * */
                                        if (abs(pointTouch.x - currentPointMapToScreen.x) < resources.getDimension(R.dimen.size_holder_map) &&
                                                abs(pointTouch.y - currentPointMapToScreen.y) < resources.getDimension(R.dimen.size_holder_map)) {
                                            map.uiSettings.setAllGesturesEnabled(false)
                                            mTouchDown = HOLDER
                                            activeHolderMarker(map.projection.fromScreenLocation(pointTouch))
                                        } else {
                                            if(mCanMoveTee) {
                                                currentPointMapToScreen = map.projection.toScreenLocation(teeMarker.position)
                                                val isTeeTouch = abs(pointTouch.x - currentPointMapToScreen.x) < resources.getDimension(R.dimen.size_holder_map)
                                                        && abs(pointTouch.y - currentPointMapToScreen.y) < resources.getDimension(R.dimen.size_holder_map)
                                                if (isTeeTouch) {
                                                    mTouchDown = TEE
                                                    map.uiSettings.setAllGesturesEnabled(false)
                                                } else {
                                                    map.uiSettings.setAllGesturesEnabled(true)
                                                }
                                            }else{
                                                map.uiSettings.setAllGesturesEnabled(true)
                                            }
                                        }
                                    }else{
                                        map.uiSettings.setAllGesturesEnabled(false)
                                    }
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    when(mTouchDown){
                                        TEE -> {
                                            if(mCanMoveTee) {
                                                val pointDrag = Point()
                                                pointDrag.set(currentX, currentY)
                                                teeMarker.position = map.projection.fromScreenLocation(pointDrag)

                                                mGoogleMapBehavior?.onChangeDistance(teeMarker.position, holderMarker.position,
                                                        flagMarker.position,this@EasyGolfMapFragment,mZoomStateGreen)
                                                updateLine()
                                            }
                                        }
                                        HOLDER -> {
                                            val pointDrag = Point()
                                            pointDrag.set(currentX, currentY)
                                            GoogleMapsHelper.getDestinationPoint(map.projection.fromScreenLocation(pointDrag),
                                                    map.cameraPosition.bearing.toDouble(),getDist(mSizeHolderActiveLine,holderMarker.position))?.let {

                                                if (MapHelper.getDistanceLatLng(it, flagMarker.position, false) <= 5 && !mHitHolderToFlag) {
                                                    holderMarker.position = flagMarker.position
                                                } else {
                                                    if(mHitHolderToFlag) {
                                                        val pointsGreen = mPolylineGreen?.points
                                                        if (!pointsGreen.isNullOrEmpty()) {
                                                            if (PolyUtil.containsLocation(it, pointsGreen, true)) {
                                                                holderMarker.position = it
                                                            }else{
                                                                GoogleMapsHelper.getCenterPointFromList(pointsGreen)?.let { point->
                                                                    holderMarker.position = getPointNearPolyline(
                                                                            point
                                                                            ,it,pointsGreen)
                                                                }
                                                            }
                                                        }else{
                                                            holderMarker.position = it
                                                        }
                                                        flagMarker.position = holderMarker.position
                                                    }else{
                                                        holderMarker.position = it
                                                    }
                                                }
                                            }

                                            mGoogleMapBehavior?.onCameraMoveListener(teeMarker.position,holderMarker.position,
                                                    flagMarker.position,this@EasyGolfMapFragment,mZoomStateGreen)
                                            updateLine()
                                        }
                                        FLAG -> {

                                        }
                                        MAP,NONE -> {
                                            if(mZoomStateGreen == ZOOMING){
                                                mZoomStateGreen = ZOOM_OUT
                                                view
                                            }
                                            map.uiSettings.setAllGesturesEnabled(true)
                                        }
                                    }
                                }

                                MotionEvent.ACTION_UP->{
                                    when(mTouchDown){
                                        TEE -> {

                                        }
                                        HOLDER -> {
                                            unActiveHolderMarker()
                                        }
                                        FLAG -> {

                                        }
                                        MAP -> {

                                        }
                                        NONE -> {

                                        }
                                    }
                                    mTouchDown = NONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getDist(source:Int,atPosition:LatLng):Double {
       return mGoogleMap?.let { map->
            GoogleMapsHelper.getPerPixelToMeter(atPosition, map) * source / 4000f
        }?:0.0
    }

    fun toScreenLocation(latLng: LatLng?) = mGoogleMap?.projection?.toScreenLocation(latLng)

    fun getTeeMarkerPosition() = mTeeMarker?.position

    private var mCallbackAnimationTeeMarker = object : EventAnimationMarker {
        override fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation: Float) {
            mTeeMarker?.position = pos
            mHolderMarker?.let { holderMarker ->
                mTeeMarker?.let { teeMarker ->
                    mFlagMarker?.let { flagMarker ->
                        mGoogleMapBehavior?.onCameraMoveListener(teeMarker.position,holderMarker.position,
                                flagMarker.position,this@EasyGolfMapFragment,mZoomStateGreen)
                    }
                }
            }

            updateLine()
        }
    }

    private var mCurrentAnimatorTeeMarker: Animator? = null
    fun stopAnimationTeeMarker(){
        mCurrentAnimatorTeeMarker?.end()
    }
    fun enableMoveTee(isEnable:Boolean){
        mCanMoveTee = isEnable
    }
    fun updateTeeMarkerPosition(destination: LatLng,duration:Long? = null){
        mTeeMarker?.let { teeMarker->
            if(duration == null) {
                teeMarker.position = destination
                updateLine()
            }else{
                mCurrentAnimatorTeeMarker?.cancel()
                mCurrentAnimatorTeeMarker = MarkerAnimation.getInstance().animateMarker(
                        destination,
                        GoogleMapsHelper.getBearingFromLocation(destination,teeMarker.position),
                        teeMarker,mCallbackAnimationTeeMarker,
                        duration
                        )
            }
        }
    }

    fun onUpdateDistance(){
        mHolderMarker?.let { holderMarker ->
            mTeeMarker?.let { teeMarker ->
                mFlagMarker?.let { flagMarker ->
                    mGoogleMapBehavior?.onChangeDistance(teeMarker.position, holderMarker.position,
                            flagMarker.position, this@EasyGolfMapFragment,mZoomStateGreen)
                }
            }
        }
    }

    fun onChangeColorTee(@DrawableRes iconTee: Int,context: Context){
        mTeeMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(context,iconTee)))
    }

    fun updateFlagMarker(flagLatLng: LatLng, @DrawableRes iconFlag: Int, context: Context){
        mFlagMarker?.position = flagLatLng
        mFlagMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(context,iconFlag)))
        updateLine()
        onUpdateDistance()
    }

    private fun moveHolderToFlag(){
        mFlagMarker?.position?.let { newPosition->
            mHolderMarker?.position = newPosition
            updateLine()
        }
    }

    /**
     * TODO @quipham
     * */
    private fun getPointNearPolyline(start:LatLng,end:LatLng,points: List<LatLng>):LatLng?{
        return if(points.isNotEmpty()){
            //chi list ra lam 2
            val pointStart = points[0]
            val pointEnd = points[points.size/2]

            if(PolyUtil.distanceToLine(pointStart,start, end) < PolyUtil.distanceToLine(pointEnd,start, end)){
                // tim 3 diem 1 2 3
                val pointA = points[points.size - 1]
                val pointO = pointStart
                val pointB = points[1]

            }else{

            }

            var bestNearByPoint = points[0]
            var minDistance = PolyUtil.distanceToLine(bestNearByPoint,start, end)
            for (i in 1 until points.size){
                val distanceTemp = PolyUtil.distanceToLine(points[i],start, end)
                if(distanceTemp < minDistance){
                    bestNearByPoint = points[i]
                    minDistance = distanceTemp
                }
            }
            bestNearByPoint
        }else null
    }

    private fun activeHolderMarker(source:LatLng? = null){
        mGoogleMap?.also { map->
            mHolderMarker?.let { holderMarker->
                mTeeMarker?.let { teeMarker->
                    mFlagMarker?.let { flagMarker->
                        GoogleMapsHelper.getDestinationPoint(source?:holderMarker.position,
                                map.cameraPosition.bearing.toDouble(),
                                getDist(mSizeHolderActiveLine,source?:holderMarker.position))?.let { newLatLngHolder->
                            if(mZoomStateGreen == ZOOM_IN) {
                                mPolylineGreen?.points?.let { points ->
                                    if (PolyUtil.containsLocation(newLatLngHolder, points, true)) {
                                        activeHolderMarkerWithAnimation(map, newLatLngHolder, teeMarker, holderMarker, flagMarker)
                                    } else {
                                        getPointNearPolyline(holderMarker.position, newLatLngHolder, points)?.let { minPoint ->
                                            activeHolderMarkerWithAnimation(
                                                    map,
                                                    minPoint,
                                                    teeMarker, holderMarker, flagMarker)
                                        }
                                    }
                                } ?: activeHolderMarkerWithAnimation(map, newLatLngHolder, teeMarker, holderMarker, flagMarker)
                            }else{
                                activeHolderMarkerWithAnimation(map, newLatLngHolder, teeMarker, holderMarker, flagMarker)
                            }
                        }
                    }
                }
            }
            mHolderMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerHolderActiveBitmapFromView()))
        }
    }

    private fun activeHolderMarkerWithAnimation(map:GoogleMap,newLatLngHolder:LatLng,teeMarker:Marker,holderMarker:Marker,flagMarker:Marker){
        GoogleMapsHelper.animateMarker(newLatLngHolder, map.cameraPosition.bearing,holderMarker,object : EventAnimationMarker {
            override fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation: Float) {
                mHolderMarker?.position = pos
                if(mHitHolderToFlag) {
                    flagMarker.position = pos
                }
                onUpdateDistance()
                updateLine()
            }
        },100)?.apply {
            addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
        }
    }

    private fun unActiveHolderMarker(){
        mHolderMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerHolderUnActiveBitmapFromView()))
    }

    private fun updateLine(){
        mGoogleMap?.let { map->
            mHolderMarker?.let { holderMarker ->
                val radiusHolderOnMap =  mSizeHolderActive / 4 *  GoogleMapsHelper.getPerPixelToMeter(holderMarker.position,map)
                val dist = getDist(if(mTouchDown == HOLDER) mSizeHolderActive else mSizeHolderMarker,holderMarker.position)
                mTeeMarker?.let { teeMarker ->
                    if(mPolylineTeeToHolder == null) {
                        mPolylineTeeToHolder = drawLine(teeMarker.position, holderMarker.position, dist)
                    }else{
                        val distanceToTee: Double = GoogleMapsHelper.getDistanceLatLng(teeMarker.position, holderMarker.position)
                        val pointsStartTarget = ArrayList<LatLng>()
                        if (distanceToTee > radiusHolderOnMap) {
                            pointsStartTarget.add(teeMarker.position)
                            pointsStartTarget.add(MapHelper.getDestinationPoint(holderMarker.position,
                                    MapHelper.getBearingFromLocation(holderMarker.position, teeMarker.position).toDouble(), dist))
                        }
                        mPolylineTeeToHolder?.points = pointsStartTarget
                    }
                }
                mFlagMarker?.let { flagMarker ->
                    if(mPolylineFlagToHolder == null) {
                        mPolylineFlagToHolder = drawLine(flagMarker.position, holderMarker.position, dist)
                    }else{
                        val distanceToFlag: Double = MapHelper.getDistanceLatLng(flagMarker.position, holderMarker.position, false)
                        val pointsStartTarget = ArrayList<LatLng>()

                        if (distanceToFlag > radiusHolderOnMap) {
                            pointsStartTarget.add(flagMarker.position)
                            pointsStartTarget.add(MapHelper.getDestinationPoint(holderMarker.position,
                                    MapHelper.getBearingFromLocation(holderMarker.position, flagMarker.position).toDouble(), dist))
                        }
                        mPolylineFlagToHolder?.points = pointsStartTarget
                    }
                }
            }
        }

    }

    fun setUpMapForPlayGolf(teeLatLng:LatLng, @DrawableRes iconTee: Int, flagLatLng: LatLng, @DrawableRes iconFlag: Int, holderLatLng: LatLng, context: Context){
        if(mTeeMarker == null){
            mTeeMarker =  createMarker(teeLatLng,getMarkerBitmapFromView(context,iconTee))
        }else{
            mTeeMarker?.position = teeLatLng
        }

        if(mFlagMarker == null){
            mFlagMarker =  createMarker(flagLatLng,getMarkerBitmapFromView(context,iconFlag),0.35f, 0.85f)
            mFlagMarker?.zIndex = Float.MAX_VALUE
        }else{
            mFlagMarker?.position = flagLatLng
        }

        if(mHolderMarker == null){
            mHolderMarker =  createMarker(holderLatLng,getMarkerHolderBitmapFromView(context))
        }else{
            mHolderMarker?.position = holderLatLng
        }
        updateLine()
        mGoogleMapBehavior?.onChangeDistance(teeLatLng,holderLatLng,flagLatLng,this,ZOOM_OUT)
        zoomHoleBoundsWithDiagonal(teeLatLng,flagLatLng)
    }

    fun zoomGreen(coordinates:List<EasyGolfLocation>?){
        when(mZoomStateGreen){
            ZOOM_IN -> {
                mTeeMarker?.let { teeMarker->
                    mFlagMarker?.let { flagMarker->
                        mZoomStateGreen = ZOOMING
                        mHitHolderToFlag = false
                        coordinates?.let { drawGreen(it,true)}
                        zoomHoleBoundsWithDiagonal(teeMarker.position,flagMarker.position)
                    }
                }
            }
            ZOOMING -> {
            }
            ZOOM_OUT -> {
                mFlagMarker?.let { flagMarker->
                    mZoomStateGreen = ZOOMING
                    moveHolderToFlag()
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(flagMarker.position,MAX_ZOOM_IN_MAP)
                            ,object : GoogleMap.CancelableCallback{
                        override fun onCancel() {}
                        override fun onFinish() {
                            mHitHolderToFlag = true
                            mZoomStateGreen = ZOOM_IN
                            coordinates?.let { drawGreen(it) }
                            mHolderMarker?.let { holderMarker->
                                mTeeMarker?.let { teeMarker->
                                    mFlagMarker?.let { flagMarker->
                                        mGoogleMapBehavior?.onFinishZoomHole(teeMarker.position,holderMarker.position,
                                                flagMarker.position,this@EasyGolfMapFragment,mZoomStateGreen)
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }

    }

    private fun drawGreen(coordinates:List<EasyGolfLocation>?,isRemove:Boolean = false){
        mGoogleMap?.let { map->
            val listPoints = ArrayList<LatLng>()
            coordinates?.forEachIndexed { _, easyGolfLocation ->
                listPoints.add(LatLng(easyGolfLocation.latitude, easyGolfLocation.longitude))
            }?:listPoints.addAll(mPolylineGreen?.points?:ArrayList())

            if (listPoints.isNotEmpty()) {
                listPoints.add(listPoints.first())
            }
            mPolylineGreen?.remove()
            mPolylineGreen = map.addPolyline(context?.let { ContextCompat.getColor(it, R.color.green_border_color) }?.let {
                PolylineOptions().color(it)
                        .startCap(RoundCap())
                        .endCap(RoundCap())
                        .geodesic(true)
                        .jointType(JointType.ROUND)
                        .width(7f)
            }
            )
            val tAnimator = ValueAnimator.ofFloat(0f, 1f)
            tAnimator.interpolator =  LinearInterpolator()
            tAnimator.duration = 700

            tAnimator.addUpdateListener {
                val percentValue = if(!isRemove) (it.animatedValue as Float) else 1 -(it.animatedValue as Float)
                mPolylineGreen?.points = listPoints.subList(0,(percentValue * listPoints.size).toInt())
            }
            tAnimator.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(p0: Animator?) {}

                override fun onAnimationEnd(p0: Animator?) {
                    if(isRemove){
                        mPolylineGreen?.remove()
                    }
                }
                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationStart(p0: Animator?) {}
            })
            tAnimator.start()
        }
    }

    fun getSnapShot(callback: (Bitmap)->Unit){
        mGoogleMap?.let { map->
            map.snapshot {
                callback(it)
            }
        }
    }

    private fun zoomHoleBoundsWithDiagonal(startPosition: LatLng,endPosition: LatLng,isBonusPixel:Boolean = false){
        val bonusPixel = if(isBonusPixel) IN_BOUNDS_DIAGONAL else 1.0
        val dis = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dis)
        val widthPixel = dis.widthPixels
        val heightPixel = dis.heightPixels

        val maxWidth = (widthPixel * bonusPixel ).toInt()
        val maxHeight = (heightPixel * bonusPixel).toInt()

        val targetCameraPosition = CameraPosition.Builder()
                .bearing(MapHelper.getBearingFromLocation(startPosition, endPosition))
                .target(MapHelper.computeCentroid(startPosition,endPosition))
                .zoom(getBoundsZoomLevel(createBoundsWithMinDiagonal(startPosition,endPosition),maxWidth,maxHeight).toFloat())
                .build()

        val targetCameraUpdateFactory = CameraUpdateFactory.newCameraPosition(targetCameraPosition)
        mGoogleMap?.animateCamera(targetCameraUpdateFactory, object : GoogleMap.CancelableCallback {
            /**
             * Finish zoom hole
             * */
            override fun onFinish() {
                mZoomStateGreen = ZOOM_OUT
                mHolderMarker?.let { holderMarker->
                    mTeeMarker?.let { teeMarker->
                        mFlagMarker?.let { flagMarker->
                            mGoogleMapBehavior?.onFinishZoomHole(teeMarker.position,holderMarker.position,
                                    flagMarker.position,this@EasyGolfMapFragment,mZoomStateGreen)
                        }
                    }
                }
            }
            override fun onCancel() {
                Log.e("WOW","On cancel")
            }
        })
    }

    private fun drawLine(startPosition: LatLng,endPosition: LatLng,dist:Double = 0.0) :Polyline? {
        return mGoogleMap?.addPolyline(PolylineOptions()
                    .add(startPosition, GoogleMapsHelper.getDestinationPoint(endPosition,
                            GoogleMapsHelper.getBearingFromLocation(endPosition, startPosition).toDouble(),
                            dist))
                    .endCap(RoundCap())
                    .startCap(RoundCap())
                    .width(6f)
                    .geodesic(true)
                    .color(Color.WHITE))
    }

    private fun createMarker(point: LatLng, bitmap: Bitmap, xAnchor:Float = 0.5f,yAnchor :Float = 0.5f):Marker?{
         return mGoogleMap?.addMarker(
                MarkerOptions()
                        .position(point)
                        .anchor(xAnchor,yAnchor)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        )
    }

    private fun getMarkerBitmapFromView(context: Context, @DrawableRes icon:Int): Bitmap = createCustomMarker(createMarkerView(context,icon),1.3f)

    private fun getMarkerHolderBitmapFromView(context: Context): Bitmap {
        return  createCustomMarker(createHolderMap(context))
    }

    private fun getMarkerHolderActiveBitmapFromView(): Bitmap {
        return  getMarkerBitmapFromView(R.layout.view_holder_marker_golf_active)
    }

    private fun getMarkerHolderUnActiveBitmapFromView(): Bitmap {
        return  getMarkerBitmapFromView(R.layout.view_holder_marker_golf)
    }

    private fun getMarkerBitmapFromView(layout: Int): Bitmap {
        val customMarkerView = LayoutInflater.from(context).inflate(layout, null)
        customMarkerView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    private fun createHolderMap(context: Context):View{
        val containerHolderView = FrameLayout(context)
        val imgCircleHolderView = ImageView(context)
        imgCircleHolderView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.circle_white))
        imgCircleHolderView.adjustViewBounds = true
        containerHolderView.addView(
                imgCircleHolderView,
                mSizeHolderMarker,
                mSizeHolderMarker
        )
        (imgCircleHolderView.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
        val iconDotHolderView = ImageView(context)
        iconDotHolderView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.dot_white))
        iconDotHolderView.adjustViewBounds = true
        containerHolderView.addView(
                iconDotHolderView,
                mSizeDotHolderMarker,
                mSizeDotHolderMarker
        )
        (iconDotHolderView.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
        return containerHolderView
    }

    private fun createMarkerView(context: Context,@DrawableRes res:Int):View{
        val imgMarker = ImageView(context)
        imgMarker.adjustViewBounds = true
        imgMarker.setImageDrawable(ContextCompat.getDrawable(context,res))
        return imgMarker
    }

    private fun createCustomMarker(customMarkerView:View, disMore: Float = 1f): Bitmap{
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val measuredWidth =  (customMarkerView.measuredWidth * disMore).toInt()
        val measuredHeight =  (customMarkerView.measuredHeight * disMore).toInt()
        customMarkerView.layout(0, 0,measuredWidth,measuredHeight)
        val returnedBitmap = Bitmap.createBitmap(measuredWidth,measuredHeight,
                Bitmap.Config.ARGB_8888)

        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }
    /**
     * zoom camera
     * */
    private fun createBoundsWithMinDiagonal(startPosition: LatLng,endPosition: LatLng): LatLngBounds {
        val builder = LatLngBounds.Builder()
        builder.include(startPosition)
        builder.include(endPosition)
        val dis = MapHelper.getDistanceLatLng(startPosition,endPosition,false) * 1.1
        val center = MapHelper.computeCentroid(startPosition,endPosition)
        val northEast = move(center, dis,dis)
        val southWest = move(center, -1 * dis, -1 * dis)
        builder.include(southWest)
        builder.include(northEast)
        return builder.build()
    }

    private fun move(startLL: LatLng, toNorth: Double, toEast: Double): LatLng {
        val lonDiff = meterToLongitude(toEast, startLL.latitude)
        val latDiff = meterToLatitude(toNorth)
        return LatLng(startLL.latitude + latDiff, startLL.longitude + lonDiff)
    }

    private fun meterToLatitude(meterToNorth: Double): Double {
        val rad = meterToNorth / AppUtil.EARTH_RADIUS
        return Math.toDegrees(rad)
    }

    private fun meterToLongitude(meterToEast: Double, latitude: Double): Double {
        val latArc = Math.toRadians(latitude)
        val radius = cos(latArc) * AppUtil.EARTH_RADIUS
        val rad = meterToEast / radius
        return Math.toDegrees(rad)
    }

    private fun getBoundsZoomLevel(bounds: LatLngBounds, mapWidthPx: Int, mapHeightPx: Int): Double {
        val ne = bounds.northeast
        val sw = bounds.southwest
        val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI
        val lngDiff = ne.longitude - sw.longitude
        val lngFraction = if (lngDiff < 0) {
            (lngDiff + 360)
        } else {
            lngDiff / 360
        }
        val latZoom = zoom(mapHeightPx, latFraction)
        val lngZoom = zoom(mapWidthPx, lngFraction)

        val result = (latZoom + lngZoom)/2
        return result.coerceAtMost(MAX_ZOOM)
    }

    private fun latRad(lat: Double): Double {
        val sin = sin(lat * Math.PI / 180)
        val radX2 = ln((1 + sin) / (1 - sin)) / 2
        return radX2.coerceAtMost(Math.PI).coerceAtLeast(-Math.PI) / 2
    }

    private fun zoom(mapPx: Int, fraction: Double) = floor(ln(mapPx / WORLD_PX / fraction) / LN2)
}