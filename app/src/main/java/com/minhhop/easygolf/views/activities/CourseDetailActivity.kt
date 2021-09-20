package com.minhhop.easygolf.views.activities
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle1
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.listeners.OnComplete
import com.minhhop.easygolf.presentation.support.StarRattingView
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.models.common.Battle
import com.minhhop.easygolf.presentation.course.ScoreCardCourseActivity
import com.minhhop.easygolf.utils.TextViewUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class CourseDetailActivity : WozBaseActivity() {

    private var mSelectCourse = SELECT_COURSE.PLAY_ROUND
    private var mIdCourse: String? = ""
    private var mIdClub: String? = null

    private var mCurrentClub: Club? = null

    private var mLayoutRating: StarRattingView? = null
    private var txtPhone: TextView? = null
    private var txtLocation: TextView? = null

    private var mViewRoot: CoordinatorLayout? = null
    private var mNameCourse: String? = null
    private val mLocationCourse = PointMap()

    private var mBtContinueRound: View? = null
    private var mSheetBehavior: BottomSheetBehavior<*>? = null
    private var mTxtNameCourse: TextView? = null

    private var mSizeListCourse = 0
    private var mListScorecardFirst: ArrayList<ScorecardClub>? = null

    private var mIdRoundBattle = ""
    private var mDatabase: DatabaseReference? = null
    private var mValueEventListener: ValueEventListener? = null

    private var mExitPlayGameBattle = false
    private var mIsClickMeNow = false

    private enum class SELECT_COURSE {
        PLAY_ROUND,
        SCORECARD
    }

    override fun setLayoutView(): Int {
        // inside your activity (if you did not enable transitions in your theme)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        // set an exit transition
        window.exitTransition = Explode()


        EventBus.getDefault().register(this)
        return R.layout.activity_course_detail
    }

    override fun initView() {

        mViewRoot = findViewById(R.id.viewRoot)

        mBtContinueRound = findViewById(R.id.btPlayContinueGround)
        mBtContinueRound!!.setOnClickListener(this)

        val width = AppUtil.getWidthScreen(this)
        val viewAnimationLoading = findViewById<View>(R.id.viewAnimationLoading)
        val layoutParams = viewAnimationLoading.layoutParams
        layoutParams.width = width
        viewAnimationLoading.requestLayout()

        mLayoutRating = findViewById(R.id.layoutRating)
        mTxtNameCourse = findViewById(R.id.txtNameCourse)
        txtPhone = findViewById(R.id.txtPhone)
        txtLocation = findViewById(R.id.txtLocation)
        val currentIntent = intent
        val imgCourseLink = currentIntent.getStringExtra(Contains.EXTRA_IMAGE_COURSE)
        mIdClub = currentIntent.getStringExtra(Contains.EXTRA_ID_COURSE)
        mIdCourse = mIdClub
        mNameCourse = currentIntent.getStringExtra(Contains.EXTRA_NAME_COURSE)
        mTxtNameCourse!!.text = mNameCourse
        val imgCourse = findViewById<ImageView>(R.id.imgCourse)


        imgCourseLink?.let {
            supportPostponeEnterTransition()
            imgCourse.load(imgCourseLink){
                supportStartPostponedEnterTransition()
            }
            supportPostponeEnterTransition()
        }


        findViewById<View>(R.id.btPlayNewGround).setOnClickListener(this)

        findViewById<View>(R.id.actionPhone).setOnClickListener(this)
        findViewById<View>(R.id.actionDirection).setOnClickListener(this)
        findViewById<View>(R.id.actionScorecards).setOnClickListener(this)
        findViewById<View>(R.id.actionInformation).setOnClickListener(this)

        val layoutBottomSheet = findViewById<View>(R.id.layoutBottomSheet)
        mSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)

    }

    fun ImageView.load(url: String, onLoadingFinished: () -> Unit = {}) {

        val listener = object: RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }

        }
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.img_holder_golf_radius))
                .listener(listener)
                .into(this)
    }

    private fun getDataFirebase() {
        showLoading()
        val currentUserEntity = DatabaseService.getInstance().currentUserEntity
        if (currentUserEntity != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("users").child(currentUserEntity.id)
                    .child("battles")

            mValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) {
                        var haveBattle: Battle? = Battle()
                        for (postSnapshot in dataSnapshot.children) {
                            haveBattle = postSnapshot.getValue(Battle::class.java)
                        }
                        if (haveBattle != null) {
                            mIdRoundBattle = haveBattle.round_id
                            mBtContinueRound!!.visibility = View.VISIBLE
                            mExitPlayGameBattle = true
                        }
                    } else {
                        if (DatabaseService.getInstance().getDataRoundGolf(mIdClub!!) != null) {
                            mBtContinueRound!!.visibility = View.VISIBLE
                            mExitPlayGameBattle = false
                        } else {
                            mBtContinueRound!!.visibility = View.GONE
                            mExitPlayGameBattle = false
                        }
                    }

                    Log.e("WOW","mExitPlayGameBattle: $mExitPlayGameBattle")
                    hideLoading()
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }

            val queryBattle = mDatabase!!.orderByChild("club_id").equalTo(mIdClub)
            queryBattle.addValueEventListener(mValueEventListener!!)
        }
    }


    override fun onResume() {
        super.onResume()
        mIsClickMeNow = false
        getDataFirebase()
    }


    override fun onPause() {
        mDatabase!!.removeEventListener(mValueEventListener!!)
        super.onPause()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.btPlayNewGround -> if (!mIsClickMeNow) {
                if (mSizeListCourse > 0) {
                    if (mSizeListCourse == 1) {
                        DatabaseService.getInstance().removeDataRoundGolf(mIdClub!!, OnComplete {
                            EasyGolfNavigation1.playGolfDirection(this,
                                    PlayGolfBundle1(false,null,mIdClub!!,mIdCourse!!,mExitPlayGameBattle))
                        },true)
                        mIsClickMeNow = true

                    } else {
                        mSelectCourse = SELECT_COURSE.PLAY_ROUND
                        showSelectCourse()

                    }
                }
            }
            R.id.actionPhone -> {}
//                Dexter.withActivity(this).withPermission(Manifest.permission.CALL_PHONE)
//                    .withListener(object : PermissionListener {
//                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                            val callIntent = Intent(Intent.ACTION_CALL)
//                            callIntent.data = Uri.parse("tel:" + txtPhone!!.text.toString())
//                            startActivity(callIntent)
//                        }
//
//                        override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                            if (response.isPermanentlyDenied) {
//                                val dataExplanation = getString(R.string.explanation, response.permissionName)
//
//                                Snackbar.make(mViewRoot!!, dataExplanation, Snackbar.LENGTH_INDEFINITE)
//                                        .setAction("ok") {
//
//                                            val intent = Intent()
//                                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                            val uri = Uri.fromParts("package", packageName, null)
//                                            intent.data = uri
//                                            startActivity(intent)
//                                        }.show()
//                            }
//                        }
//
//                        override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
//                            token.continuePermissionRequest()
//                        }
//                    }).check()
            R.id.actionDirection -> {
                val gmmIntentUri = Uri.parse("google.navigation:q=" + mLocationCourse.latitude
                        + "," + mLocationCourse.longitude + "&avoid=tf" + "(" + mNameCourse + ")")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            R.id.actionScorecards -> if (mSizeListCourse > 0) {
                if (mSizeListCourse == 1) {
                    if (mListScorecardFirst != null) {
                        val bundleCourse = Bundle()
                        bundleCourse.putParcelableArrayList(Contains.EXTRA_SCORECARD, mListScorecardFirst)
                        startActivity(ScoreCardCourseActivity::class.java, bundleCourse)
                    }
                } else {
                    mSelectCourse = SELECT_COURSE.SCORECARD
                    showSelectCourse()
                }
            }
            R.id.actionInformation -> mSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)

            R.id.btPlayContinueGround -> if (!mIsClickMeNow) {
                if (TextUtils.isEmpty(mIdRoundBattle)) {
                    val dataRealm = DatabaseService.getInstance().getDataRoundGolf(mIdClub!!)
                    val roundMatch = dataRealm!!.roundMatch
                    if (roundMatch != null) {
                        mIdCourse = roundMatch.course.id
                        EasyGolfNavigation1.playGolfDirection(this,
                                PlayGolfBundle1(false, null, mIdClub!!, mIdCourse!!))

                    } else {
                        showMessage("course is null", null)
                    }
                } else {
                    DatabaseService.getInstance().removeDataRoundGolf(mIdClub!!, OnComplete {

                        EasyGolfNavigation1.playGolfDirection(this,
                                PlayGolfBundle1(true, mIdRoundBattle, mIdClub!!, mIdCourse!!))
                    }, false)
                }
                mIsClickMeNow = true
            }
        }

    }

    override fun loadData() {
        showLoading()

        registerResponse(ApiService.getInstance().golfService.getClubDetails(mIdClub),object: HandleResponse<Club>{
            override fun onSuccess(result: Club) {
                mCurrentClub = result

                mTxtNameCourse!!.text = result.name
                if (result.courses.size > 0) {
                    mListScorecardFirst = result.courses[0].scorecardClubs
                    mIdCourse = result.courses[0].id
                    mIdClub = result.id
                    mSizeListCourse = result.courses.size
                }
                mLocationCourse.latitude = result.coordinate.lat
                mLocationCourse.longitude = result.coordinate.lon
                mLayoutRating!!.setRatting(result.rating)
                txtLocation!!.text = result.address

                if (!TextUtils.isEmpty(result.phoneNumber)) {
                    txtPhone!!.text = result.phoneNumber
                    txtPhone!!.movementMethod = LinkMovementMethod.getInstance()
                    TextViewUtil.removeUnderLine(txtPhone!!.text as Spannable)
                }
                val viewLoading = findViewById<View>(R.id.viewLoadingData)
                viewLoading.animate()
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                viewLoading.visibility = View.GONE
                            }
                        })
                        .alpha(0f)
                        .setDuration(500)
                        .start()

                findViewById<View>(R.id.layoutContent).visibility = View.VISIBLE
            }

        })
    }


    private fun showSelectCourse() {

//        SelectCourseDialog1(this)
//                .setData(mCurrentClub!!.courses,object: EventOnCourse{
//                    override fun onSelect(item: CourseClub) {
//                        when (mSelectCourse) {
//                            SELECT_COURSE.PLAY_ROUND -> {
//                                DatabaseService.getInstance().removeDataRoundGolf(mIdClub!!, OnComplete {
//                                    mIdCourse = item.id
//                                    EasyGolfNavigation1.playGolfDirection(this@CourseDetailActivity,
//                                            PlayGolfBundle(false, null, mIdClub!!, mIdCourse!!,mExitPlayGameBattle))
//                                })
//                            }
//                            SELECT_COURSE.SCORECARD -> {
//                                val bundleCourse = Bundle()
//                                bundleCourse.putParcelableArrayList(Contains.EXTRA_SCORECARD, item.scorecardClubs)
//                                startActivity(ScoreCardCourseActivity::class.java, bundleCourse)
//                            }
//                        }
//                    }
//
//                }).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        finish()
    }


}
