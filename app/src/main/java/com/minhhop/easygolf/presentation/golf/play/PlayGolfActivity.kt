package com.minhhop.easygolf.presentation.golf.play

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.EasyGolfLocation
import com.minhhop.core.domain.golf.Hole
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.AddPlayerBundle
import com.minhhop.easygolf.framework.bundle.EndGameBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle.TypeGame.*
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.HostEndGameDialog
import com.minhhop.easygolf.framework.golf.LocationHelper
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.golf.GoogleMapsHelper
import com.minhhop.easygolf.presentation.golf.component.menu.MenuMoreView
import com.minhhop.easygolf.presentation.golf.component.menu.MenuMoreView.CODE.*
import com.minhhop.easygolf.presentation.golf.component.score.EnterScoreBottomSheetFragment
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView
import com.minhhop.easygolf.presentation.golf.component.tee.ChangeTeeBottomSheetFragment
import com.minhhop.easygolf.presentation.player.EasyGolfPlayerAdapter
import com.minhhop.easygolf.widgets.WozViewDistance
import com.minhhop.easygolf.widgets.WozViewDistance.POSITION.*
import kotlinx.android.synthetic.main.activity_play_golf.*
import kotlinx.android.synthetic.main.bottom_sheet_golf.*
import kotlinx.android.synthetic.main.layout_loading_map.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt


class PlayGolfActivity : EasyGolfActivity<PlayGolfViewModel>() {
    companion object {
        private const val MAX_DISTANCE_TO_SHOW_TRACK = 500
        private const val REQUEST_CODE = 123
        private const val STATE_HOLE = "STATE_HOLE"
        const val MAX_FRIEND_IN_BATTLE = 4
    }

    override val mViewModel: PlayGolfViewModel by viewModel()
    private var mEasyGolfPlayerAdapter: EasyGolfPlayerAdapter? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mLocationClientUpdate: LocationHelper? = null

    /**
     * MARK:  Views in top
     * */
    private var mIsOnGameWithUpdateUserLocation: Boolean = false
    private var mLocationPlayer: EasyGolfLocation = EasyGolfLocation.instance()
    private var mEasyGolfMapFragment: EasyGolfMapFragment? = null

    private val mAddPlayerCallback = object : EasyGolfPlayerAdapter.EasyGolfPlayerAdapterListener {
        override fun onAddPlayer() {
            if (mEasyGolfPlayerAdapter?.getRealCount() ?: 0 < MAX_FRIEND_IN_BATTLE) {
                val limit = mEasyGolfPlayerAdapter?.getRealCount()?.let { count -> MAX_FRIEND_IN_BATTLE - count }
                        ?: -1
                EasyGolfNavigation.addPlayerDirection(this@PlayGolfActivity,
                        REQUEST_CODE,
                        limit,
                        AddPlayerBundle.toBlackList(mEasyGolfPlayerAdapter?.getListUserExit())
                )
            } else {
                toast(getString(R.string.limit_add_player, MAX_FRIEND_IN_BATTLE + 1))
            }
        }

        override fun onDeletePlayerEnable(user: User?) {
            if (user != null && mViewModel.typeGame() == BATTLE_GAME) {
                mViewModel.roundGolfLiveData.value?.id?.let { roundGolfId ->
                    mViewModel.removeMembersFromBattle(roundGolfId, user)
                }
            }

            btCancelDeletePlayer.visibility = if (user == null) View.VISIBLE else View.INVISIBLE
        }

        override fun onPlayerClick(user: User?) {
            mViewModel.roundGolfLiveData.value?.id?.let { roundGolfId ->
                mViewModel.mCurrentHoleLiveData.value?.let { hole ->
                    EnterScoreBottomSheetFragment()
                            .setInfoHole(roundGolfId, hole, user)
                            .show(supportFragmentManager)
                }
            }
        }
    }

    private val mHandleEventMenuMore = object : MenuMoreView.HandleMenuMore {
        override fun onClick(code: MenuMoreView.CODE) {
            hideMask()
            when (code) {
                VIEW_GREEN -> {
                    viewPhotoGreen.showPhoto()
                }

                REPORT_THIS_HOLE -> {
                    EasyGolfNavigation.feedbackGolfDirection(this@PlayGolfActivity)
                }
                SHARE -> {
                    mEasyGolfMapFragment?.getSnapShot {
                        mViewModel.takeScreenShoot(it, this@PlayGolfActivity) { snapFile ->
                            EasyGolfNavigation.shareFile(this@PlayGolfActivity, snapFile)
                        }
                    }
                }
                END_GAME -> {
                    directionEndgame()
                }
                CHANGE_UNIT -> {
                    menuMore.getUnit().let { unit ->
                        mViewModel.setUnit(unit)
                        distanceStartView.changeUnit(unit)
                        distanceEndView.changeUnit(unit)
                    }
                }
                CHANGE_DEFAULT_TEE -> {
                    ChangeTeeBottomSheetFragment().show(supportFragmentManager)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    val addPlayerBundle = EasyGolfNavigation.addPlayerBundle(data)
                    addPlayerBundle?.getResult()?.let { users ->
                        mViewModel.roundGolfLiveData.value?.let { roundGolf ->
                            when (mViewModel.mPlayGolfBundle?.typeGame) {
                                NEW_GAME -> {
                                    roundGolf.id?.let { roundGolfId ->
                                        roundGolf.course_id?.let { courseId ->
                                            mViewModel.createBattleGame(roundGolfId, courseId, users)
                                        }
                                    }

                                }
                                BATTLE_GAME -> {

                                    roundGolf.id?.let { roundGolfId ->
                                        roundGolf.course_id?.let { courseId ->
                                            mViewModel.addMembersToBattle(roundGolfId, courseId, users)
                                        }
                                    }
                                }
                                EXPLORE, UNKNOWN,null -> {
                                } // do nothing
                            }
                        }

                    }
                }
            }
        }
    }

    override fun needToOverrideFontSize(): Boolean = true

    override fun setLayout(): Int = R.layout.activity_play_golf

    override fun onSaveInstanceState(outState: Bundle) {
        mViewModel.mCurrentHoleLiveData.value?.let { hole ->
            outState.run {
                putInt(STATE_HOLE, hole.number)
            }
        }
        super.onSaveInstanceState(outState)
    }

    override fun initView() {

        /**
         * Start render map in to containerMap
         * */
        viewMask()
        mEasyGolfMapFragment = (supportFragmentManager.findFragmentByTag(EasyGolfMapFragment.TAG) as? EasyGolfMapFragment)
                ?: EasyGolfMapFragment()
        val transition = supportFragmentManager.beginTransaction()
        transition.replace(R.id.containerMap, mEasyGolfMapFragment!!)
        transition.commit()

        mEasyGolfPlayerAdapter = EasyGolfPlayerAdapter(this, mViewModel.getProfileUser()?.id)
        mEasyGolfPlayerAdapter?.addListener(mAddPlayerCallback)
        mEasyGolfPlayerAdapter?.showScoreForPLayer(true)
        val layoutManager = GridLayoutManager(this, 4)
        listPlayer.layoutManager = layoutManager
        listPlayer.setHasFixedSize(true)
        listPlayer.isNestedScrollingEnabled = false
        listPlayer.adapter = mEasyGolfPlayerAdapter
        /**
         * Start get data from bundle
         * */
        EasyGolfNavigation.playGolfBundle(intent)?.let { playGolfBundle ->
            mEasyGolfMapFragment?.registerGoogleMapBehavior(googleMapBehavior)
            mViewModel.mPlayGolfBundle = playGolfBundle

            if (playGolfBundle.typeGame == BATTLE_GAME) {
                playGolfBundle.roundId?.let { roundId ->
                    mViewModel.getBattleRoundInFirebase(roundId) {
                        if (it != null && it.is_host == true) {
                            mEasyGolfPlayerAdapter?.addItemAt(null, 0)
                            mEasyGolfPlayerAdapter?.enableDelete(true)
                        } else {
                            mEasyGolfPlayerAdapter?.enableDelete(false)
                            /**
                             * Observer when this round was delete or current user kick from round by host
                             * */
                            mViewModel.mDisableFromRound.observe(this, Observer { wasKicked->
                                if(!wasKicked){
                                    // this round was delete or complete
                                    HostEndGameDialog(this@PlayGolfActivity,object : HostEndGameDialog.ListenerConfirmData{
                                        override fun onConfirm(dialog: HostEndGameDialog) {
                                            mViewModel.fetchRoundGolf(roundId){ newRoundGolf->
                                                mViewModel.deleteDataRound()
                                                if(newRoundGolf.isFinish()){
                                                    directionEndgame()
                                                }
                                                finish()
                                                dialog.dismiss()
                                            }
                                        }
                                    }).show()
                                }else{
                                    //TODO change ui make it oke
                                    HostEndGameDialog(this@PlayGolfActivity,object : HostEndGameDialog.ListenerConfirmData{
                                        override fun onConfirm(dialog: HostEndGameDialog) {
                                            mViewModel.deleteDataRound()
                                            dialog.dismiss()
                                            finish()
                                        }
                                    }).show()
                                }
                            })
                        }
                    }
                }

            } else {
                mEasyGolfPlayerAdapter?.addItemAt(null, 0)
                mEasyGolfPlayerAdapter?.enableDelete(true)
            }
            /**
             * add user default into players
             * */
            mViewModel.getProfileUser()?.let { user ->
                mEasyGolfPlayerAdapter?.addItem(user)
            }
            getLocation()

        } ?: finish()

        mViewModel.mMemberFirebaseLiveData.observe(this, Observer {
            if (it.isRemove) {
                toast(getString(R.string.member_remove_from_battle,it.user.fullName))
                mEasyGolfPlayerAdapter?.removeItemNotEnableCancel(it.user.id)
            } else {
                mEasyGolfPlayerAdapter?.addItem(it.user)
            }
        })

        mViewModel.updateScoreLiveData.observe(this, Observer {
            mViewModel.mCurrentHoleLiveData.value?.let { currentHole ->
                mViewModel.getProfileUser()?.let { user ->
                    updateScoreButton(it?.get(user.id)?.score, currentHole)
                }
                updateDataForBottomSheet(it, currentHole)
            }
        })
        /**
         * update views when change hole
         * */
        mViewModel.mCurrentHoleLiveData.observe(this, Observer { hole ->
            viewPhotoGreen.setPhoto(hole.image)
            if (hole.image.isNullOrEmpty()) {
                menuMore.hideYardBook()
            } else {
                menuMore.showYardBook()
            }
            /**
             *@author  move immediate tee to user location if have update location when change hole
             * **/
            mIsOnGameWithUpdateUserLocation = false

            distanceStartView.hide()
            distanceEndView.hide()
            /**
             * remove old coordinates
             * */
            mEasyGolfMapFragment?.zoomGreen(hole.green?.coordinates)
            /**
             * update index hole
             * */
            listHoleView.updateIndexHole(hole.number - 1)
            textDropdownHole.text = hole.number.toString()
            txtPar.text = hole.par.toString()
            txtIndex.text = hole.index.toString()
            updatePreviewNextHoleView()
            /**
             * get current type by type pass from PlayGolfBundle
             * */
            TeeUtils.getTeeByType(hole.tees, mViewModel.mPlayGolfBundle?.teeType)?.let { currentTee ->
                txtCurrentDistance.text = currentTee.yard.roundToInt().toString()
                hole.green?.let { green ->
                    green.getDefaultGreen()?.let { currentFrag ->
                        val positionTeeTemp = LatLng(currentTee.latitude, currentTee.longitude)
                        val locationPLayerLatLng = LatLng(mLocationPlayer.latitude, mLocationPlayer.longitude)

                        /**
                         * if current position player is near tee set position tee at player position
                         * else use position tee from data
                         * **/
                        val positionTee = if (!mLocationPlayer.isEmpty() &&
                                isStandNearTee(distanceUserLocationToTee(locationPLayerLatLng, positionTeeTemp))) {
                            locationPLayerLatLng
                        } else {
                            positionTeeTemp
                        }
                        val holderPositionTemp = if (hole.par == 3) LatLng(currentFrag.latitude, currentFrag.longitude) else LatLng(hole.latitude, hole.longitude)
                        mEasyGolfMapFragment?.setUpMapForPlayGolf(
                                positionTee,
                                TeeUtils.getResIcon(currentTee),
                                LatLng(currentFrag.latitude, currentFrag.longitude),
                                R.drawable.ic_icon_flag_white,
                                holderPositionTemp,
                                this
                        ) ?: unknownError()
                    }
                } ?: unknownError()
                /**TODO create green from tee 300 miles */
            } ?: unknownError()
        })

        /**
         * update views when change unit
         * */
        mViewModel.unitGolfLiveData.observe(this, Observer { unit ->
            mEasyGolfMapFragment?.onUpdateDistance()
            mViewModel.mCurrentHoleLiveData.value?.let { hole ->
                TeeUtils.getTeeByType(hole.tees, mViewModel.mPlayGolfBundle?.teeType)?.let { currentTee ->
                    txtCurrentDistance.text = GoogleMapsHelper.convertYardMeter(unit, currentTee.yard).roundToInt().toString()
                    txtFormatDistance.text = if (unit == GoogleMapsHelper.UnitGolf.YARD) getString(R.string.yards) else getString(R.string.meters)
                }
            }
        })
        /**
         * update views when change tee
         * */
        mViewModel.changeTeeLiveData.observe(this, Observer { tee ->
            mEasyGolfMapFragment?.onChangeColorTee(TeeUtils.getResIcon(tee), this)
        })
        registerOnClickView()
    }

    override fun onCreateWithState(savedInstanceState: Bundle?) {
        mViewModel.roundGolfLiveData.observe(this, Observer {
            /**
             * check if first enter activity
             * @param @savedInstanceState will not null if reload activity when necessary
             * */
            if (mViewModel.mPlayGolfBundle?.typeGame == NEW_GAME) {
                if (savedInstanceState == null) {
                    mViewModel.mPlayGolfBundle?.getMembers()?.let { members ->
                        if (members.isNotEmpty() && it.id != null && it.course_id != null) {
                            it.id?.let { roundGolfId ->
                                it.course_id?.let { courseId ->
                                    mViewModel.createBattleGame(roundGolfId, courseId, members)
                                }
                            }
                        }
                    }
                    mViewModel.mPlayGolfBundle?.clearMember()
                }
            }
            listHoleView.setDataHole(it.holes ?: ArrayList())

            val holeTarget = savedInstanceState?.run {
                val numberHole = getInt(STATE_HOLE) - 1
                if (numberHole >= 0 && numberHole < it.holes?.size ?: -1) {
                    it?.holes?.get(numberHole)
                } else {
                    it.math ?: it.holes?.first()
                }
            } ?: (it.math ?: it.holes?.first())

            holeTarget?.let { holeResult ->
                mViewModel.setHole(holeResult)
            }
            /**
             * tracking user is near tee?
             * */
            updateLocationUser()
            hideMask()
        })
    }

    override fun viewMask() {
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            layoutLoading.visibility = View.VISIBLE
            bottom_sheet_golf.visibility = View.INVISIBLE
        } else {
            super.viewMask()
        }
    }

    override fun hideMask() {
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            layoutLoading.visibility = View.GONE
            mStatusLoading = StatusLoading.FINISH_LOAD
            bottom_sheet_golf.visibility = View.VISIBLE
        } else {
            super.hideMask()
        }
    }

    private fun setupViewForExplore() {
        wozGolfButton.visibility = View.GONE
        bottom_sheet_golf.visibility = View.GONE
    }

    private fun setupViewForNewGame() {
        wozGolfButton.visibility = View.VISIBLE
        bottom_sheet_golf.visibility = View.VISIBLE
    }

    private fun updateDataForBottomSheet(dataScore: HashMap<String, DataScoreGolf?>?, hole: Hole) {
        /**
         * start set data for bottom sheet
         * */
        mEasyGolfPlayerAdapter?.setDataScore(dataScore, hole.par)
    }

    private fun updateScoreButton(score: Int?, hole: Hole) {
        if (score != null) {
            valueScore.text = score.toString()
            valueScore.setBackgroundResource(EasyGolfPickerScoreView.getResResourceWhite(score, hole.par))
            valueScore.visibility = View.VISIBLE
            emptyScore.visibility = View.GONE
        } else {
            valueScore.visibility = View.GONE
            emptyScore.visibility = View.VISIBLE
        }
    }

    /**
     * MARK: click change tee
     * */
    private fun registerOnClickView() {
        wozDistanceView.setEvent(object : WozViewDistance.EventWozDistance {
            override fun onSelected(pos: WozViewDistance.POSITION) {
                mViewModel.mCurrentHoleLiveData.value?.let { hole ->
                    var currentFlag: EasyGolfLocation? = null
                    val iconFlagNew = when (pos) {
                        BLACK -> {
                            currentFlag = hole.green?.blue
                            R.drawable.ic_icon_flag_blue
                        }
                        CENTER -> {
                            currentFlag = hole.green?.white
                            R.drawable.ic_icon_flag_white
                        }
                        FRONT -> {
                            currentFlag = hole.green?.red
                            R.drawable.ic_icon_flag_red
                        }
                    }
                    currentFlag?.let { currentFrag ->
                        mEasyGolfMapFragment?.updateFlagMarker(LatLng(currentFrag.latitude,
                                currentFrag.longitude), iconFlagNew, this@PlayGolfActivity)
                    }
                }
            }

        })

        /**
         *  Handle bottom sheet behavior
         * */
        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_golf)
        mBottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                viewHandlerSheet.rotationX = slideOffset * -180
                viewMask.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        viewMaskBottomSheet.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        viewMask.alpha = 1f
                        viewHandlerSheet.rotationX = 0f
                        viewMaskBottomSheet.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        if (viewMaskBottomSheet.visibility == View.GONE) {
                            viewMaskBottomSheet.visibility = View.VISIBLE
                        }
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                }
            }
        })

        viewMaskBottomSheet.setOnClickListener {
            if (mBottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        viewHandlerSheet.setOnClickListener {
            viewBottomSheetBehavior()
        }

        listHoleView.setOnClickHole { hole ->
            mViewModel.setHole(hole)
            hideMask()
        }

        containerTextHole.setOnClickListener {
            hideBottomSheetBehavior()
            viewMask()
            listHoleView.expand()
        }

        iconViewGreen.setOnClickListener {
            distanceStartView.hide()
            distanceEndView.hide()
            mEasyGolfMapFragment?.zoomGreen(mViewModel.mCurrentHoleLiveData.value?.green?.coordinates)
        }

        btBackHole.setOnClickListener {
            getPreviewHole()?.let { previewHole ->
                mViewModel.setHole(previewHole)
            }
        }

        btNextHole.setOnClickListener {
            getNextHole()?.let { nextHole ->
                mViewModel.setHole(nextHole)
            }
        }

        wozGolfButton.setOnClickListener {
            mViewModel.roundGolfLiveData.value?.id?.let { roundGolfId ->
                mViewModel.mCurrentHoleLiveData.value?.let { hole ->
                    val userTarget = if (mViewModel.typeGame() == BATTLE_GAME) {
                        mViewModel.getProfileUser()
                    } else {
                        null
                    }
                    val enterScoreFragment = EnterScoreBottomSheetFragment()
                    if (!enterScoreFragment.isVisible) {
                        enterScoreFragment.setInfoHole(roundGolfId, hole, userTarget)
                                .addCallback(object : EnterScoreBottomSheetFragment.OnEnterScoreCallback {
                                    override fun onSave() {
                                        getNextHole()?.let { nextHole ->
                                            mViewModel.setHole(nextHole)
                                        }
                                    }
                                })
                                .show(supportFragmentManager)
                    }
                }
            }
        }

        menuMore.setEvent(mHandleEventMenuMore)
        actionMenuMore.setOnClickListener {
            if (!menuMore.isOpen()) {
                menuMore.openView()
                viewMask()
            }
        }
        btEndGame.setOnClickListener {
            directionEndgame()
        }
        btViewScoreCard.setOnClickListener {
            directionScorecard()
        }
        btCancelDeletePlayer.setOnClickListener {
            mEasyGolfPlayerAdapter?.cancelShakeView()
            btCancelDeletePlayer.visibility = View.INVISIBLE
        }
    }

    private fun directionEndgame(){
        mViewModel.roundGolfLiveData.value?.id?.let { roundGolfId->
            EasyGolfNavigation.endGameGolfDirection(this@PlayGolfActivity,
                    EndGameBundle(
                            roundGolfId,
                            mViewModel.changeTeeLiveData.value?.type?:mViewModel.mPlayGolfBundle?.teeType,
                            mViewModel.mPlayGolfBundle?.scorecards,
                            mViewModel.typeGame()?: NEW_GAME
                    )
            )
        }
    }

    private fun directionScorecard(){
        mViewModel.roundGolfLiveData.value?.id?.let { roundGolfId->
            EasyGolfNavigation.scorecardDirection(this@PlayGolfActivity,
                    EndGameBundle(
                            roundGolfId,
                            mViewModel.changeTeeLiveData.value?.type?:mViewModel.mPlayGolfBundle?.teeType,
                            mViewModel.mPlayGolfBundle?.scorecards,
                            mViewModel.typeGame()?: NEW_GAME
                    )
            )
        }
    }

    private fun viewBottomSheetBehavior() {
        if (mBottomSheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun hideBottomSheetBehavior() {
        if (mBottomSheetBehavior?.state != BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun hideViewMaskCallBack() {
        listHoleView.collapse()
        hideBottomSheetBehavior()
        menuMore.closeView()
        hideMask()
    }

    override fun loadData() {
        /**
         * set profile user
         * */
        /**
         * MARK: current hole
         * */

        /**
         * End init data
         * */
    }

    private fun getLocation() {
        val locationHelper = LocationHelper()
        locationHelper.startLocationUpdatesForActivity(this, false, object : LocationHelper.OnLocationCatch {
            override fun onCatch(mLatLng: LatLng?, bearing: Float) {
                mLatLng?.apply {
                    mLocationPlayer.set(this.latitude, this.longitude)
                    startRoundGolf()
                }
            }

            override fun locationNotAvailable() {
                startRoundGolf()
            }

            override fun onPermissionDenied() {
                // TODO show permission dialog
            }
        })
    }

    private fun startRoundGolf() {
        mViewModel.mPlayGolfBundle?.let { playGolfBundle ->
            when (playGolfBundle.typeGame) {
                EXPLORE,UNKNOWN -> {
                    setupViewForExplore()
                }
                BATTLE_GAME, NEW_GAME -> {
                    setupViewForNewGame()
                }
            }
            mViewModel.startRound(playGolfBundle.roundId, playGolfBundle.courseId,
                    mLocationPlayer.latitude, mLocationPlayer.longitude, playGolfBundle.teeType)
        }
    }

    private fun unknownError() {
        showCommonMessage(getString(R.string.unknown_name), DialogInterface.OnClickListener { _, _ ->
            finish()
        })
    }

    private fun updatePreviewNextHoleView() {
        if (canPreviewHole()) {
            btBackHole.isEnabled = true
            btBackHole.alpha = 1f
        } else {
            btBackHole.isEnabled = false
            btBackHole.alpha = 0.3f
        }
        if (canNextHole()) {
            btNextHole.isEnabled = true
            btNextHole.alpha = 1f
        } else {
            btNextHole.isEnabled = false
            btNextHole.alpha = 0.3f
        }
    }

    private fun canPreviewHole(): Boolean {
        return mViewModel.mCurrentHoleLiveData.value?.let { hole ->
            hole.number > 1
        } ?: false
    }

    private fun getPreviewHole(): Hole? {
        return if (canPreviewHole()) {
            mViewModel.roundGolfLiveData.value?.let { roundGolf ->
                val index = (mViewModel.mCurrentHoleLiveData.value?.number ?: 0) - 2
                if (index >= 0 && index < roundGolf.holes?.size ?: 0) {
                    roundGolf.holes?.get(index)
                } else null
            }
        } else null
    }

    private fun canNextHole(): Boolean {
        return mViewModel.roundGolfLiveData.value?.let { roundGolf ->
            mViewModel.mCurrentHoleLiveData.value?.let { hole ->
                hole.number < (roundGolf.holes?.size ?: 0)
            } ?: false
        } ?: false
    }

    private fun getNextHole(): Hole? {
        return if (canNextHole()) {
            mViewModel.roundGolfLiveData.value?.let { roundGolf ->
                val index = (mViewModel.mCurrentHoleLiveData.value?.number ?: 0)
                if (index >= 0 && index < roundGolf.holes?.size ?: 0) {
                    roundGolf.holes?.get(index)
                } else null
            }
        } else null
    }

    private fun updateDistanceView(teePosition: LatLng, holderPosition: LatLng, flagPosition: LatLng,
                                   easyGolfMapFragment: EasyGolfMapFragment, zoomState: EasyGolfMapFragment.ZoomStateGreen) {
        val computeCentroidStart = if (zoomState == EasyGolfMapFragment.ZoomStateGreen.ZOOM_IN)
            GoogleMapsHelper.getDestinationPoint(
                    holderPosition,
                    GoogleMapsHelper.getBearingFromLocation(teePosition, holderPosition) - 180.0,
                    easyGolfMapFragment.getDist(distanceStartView.width * 3, holderPosition))
                    ?: GoogleMapsHelper.computeCentroid(teePosition, holderPosition)
        else GoogleMapsHelper.computeCentroid(teePosition, holderPosition)

        easyGolfMapFragment.toScreenLocation(
                GoogleMapsHelper.getDestinationPoint(
                        computeCentroidStart,
                        GoogleMapsHelper.getBearingFromLocation(teePosition, holderPosition) - 90.0,
                        easyGolfMapFragment.getDist(distanceStartView.width, computeCentroidStart)
                )
        )?.let { pointDistanceStart ->
            distanceStartView.setValue(GoogleMapsHelper.getDistanceLatLng(teePosition, holderPosition, mViewModel.getUnit()).toInt())
            distanceStartView.x = pointDistanceStart.x.toFloat() - (distanceStartView.width / 2.0f)
            distanceStartView.y = pointDistanceStart.y.toFloat() - (distanceStartView.height / 2.0f)
        }

        val computeCentroidEnd = GoogleMapsHelper.computeCentroid(flagPosition, holderPosition)
        easyGolfMapFragment.toScreenLocation(
                GoogleMapsHelper.getDestinationPoint(
                        computeCentroidEnd,
                        GoogleMapsHelper.getBearingFromLocation(flagPosition, holderPosition) + 270.0,
                        easyGolfMapFragment.getDist(distanceStartView.width, computeCentroidEnd)
                )
        )?.let { pointDistanceEnd ->
            distanceEndView.setValue(GoogleMapsHelper.getDistanceLatLng(flagPosition, holderPosition, mViewModel.getUnit()).toInt())
            distanceEndView.x = pointDistanceEnd.x.toFloat() - (distanceStartView.width / 2.0f)
            distanceEndView.y = pointDistanceEnd.y.toFloat() - (distanceStartView.height / 2.0f)
        }
    }

    /**
     * end tracking
     * */

    override fun onPause() {
        super.onPause()
        mEasyGolfMapFragment?.stopAnimationTeeMarker()
        mLocationClientUpdate?.removeLocationUpdates()
        /**
         *remove observable firebase
         * */
        mViewModel.onStateFirebaseDatabasePause()
    }

    override fun onResume() {
        super.onResume()
        if (mViewModel.roundGolfLiveData.value != null && mLocationClientUpdate != null && mStatusLoading != StatusLoading.FIRST_LOAD) {
            mIsOnGameWithUpdateUserLocation = false
            updateLocationUser()
            /**
             *register observable firebase
             * */
            mViewModel.onStateFirebaseDatabaseResume()
        }
    }

    private var mLocationUserUpdateCatch = object : LocationHelper.OnLocationCatch {
        override fun onCatch(mLatLng: LatLng?, bearing: Float) {
            mLatLng?.apply {
                mLocationPlayer.set(this.latitude, this.longitude)
                val destination = LatLng(this.latitude, this.longitude)
                distanceUserLocationToTee(destination)?.let { distanceToTee ->
                    val isEnableMoveTee = if (isStandNearTee(distanceToTee)) {
                        mEasyGolfMapFragment?.updateTeeMarkerPosition(
                                destination,
                                /***
                                 * move immediate tee to user location if have update location when app wakeup
                                 * just update with (200 miles = 5000 milliseconds)
                                 * */
                                if (mIsOnGameWithUpdateUserLocation) (distanceToTee * 25).toLong() else {
                                    mIsOnGameWithUpdateUserLocation = true
                                    null
                                }
                        )
                        false
                    } else {
                        true
                    }
                    mEasyGolfMapFragment?.enableMoveTee(isEnableMoveTee)
                } ?: mEasyGolfMapFragment?.enableMoveTee(true)
            } ?: mEasyGolfMapFragment?.enableMoveTee(true)
        }

        override fun locationNotAvailable() {}

        override fun onPermissionDenied() {
            // TODO show permission dialog
        }
    }

    private fun isStandNearTee(distance: Double?): Boolean {
        return distance?.let {
            it <= MAX_DISTANCE_TO_SHOW_TRACK
        } ?: false
    }

    private fun distanceUserLocationToTee(currentUser: LatLng, teePosition: LatLng): Double? {
        return GoogleMapsHelper.getDistanceLatLng(currentUser, teePosition)
    }

    private fun distanceUserLocationToTee(currentUser: LatLng): Double? {
        return mEasyGolfMapFragment?.getTeeMarkerPosition()?.let { teePosition ->
            distanceUserLocationToTee(currentUser, teePosition)
        }
    }

    /**
     * check position user is nearby current tee
     * */
    private fun updateLocationUser() {
        if (mLocationClientUpdate == null) {
            mLocationClientUpdate = LocationHelper()
            mLocationClientUpdate?.startLocationUpdatesForActivity(this, true, mLocationUserUpdateCatch)
        } else {
            mLocationClientUpdate?.updateRequest(this)
        }
    }
    /**
     * end tracking
     * */

    /**
     * for maps behavior
     * */
    private val googleMapBehavior = object : GoogleMapBehavior {
        override fun onCameraMoveListener(teePosition: LatLng, holderPosition: LatLng, flagPosition: LatLng,
                                          easyGolfMapFragment: EasyGolfMapFragment, zoomState: EasyGolfMapFragment.ZoomStateGreen) {
            updateDistanceView(teePosition, holderPosition, flagPosition, easyGolfMapFragment, zoomState)
        }

        override fun onChangeDistance(teePosition: LatLng, holderPosition: LatLng, flagPosition: LatLng,
                                      easyGolfMapFragment: EasyGolfMapFragment, zoomState: EasyGolfMapFragment.ZoomStateGreen) {
            updateDistanceView(teePosition, holderPosition, flagPosition, easyGolfMapFragment, zoomState)
            mViewModel.mCurrentHoleLiveData.value?.green?.let { green ->
                green.blue?.let { blueGreen ->
                    wozDistanceView.setValue(GoogleMapsHelper.getDistanceLatLng(
                            LatLng(teePosition.latitude, teePosition.longitude),
                            LatLng(blueGreen.latitude, blueGreen.longitude),
                            mViewModel.getUnit()
                    ), BLACK)
                }
                green.white?.let { whiteGreen ->
                    wozDistanceView.setValue(GoogleMapsHelper.getDistanceLatLng(
                            LatLng(teePosition.latitude, teePosition.longitude),
                            LatLng(whiteGreen.latitude, whiteGreen.longitude),
                            mViewModel.getUnit()
                    ), CENTER)
                }
                green.red?.let { redGreen ->
                    wozDistanceView.setValue(GoogleMapsHelper.getDistanceLatLng(
                            LatLng(teePosition.latitude, teePosition.longitude),
                            LatLng(redGreen.latitude, redGreen.longitude),
                            mViewModel.getUnit()
                    ), FRONT)
                }
            }
        }

        override fun onFinishZoomHole(teePosition: LatLng, holderPosition: LatLng, flagPosition: LatLng,
                                      easyGolfMapFragment: EasyGolfMapFragment, zoomState: EasyGolfMapFragment.ZoomStateGreen) {
            updateDistanceView(teePosition, holderPosition, flagPosition, easyGolfMapFragment, zoomState)
            distanceStartView.show()
            if (zoomState != EasyGolfMapFragment.ZoomStateGreen.ZOOM_IN) {
                distanceEndView.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        finish()
    }
}