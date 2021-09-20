package com.minhhop.easygolf.presentation.club.detail.overview

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minhhop.core.domain.User
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.core.domain.golf.ClubPhoto
import com.minhhop.core.domain.golf.Course
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.core.domain.profile.People
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.bundle.AddPlayerBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.common.GolfUtils
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailActivity
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailViewModel
import com.minhhop.easygolf.presentation.club.detail.club.FollowersFragment
import com.minhhop.easygolf.presentation.club.detail.photo.ClubPhotoAdapter
import com.minhhop.easygolf.presentation.club.detail.review.RatingView
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import com.minhhop.easygolf.presentation.dialog.CommonSelectBottomSheetDialog
import com.minhhop.easygolf.presentation.dialog.course.SelectCourseDialog
import com.minhhop.easygolf.presentation.dialog.scorecard.SelectScorecardDialog
import com.minhhop.easygolf.presentation.golf.play.PlayGolfActivity
import com.minhhop.easygolf.presentation.player.EasyGolfPlayerAdapter
import kotlinx.android.synthetic.main.acitivity_user_profile_demo.followers_recyclerviw
import kotlinx.android.synthetic.main.acitivity_user_profile_demo.followers_tv
import kotlinx.android.synthetic.main.fragment_course_overview.*
import kotlinx.android.synthetic.main.fragment_course_overview.view.*
import kotlinx.android.synthetic.main.news_feed_row_layout.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.net.URI


class ClubOverviewFragment : EasyGolfFragment<EasyGolfClubDetailViewModel>(), EasyGolfPlayerAdapter.EasyGolfPlayerAdapterListener,
        ClubPhotoAdapter.ClubPhotoAdapterListener {
    companion object {
        const val TAG = "ClubOverviewFragment"
        const val REQUEST_CODE = 123
    }

    private var followersAdapter: FollowersAdapter? = null
    private val mMaxPhotoGetFromGallery = 5
    override val mViewModel by sharedViewModel<EasyGolfClubDetailViewModel>()
    private var mListPhotoGallery = ArrayList<File>()
    private var mUrlFilePicture: String? = null
    private var mClubPhotoAdapter: ClubPhotoAdapter? = null
    private var mEasyGolfPlayerAdapter: EasyGolfPlayerAdapter? = null
    private var mNeedToCheckBattle: Boolean = false
    override fun setLayout(): Int = R.layout.fragment_course_overview

    override fun initView(viewRoot: View) {
        context?.let { context ->
            val layoutManager = GridLayoutManager(context, 4)
            viewRoot.listPlayer.layoutManager = layoutManager
            viewRoot.listPlayer.setHasFixedSize(true)
            viewRoot.listPlayer.isNestedScrollingEnabled = false

            val currentUser = mViewModel.getProfileUserInLocal()

            mEasyGolfPlayerAdapter = EasyGolfPlayerAdapter(context, currentUser?.id)
            viewRoot.listPlayer.adapter = mEasyGolfPlayerAdapter
            mEasyGolfPlayerAdapter?.addListener(this)
            mEasyGolfPlayerAdapter?.addItemAt(null, 0)
            /**
             * add user default into players
             * */
            currentUser?.let { user ->
                mEasyGolfPlayerAdapter?.addItem(user)
            }

            viewRoot.listClubPhotos.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            viewRoot.listClubPhotos.setHasFixedSize(true)
            viewRoot.listClubPhotos.isNestedScrollingEnabled = false
            mClubPhotoAdapter = ClubPhotoAdapter(context, mViewModel.getProfileUserInLocal())
            mClubPhotoAdapter?.addListener(this)
            viewRoot.listClubPhotos.adapter = mClubPhotoAdapter
            mClubPhotoAdapter?.addItem(null)

            viewRoot.viewHandicapEligible.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.easygolf_handicap))
                        .setMessage(getString(R.string.handicap_eligible))
                        .setPositiveButton(getString(android.R.string.ok)) { dialog, _ -> dialog?.dismiss() }.show()
            }
        }
        mViewModel.clubDataLive.observe(viewLifecycleOwner, Observer {
            viewRoot.textNameCourse.text = it.name
            viewRoot.textRating.text = RatingView.roundRate(it.rating)
            it.website?.let { website ->
                try {
                    val uri = URI(website)
                    viewRoot.textWebLink.text = uri.host
                } catch (e: Exception) {
                    showCommonMessage(e.localizedMessage)
                }
            }
            viewRoot.textAddress.text = it.address
            viewRoot.textDescription.text = it.description
        })
        mViewModel.currentCourseLiveData.observe(viewLifecycleOwner, Observer { course ->
            checkExitBattleGame()
            viewRoot.btSelectCourse.text = course?.name
        })

        mViewModel.currentScorecardLiveData.observe(viewLifecycleOwner, Observer { scorecardDefault ->
            viewRoot.btSelectTee.text = getString(R.string.tee_format_distance, scorecardDefault.type, scorecardDefault.yard.toString())
            mEasyGolfPlayerAdapter?.updateScorecard(scorecardDefault)
            /**
             * check this course can calculate handicap
             * */
            viewRoot.viewHandicapEligible.visibility = if (GolfUtils.canCalculateHandicapByCourse(scorecardDefault)) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (viewRoot.btSelectTee.compoundDrawables.isNotEmpty()) {
                viewRoot.btSelectTee.compoundDrawables[0]?.colorFilter = PorterDuffColorFilter(TeeUtils.getColorByType(scorecardDefault.type), PorterDuff.Mode.SRC_IN)
            }
        })

        mViewModel.clubPhotosLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                mClubPhotoAdapter?.addListItem(it)
            }
        })
        mViewModel.followersLiveData.observe(this, Observer {
            it?.let {
                setFollowers(it)
            }
            hideMask()
        })
        mViewModel.clubFeeds.observe(this, Observer {
            it?.let {
                it.data.let { feeds ->
                    if (feeds.isNotEmpty()) {
                        clubNewsFeedCardView.visibility = View.VISIBLE
                        post_profile_iv_layout.setData(feeds[0], null, 1)
                    } else {
                        clubNewsFeedCardView.visibility = View.GONE
                    }

                }
            }
            hideMask()
        })
        mViewModel.positionDeleteOverviewLiveData.observe(viewLifecycleOwner, Observer {
            mClubPhotoAdapter?.removeItemAt(it)
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })

        mViewModel.newPhotoInsertLiveData.observe(viewLifecycleOwner, Observer {
            mClubPhotoAdapter?.addListItemAt(it, 1)
            mUrlFilePicture?.let { urlPhotoUploaded ->
                mViewModel.deleteFileImage(urlPhotoUploaded)
                mUrlFilePicture = null
            }
            if (mListPhotoGallery.isNotEmpty()) {
                mListPhotoGallery.forEach { file ->
                    mViewModel.deleteFileImage(file.absolutePath)
                }
            }
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })

        mViewModel.uploadPhotoScorecardCallbackLiveData.observe(viewLifecycleOwner, Observer {
            context?.apply {
                MaterialAlertDialogBuilder(this)
                        .setMessage(getString(R.string.scorecard_uploaded))
                        .setCancelable(false)
                        .setPositiveButton(this.getString(android.R.string.ok), null)
                        .show()
            }
            mUrlFilePicture?.let { urlPhotoUploaded ->
                mViewModel.deleteFileImage(urlPhotoUploaded)
                mUrlFilePicture = null
            }
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })
    }


    private fun setFollowers(followers: FollowersResponse) {
        followers.data.let {
            if (it.isNotEmpty()) {
                followersMainCardView.visibility = View.VISIBLE
                context?.let { context ->
                    val mLayoutManager = LinearLayoutManager(context)
                    mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    followers_recyclerviw.layoutManager = mLayoutManager
                    followersAdapter = FollowersAdapter(context, 1)
                    followers_recyclerviw.adapter = followersAdapter
                    val tempUsers: ArrayList<People> = ArrayList()
                    if (followers.data.size > 6) {
                        for (x in 0..5) {
                            tempUsers.add(followers.data[x])
                        }
                        followers_tv.visibility = View.VISIBLE
                        followers_tv.text = "+${(it.size - 6).toString()}"
                    } else {
                        for (element in followers.data) {
                            tempUsers.add(element)
                        }
                        followers_tv.visibility = View.GONE
                    }
                    Log.e("Testisohssss", "${tempUsers.size}")
                    followersAdapter?.setDataList(tempUsers)
                }
            } else {
                followersMainCardView.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mNeedToCheckBattle) {
            checkExitBattleGame()
            mNeedToCheckBattle = false
        }
    }

    private fun checkExitBattleGame() {
        mStatusLoading = StatusLoading.FIRST_LOAD
        viewRoot.buttonStartRound.setStatus(EasyGolfButton.Status.PROCESS)
        viewRoot.buttonEnterScorecard.setStatus(EasyGolfButton.Status.PROCESS)

        mViewModel.getProfileUserInLocal()?.let { userAdmin ->
            val endIndex = (mEasyGolfPlayerAdapter?.itemCount ?: -1) - 1
            for (index in endIndex downTo 0) {
                if (mEasyGolfPlayerAdapter?.getItem(index)?.id != userAdmin.id) {
                    mEasyGolfPlayerAdapter?.removeItemAt(index)
                } else {
                    break
                }
            }
        }

        mViewModel.getBattleRoundWithCourseInFirebase {
            if (it != null) {
                // set Tee
                mViewModel.setCurrentScorecardWithTeeType(it.tee_type)
                it.round_id?.let { roundId ->
                    /**
                     * check permission remove member in the battle
                     * */
                    if (it.is_host == false) {
                        if (mEasyGolfPlayerAdapter?.getItem(0) == null) {
                            mEasyGolfPlayerAdapter?.removeItemAt(0)
                        }
                        mEasyGolfPlayerAdapter?.enableDelete(false)
                    } else {
                        mEasyGolfPlayerAdapter?.enableDelete(true)
                    }

                    mViewModel.getMemberInBattle(roundId) { member ->
                        mEasyGolfPlayerAdapter?.addItem(member)
                    }
                    if (it.is_pending == true) {
                        viewRoot.buttonEnterScorecard.setText(getString(R.string.cancel))
                        viewRoot.buttonStartRound.setText(getString(R.string.join_round))
                    } else {
                        viewRoot.buttonEnterScorecard.setText(getString(R.string.enter_scorecard))
                        viewRoot.buttonStartRound.setText(getString(R.string.play_continue_round))
                    }
                }
                viewRoot.buttonStartRound.setStatus(EasyGolfButton.Status.IDLE)
            } else {
                var teeType: String? = null
                mViewModel.getRoundGolfInLocal()?.let { roundGolfLocal ->
                    teeType = roundGolfLocal.teeType
                    viewRoot.buttonStartRound.setText(getString(R.string.play_continue_round))
                } ?: viewRoot.buttonStartRound.setText(getString(R.string.start_a_round))

                mViewModel.setCurrentScorecardWithTeeType(teeType)

                if (mEasyGolfPlayerAdapter?.getItem(0) != null) {
                    mEasyGolfPlayerAdapter?.addItemAt(null, 0)
                }
                mEasyGolfPlayerAdapter?.enableDelete(true)

                viewRoot.buttonEnterScorecard.setText(getString(R.string.enter_scorecard))
            }
            viewRoot.buttonStartRound.setStatus(EasyGolfButton.Status.IDLE)
            viewRoot.buttonEnterScorecard.setStatus(EasyGolfButton.Status.IDLE)
            mStatusLoading = StatusLoading.FINISH_LOAD
        }
    }

    override fun loadData() {
        viewRoot.btExploreCourse.setOnClickListener {
            if (mStatusLoading == StatusLoading.FINISH_LOAD) {
                mViewModel.currentCourseLiveData.value?.let { course ->
                    mViewModel.currentScorecardLiveData.value?.let { scorecard ->
                        context?.let { context ->
                            EasyGolfNavigation.playGolfDirection(context,
                                    PlayGolfBundle(
                                            PlayGolfBundle.TypeGame.EXPLORE,
                                            null,
                                            course.id,
                                            scorecard.type,
                                            scorecards = null
                                    ))
                            mNeedToCheckBattle = true
                        }
                    }
                }
            }
        }

        viewRoot.followers_tv.setOnClickListener {
            mViewModel.clubDataLive.value?.let { club ->
                showFollowersFragment(club.id)
            }
        }
        viewRoot.viewMoreFeeds.setOnClickListener {
            mViewModel.clubDataLive.value?.let { club ->
                EasyGolfNavigation.feedClubDirection(this, club.id)
            }
        }
        viewRoot.btSelectCourse.setOnClickListener {
            mViewModel.clubDataLive.value?.let { club ->
                context?.let { context ->
                    SelectCourseDialog(context)
                            .setData(club.courses, object : SelectCourseDialog.SelectCourseListener {
                                override fun onSelected(course: Course) {
                                    mViewModel.setCourse(course)
                                }
                            }).show()
                }
            }
        }
        viewRoot.btSelectTee.setOnClickListener {
            context?.let { context ->
                mViewModel.getListScoreCard()?.let { scorecards ->
                    SelectScorecardDialog(context)
                            .setData(scorecards, object : SelectScorecardDialog.SelectScorecardListener {
                                override fun onSelected(scorecard: Scorecard) {
                                    mViewModel.setCurrentScorecard(scorecard)
                                }
                            })
                            .show()
                }
            }
        }

        viewRoot.buttonStartRound.setOnClickListener {
            if (mStatusLoading == StatusLoading.FINISH_LOAD) {
                mViewModel.currentCourseLiveData.value?.let { course ->
                    mViewModel.currentScorecardLiveData.value?.let { scorecard ->
                        context?.let { context ->
                            val typeGame = if (mViewModel.mBattleRound == null) {
                                PlayGolfBundle.TypeGame.NEW_GAME
                            } else {
                                if (mViewModel.mBattleRound?.is_pending == true) {
                                    mViewModel.mBattleRound?.round_id?.let { roundId ->
                                        mViewModel.updateStatusPendingInBattleForUser(roundId)
                                    }
                                }
                                PlayGolfBundle.TypeGame.BATTLE_GAME
                            }
                            EasyGolfNavigation.playGolfDirection(context,
                                    PlayGolfBundle(
                                            typeGame,
                                            mViewModel.mBattleRound?.round_id,
                                            course.id,
                                            scorecard.type,
                                            PlayGolfBundle.toScorecard(course.scorecard),
                                            if (typeGame == PlayGolfBundle.TypeGame.NEW_GAME)
                                                PlayGolfBundle.toMembers(mEasyGolfPlayerAdapter?.getListUserExit())
                                            else null
                                    ))
                            mNeedToCheckBattle = true
                            (activity)?.overridePendingTransition(0, 0)
                            (activity as? EasyGolfClubDetailActivity)?.viewLoadingMap()
                        }
                    }
                }
            }
        }
        viewRoot.btCancelDeletePlayer.setOnClickListener {
            mEasyGolfPlayerAdapter?.cancelShakeView()
            viewRoot.btCancelDeletePlayer.visibility = View.INVISIBLE
        }

        viewRoot.buttonEnterScorecard.setOnClickListener {
            if (mStatusLoading == StatusLoading.FINISH_LOAD) {
                if (mViewModel.mBattleRound?.is_pending == true) {
                    mStatusLoading = StatusLoading.FIRST_LOAD
                    viewRoot.buttonEnterScorecard.setStatus(EasyGolfButton.Status.PROCESS)
                    mViewModel.removeMembersFromBattle()
                } else {
                    CommonSelectBottomSheetDialog()
                            .addSectionView(
                                    if (mEasyGolfPlayerAdapter?.getListUserExit()?.isNullOrEmpty() == true) {
                                        arrayOf(getString(R.string.upload_scorecard),
                                                getString(R.string.enter_pass_score),
                                                getString(R.string.quick_input))
                                    } else {
                                        arrayOf(getString(R.string.upload_scorecard),
                                                getString(R.string.quick_input))
                                    })
                            .addCallback(object : CommonSelectBottomSheetDialog.SectionCallback {
                                override fun onSelectSectionView(position: Int) {
                                    when (position) {
                                        0 -> {
                                            EasyGolfNavigation.pickPhotoGalleryDirection(this@ClubOverviewFragment,
                                                    Contains.REQUEST_CODE_SCORECARD_PHOTO_GALLERY)
                                        }
                                        1 -> {
                                            context?.let { context ->
                                                mViewModel.currentCourseLiveData.value?.let { course ->
                                                    mViewModel.currentScorecardLiveData.value?.let { scorecard ->
                                                        EasyGolfNavigation.enterPassScoreDirection(context, course.id, scorecard.type)
                                                    }
                                                }
                                            }
                                        }
                                        else -> {
                                            toast("coming soon")
                                        }
                                    }
                                }
                            })
                            .show(childFragmentManager)
                }
            }
        }
    }

    private fun showFollowersFragment(clubId: String) {
        activity?.let {
            val ft = it.supportFragmentManager.beginTransaction()
            val fragment = FollowersFragment()
            val bundle = Bundle()
            bundle.putString("clubId", clubId)
            fragment.arguments = bundle
            ft.replace(R.id.viewRoot, fragment, "followersfragment").addToBackStack(null).commit()
        }
    }

    override fun onAddPlayer() {
        if (mStatusLoading == StatusLoading.FINISH_LOAD) {
            if (mEasyGolfPlayerAdapter?.getRealCount() ?: 0 < PlayGolfActivity.MAX_FRIEND_IN_BATTLE) {
                val limit = mEasyGolfPlayerAdapter?.getRealCount()?.let { count -> PlayGolfActivity.MAX_FRIEND_IN_BATTLE - count }
                        ?: -1
                EasyGolfNavigation.addPlayerDirection(this, REQUEST_CODE, limit, AddPlayerBundle.toBlackList(mEasyGolfPlayerAdapter?.getListUserExit()))
            } else {
                context?.let { context ->
                    toast(context.getString(R.string.limit_add_player, PlayGolfActivity.MAX_FRIEND_IN_BATTLE + 1))
                }
            }
        }
    }

    override fun onDeletePlayerEnable(user: User?) {
        viewRoot.btCancelDeletePlayer.visibility = if (user == null) {
            View.VISIBLE
        } else {
            mViewModel.removeMembersFromBattle(user)
            View.INVISIBLE
        }
    }

    override fun onPlayerClick(user: User?) {

    }


    /**
     * Club Photos
     * */
    override fun onDelete(photo: ClubPhoto, position: Int) {
        mViewModel.clubDataLive.value?.let { club ->
            context?.let { context ->
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.delete))
                        .setMessage(getString(R.string.delete_conversation))
                        .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                            (activity as? EasyGolfClubDetailActivity)?.viewMask()
                            mViewModel.deleteClubPhoto(club.id, photo.id, position, true)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

            }
        }
    }

    override fun onClick(photo: ClubPhoto, position: Int) {
        //TODO show slide photos
    }

    override fun uploadPhoto() {
        EasyGolfNavigation.pickPhotoGalleryDirection(this@ClubOverviewFragment, Contains.REQUEST_CODE_PHOTO_GALLERY, mMaxPhotoGetFromGallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    val addPlayerBundle = EasyGolfNavigation.addPlayerBundle(data)
                    addPlayerBundle?.getResult()?.let { users ->
                        mEasyGolfPlayerAdapter?.addListItem(users)
                        mViewModel.addMembersToBattle(users)
                    }
                }
            }
            Contains.REQUEST_CODE_PHOTO_GALLERY -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    EasyGolfNavigation.getGalleryBundle(data)?.getResult()?.let { galleryMedias ->
                        if (galleryMedias.isNotEmpty()) {
                            try {
                                galleryMedias.forEach { media ->
                                    context?.contentResolver?.openInputStream(Uri.parse(media.path))?.let { inputStream ->
                                        Contains.fileDirClubPhoto(context).let { fileSafe ->
                                            mViewModel.createImageFileHolder(fileSafe, inputStream)?.let { fileResult ->
                                                mUrlFilePicture = fileResult.absolutePath
                                                mViewModel.resizeFileImage(fileResult.absolutePath)
                                                mListPhotoGallery.add(fileResult)
                                            }
                                        }
                                    }
                                }
                                mViewModel.clubDataLive.value?.let { club ->
                                    (activity as? EasyGolfClubDetailActivity)?.viewMask()
                                    mViewModel.uploadPhotoFileForClub(club.id, mListPhotoGallery)
                                } ?: (activity as? EasyGolfClubDetailActivity)?.hideMask()

                            } catch (e: Exception) {
                                showCommonMessage(e.localizedMessage)
                            }
                        }
                    }
                }
            }
            Contains.REQUEST_CODE_SCORECARD_PHOTO_GALLERY -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    EasyGolfNavigation.getGalleryBundle(data)?.getResult()?.let { galleryMedias ->
                        if (galleryMedias.isNotEmpty()) {
                            try {
                                galleryMedias.first().let { media ->
                                    context?.contentResolver?.openInputStream(Uri.parse(media.path))?.let { inputStream ->
                                        Contains.fileDirClubPhoto(context).let { fileSafe ->
                                            mViewModel.createImageFileHolder(fileSafe, inputStream)?.let { fileResult ->
                                                mUrlFilePicture = fileResult.absolutePath
                                                mViewModel.resizeFileImage(fileResult.absolutePath)
                                                (activity as? EasyGolfClubDetailActivity)?.viewMask()
                                                mViewModel.uploadScorecardGolf(fileResult, mEasyGolfPlayerAdapter?.getListUserExit())
                                            }
                                        }

                                    }
                                }
                            } catch (e: Exception) {
                                showCommonMessage(e.localizedMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}