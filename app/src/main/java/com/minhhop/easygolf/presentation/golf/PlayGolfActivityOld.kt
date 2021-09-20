package com.minhhop.easygolf.presentation.golf

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.minhhop.core.domain.ResultNotification
import com.minhhop.easygolf.framework.models.request.DataPushNotificationMembersRequest
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.PickerHoleAdapter
import com.minhhop.easygolf.adapter.PlayerAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle1
import com.minhhop.easygolf.framework.common.EasyGolfBounceInterpolator
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.framework.dialogs.*
import com.minhhop.easygolf.framework.golf.LocationHelper
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.listeners.*
import com.minhhop.easygolf.presentation.custom.DistanceGolfView
import com.minhhop.easygolf.presentation.golf.play.EasyGolfMapFragment
import com.minhhop.easygolf.services.*
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.models.common.Battle
import com.minhhop.easygolf.framework.models.common.BattleScorePlayer
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import com.minhhop.easygolf.framework.models.entity.DataRoundGolfEntity
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.presentation.endgame.EndGameActivity
import com.minhhop.easygolf.presentation.golf.component.marker.EventAnimationMarker
import com.minhhop.easygolf.presentation.golf.component.marker.MarkerAnimation
import com.minhhop.easygolf.views.activities.*
import com.minhhop.easygolf.views.fragments.MapWrapperLayout
import com.minhhop.easygolf.presentation.golf.component.menu.MenuMoreView
import com.minhhop.easygolf.presentation.golf.component.menu.MenuMoreView.CODE.*
import com.minhhop.easygolf.presentation.golf.component.tee.OnTeeListener
import com.minhhop.easygolf.presentation.feedback.FeedbackActivity
import com.minhhop.easygolf.widgets.ViewGreenLayout
import com.minhhop.easygolf.widgets.WozGolfButton
import com.minhhop.easygolf.widgets.WozViewDistance
import com.minhhop.easygolf.widgets.course.ChangTeeView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import eu.bolt.screenshotty.Screenshot
import eu.bolt.screenshotty.ScreenshotBitmap
import eu.bolt.screenshotty.ScreenshotManagerBuilder
import eu.bolt.screenshotty.ScreenshotResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.sin

open class PlayGolfActivityOld : WozBaseActivity(), OnMapReadyCallback, MapWrapperLayout.OnDragListener,
        MenuMoreView.HandleMenuMore, OnTeeListener {


//    private val playGolfViewModel:PlayGolfViewModel by inject()

    /**
     * State view green
     * */
    enum class StateViewGreen{
        ZOOM_IN,
        ZOOMING,
        ZOOM_OUT
    }
    /**
     * Const value
     * */
    companion object {
        private const val REQUEST_CODE_ADD_PLAYER = 124

        private const val MAX_ZOOM_IN_MAP = 20f
        private const val WORLD_PX = 256
        private const val MAX_ZOOM = 30
        private const val LN2 = 0.6931471805599453
        private const val TAG_FLAG = 0
        private const val TAG_HOLDER = 1
        private const val TAG_TEE = 2
        private const val MAX_DISTANCE_TO_SHOW_TRACK = 500
        private const val UNIT_PIXEL_METER = 156543.03392
        private const val IN_BOUNDS_DIAGONAL = 3.3
        private const val OUT_BOUNDS_DIAGONAL = 0.5

        private const val IN_BOUNDS_DIAGONAL_SHEET_BOTTOM = 3.3

        private const val OUT_BOUNDS_DIAGONAL_SHEET_BOTTOM = 0.75
    }
    private var mTargetRadiusHolder: Double = 53.775
    private var mTargetRadiusHolderActive: Double = 53.775
    private var mTargetRadiusHolderActiveLine: Double = 53.775
    private var mPositionHolder: LatLng = LatLng(10.833866, 106.651994)
    private var mPositionFlag: LatLng = LatLng(10.835892, 106.651798)
    private var mPositionTee: LatLng = LatLng(10.831569, 106.651827)


    /**
     * Start zone for value
     * */
    private var mCurrentUser: LatLng? = null
    /**
     * history points hole
     */
    private var isOnGolfCourse = false
    private var imagePath: File? = null
    protected lateinit var mDataPlayGolfs:Array<DataPlayGolf>

    private var isFormatYard: Boolean = true
    private var mSizeViewDistance: Float = 0.0f
    private var mPerPixelToMeter: Double = 0.0

    private var mNameCourse:String? = null
    private var mNameClub:String? = null

    private var mIndexTeeDefault = -1
    /**
     * Adapter list hole
     * */
    protected var mPickerHoleAdapter: PickerHoleAdapter? = null
    /**
     * When map is ready
     * */
    private var mIsMapReady = false

    private lateinit var mIdClub: String
    private lateinit var mIdCourse: String
    /**
     * hole current
     */
    protected var mHoleCurrent: Hole? = null
    /**
     * Round Match current
     */
    protected var mRoundMathCurrent: RoundMatch? = null
    /**
     * green current
     */
    private var mCurrentGreen: HolderGreen? = null
    /**
     * End zone for value
     * */
    /**
     * Start zone for view
     * */
    private var mImgPlayer: CircleImageView? = null
    private var mTxtNamePlayer: TextView? = null
    private lateinit var mViewRoot: CoordinatorLayout

    private var mGoogleMap: GoogleMap? = null
    private var mMarkerTee: Marker? = null
    private var mMarkerHolder: Marker? = null
    private var mMarkerFlag: Marker? = null

    private lateinit var mDistanceStartView: DistanceGolfView
    private lateinit var mDistanceEndView: DistanceGolfView
    /**
     * View in sheet bottom
     * */
    private var mTxtScoreSheet: TextView? = null
    private lateinit var mImageScoreSheet: ImageView
    private lateinit var mLayoutScoreSheet: View

    private lateinit var mLayoutFairway: View
    private var mTxtFairwaySheet: TextView? = null
    private var mIconFairway: ImageView? = null

    private var mTxtGreenInRegulationSheet: TextView? = null
    private var mLayoutGreen: View? = null
    private var mIconGreen: ImageView? = null

    private var mTxtPuttSheet: TextView? = null
    /**
     * Button score
     * */
    protected var mWozGolfButton: WozGolfButton? = null

    protected var mScoreGolfUpdate: ScoreGolfDialog? = null
    /**
     * Text show value par
     * */
    private var mTxtPar: TextView? = null
    /**
     * TextView current hole
     * */
    private var mTxtCurrentHole: TextView? = null
    /**
     * line from tee to current holder
     */
    private lateinit var mWozViewDistance: WozViewDistance
    /**
     * line from tee or last track to current holder
     */
    private var polylineStart: Polyline? = null
    /**
     * line from tracks to current holder
     */
    /**
     * line from flag to current holder
     *
     */
    private var polylineEnd: Polyline? = null
    /**
     * line on green
     *
     */
    private var mPolylineGreen: Polyline? = null

    /**
     * If user touch on the holder
     * */
    private var mIsHolder = false

    /**
     * If user touch on the tee
     * */
    private var mIsTeeTouch = false

    /**
     * View layer
     * */
    private var mViewLayer: View? = null
    /**
     * View list hole
     * */
    private var mViewListHole: View? = null
    /**
     * View Bottom Sheet
     * */
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var mViewHandlerSheet: View? = null
    /**
     * View contain sheet bottom
     * */
    private var mContainerSheet: View? = null
    /**
     * View list hole
     * */
    private var mBtBackHole: ImageView? = null
    /**
     * View list hole
     * */
    private var mBtNextHole: ImageView? = null
    /**
     * Button view green
     * */
    private lateinit var mBtViewGreen: View

    private lateinit var mIconViewGreen:ImageView

    private lateinit var mTxtCurrentDistance: TextView

    private lateinit var mTxtIndex: TextView


    /**
     * List view player
     * */
    private lateinit var mListPlayer: RecyclerView

    /**
     * View change default tee
     * */
    private lateinit var mViewChangeTee:ChangTeeView


    /**
     *View Green
     * */
    private lateinit var mViewPhotoGreen:ViewGreenLayout

    private var mViewGreen: Polygon? = null
    private var mStateViewGreen = StateViewGreen.ZOOM_OUT
    /**
     *View list point green
     * */
    private var mListPointViewGreen = ArrayList<LatLng>()

    private lateinit var mMenuMoreView: MenuMoreView
    private lateinit var mActionMenuMore:View

    /**
     * End zone for view
     * */

    private var mHitFlag = false

    /**
     * Show tutorial
     * */
    private var mIsShowTutorial: Boolean = true

    /**
     * ProjectionManager
     * */
    private var mProjectionManager:MediaProjectionManager? = null

    /**
     * Current Tee
     * */
    private var mCurrentTee:Tee? = null

    private var mIsOpenMenu = false

    /**
     * Service update current location
     * */

    private var mLocationHelperUpdate = LocationHelper()

    /**
     * Current id round
     * */

    private lateinit var mCurrentIdRound:String

    /**
     * DatabaseReference
     * */
    private lateinit var mProfileUserEntity: UserEntity
    private var mAdapterPlayer: PlayerAdapter? = null
    private var mPlayWithBattle = false

    private lateinit var mActionCancelRemoveFriend:View
    /**
     * Player is ready to play this game with battle
     * */
    private var mExitPlayGameBattle = false
    private var mIdRoundBattle: String? = null

    private var mDataFirebaseMemberRound:DatabaseReference? = null
    private var mEventMemberRound:ChildEventListener? = null

    private var mDataFirebaseBattle:DatabaseReference? = null
    private var mEventInviteBattle:ChildEventListener? = null


    /**
     * Check host is end game if while play battle
     * */
    private var mFirebaseCheckEndGameBattle:DatabaseReference? = null
    private var mEventCheckEndGameBattle:ChildEventListener? = null

    private var mFBListenerMyHole:DatabaseReference? = null
    private var mValueListenerMyHole:ValueEventListener? = null
    /**
     * status when this activity is active
     * */
    private var mIsRunning = true
    /**
     * Start main zone
     * */
    override fun setLayoutView(): Int {
        EventBus.getDefault().register(this)
        return R.layout.activity_play_golf_old
    }

    /**
     * Area no click
     * */

    private lateinit var mTopViewArea:View
    private lateinit var mStartViewArea:View
    private lateinit var mBottomViewArea:View
    private lateinit var mEndViewArea:View

    private var mTopArea = 0
    private var mBottomArea = 0
    private var mStartArea = 0
    private var mEndArea = 0


    private val screenshotManager by lazy {
        ScreenshotManagerBuilder(this)
                .withPermissionRequestCode(888)
                .build()

    }

    private var mScreenshotSubscription: ScreenshotResult.Subscription? = null

    override fun initView() {
        /**
         * Start get data from bundle
         * */

        val playGOlfBundle = EasyGolfNavigation1.playGolfBundle(intent)
        if (playGOlfBundle != null){
            mIdClub = playGOlfBundle.clubId
            mIdCourse = playGOlfBundle.courseId
            mPlayWithBattle = playGOlfBundle.playWithBattle
            mIdRoundBattle = playGOlfBundle.round
            mExitPlayGameBattle = playGOlfBundle.exitGameBattle

        }else{
            finish()
        }


        mProfileUserEntity = DatabaseService.getInstance().currentUserEntity!!
        /**
         * Start find views
         * */
        mViewPhotoGreen = findViewById(R.id.viewPhotoGreen)
        mViewChangeTee = findViewById(R.id.viewChangeListTee)
        mViewChangeTee.setEvent(this)
        mIconViewGreen = findViewById(R.id.iconViewGreen)

        mIsShowTutorial = PreferenceService.getInstance().tutorial
        mBtViewGreen = findViewById(R.id.btViewGreen)
        mBtViewGreen.setOnClickListener(this)
        findViewById<View>(R.id.iconChat).setOnClickListener(this)


        mActionMenuMore = findViewById(R.id.actionMenuMore)
        mActionMenuMore.setOnClickListener(this)

        mMenuMoreView = findViewById(R.id.menuMore)
        mMenuMoreView.closeView()
        mMenuMoreView.setEvent(this)

        findViewById<View>(R.id.containerSheet).setOnClickListener(this)
//        mLayoutScoreSheet = findViewById(R.id.layoutScoreSheet)
        mLayoutScoreSheet.setOnClickListener {
            mWozGolfButton!!.showDialogScore()
        }

//        mTxtScoreSheet = findViewById(R.id.txtScoreSheet)
//        mImageScoreSheet = findViewById(R.id.imgScoreSheet)

        mLayoutFairway = findViewById(R.id.layoutFairway)
        mLayoutFairway.setOnClickListener {
            mWozGolfButton!!.showDialogScore()
        }
//        mIconFairway = findViewById(R.id.iconFairway)
//        mTxtFairwaySheet = findViewById(R.id.txtFairwaySheet)

//        mLayoutGreen = findViewById(R.id.layoutGreen)
        mLayoutGreen!!.setOnClickListener {
            mWozGolfButton!!.showDialogScore()
        }
//        mTxtGreenInRegulationSheet = findViewById(R.id.txtGreenInRegulationSheet)
////        mIconGreen = findViewById(R.id.iconGreen)
//
//        mTxtPuttSheet = findViewById(R.id.txtPuttSheet)
        mTxtPuttSheet!!.setOnClickListener {
            mWozGolfButton!!.showDialogScore()
        }

        /**
         * End find views
         * */


        /**
         * Start area touch in map
         * */
        mTopViewArea = findViewById(R.id.toolBarBack)
        mTopViewArea.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mTopViewArea.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val pointViewArea = IntArray(2)
                mTopViewArea.getLocationOnScreen(pointViewArea)
                mTopArea = pointViewArea[1] + mTopViewArea.height
            }

        })

        mEndViewArea = findViewById(R.id.wozGolfButton)
        mEndViewArea.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mEndViewArea.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val pointViewArea = IntArray(2)
                mEndViewArea.getLocationOnScreen(pointViewArea)
                mEndArea = pointViewArea[0]
            }

        })

        mBottomViewArea = findViewById(R.id.btBackHole)
        mBottomViewArea.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mBottomViewArea.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val pointViewArea = IntArray(2)
                mBottomViewArea.getLocationOnScreen(pointViewArea)
                mBottomArea = pointViewArea[1]
            }

        })
        mStartViewArea = findViewById(R.id.iconChat)
        mStartViewArea.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                mStartViewArea.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val pointViewArea = IntArray(2)
                mStartViewArea.getLocationOnScreen(pointViewArea)
                mStartArea = pointViewArea[0] + mStartViewArea.width
            }


        })


        mWozViewDistance = findViewById(R.id.holderDistanceGreen)
        mTxtCurrentHole = findViewById(R.id.txtCurrentHole)


        /**
         * End area touch in map
         * */



        /**
         * If player is have a battle exit so hide add player
         * */
//        if(mExitPlayGameBattle){
//            findViewById<View>(R.id.containListPlayer).visibility = View.GONE
//        }

        mListPlayer = findViewById(R.id.listPlayer)
        mListPlayer.layoutManager = GridLayoutManager(this,3)
        mListPlayer.setRecycledViewPool(RecyclerView.RecycledViewPool())

//        mActionCancelRemoveFriend = findViewById(R.id.actionCancelRemoveFriend)
        mActionCancelRemoveFriend.visibility = View.GONE
        mActionCancelRemoveFriend.setOnClickListener {
            hideRemoveFriendBattle()
        }

        mTxtIndex = findViewById(R.id.txtIndex)
        mViewRoot = findViewById(R.id.viewRoot)
        mTxtPar = findViewById(R.id.txtPar)
        mWozGolfButton = findViewById(R.id.wozGolfButton)

        mTxtCurrentDistance = findViewById(R.id.txtCurrentDistance)
        /**
         * Call when save score
         * */
        mWozGolfButton!!.setEvent(object : EventWozButtonGolf {

            override fun onMore() {
                mHoleCurrent?.apply {
                    val bundle = Bundle()
                    bundle.putInt(Contains.EXTRA_PAR, par)
                    startActivityForResult(WozListScoreMoreActivity::class.java, Contains.REQUEST_SCORE_MORE, bundle)
                    overridePendingTransition(R.anim.show_alpha, R.anim.hide_alpha)
                }
            }

            override fun onSaveMe(data: DataPlayGolf) {
                mHoleCurrent?.apply {
                    mHitFlag = false
                    if(mPlayWithBattle) {
                        updateScoreToBattle(data)
                    }
                    hiddenViewDistance()
                    if(mHoleCurrent!!.number < mRoundMathCurrent!!.holes.size) {
                        mHoleCurrent = mRoundMathCurrent!!.holes[mHoleCurrent!!.number]
                        mPickerHoleAdapter!!.setSelected(mHoleCurrent!!.number)
                        initDataGolf()

                    }
                    updateArrowNext()
                    mDataPlayGolfs[number - 1] = data
                    data.distance = yard
                    data.par = par
                    data.idClub = mIdClub
                    DatabaseService.getInstance()
                            .updateScoreDataRoundGolf(number,data,OnComplete { })

                    if(!mPlayWithBattle) {
                        updateScoreOnSheet(mDataPlayGolfs[mHoleCurrent!!.number - 1])
                    }else{
                        FirebaseDatabase.getInstance().getReference("rounds").child(mIdRoundBattle.toString())
                                .child("holes").child(mHoleCurrent!!.number.toString()).child(mProfileUserEntity.id)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        val dataBattle = dataSnapshot.getValue(BattleScorePlayer::class.java)

                                        val dataScore = DataPlayGolf()
                                        if(dataBattle != null) {
                                            dataScore.mValueScore = dataBattle.score
                                        }else{
                                            dataScore.mValueScore = 0
                                        }
                                        mDataPlayGolfs[mHoleCurrent!!.number - 1] = dataScore
                                        updateScoreOnSheet(mDataPlayGolfs[mHoleCurrent!!.number - 1])
                                    }

                                })
                    }
                }
            }
        })



        /**
         * Display information player
         * */
        mImgPlayer = findViewById(R.id.imgPlayer)
        mTxtNamePlayer = findViewById(R.id.txtNamePlayer)

        findViewById<View>(R.id.btViewScoreCard).setOnClickListener(this)
//        findViewById<View>(R.id.btExitGame).setOnClickListener(this)

        mDistanceStartView = findViewById(R.id.viewDistanceStart)
        mDistanceEndView = findViewById(R.id.viewDistanceEnd)

        mDistanceStartView.visibility = View.INVISIBLE
        mDistanceEndView.visibility = View.INVISIBLE

        mPickerHoleAdapter = PickerHoleAdapter(this@PlayGolfActivityOld) { hole ->
            mHitFlag = false
            hiddenViewDistance()
            mHoleCurrent = hole
            initDataGolf()
            updateArrow()
            mViewLayer!!.visibility = View.GONE
            mViewListHole!!.visibility = View.GONE
        }

        findViewById<RecyclerView>(R.id.listHole).apply {
            layoutManager = GridLayoutManager(this@PlayGolfActivityOld, 6)
            adapter = mPickerHoleAdapter
        }
        /**
         * Find and set event quick back and next hole
         * */
        mBtBackHole = findViewById(R.id.btBackHole)
        mBtBackHole!!.setOnClickListener(this)
        mBtNextHole = findViewById(R.id.btNextHole)
        mBtNextHole!!.setOnClickListener(this)

        mWozViewDistance.setEvent(object : WozViewDistance.EventWozDistance {

            override fun onSelected(pos: WozViewDistance.POSITION) {
                mHoleCurrent?.apply {

                    mPositionFlag = when (pos) {
                        WozViewDistance.POSITION.BLACK -> {
                            mCurrentGreen = greens.getDefaultGreen(Green.FLAG.BLUE)
                            mCurrentGreen!!.typeFlag = HolderGreen.TYPE_FLAG.BLUE

                            LatLng(mCurrentGreen!!.latitude,
                                    mCurrentGreen!!.longitude)
                        }
                        WozViewDistance.POSITION.CENTER -> {
                            mCurrentGreen = greens.getDefaultGreen(Green.FLAG.WHITE)
                            mCurrentGreen!!.typeFlag = HolderGreen.TYPE_FLAG.WHITE
                            LatLng(mCurrentGreen!!.latitude,
                                    mCurrentGreen!!.longitude)

                        }
                        WozViewDistance.POSITION.FRONT -> {
                            mCurrentGreen = greens.getDefaultGreen(Green.FLAG.RED)
                            mCurrentGreen!!.typeFlag = HolderGreen.TYPE_FLAG.RED

                            LatLng(mCurrentGreen!!.latitude,
                                    mCurrentGreen!!.longitude)
                        }
                    }

                    mMarkerFlag?.position = mPositionFlag
                    if (mHitFlag || mStateViewGreen == StateViewGreen.ZOOM_IN) {
                        mPositionHolder = mPositionFlag
                        mMarkerHolder?.position = mPositionHolder
                        Log.e("WOW","go here")
                    }
                    mMarkerFlag?.setIcon(BitmapDescriptorFactory
                            .fromBitmap(getMarkerFlagBitmapFromView(Green.getDrawFlagGreen(mCurrentGreen!!.typeFlag))))
                    drawLine(mGoogleMap)
                    updateViewDistance()
                    /**
                     * Check if state zoom in -> move distance view start to flag
                     * */
                    moveViewDistanceStartToFlag()
                }
            }
        })

        /**
         * Handle gps
         * */
        handleGPS()

        /**
         * Handle current position
         * */



        /**
         * Get size view distance
         * */
        mSizeViewDistance = resources.getDimension(R.dimen.size_view_distance_golf)

        /**
         * Set event button show list hole
         * */
        mViewLayer = findViewById(R.id.layer)
        mViewListHole = findViewById(R.id.viewListHole)
        findViewById<View>(R.id.optionHole).setOnClickListener {
            mViewLayer!!.visibility = View.VISIBLE
            mViewListHole!!.visibility = View.VISIBLE
        }

        mViewLayer?.setOnClickListener {
            it.visibility = View.GONE
            mViewListHole!!.visibility = View.GONE
            mBottomSheetBehavior?.apply {
                if (this.state == BottomSheetBehavior.STATE_EXPANDED){
                    mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        }
        /**
         *  Handle bottom sheet behavior
         * */
        mContainerSheet = findViewById(R.id.containerSheet)

        mViewHandlerSheet = findViewById(R.id.viewHandlerSheet)
        mViewHandlerSheet!!.visibility = View.GONE

        val llBottomSheet = findViewById<View>(R.id.bottom_sheet_golf)
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet)

        mBottomSheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, per: Float) {

                mViewLayer?.alpha = per
                mViewHandlerSheet?.rotationX = per * 180
                if(mViewLayer?.visibility != View.VISIBLE){
                    mViewLayer?.visibility = View.VISIBLE
                }
            }

            override fun onStateChanged(p0: View, state: Int) {
                // Zoom map to fit see all

                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mViewLayer?.visibility = View.VISIBLE
//                        zoomInOutSheetBottom(false)
                        mIsOpenMenu = true
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                        zoomInOutSheetBottom(true)
                        mViewLayer?.visibility = View.GONE
                        mViewLayer?.alpha = 1f
                        hideRemoveFriendBattle()
                    }
                    BottomSheetBehavior.STATE_DRAGGING->{

                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED->{}

                    BottomSheetBehavior.STATE_SETTLING->{}

                    BottomSheetBehavior.STATE_HIDDEN->{}
                }
            }

        })

        findViewById<View>(R.id.viewHandlerSheet).setOnClickListener {
            mBottomSheetBehavior?.apply {
                if (this.state == BottomSheetBehavior.STATE_COLLAPSED){
                    mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }

        checkInvite()
    }

    override fun loadData() {
//        playGolfViewModel.roundMatchLive.observe(this,androidx.lifecycle.Observer { result->
//
//        })
    }

    override fun onDestroy() {
        mDataFirebaseBattle?.apply {
            this.removeEventListener(mEventInviteBattle!!)
        }
        mDataFirebaseMemberRound?.removeEventListener(mEventMemberRound!!)
        EventBus.getDefault().unregister(this)
        mFirebaseCheckEndGameBattle?.removeEventListener(mEventCheckEndGameBattle!!)

        mValueListenerMyHole?.apply {
            mFBListenerMyHole?.removeEventListener(this)
        }

        mScreenshotSubscription?.dispose()

        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mIsRunning = false
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        mLocationHelperUpdate.removeLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        mIsRunning = true
        mIdRoundBattle?.apply {
            if(mFirebaseCheckEndGameBattle == null) {
                mFirebaseCheckEndGameBattle = FirebaseDatabase.getInstance()
                        .getReference("rounds").child(this)
                mEventCheckEndGameBattle = object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {}
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {}
                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        /**
                         * show dialog host is end game
                         * */
//                        if(mPlayWithBattle) {
//                            HostEndGameDialog(this@PlayGolfActivityOld,
//                                    object : HostEndGameDialog.ListenerConfirmData {
//                                        override fun onConfirm() {
//                                            DatabaseService.getInstance().removeDataRoundGolf(mIdClub,OnComplete
//                                            {
//                                                val bundle = Bundle()
//                                                bundle.putString(Contains.EXTRA_ID_ROUND_BATTLE, this@apply)
//                                                bundle.putBoolean(Contains.EXTRA_IS_SHOW_LIKE_HISTORY, true)
//                                                bundle.putString(Contains.EXTRA_ID_CLUB, mIdClub)
//                                                bundle.putString(Contains.EXTRA_NAME_COURSE, mNameCourse)
//                                                bundle.putString(Contains.EXTRA_NAME_CLUB, mNameClub)
//                                                startActivity(EndGameActivity::class.java, bundle)
//                                                finish()
//                                            })
//
//                                        }
//
//                                    }).show()
//
//                            mPlayWithBattle = false
//                        }
                    }
                }

                mFirebaseCheckEndGameBattle?.addChildEventListener(mEventCheckEndGameBattle!!)
            }

        }

        updateLocationGPS()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        finish()
    }

    fun show(screenshot: Screenshot,bitmapMap: Bitmap) {
        val bitmap = when (screenshot) {
            is ScreenshotBitmap -> screenshot.bitmap
        }
        val bmOverlay = Bitmap.createBitmap(
                bitmap.width, bitmap.height,
                bitmap.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bitmapMap,  Matrix(), null)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        saveBitmap(bmOverlay)
        shareIt()
    }

    private fun createCaptureScreenShot(){
        val callbackSnap = GoogleMap.SnapshotReadyCallback { bitmap ->
            bitmap?.apply {
                val screenshotResult = screenshotManager.makeScreenshot()
                mScreenshotSubscription = screenshotResult.observe(
                        onSuccess = {
                            show(it,this)
                        },
                        onError = {  }
                )

            }
        }

        mGoogleMap?.let{ mMap->
            mMap.setOnMapLoadedCallback(object : GoogleMap.OnMapLoadedCallback{
                override fun onMapLoaded() {
                    mMap.snapshot(callbackSnap)
                }

            })
        }


    }

    /**
     * Event select menu bar()
     * */
    override fun onClick(code: MenuMoreView.CODE) {
        when (code) {
            VIEW_GREEN -> {
                mViewPhotoGreen.showPhoto()
            }
            REPORT_THIS_HOLE -> {
                startActivity(FeedbackActivity::class.java)
            }
            SHARE -> {

                //  check permission here
//                Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        .withListener(object : MultiplePermissionsListener {
//                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                                if(report.areAllPermissionsGranted()){
//                                    createCaptureScreenShot()
//                                }
//                            }
//                            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
//                                token.continuePermissionRequest()
//                            }
//
//                        }).onSameThread().check()

            }
            END_GAME -> {

                val bundle = Bundle()
                bundle.putBoolean(Contains.EXTRA_PLAY_WITH_BATTLE,mPlayWithBattle)
                bundle.putString(Contains.EXTRA_ID_CLUB,mIdClub)
                bundle.putString(Contains.EXTRA_NAME_COURSE, mNameCourse)
                startActivity(EndGameActivity::class.java,bundle)


//                finishRound()
            }
            CHANGE_UNIT -> {
                isFormatYard = !isFormatYard
                title = if (isFormatYard) {
                    getString(R.string.change_to_meter)
                } else {
                    getString(R.string.change_to_yard)
                }
//                mDistanceStartView.changeUnit(isFormatYard)
//                mDistanceEndView.changeUnit(isFormatYard)

                updateViewDistance()

                mHoleCurrent?.apply {
//                    mWozViewDistance.updateDistance(mPositionTee,this,isFormatYard)
                }

                mCurrentTee?.apply {
                    mTxtCurrentDistance.text = if (isFormatYard) {
                        resources.getString(R.string.format_yard_full, yard.toString())
                    } else {
                        val tempDis = MapHelper.convertYardToMeter(yard)
                        resources.getString(R.string.format_meter, tempDis.toString())
                    }
                }
            }
            CHANGE_DEFAULT_TEE -> {
                mViewChangeTee.show()
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {

        val timeStamp = SimpleDateFormat(Contains.FORMAT_IMAGE, Locale.ENGLISH).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        imagePath = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        val fos: FileOutputStream
        try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.localizedMessage, e)
        } catch (e: IOException) {
            Log.e("GREC", e.localizedMessage, e)
        }
    }


    private fun shareIt() {
        imagePath?.let { path->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            val fileUri = FileProvider.getUriForFile(this, "com.minhhop.easygolf.provider", path)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            startActivity(Intent.createChooser(shareIntent, "easyGolf share"))
        }


//        val uri = Uri.fromFile(imagePath)
//        val sharingIntent = Intent(Intent.ACTION_SEND)
//        sharingIntent.type = "image/*"
//        val shareBody = "say something"
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "say something")
//        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
//        startActivity(Intent.createChooser(sharingIntent, "EasyGolf share"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mHoleCurrent?.apply {
            data?.apply {
                /**
                 * Handle result when select more score in dialog
                 * */

                when(requestCode){
                    Contains.REQUEST_SCORE_MORE->{
                        val score = getIntExtra(Contains.EXTRA_PAR, 0)
                        mWozGolfButton!!.updateScore(score)
                    }
                    Contains.REQUEST_SCORE_MORE_TO_ANOTHER_PLAYER->{
                        val score = getIntExtra(Contains.EXTRA_PAR,0)
                        mScoreGolfUpdate?.updateScore(score)
                    }
                    REQUEST_CODE_ADD_PLAYER ->{
                        val listUserId = this.getStringExtra(Contains.EXTRA_IS_RETURN)
                        if(listUserId.isNotEmpty()) {

                            val idRoundAddPlayer = mIdRoundBattle ?: mRoundMathCurrent!!.id.toString()

                            BattleService().addPlayer(listUserId,idRoundAddPlayer, mIdClub,
                                    this@PlayGolfActivityOld,object : BattleService.Callback{
                                override fun onSuccess(listUser: List<String>) {
                                    if (mIdRoundBattle == null) {
                                        if (!mPlayWithBattle) {
                                            mPlayWithBattle = true
                                            mIdRoundBattle = mRoundMathCurrent!!.id.toString()

                                            val listDataScore = DatabaseService.getInstance().getDataPlayGolf(mIdClub)
                                            var isExitData = false
                                            for (item in listDataScore){
                                                if(item.mValueScore != 0 || item.mValueFairwayHit != -1 || item.mValueGreenInRegulation != -1 || item.mValuePutt != -1){
                                                    isExitData = true
                                                    break
                                                }
                                            }
                                            if(isExitData) {
                                                ConfirmDataDialog(this@PlayGolfActivityOld, mIdClub, mIdRoundBattle!!,
                                                        object : ConfirmDataDialog.ListenerConfirmData {

                                                            override fun onConfirm() {
                                                                BattleService().addMe(mProfileUserEntity.id, mIdRoundBattle!!, mIdClub)
                                                                mAdapterPlayer!!.setIdRoundBattle(mIdRoundBattle.toString())
                                                                repairForBattlePlayer(true)
                                                            }

                                                         }).show()
                                            }else{
                                                BattleService().addMe(mProfileUserEntity.id, mIdRoundBattle!!, mIdClub)
                                                mAdapterPlayer!!.setIdRoundBattle(mIdRoundBattle.toString())
                                                repairForBattlePlayer(true)
                                            }
                                        }


                                    }

                                    sendNotificationInvite(idRoundAddPlayer,listUser)
                                }

                            })
                        }

                    }else->{
                        mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager?
                        mProjectionManager?.apply {
                            val projection = getMediaProjection(resultCode,data)
                            createImageReader(projection)

                        }
                    }
                }
            }

        }
    }
    /**
     * Call api to push notification to users invited success
     * **/

    private fun sendNotificationInvite(roundId:String,listUser:List<String>){

        if(listUser.isNotEmpty()){
            registerResponse(ApiService.getInstance().golfService
                    .pushNotificationToUserInviteBattle(roundId, DataPushNotificationMembersRequest(listUser)),
                    object : HandleResponse<ResultNotification>{
                        override fun onSuccess(result: ResultNotification) {

                        }
                    })

        }


    }

    private fun createImageReader(projection:MediaProjection){
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.apply {
            val display = defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            val size = Point()
            display.getRealSize(size)
            val widthImg = size.x
            val heightImg = size.y

            val mDensity = metrics.densityDpi
            val mImageReader = ImageReader.newInstance(widthImg,heightImg,PixelFormat.RGBA_8888,10)
            val handler = Handler()
            val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
            projection.createVirtualDisplay("screen-mirror",widthImg,heightImg,mDensity,flags,mImageReader.surface,null,handler)

            mImageReader.setOnImageAvailableListener({
                it.setOnImageAvailableListener(null,handler)
                val img = it.acquireLatestImage()
                val planes = img.planes
                Toast.makeText(this@PlayGolfActivityOld,"planes: ${planes.size}",Toast.LENGTH_SHORT).show()

                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride

                val rowPadding = rowStride - pixelStride * metrics.widthPixels
                /**
                 * Create bitmap
                 * */
                val bmp = Bitmap.createBitmap(metrics.widthPixels + rowPadding/pixelStride,
                        metrics.heightPixels,Bitmap.Config.ARGB_8888)
                bmp.copyPixelsFromBuffer(buffer)

                img.close()
                it.close()
                val realSizeBitmap = Bitmap.createBitmap(bmp,0,0,metrics.widthPixels,bmp.height)
                bmp.recycle()

//
//                if(realSizeBitmap == null){
//                    Toast.makeText(this@PlayGolfActivityOld,"is null bitmap",Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(this@PlayGolfActivityOld,"not never null bitmap",Toast.LENGTH_SHORT).show()
//                }

                saveBitmap(realSizeBitmap)
                shareIt()
            },handler)

        }
    }
    /**
     * Event change default tee
     * */
    override fun onClickTee(tee: com.minhhop.core.domain.golf.Tee?, position: Int) {
//        tee?.apply {
//            mMarkerTee?.setIcon(BitmapDescriptorFactory.fromBitmap(getTeeBitmapFromView(this.resIcon)))
//            mIndexTeeDefault = position
//            mCurrentTee = this
//            mTxtCurrentDistance.text = if (isFormatYard) {
//                resources.getString(R.string.format_yard_full, yard.toString())
//            } else {
//                val tempDis = MapHelper.convertYardToMeter(yard)
//                resources.getString(R.string.format_meter, tempDis.toString())
//            }
//        }
    }


    private fun repairForBattlePlayer(isHost:Boolean){
        if(mAdapterPlayer == null) {
            mAdapterPlayer = PlayerAdapter(this, mIdRoundBattle!!.toString(),
                    isHost, object : OnAddPlayerListener {
                override fun showButtonCanncel() {
                    mActionCancelRemoveFriend.visibility = View.VISIBLE
                }

                override fun removeUser(player: ProfileUser) {
                    ConfirmDialog(this@PlayGolfActivityOld)
                            .setContent(getString(R.string.content_remove_player_from_round,player.getFullName()))
                            .setOnConfirm {
                                actionRemoveFriendInThisBattle(player._id)
                            }.show()
                }

                override fun onClickPlayer(player: ProfileUser, data: BattleScorePlayer) {
                    showDialogScoreAnother(player, data)
                }

                override fun onAdd() {
                    val bundle = Bundle()
                    mAdapterPlayer?.apply {
                        bundle.putString(Contains.EXTRA_BLACK_LIST,this.getListFriendByString())
                    }
                    bundle.putBoolean(Contains.EXTRA_LIMIT,true)
                    startActivityForResult(AddPeopleChatActivity::class.java, REQUEST_CODE_ADD_PLAYER,bundle)
                }
            })
            mListPlayer.adapter = mAdapterPlayer
        }

        if(mPlayWithBattle) {

            if(mDataFirebaseMemberRound == null) {
                mDataFirebaseMemberRound = FirebaseDatabase.getInstance()
                        .getReference("rounds").child(mIdRoundBattle!!.toString())
                        .child("members")

                mEventMemberRound = object : ChildEventListener     {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        val idMember = dataSnapshot.getValue(MemberRoundBattle::class.java)
                        idMember?.let { _idMember ->
                            FirebaseDatabase.getInstance().getReference("users").child(_idMember.id)
                                    .child("profile")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(snapProfile: DataSnapshot) {
                                            val member = snapProfile.getValue(ProfileUser::class.java)
                                            member?.apply {
                                                if (!mProfileUserEntity.id.equals(_idMember.id, false)) {
                                                    _id = _idMember.id
                                                    mAdapterPlayer!!.addItem(this)
                                                }
                                            }
                                        }

                                    })
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        if (mIsRunning) {
                            val idMember = dataSnapshot.getValue(MemberRoundBattle::class.java)
                            idMember?.let { _idMember ->
                                FirebaseDatabase.getInstance().getReference("users").child(_idMember.id)
                                        .child("profile")
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(snapProfile: DataSnapshot) {
                                                val member = snapProfile.getValue(ProfileUser::class.java)
                                                member?.apply {
                                                    _id = _idMember.id

                                                }?.let {
                                                    Toast.makeText(this@PlayGolfActivityOld,
                                                            "${it.getFullName()} was leave this round", Toast.LENGTH_LONG).show()
                                                    mAdapterPlayer!!.removePlayer(it)
                                                }
                                            }

                                        })
                            }
                        }
                    }
                }

                mDataFirebaseMemberRound!!.addChildEventListener(mEventMemberRound!!)
            }


        }
    }

    private fun showDialogScoreAnother(player: ProfileUser, data: BattleScorePlayer){

        mHoleCurrent?.let { hole->
            mScoreGolfUpdate =
                    ScoreGolfDialog(this@PlayGolfActivityOld,hole.number,hole.par,hole.index,
                            player.getFullName(),player.avatar)

            mScoreGolfUpdate!!
                    .setScoreWhenStart(data.score)
                    .setFairwayWhenStart(data.fairway_hit)
                    .setGreenWhenStart(data.green)
                    .setPuttWhenStart(data.putts)
                    .setEventScore(object : EventMoreScore{
                        override fun close() {

                        }

                        override fun callMe() {
                            val bundle = Bundle()
                            bundle.putInt(Contains.EXTRA_PAR, hole.par)
                            startActivityForResult(WozListScoreMoreActivity::class.java,
                                    Contains.REQUEST_SCORE_MORE_TO_ANOTHER_PLAYER, bundle)
                            overridePendingTransition(R.anim.show_alpha, R.anim.hide_alpha)
                        }

                        override fun saveMe(score: Int, fairway: Int, green: Int,
                                            putt: Int, resWhite: Int, res: Int) {
                            val dataGolfUpdate = DataPlayGolf()
                            dataGolfUpdate.mValueScore = score
                            dataGolfUpdate.mValueFairwayHit = fairway
                            dataGolfUpdate.mValueGreenInRegulation = green
                            dataGolfUpdate.mValuePutt = putt
                            updateScoreToBattle(dataGolfUpdate,player._id)
                            mScoreGolfUpdate?.dismiss()
                        }

                    })
                    .show()
        }

    }

    private fun updateScoreToBattle(data: DataPlayGolf, idPlayer: String? = null){
        val idPlayerUpdate = idPlayer ?: mProfileUserEntity.id

        mHoleCurrent?.let { _hole->
            val mDbReferenceScore = FirebaseDatabase.getInstance().getReference("rounds")
                    .child(mIdRoundBattle.toString()).child("holes")
                    .child(_hole.number.toString()).child(idPlayerUpdate)
            val dataScore = HashMap<String,Int>()
            dataScore["score"] = data.mValueScore
            if(data.mValueFairwayHit >= 0)
            dataScore["fairway_hit"] = data.mValueFairwayHit
            if(data.mValueGreenInRegulation >= 0)
            dataScore["green"] = data.mValueGreenInRegulation
            if(data.mValuePutt >= 0)
            dataScore["putts"] = data.mValuePutt
            mDbReferenceScore.setValue(dataScore)
        }

    }



    private fun hideRemoveFriendBattle(){
        mAdapterPlayer?.apply {
            this.hideOrOpenRemove()
        }
        mActionCancelRemoveFriend.visibility = View.GONE
    }

    private fun actionRemoveFriendInThisBattle(id:String){
        if(mPlayWithBattle && mDataFirebaseMemberRound != null){
            mDataFirebaseMemberRound!!.orderByChild("id").equalTo(id)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (item in p0.children){
                        item.key?.let {
                            mDataFirebaseMemberRound!!.child(it).setValue(null)
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("users").child(id).child("battles")
                            .orderByChild("round_id").equalTo(mIdRoundBattle)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    Log.e("WOW","key2: ${p0.key}")
                                    for (item in p0.children){
                                        item.key?.let {
                                            FirebaseDatabase.getInstance().getReference("users").child(id)
                                                    .child("battles").child(it).setValue(null)
                                        }
                                    }
                                }

                            })
                }

            })
        }
    }

    /**
     * Handle current position
     * */
    private fun updateLocationGPS(){

//        mLocationHelperUpdate.startLocationUpdatesForActivity(this,
//                true, object : LocationHelper.OnLocationCatch {
//            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
//                mLatLng?.apply {
//
//                    //                    if(MapHelper.getDistanceLatLng(this,mPositionTee,true) <= MAX_DISTANCE_TO_SHOW_TRACK){
//
//                    /**
//                     * Update position current user
//                     * */
////                    Log.e("WOW","update location time ${this.latitude} - ${this.longitude}")
//                    mCurrentUser = this
//                    /**
//                     * Hide track when user far from tee
//                     * */
////                    if (MapHelper.getDistanceLatLng(mCurrentUser, mPositionTee, true) <= MAX_DISTANCE_TO_SHOW_TRACK) {
////                        mPositionTee = mCurrentUser!!
////                        mMarkerTee?.position = mPositionTee
////                    }
//
//
//                    if (isOnGolfCourse) {
//
//                        MarkerAnimation.getInstance().animateMarker(mCurrentUser!!, bearing, mMarkerTee
//                                , object : EventAnimationMarker {
//
//                            override fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation: Float) {
//                                Log.e("WOW","call back update location golf")
//                                val distanceFromUserToTee = MapHelper.getDistanceLatLng(mPositionTee,mCurrentUser!!,true)
//                                mPositionTee = if(!mIsRunning || distanceFromUserToTee > 50){
//                                    mCurrentUser!!
//                                }else{
//                                    pos
//                                }
//                                mMarkerTee?.also {
//                                    it.position = mPositionTee
//                                    it.rotation = rotation
//                                }
//
//
//                                drawLine(mGoogleMap)
//                                updateViewDistance()
//                                updateWozViewDistance(false)
//
//                                if(!mIsRunning){
//                                    animation.cancel()
//                                }
//                                if(distanceFromUserToTee  > 50){
//                                    animation.cancel()
//                                }
//                            }
//                        })
//                    }else{
//                        isOnGolfCourse = MapHelper.getDistanceLatLng(mCurrentUser, mPositionTee, true) <= MAX_DISTANCE_TO_SHOW_TRACK
//                    }
//
//                    /**
//                     * check should enable track shot
//                     * */
//                }
//            }
//
//            override fun locationNotAvailable() {
//
//            }
//
//        })
    }

    private fun handleGPS(){
        /**
         * TODO set player temp
         * */
        Log.e("WOW","mPlayWithBattle2: $mPlayWithBattle")
        if(mPlayWithBattle) {
            FirebaseDatabase.getInstance().getReference("users").child(mProfileUserEntity.id)
                    .child("battles").orderByChild("club_id").equalTo(mIdClub)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {

                            var haveBattle: Battle? = Battle()
                            for (postSnapshot in dataSnapshot.children) {
                                haveBattle = postSnapshot.getValue(Battle::class.java)

                            }
                            if (haveBattle != null) {
                                repairForBattlePlayer(haveBattle.is_host)
                                getLocationForLoadData()
                            }

                        }

                    })


        } else{
            mAdapterPlayer = PlayerAdapter(this,null,
                    true,object : OnAddPlayerListener{
                override fun showButtonCanncel() {
                    mActionCancelRemoveFriend.visibility = View.VISIBLE
                }

                override fun removeUser(player: ProfileUser) {
                    ConfirmDialog(this@PlayGolfActivityOld)
                            .setContent(getString(R.string.content_remove_player_from_round,player.getFullName()))
                            .setOnConfirm {
                                actionRemoveFriendInThisBattle(player._id)
                            }.show()
                }

                override fun onClickPlayer(player: ProfileUser, data: BattleScorePlayer) {
                    showDialogScoreAnother(player, data)
                }

                override fun onAdd() {
                    val bundle = Bundle()
                    mAdapterPlayer?.apply {
                        bundle.putString(Contains.EXTRA_BLACK_LIST,this.getListFriendByString())
                    }
                    bundle.putBoolean(Contains.EXTRA_LIMIT,true)
                    startActivityForResult(AddPeopleChatActivity::class.java, REQUEST_CODE_ADD_PLAYER,bundle)
                }

            })
            mListPlayer.adapter = mAdapterPlayer!!

            getLocationForLoadData()
        }

    }

    /**
     * Load data from realm or api by location
     * */

    private fun getLocationForLoadData(){
        val locationHelper = LocationHelper()
//        locationHelper.startLocationUpdates(this,
//                false, mViewRoot, object : LocationHelper.OnLocationCatch {
//            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
//                mLatLng?.apply {
//                    mCurrentUser = this
//                    val data = DataLocation()
//                    data.tee = "blue"
//                    data.latitude = latitude
//                    data.longitude = longitude
//                    getOverrideData(data)
//                }
//            }
//
//            override fun locationNotAvailable() {
//                AccessLocationDialog(this@PlayGolfActivityOld, null)
//                        .show()
//            }
//        })
    }

    /**
     * if do not play in the battle -> check someone invite to
     * */
    private fun checkInvite(){
        if(!mPlayWithBattle && mDataFirebaseBattle == null){
            mDataFirebaseBattle = FirebaseDatabase.getInstance().getReference("users").child(mProfileUserEntity.id)
                    .child("battles")

            mEventInviteBattle = object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {

                    val currentBattle = snapshot.getValue(Battle::class.java)
                    currentBattle?.apply {
                        /**
                         * someone is invite you to play battle
                         */
                        if(!this.is_host) {
                            InviteToBattleDialog(this@PlayGolfActivityOld, mProfileUserEntity.id,
                                    this, object : InviteToBattleDialog.EventInvite {
                                override fun onAccept() {
                                    DatabaseService.getInstance().removeDataRoundGolf(mIdClub,OnComplete {
                                        mPlayWithBattle = true
                                        mIdRoundBattle = this@apply.round_id

                                        EasyGolfNavigation1.playGolfDirection(this@PlayGolfActivityOld,
                                                PlayGolfBundle1(true,mIdRoundBattle,mIdClub,mIdCourse))
                                        finish()
                                    })

                                }

                            }).show()
                        }

                    }

                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }
            }
            mDataFirebaseBattle!!.orderByChild("club_id").equalTo(mIdClub).
                    addChildEventListener(mEventInviteBattle!!)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            mViewHandlerSheet!!.visibility = View.VISIBLE
            mGoogleMap = this
//            this.isMyLocationEnabled = true
            uiSettings.isCompassEnabled = false
            uiSettings.isMapToolbarEnabled = false
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            hideLoading()
            /**
             * Update camera
             * */
            setOnCameraMoveListener {
                updateDrawLineTeeHolderFlag()
            }

            setMaxZoomPreference(MAX_ZOOM_IN_MAP)
            setMinZoomPreference(15f)

            repairMap()
            /**
             * Map click outside
             * */
            setOnMapClickListener {
                mBottomSheetBehavior?.apply {
                    if (this.state == BottomSheetBehavior.STATE_EXPANDED){
                        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

                if(!mIsOpenMenu) {

                    val pointClick = projection.toScreenLocation(it)

                    if (pointClick.x in (mStartArea + 1) until mEndArea && pointClick.y > mTopArea && pointClick.y < mBottomArea) {

                        MarkerAnimation.getInstance().animateMarker(it, MapHelper.getBearingFromLocation(it, mPositionHolder), mMarkerHolder,
                                object : EventAnimationMarker {

                                    override fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation: Float) {
                                        if(!mIsRunning){
                                            animation.cancel()
                                        }
                                        mPositionHolder = pos
                                        mMarkerHolder?.position = pos
                                        updateViewDistance()
                                        drawLine(mGoogleMap)
                                    }

                                }, 100)

                    }
                }
                mIsOpenMenu = false
            }
        }
    }

    private fun repairMap() {
        mGoogleMap?.apply {
            //for flag
            if (mMarkerFlag == null) {
                mMarkerFlag = addMarker(createMarkerFlag(mPositionFlag))
                mMarkerFlag!!.tag = TAG_FLAG
            } else {
                mMarkerFlag!!.position = mPositionFlag
                mMarkerFlag?.setIcon(BitmapDescriptorFactory
                        .fromBitmap(getMarkerFlagBitmapFromView(Green.getDrawFlagGreen(HolderGreen.TYPE_FLAG.WHITE))))
            }

            if (mMarkerHolder == null) {
                mMarkerHolder = addMarker(createMarker(mPositionHolder, R.layout.view_holder_marker_golf))
                mMarkerHolder!!.tag = TAG_HOLDER
            } else {
                mMarkerHolder!!.position = mPositionHolder
            }

            if (MapHelper.getDistanceLatLng(mPositionTee, mCurrentUser, true) <= MAX_DISTANCE_TO_SHOW_TRACK) {
                mPositionTee = mCurrentUser!!
            }

            if (mMarkerTee == null) {
                mMarkerTee = addMarker(createMarker(mPositionTee, R.layout.view_custom_marker_golf))
                mCurrentTee?.apply {
                    mMarkerTee?.setIcon(BitmapDescriptorFactory.fromBitmap(getTeeBitmapFromView(this.resIcon)))
                }
                mMarkerTee!!.tag = TAG_TEE
            } else {
                mMarkerTee!!.position = mPositionTee
            }

//            for (e in mHistoryPointMap[mHoleCurrent!!.number - 1]) {
//                mPointTrack.add(addMarker(createMarker(e, R.layout.view_marker_tick)))
//            }


            /**
             * Reset view green
             * */
            mListPointViewGreen.clear()
            mViewGreen?.apply {
                remove()
            }


            /**
             * Set view distance to holder position
             * */
                zoomHoleBoundsWithDiagonal(isZoomIn = true, isCallback = true)


        }
    }

    /**
     * zoom camera to view hole to change state sheet bottom
     * */
    private fun zoomInOutSheetBottom(isZoomIn:Boolean = true){

        mGoogleMap?.apply {
            mHoleCurrent?.apply {
                val bonusPixel = if(isZoomIn){
                    if(par != 3){
                        1.0
                    }else {
                        IN_BOUNDS_DIAGONAL_SHEET_BOTTOM
                    }
                }else{
                    OUT_BOUNDS_DIAGONAL_SHEET_BOTTOM
                }

                val targetCamera = if(isZoomIn){
                    MapHelper.computeCentroid(mPositionTee,mPositionFlag)
                }else{
                    mPositionTee
                }

                val dis = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dis)
                val widthPixel = dis.widthPixels
                val heightPixel = dis.heightPixels

                val maxWidth = if(par != 3){
                    (widthPixel * bonusPixel).toInt()
                }else{
                    (widthPixel * bonusPixel).toInt()
                }

                val maxHeight = if(par != 3){
                    (heightPixel * bonusPixel).toInt()
                }else{
                    (heightPixel * bonusPixel).toInt()
                }

                val cp = CameraPosition.Builder()
                        .bearing(MapHelper.getBearingFromLocation(mPositionTee, mPositionFlag))
                        .target(targetCamera)
                        .zoom(getBoundsZoomLevel(createBoundsWithMinDiagonal(), maxWidth,maxHeight).toFloat())
                        .build()
                val cu = CameraUpdateFactory.newCameraPosition(cp)
                animateCamera(cu)
            }
        }


    }
    /**
     * zoom camera to view hole
     * */

    private fun zoomHoleBoundsWithDiagonal(isZoomIn:Boolean = true,isCallback: Boolean = false){
        mGoogleMap?.also { googleMap ->
            mHoleCurrent?.apply {

                val bonusPixel = if(isZoomIn){
                    IN_BOUNDS_DIAGONAL
                }else{
                    OUT_BOUNDS_DIAGONAL
                }

                val dis = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dis)
                val widthPixel = dis.widthPixels
                val heightPixel = dis.heightPixels

                val maxWidth = if(par != 3){
                    widthPixel
                }else{
                    (widthPixel * bonusPixel).toInt()
                }

                val maxHeight = if(par != 3){
                    heightPixel
                }else{
                    (heightPixel * bonusPixel).toInt()
                }

                val targetCameraPosition = CameraPosition.Builder()
                        .bearing(MapHelper.getBearingFromLocation(mPositionTee, mPositionFlag))
                        .target(MapHelper.computeCentroid(mPositionTee,mPositionFlag))
                        .zoom(getBoundsZoomLevel(createBoundsWithMinDiagonal(),maxWidth,maxHeight).toFloat())
                        .build()

                val targetCameraUpdateFactory = CameraUpdateFactory.newCameraPosition(targetCameraPosition)

                if(isCallback) {
                    googleMap.animateCamera(targetCameraUpdateFactory, object : GoogleMap.CancelableCallback {
                        /**
                         * Finish zoom hole
                         * */
                        override fun onFinish() {

                            AnimationUtils.loadAnimation(this@PlayGolfActivityOld, R.anim.show_right_to_left)
                                    .also {
                                        /**
                                         * Show view distance when finish zoom
                                         * */
                                        mWozViewDistance.also { wozViewDistance ->
                                            wozViewDistance.visibility = View.VISIBLE
                                            wozViewDistance.startAnimation(it)
                                        }

                                        /**
                                         * Show button view green when finish zoom
                                         * */
                                        mBtViewGreen.also { vg ->
                                            vg.visibility = View.VISIBLE
                                            vg.startAnimation(it)
                                        }
                                    }
                            updateDrawLineTeeHolderFlag()
                            showViewDistance()

                            mIsMapReady = true
                        }

                        override fun onCancel() {}

                    })
                }else{
                    googleMap.animateCamera(targetCameraUpdateFactory)
                }
            }
        }
    }

    /**
     * Draw line tee to holder and holder to flag
     * */
    private fun updateDrawLineTeeHolderFlag(){
        mGoogleMap?.apply {
            if(mStateViewGreen == StateViewGreen.ZOOM_IN){
                if(this.cameraPosition.zoom != MAX_ZOOM_IN_MAP){
                    mStateViewGreen = StateViewGreen.ZOOM_OUT
                    mIconViewGreen.setImageDrawable(getDrawable(R.drawable.ic_icon_view_green))
                    mIconViewGreen.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .start()
                    hiddenBottomViewDistance()
                    showBottomViewDistance()
                    showTopViewDistance()
                    mPolylineGreen?.remove()
                }
            }

            /**
             * Calculator unit pixel - meter
             * */

            mPerPixelToMeter = UNIT_PIXEL_METER * Math.cos(mPositionHolder.latitude * Math.PI / 180)/
                    Math.pow(2.0,cameraPosition.zoom.toDouble())

            /**
             * get size current hole in zoom google map
             * */
            mTargetRadiusHolder = resources.getDimension(R.dimen.size_holder_map) / 4 * mPerPixelToMeter
            drawLine(this)
            updateViewDistance()
        }

    }

    protected open fun getOverrideData(dataLocation: DataLocation) {
        val dataRoundRealm = DatabaseService.getInstance().getDataRoundGolf(mIdClub)
        if(dataRoundRealm != null){

            mNameCourse = dataRoundRealm.roundMatch!!.course.name
            mNameClub = dataRoundRealm.roundMatch!!.clubName

            mCurrentIdRound = dataRoundRealm.roundMatch!!.id.toString()
            val list = DatabaseService.getInstance()
                    .getDataPlayGolf(mIdClub)

            mDataPlayGolfs = Array(dataRoundRealm.roundMatch!!.holes.size, init = { DataPlayGolf() })
            for(i in list.indices){
                if(i < mDataPlayGolfs.size) {
                    val item = DataPlayGolf()
                    val dataItem = list[i]
                    item.numberHole = dataItem.numberHole
                    item.idClub = dataItem.idClub
                    item.id = dataItem.id
                    item.mValueScore = dataItem.mValueScore
                    item.mValueFairwayHit = dataItem.mValueFairwayHit
                    item.mValueGreenInRegulation = dataItem.mValueGreenInRegulation
                    item.mValuePutt = dataItem.mValuePutt
                    item.resId = dataItem.resId
                    item.resIdWhite = dataItem.resIdWhite
                    item.teeId = dataItem.teeId
                    item.holeId = dataItem.holeId
                    item.flagLatitude = dataItem.flagLatitude
                    item.flagLongitude = dataItem.flagLongitude

//                if(i)
                    mDataPlayGolfs[i] = item
                }
            }

            setRoundMatchPlay(dataRoundRealm.roundMatch!!)
        }else {
            val response = if(mPlayWithBattle){
                ApiService.getInstance().golfService.getRoundDetail(mIdRoundBattle!!)
            }else{
                ApiService.getInstance().golfService.getRoundMath(mIdCourse, dataLocation)
            }

            registerResponse(response,
                    object : HandleResponse<RoundMatch> {
                        override fun onSuccess(result: RoundMatch) {
                            val dataRoundGolf = DataRoundGolfEntity()
                            dataRoundGolf.roundMatch = result
                            mCurrentIdRound = result.id.toString()
                            dataRoundGolf.id = mIdClub
                            mNameCourse = result.course.name
                            mNameClub = result.clubName
                            for (i in 0 until result.holes.size) {
                                val item = DataPlayGolf()
                                item.numberHole = result.holes[i].number
                                item.idClub = mIdClub
                                item.distance = result.holes[i].yard
                                item.par = result.holes[i].par
                                item.index = result.holes[i].index
                                item.holeId = result.holes[i].holeId
                                item.teeId = Tee.getDefault(result.holes[i].tees).id
                                val currentGreen = result.holes[i].greens
                                if(currentGreen != null) {
                                    item.flagLatitude = currentGreen.getDefaultGreen(null).latitude
                                    item.flagLongitude = currentGreen.getDefaultGreen(null).longitude
                                }
                                dataRoundGolf.scores.add(item)

                            }
                            DatabaseService.getInstance().saveDataRoundGolf(dataRoundGolf,OnComplete  {
                                mDataPlayGolfs = Array(result.holes.size, init = { DataPlayGolf() })
                                if(!mPlayWithBattle) {
                                    setRoundMatchPlay(result)
                                }else{
                                    FirebaseDatabase.getInstance().getReference("rounds").child(mIdRoundBattle.toString())
                                            .child("holes").addListenerForSingleValueEvent(object : ValueEventListener{
                                                override fun onCancelled(databaseError: DatabaseError) {}
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    for (item in dataSnapshot.children ){
                                                        val dataBattleScore = item.child(mProfileUserEntity.id)
                                                                .getValue(BattleScorePlayer::class.java)
                                                        dataBattleScore?.apply {

                                                            if(item.key != null) {
                                                                val data = DataPlayGolf()
                                                                data.mValueScore = score
                                                                data.mValueFairwayHit = fairway_hit
                                                                data.mValueGreenInRegulation = green
                                                                data.mValuePutt = putts
                                                                data.idClub = mIdClub

                                                                if(item.key!!.toInt() - 1 < mDataPlayGolfs.size) {
                                                                    mDataPlayGolfs[item.key!!.toInt() - 1] = data
                                                                }
                                                                DatabaseService.getInstance()
                                                                        .updateScoreDataRoundGolf(item.key!!.toInt(),
                                                                                data,OnComplete  { })
                                                            }
                                                        }
                                                    }

                                                    setRoundMatchPlay(result)
                                                }

                                            })
                                }
                            })
                        }
                    })
        }

    }


    private fun setRoundMatchPlay(result: RoundMatch){

        val indexHole = DatabaseService.getInstance().getNumberTrackingHoleGolf(mIdClub)


        mRoundMathCurrent = result
        mHoleCurrent = result.holes[indexHole]

        /**
         * set list hole for adapter
         * */
        mPickerHoleAdapter!!.addListItem(mRoundMathCurrent!!.holes)
        mPickerHoleAdapter!!.setSelected(mHoleCurrent!!.number - 1)

        initDataGolf()
        getProfileUser()
    }

    protected open fun getProfileUser() {

        registerResponse(ApiService.getInstance().userService.profile(),object :HandleResponse<UserEntity>{
            override fun onSuccess(result: UserEntity) {
                mTxtNamePlayer?.text = result.fullName
                Picasso.get().load(result.avatar)
                        .placeholder(R.drawable.ic_icon_user_default)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgPlayer)

            }

        })
    }

    private fun initDataGolf() {
        mBottomSheetBehavior?.apply {
            when (state) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        mWozViewDistance.visibility = View.INVISIBLE
        mBtViewGreen.visibility = View.INVISIBLE

        mHoleCurrent?.apply {

            /**
             * set photo view green
             * */
            if(this.image.isNullOrEmpty()){
                mMenuMoreView.hideYardBook()
            }else{
                mMenuMoreView.showYardBook()
                mViewPhotoGreen.setPhoto(this.image)
            }

            mPolylineGreen?.remove()

            if(mPlayWithBattle) {
                mAdapterPlayer!!.setNumberHole(number)
                mAdapterPlayer!!.setParHole(par)
            }

            if (number - 2 < 0) {
                mBtBackHole!!.isEnabled = false
                mBtBackHole!!.alpha = 0.3f
            }

            val dataGolf = DatabaseService.getInstance().findDataPlayGolfNoHaveScore(mIdClub)

            if (number >= mRoundMathCurrent!!.holes.size && dataGolf == null) {
                mBtNextHole!!.isEnabled = false
                mBtNextHole!!.alpha = 0.3f
            }

            mTxtIndex.text = index.toString()

            /**
             * Set data play golf for woz button
             * */

            mTxtCurrentHole!!.text = resources.getString(R.string.format_view_hole_current, number.toString())

            if(greens == null){
                return
            }

            mPositionFlag = LatLng(greens.getDefaultGreen(null).latitude,
                    greens.getDefaultGreen(null).longitude)


            mPositionTee = if (tees == null) {
                if (mCurrentUser != null) {
                    mCurrentUser!!
                } else {
                    return
                }

            } else {

                val currentTeeType:String? =  if (mCurrentTee != null) {
                    mCurrentTee!!.type
                } else {
                   null
                }

                mCurrentTee = tees[Tee.getPositionByType(tees,currentTeeType)]

                LatLng(mCurrentTee!!.latitude,
                        mCurrentTee!!.longitude)
            }
            mPositionHolder = if (latitude == 0.0 || longitude == 0.0) {
                    MapHelper.computeCentroid(mPositionTee, mPositionFlag)
                } else {
                    if (mHoleCurrent != null) {
                        if (mHoleCurrent!!.par == 3) {
                            mHitFlag = true
                            mPositionFlag

                        } else {
                            LatLng(latitude, longitude)
                        }
                    } else {
                        LatLng(latitude, longitude)
                    }
                }

            mTxtPar!!.text = mHoleCurrent!!.par.toString()


            /**
             * set tee in current hole
             * */
            mCurrentTee?.apply {
                if(mIndexTeeDefault == -1){
                    mIndexTeeDefault = Tee.getPositionByType(tees,type)
                }
                mViewChangeTee.setData(tees,mIndexTeeDefault)



                mTxtCurrentDistance.text = if (isFormatYard) {
                    resources.getString(R.string.format_yard_full, yard.toString())
                } else {
                    val tempDis = MapHelper.convertYardToMeter(yard)
                    resources.getString(R.string.format_meter, tempDis.toString())
                }
            }


            /**
             * Set data if plan
             * */

            dataPlanPlayGolf()

            mWozGolfButton!!.initData(mHoleCurrent!!.number, mHoleCurrent!!.par, mHoleCurrent!!.index)


            if(!mPlayWithBattle) {
                mWozGolfButton!!.setDataPlayGolf(mDataPlayGolfs[number - 1])
                updateScoreOnSheet(mDataPlayGolfs[number - 1])
            }else{
                mFBListenerMyHole = FirebaseDatabase.getInstance().getReference("rounds").child(mIdRoundBattle.toString())
                        .child("holes").child(mHoleCurrent!!.number.toString()).child(mProfileUserEntity.id)
                mValueListenerMyHole = object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val dataBattle = dataSnapshot.getValue(BattleScorePlayer::class.java)
                                val dataScore = DataPlayGolf()

                                dataBattle?.let {
                                    dataScore.mValueScore = it.score
                                    dataScore.mValueFairwayHit = it.fairway_hit
                                    dataScore.mValueGreenInRegulation = it.green
                                    dataScore.mValuePutt = it.putts
                                }

                                mDataPlayGolfs[number - 1] = dataScore
                                updateScoreOnSheet(mDataPlayGolfs[number - 1])
                                mWozGolfButton!!.setDataPlayGolf(mDataPlayGolfs[number - 1])
                            }

                        }
                mFBListenerMyHole?.addValueEventListener(mValueListenerMyHole!!)
            }
        }

        isOnGolfCourse = MapHelper.getDistanceLatLng(mCurrentUser, mPositionTee, true) <= MAX_DISTANCE_TO_SHOW_TRACK
        /**
         * Start set value distance
         * */
        updateWozViewDistance()
        /**
         * End set value distance
         * */
        if (mGoogleMap == null) {
            val customMapFragment = supportFragmentManager.findFragmentById(R.id.containerMap) as EasyGolfMapFragment?
            customMapFragment?.getMapAsync(this)
//            customMapFragment?.setOnDragListener(this)
        } else {
            repairMap()
        }
    }

    private fun updateWozViewDistance(isReset: Boolean = true) {
        mWozViewDistance.apply {
            mHoleCurrent?.apply {

//                setValueBack(MapHelper.getDistanceLatLng(mPositionTee,
//                        LatLng(greens.blue.latitude, greens.blue.longitude), isFormatYard))
//
//                val tempDis = MapHelper.getDistanceLatLng(mPositionTee,
//                        LatLng(greens.white.latitude, greens.white.longitude), isFormatYard)
//                setValueCenter(tempDis)
//
//                setValueFront(MapHelper.getDistanceLatLng(mPositionTee,
//                        LatLng(greens.red.latitude, greens.red.longitude), isFormatYard))
//
//
//                if (isReset) {
//                    resetMe()
//                }
            }

        }
    }

    private fun showViewDistance() {
        val  oldAnimationStart = mDistanceStartView.animation
        oldAnimationStart?.apply {
            if(!hasEnded()){
                mDistanceStartView.animation.cancel()
            }
        }

        val eAniStart = AnimationUtils.loadAnimation(this, R.anim.animation_show_view_distance)
        val interpolator = EasyGolfBounceInterpolator(0.2, 10.0)
        eAniStart.interpolator = interpolator
        mDistanceStartView.startAnimation(eAniStart)

        eAniStart.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                mDistanceStartView.visibility = View.VISIBLE
                startCountAnimation(mDistanceStartView)
            }
        })


        val  oldAnimationEnd = mDistanceEndView.animation
        oldAnimationEnd?.apply {
            if(!hasEnded()){
                mDistanceEndView.animation.cancel()
            }
        }

        val eAniEnd = AnimationUtils.loadAnimation(this, R.anim.animation_show_view_distance)

        eAniEnd.interpolator = interpolator
        mDistanceEndView.startAnimation(eAniEnd)

        eAniEnd.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                mDistanceEndView.visibility = View.VISIBLE
                startCountAnimation(mDistanceEndView)
            }
        })

    }

    /**
     * Animation make count
     *
     */

    private fun startCountAnimation(distanceView: DistanceGolfView) {
        val animator = ValueAnimator.ofInt(0, distanceView.getValue())
        animator.duration = 500
        animator.addUpdateListener {
            distanceView.setValue(it.animatedValue as Int,false)
        }
        animator.start()
    }

    private fun hiddenViewDistance() {
        if (mDistanceStartView.animation != null) {
            mDistanceStartView.animation.cancel()
        }
        mDistanceStartView.visibility = View.GONE

        if (mDistanceEndView.animation != null) {
            mDistanceEndView.animation.cancel()
        }
        mDistanceEndView.visibility = View.GONE

    }

    private fun hiddenTopViewDistance() {
        if (mDistanceEndView.animation != null) {
            mDistanceEndView.animation.cancel()
        }
        mDistanceEndView.visibility = View.GONE

    }

    private fun showTopViewDistance() {
        val interpolator = EasyGolfBounceInterpolator(0.2, 10.0)
        val  oldAnimationEnd = mDistanceEndView.animation
        oldAnimationEnd?.apply {
            if(!hasEnded()){
                mDistanceEndView.animation.cancel()
            }
        }

        val eAniEnd = AnimationUtils.loadAnimation(this, R.anim.animation_show_view_distance)

        eAniEnd.interpolator = interpolator
        mDistanceEndView.startAnimation(eAniEnd)

        eAniEnd.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {

            }
            override fun onAnimationStart(animation: Animation?) {
                mDistanceEndView.visibility = View.VISIBLE
                startCountAnimation(mDistanceEndView)
            }
        })
    }

    private fun hiddenBottomViewDistance() {
        if (mDistanceStartView.animation != null) {
            mDistanceStartView.animation.cancel()
        }
        mDistanceStartView.visibility = View.GONE
    }

    private fun showBottomViewDistance() {
        val interpolator = EasyGolfBounceInterpolator(0.2, 10.0)
        val  oldAnimationEnd = mDistanceStartView.animation
        oldAnimationEnd?.apply {
            if(!hasEnded()){
                mDistanceStartView.animation.cancel()
            }
        }

        val eAniStart = AnimationUtils.loadAnimation(this, R.anim.animation_show_view_distance)

        eAniStart.interpolator = interpolator
        mDistanceStartView.startAnimation(eAniStart)

        eAniStart.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                mDistanceStartView.visibility = View.VISIBLE
                startCountAnimation(mDistanceStartView)
            }
        })

    }

    private fun moveViewDistanceStartToFlag(){
        if(mStateViewGreen == StateViewGreen.ZOOM_IN) {
            mGoogleMap?.apply {
                mStateViewGreen = StateViewGreen.ZOOM_IN
                hiddenBottomViewDistance()
                /**
                 * Set view distance start to near flag - from @param 3 time size view distance
                 * (this is only to do when zoom green)
                 * */
                val posStart = mPositionFlag
                val pointStartTemp = projection.toScreenLocation(MapHelper
                        .getDestinationPoint(MapHelper.computeCentroid(posStart, mPositionHolder),
                                MapHelper.getBearingFromLocation(posStart, mPositionHolder) - 200.0,
                                mSizeViewDistance * mPerPixelToMeter * 3 / 5000))

//                mViewDistanceStart.x = pointStartTemp.x.toFloat() - (mViewDistanceStart.width / 2.0f)
//                mViewDistanceStart.y = pointStartTemp.y.toFloat() - (mViewDistanceStart.height / 2.0f)

                mDistanceStartView.x = pointStartTemp.x.toFloat()
                mDistanceStartView.y = pointStartTemp.y.toFloat()

                showBottomViewDistance()
            }
        }
    }

    protected open fun dataPlanPlayGolf() {}

    private fun getResWhite(score: Int):Int{
        return when(val index = mHoleCurrent!!.par - score){
            0->{
                R.drawable.circle_par
            }
            1->{
                R.drawable.circle_single_point
            }
            2->{
                R.drawable.circle_double_point
            }
            -1->{
                R.drawable.rectangle_single_point
            }
            -6->{
                R.drawable.rectangle_single_red_point
            }
            else->{
                if(index > 0){
                    R.drawable.circle_double_point
                }else{
                    R.drawable.rectangle_double_point
                }
            }

        }
    }

    private fun updateScoreOnSheet(data: DataPlayGolf) {
        if (data.mValueScore > 0) {
            mTxtScoreSheet!!.text = data.mValueScore.toString()
            mImageScoreSheet.setImageDrawable(null)
            mLayoutScoreSheet.setBackgroundResource(getResWhite(data.mValueScore))
        } else {
            mTxtScoreSheet!!.text = null
            mImageScoreSheet.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_icon_score_blue))
            mLayoutScoreSheet.setBackgroundResource(0)
        }

        if (data.mValueFairwayHit >= 0) {
            mLayoutFairway.setBackgroundResource(R.drawable.circle_single_point)
            when (data.mValueFairwayHit) {
                0 -> {
                    mIconFairway!!.setImageResource(R.drawable.ic_icon_miss_left_fairway)
                }
                1 -> {
                    mIconFairway!!.setImageResource(R.drawable.ic_icon_correct)
                }
                2 -> {
                    mIconFairway!!.setImageResource(R.drawable.ic_icon_miss_right_fairway)
                }
            }
            mTxtFairwaySheet!!.text = null

        } else {
            mLayoutFairway.setBackgroundResource(0)
            mIconFairway!!.setImageResource(0)
            mTxtFairwaySheet!!.text = resources.getString(R.string.dash)
        }


        if (data.mValueGreenInRegulation >= 0) {
            mLayoutGreen!!.setBackgroundResource(R.drawable.circle_single_point)
            when (data.mValueGreenInRegulation) {
                0 -> {
                    mIconGreen!!.setImageResource(R.drawable.ic_icon_correct)
                }
                1 -> {
                    mIconGreen!!.setImageResource(R.drawable.ic_icon_no_correct)
                }
            }
            mTxtGreenInRegulationSheet!!.text = null
        } else {
            mLayoutGreen!!.setBackgroundResource(0)
            mIconGreen!!.setImageResource(0)
            mTxtGreenInRegulationSheet!!.text = resources.getString(R.string.dash)
        }

        if (data.mValuePutt >= 0) {
            mTxtPuttSheet!!.setBackgroundResource(R.drawable.circle_single_point)
            if (data.mValuePutt < 4) {
                mTxtPuttSheet!!.text = data.mValuePutt.toString()
            } else {
                val temp = data.mValuePutt.toString() + "+"
                mTxtPuttSheet!!.text = temp
            }
        } else {
            mTxtPuttSheet!!.setBackgroundResource(0)
            mTxtPuttSheet!!.text = resources.getString(R.string.dash)
        }
    }

    override fun onDrag(motionEvent: MotionEvent?) {

        mGoogleMap?.also {
            motionEvent?.apply {
                val currentX = x.toInt()
                val currentY = y.toInt()

                when (this.action) {
                    MotionEvent.ACTION_DOWN -> {

                        if(mMenuMoreView.isOpen()){
                            mMenuMoreView.closeView()
                        }
                        val d = Point(currentX, currentY)
                        var current = it.projection.toScreenLocation(mMarkerHolder!!.position)
                        /**
                         * When touch holder area
                         * */
                        if (Math.abs(d.x - current.x) < resources.getDimension(R.dimen.size_holder_map) &&
                                Math.abs(d.y - current.y) < resources.getDimension(R.dimen.size_holder_map)) {
                            it.uiSettings.setAllGesturesEnabled(false)
                            holderMarkerOnActive(it.projection.fromScreenLocation(d))

                        } else {
                            if (!isOnGolfCourse) {
//                            if(false){
                                current = it.projection.toScreenLocation(mMarkerTee!!.position)
                                mIsTeeTouch = Math.abs(d.x - current.x) < resources.getDimension(R.dimen.size_holder_map)
                                        && Math.abs(d.y - current.y) < resources.getDimension(R.dimen.size_holder_map)
                                if (mIsTeeTouch) {
                                    it.uiSettings.setAllGesturesEnabled(false)
                                } else {
                                    it.uiSettings.setAllGesturesEnabled(true)
                                }
                            } else {
                                it.uiSettings.setAllGesturesEnabled(true)
                            }
                            mIsHolder = false
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (mIsHolder) {
                            val e = Point()
                            e.set(currentX, currentY)
                            val temp = it.projection.fromScreenLocation(e)

                            mPositionHolder = MapHelper.getDestinationPoint(temp,
                                    it.cameraPosition.bearing.toDouble(),(mTargetRadiusHolderActiveLine/1000))

                            mMarkerHolder!!.position = mPositionHolder
                            moveHolderToFlag(mPositionHolder)

                            updateViewDistance()

                            drawLine(mGoogleMap,true)
                        } else {
                            if (mIsTeeTouch) {
                                val e = Point()
                                e.set(currentX, currentY)
                                val temp = it.projection.fromScreenLocation(e)
                                mPositionTee = temp

                                mMarkerTee!!.position = temp

                                updateViewDistance()
                                updateWozViewDistance(false)
                                drawLine(mGoogleMap)
                            }
                        }
                    }

                    MotionEvent.ACTION_UP->{
                        holderMarkerOnUnActive()
                        mIsHolder = false
                        mIsTeeTouch = false
                    }
                }
            }
        }
    }

    private fun holderMarkerOnActive(source:LatLng){

        mGoogleMap?.also {
            mMarkerHolder?.apply {

                mTargetRadiusHolderActiveLine = resources.getDimension(R.dimen.size_holder_map_active_dis) / 4 * mPerPixelToMeter
                mTargetRadiusHolderActive = resources.getDimension(R.dimen.size_holder_map_active) / 4 * mPerPixelToMeter

                val temp = MapHelper.getDestinationPoint(source,
                        it.cameraPosition.bearing.toDouble(),(mTargetRadiusHolderActiveLine/1000))

                val animation = MarkerAnimation.getInstance().animateMarker(temp, it.cameraPosition.bearing,this,object : EventAnimationMarker {
                    override fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation: Float) {
                        if(!mIsRunning){
                            animation.cancel()
                        }
                        mPositionHolder = pos
                        mMarkerHolder?.position = pos
                        moveHolderToFlag(mPositionHolder)
                        updateViewDistance()
                        drawLine(it,true)
                    }
                },100)


                animation?.apply {
                    addListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            mIsHolder = true
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }
                    })
                }
                setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.layout.view_holder_marker_golf_active)))
            }
        }

    }

    private fun holderMarkerOnUnActive(){
        mMarkerHolder?.apply {
            setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.layout.view_holder_marker_golf)))
        }
        drawLine(mGoogleMap)
    }

    private fun moveHolderToFlag(currentPos: LatLng) {
        if(mStateViewGreen == StateViewGreen.ZOOM_IN){
            mPositionFlag = currentPos
            mMarkerFlag?.position = mPositionFlag
        }else {

            if (MapHelper.getDistanceLatLng(currentPos, mPositionFlag, true) <= 5) {
                mPositionHolder = mPositionFlag
                mMarkerHolder?.position = mPositionHolder
                mHitFlag = true
            } else {
                mHitFlag = false
            }
        }
    }



    private fun drawLine(googleMap: GoogleMap?,isZoomIn: Boolean = false) {
        googleMap?.apply {

            val sizeHolder = if(!isZoomIn){
                mTargetRadiusHolder
            }else{
                mTargetRadiusHolderActive
            }

            val distanceToTee: Double = MapHelper.getDistanceLatLng(mPositionTee, mPositionHolder, false)

            val distanceToFlag = MapHelper.getDistanceLatLng(mPositionFlag, mPositionHolder, false)

            if (polylineStart == null) {
                polylineStart = addPolyline(PolylineOptions()
                        .add(mPositionTee, MapHelper.getDestinationPoint(mPositionHolder,
                                MapHelper.getBearingFromLocation(mPositionHolder, mPositionTee).toDouble(),
                                sizeHolder / 1000))
                        .endCap(SquareCap())
                        .startCap(SquareCap())
                        .width(5f)
                        .geodesic(true)
                        .color(Color.WHITE))
            } else {
                val pointsStartTarget = ArrayList<LatLng>()
                pointsStartTarget.add(mPositionTee)

                if (distanceToTee > mTargetRadiusHolder) {

                    pointsStartTarget.add(MapHelper.getDestinationPoint(mPositionHolder,
                            MapHelper.getBearingFromLocation(mPositionHolder, mPositionTee).toDouble(), sizeHolder / 1000))

                }
                polylineStart!!.points = pointsStartTarget
            }

            if (polylineEnd == null) {
                polylineEnd = addPolyline(PolylineOptions()
                        .add(mPositionFlag, MapHelper.getDestinationPoint(mPositionHolder,
                                MapHelper.getBearingFromLocation(mPositionHolder, mPositionFlag).toDouble(),
                                sizeHolder / 1000))
                        .jointType(JointType.ROUND)
                        .width(5f)
                        .endCap(SquareCap())
                        .geodesic(false)
                        .startCap(SquareCap())
                        .color(Color.WHITE))
            } else {
                val pointsEnd = ArrayList<LatLng>()

                if (distanceToFlag > sizeHolder) {
                    pointsEnd.add(mPositionFlag)
                    pointsEnd.add(MapHelper.getDestinationPoint(mPositionHolder,
                            MapHelper.getBearingFromLocation(mPositionHolder, mPositionFlag).toDouble(), sizeHolder / 1000))
                }

                polylineEnd!!.points = pointsEnd
            }
        }
    }
    /**
     * View Current Green
     * */
    private fun viewGreen() {
        /**
         * Create view green
         * */

        if(mStateViewGreen == StateViewGreen.ZOOM_OUT) {
            mStateViewGreen = StateViewGreen.ZOOMING
            hiddenTopViewDistance()
            mIconViewGreen.setImageDrawable(getDrawable(R.drawable.ic_zoom_in_green))
            mIconViewGreen.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(300)
                    .start()

            mMarkerHolder?.position = mPositionFlag
            mPositionHolder = mPositionFlag
            mGoogleMap?.apply {
                animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mPositionFlag.latitude, mPositionFlag.longitude),
                        MAX_ZOOM_IN_MAP)
                        ,object : GoogleMap.CancelableCallback{
                    override fun onCancel() {

                    }

                    override fun onFinish() {
                        mStateViewGreen = StateViewGreen.ZOOM_IN
                        moveViewDistanceStartToFlag()
                        drawPolygonGreen()
                    }

                })
            }
        }else{
            if(mStateViewGreen == StateViewGreen.ZOOM_IN) {
                mViewGreen?.remove()
                zoomInOutSheetBottom(true)
                mViewGreen = null
                mStateViewGreen = StateViewGreen.ZOOM_OUT
                mIconViewGreen.setImageDrawable(getDrawable(R.drawable.ic_icon_view_green))
                mIconViewGreen.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .start()
                hiddenBottomViewDistance()
                showBottomViewDistance()
                showTopViewDistance()

                mPolylineGreen?.remove()
            }
        }
    }

    private fun loadJSONFromAsset():String? {

        return try {
            val isStream = assets.open("data_polyline.json")
            val size = isStream.available()
            val buffer = ByteArray(size)
            isStream.read(buffer)
            isStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex:IOException) {
            ex.printStackTrace();
            null
        }

    }

    private fun drawPolygonGreen(){
        mHoleCurrent?.greens?.coordinates?.let {coorinates->
            mGoogleMap?.apply {
                val listPoints = ArrayList<LatLng>()
                for (item in coorinates){
                    listPoints.add(LatLng(item.latitude,item.longitude))
                }

                mPolylineGreen?.remove()

                val polylineOptions = PolylineOptions()
                mPolylineGreen = this.addPolyline(polylineOptions)
                mPolylineGreen?.color = Color.WHITE
                mPolylineGreen?.startCap = SquareCap()
                mPolylineGreen?.endCap = SquareCap()
                mPolylineGreen?.jointType = JointType.DEFAULT
                mPolylineGreen?.width = 7f

                val tAnimator = ValueAnimator.ofFloat(0f, 1000f)
                tAnimator.interpolator =  LinearInterpolator()
                tAnimator.duration = 700

                tAnimator.addUpdateListener {
                    val percentValue = (it.animatedValue as Float)/1000
                    mPolylineGreen?.points = listPoints.subList(0,(percentValue * listPoints.size).toInt())
                }
                tAnimator.start()
            }
        }
//        mViewModel.currentHoleLive.value?.green?.coordinates?.let { coorinates->
//            mGoogleMap?.apply {
//                val listPoints = ArrayList<LatLng>()
//                for (itemPoint in coorinates){
//                    listPoints.add(LatLng(itemPoint.latitude,itemPoint.longitude))
//                }
//                val a = this.addPolyline(PolylineOptions().addAll(listPoints))
//                a.color = Color.RED
//            }
//        }

    }

    /**
     * start create a new maker
     * */
    private fun createMarker(point: LatLng, layout: Int = 0): MarkerOptions {
        val marker = MarkerOptions()
        marker.position(point)
        if (layout == 0) {
            marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.layout.view_flag_golf)))
            marker.anchor(0.35f, 0.85f)
        } else {
            marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(layout)))
            marker.anchor(0.5f, 0.5f)
        }

        return marker
    }
    /**
     * start create a bitmap to custom maker
     * */
    private fun createMarkerFlag(point: LatLng, icon: Int = 0): MarkerOptions {
        val marker = MarkerOptions()
        marker.position(point)
        marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerFlagBitmapFromView(icon)))
        marker.anchor(0.35f, 0.85f)
        return marker
    }

    @SuppressLint("InflateParams")
    private fun getMarkerFlagBitmapFromView(icon: Int): Bitmap {
        val customMarkerView = LayoutInflater.from(this).inflate(R.layout.view_flag_golf, null)
        customMarkerView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight,
                Bitmap.Config.ARGB_8888)
        if (icon != 0) {
            val img = customMarkerView.findViewById<ImageView>(R.id.profile_image)
            img.setImageResource(icon)
        }

        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)

        return returnedBitmap
    }

    private fun getMarkerBitmapFromView(layout: Int): Bitmap {
        val customMarkerView = LayoutInflater.from(this).inflate(layout, null)
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

    @SuppressLint("InflateParams")
    private fun getTeeBitmapFromView(icon: Int): Bitmap {
        val customMarkerView = LayoutInflater.from(this).inflate( R.layout.view_custom_marker_golf, null)
        customMarkerView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight,
                Bitmap.Config.ARGB_8888)
        if (icon != 0) {
            val img = customMarkerView.findViewById<ImageView>(R.id.iconTee)
            img.setImageResource(icon)
        }

        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when (id) {
                R.id.btBackHole -> {
                    mHitFlag = false
                    mStateViewGreen = StateViewGreen.ZOOM_OUT
                    hiddenViewDistance()
                    mHoleCurrent = mRoundMathCurrent!!.holes[mHoleCurrent!!.number - 2]
                    mPickerHoleAdapter!!.setSelected(mHoleCurrent!!.number)
                    initDataGolf()
                    updateArrow()
                }
                R.id.btNextHole -> {
                    mHitFlag = false
                    mStateViewGreen = StateViewGreen.ZOOM_OUT
                    hiddenViewDistance()

                    val nextNumberHole = mHoleCurrent!!.number

                    if(nextNumberHole < mRoundMathCurrent!!.holes.size){
                        mHoleCurrent = mRoundMathCurrent!!.holes[mHoleCurrent!!.number]
                        mPickerHoleAdapter!!.setSelected(mHoleCurrent!!.number)
                    }else{

                        val dataGolf = DatabaseService.getInstance().findDataPlayGolfNoHaveScore(mIdClub)
                        dataGolf?.let {
                            mHoleCurrent = mRoundMathCurrent!!.holes[it.numberHole - 1]
                            mPickerHoleAdapter!!.setSelected(it.numberHole)
                        }
                    }

                    initDataGolf()
                    updateArrow()
                }
                R.id.btViewScoreCard -> {
//                    val bundle = Bundle()
//                    bundle.putBoolean(Contains.EXTRA_PLAY_WITH_BATTLE, mPlayWithBattle)
//                    bundle.putString(Contains.EXTRA_ID_ROUND, mCurrentIdRound)
//                    bundle.putString(Contains.EXTRA_KEY_NAME_COURSE, mNameCourse)
//                    bundle.putString(Contains.EXTRA_NAME_CLUB, mNameClub)
//                    bundle.putString(Contains.EXTRA_ID_CLUB, mIdClub)
//                    startActivity(WozScoreCardActivity::class.java, bundle)
//                    mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                R.id.btViewGreen -> {
                    viewGreen()
                }

                R.id.btEndGame -> {
                    mBottomSheetBehavior?.apply {
                        if (this.state == BottomSheetBehavior.STATE_EXPANDED){
                            this.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }

                    val bundle = Bundle()
                    bundle.putBoolean(Contains.EXTRA_PLAY_WITH_BATTLE,mPlayWithBattle)
                    bundle.putString(Contains.EXTRA_ID_CLUB,mIdClub)
                    bundle.putString(Contains.EXTRA_NAME_COURSE, mNameCourse)
                    startActivity(EndGameActivity::class.java,bundle)
                }

                R.id.actionMenuMore -> {
                    mIsOpenMenu = true
                    mMenuMoreView.openView()
                }
            }
        }
    }

    private fun updateArrow() {

        if (mHoleCurrent!!.number - 2 < 0) {
            mBtBackHole!!.isEnabled = false
            mBtBackHole!!.alpha = 0.3f
        } else {
            mBtBackHole!!.isEnabled = true
            mBtBackHole!!.alpha = 1f
        }

        val dataGolf = DatabaseService.getInstance().findDataPlayGolfNoHaveScore(mIdClub)

        if (mHoleCurrent!!.number < mRoundMathCurrent!!.holes.size || dataGolf != null) {
            mBtNextHole!!.isEnabled = true
            mBtNextHole!!.alpha = 1f
        } else {
            mBtNextHole!!.isEnabled = false
            mBtNextHole!!.alpha = 0.3f
        }

        DatabaseService.getInstance().updateIndexHoleOfRoundGolf(mIdClub,mHoleCurrent!!.number)
    }

    private fun updateArrowNext() {
        val dataGolf = DatabaseService.getInstance().findDataPlayGolfNoHaveScore(mIdClub)

        if (mHoleCurrent!!.number < mRoundMathCurrent!!.holes.size || dataGolf != null) {

            mBtBackHole!!.isEnabled = true
            mBtBackHole!!.alpha = 1f
        } else {
            mBtNextHole!!.isEnabled = false
            mBtNextHole!!.alpha = 0.3f
        }
    }


    /**
     * For view Distance
     */
    private fun updateViewDistance() {

        mGoogleMap?.apply {

            val posStart = mPositionTee
            val pointStartTemp = projection.toScreenLocation(MapHelper
                    .getDestinationPoint(MapHelper.computeCentroid(posStart, mPositionHolder),
                            MapHelper.getBearingFromLocation(posStart, mPositionHolder) - 90.0,
                            mSizeViewDistance * mPerPixelToMeter / 4000))

            mDistanceStartView.setValue(Math.round(MapHelper.getDistanceLatLng(posStart, mPositionHolder, isFormatYard)).toInt())
            if(mStateViewGreen != StateViewGreen.ZOOM_IN) {
                mDistanceStartView.x = pointStartTemp.x.toFloat() - (mDistanceStartView.width / 2.0f)
                mDistanceStartView.y = pointStartTemp.y.toFloat() - (mDistanceStartView.height / 2.0f)
            }else{
                val posStartZoomIn = mPositionFlag
                val pointStartTempZoomIn = projection.toScreenLocation(MapHelper
                        .getDestinationPoint(MapHelper.computeCentroid(posStartZoomIn, mPositionHolder),
                                MapHelper.getBearingFromLocation(posStartZoomIn, mPositionHolder) - 200.0,
                                mSizeViewDistance * mPerPixelToMeter * 3 / 4000))

                mDistanceStartView.x = pointStartTempZoomIn.x.toFloat() - (mDistanceStartView.width / 2.0f)
                mDistanceStartView.y = pointStartTempZoomIn.y.toFloat() - (mDistanceStartView.height / 2.0f)
            }
            val pointEndTemp = projection.toScreenLocation(MapHelper
                    .getDestinationPoint(MapHelper.computeCentroid(mPositionFlag, mPositionHolder),
                            MapHelper.getBearingFromLocation(mPositionFlag, mPositionHolder) + 270.0, mSizeViewDistance * mPerPixelToMeter / 4000))

            mDistanceEndView.setValue(Math.round(MapHelper.getDistanceLatLng(mPositionFlag, mPositionHolder, isFormatYard)).toInt())
            mDistanceEndView.x = pointEndTemp.x.toFloat() - (mDistanceEndView.width / 2.0f)
            mDistanceEndView.y = pointEndTemp.y.toFloat() - (mDistanceEndView.height / 2.0f)
        }
    }
    /**
     * End main zone
     * */
    /**
     * Start support zone
     * */
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
        val latZoom = zoom(mapHeightPx, WORLD_PX, latFraction)
        val lngZoom = zoom(mapWidthPx, WORLD_PX, lngFraction)

        val result = Math.min (latZoom , lngZoom)
        return result.coerceAtMost(MAX_ZOOM.toDouble())
    }

    private fun latRad(lat: Double): Double {
        val sin = sin(lat * Math.PI / 180)
        val radX2 = ln((1 + sin) / (1 - sin)) / 2
        return radX2.coerceAtMost(Math.PI).coerceAtLeast(-Math.PI) / 2
    }

    private fun zoom(mapPx: Int, worldPx: Int, fraction: Double): Double {
        return floor(ln(mapPx / worldPx / fraction) / LN2)
    }

    private fun createBoundsWithMinDiagonal(): LatLngBounds {
        val builder = LatLngBounds.Builder()
        builder.include(mPositionTee)
        builder.include(mPositionFlag)
        val dis = MapHelper.getDistanceLatLng(mPositionTee,mPositionFlag,false) * 1.1
        val center = MapHelper.computeCentroid(mPositionTee,mPositionFlag)
        val northEast = move(center, 501.0, 501.0)
        val southWest = move(center,-501.0, -501.0)
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


    /**
     * End support zone
     * */
}