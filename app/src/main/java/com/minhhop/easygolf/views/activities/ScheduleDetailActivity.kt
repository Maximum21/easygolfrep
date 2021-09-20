package com.minhhop.easygolf.views.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.AppBarLayout
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ListMemberAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.ConfirmDialog
import com.minhhop.easygolf.framework.dialogs.EditNameGroupDialog
import com.minhhop.easygolf.framework.dialogs.OptionPickerImageDialog
import com.minhhop.easygolf.framework.models.Club
import com.minhhop.easygolf.framework.models.CourseClub
import com.minhhop.easygolf.framework.models.Participant
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.listeners.EventSaveNameGroup
import com.minhhop.easygolf.presentation.golf.PlayGolfActivityOld
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener
import com.minhhop.easygolf.widgets.course.EventOnCourse
import com.minhhop.easygolf.widgets.course.SelectCourseView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class ScheduleDetailActivity : WozBaseActivity() {

    companion object{
        private const val EXPAND_AVATAR_SIZE_DP = 44f
        private const val COLLAPSED_AVATAR_SIZE_DP = 33f
        private const val CROPPED_IMAGE_NAME = "CROPPED_IMAGE_NAME"
    }

    /**
     * Start zone UIView
     * */
    private lateinit var mAppBarLayout: AppBarLayout
    private lateinit var mAvatarImageView:View
    private lateinit var mTxtMakePhone:View
    private lateinit var mSelectCourseView: SelectCourseView

    /**
     * Start view for direction add friend
     * */
    private lateinit var mSpaceAddFriend: Space
    private lateinit var mActionAddFriend:View

    private val mAvatarPoint = FloatArray(2)
    private val mSpaceAvatarPoint = FloatArray(2)

    /**
     * End view for direction add friend
     * */
    private lateinit var mTxtAddFriend:TextView

    private lateinit var mTxtName:TextView
    private lateinit var mTxtLocation:TextView
    private lateinit var mTxtDate:TextView

    private lateinit var mActionReject:View
    private lateinit var mActionAccept:View
    private lateinit var mViewStage:View
    private lateinit var mIconUploadImage:View
    private lateinit var mImgCourse:ImageView
    private lateinit var mBtPlayNewGround:Button
    /**
     * End zone UIView
     * */
    private var mIdTOURNAMENT:Int = 0
    private var mIsStageEdit = false

    private lateinit var mAppBarStateChangeListener: AppBarStateChangeListener

    private lateinit var mListMemberAdapter:ListMemberAdapter

    private var mHolderDateTime = Calendar.getInstance()
    private var mModel:Participant? = null

    interface CallbackInline{
        fun callback(result:Tournament)
    }

    override fun setLayoutView(): Int = R.layout.activity_detail_schedule

    override fun setMenuRes(): Int = R.menu.menu_schedule_detail

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            mModel?.let { safeModel->
                this.getItem(0).isEnabled = safeModel.owner
                this.getItem(1).setEnabled(safeModel.owner)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when(this.itemId){
                R.id.actionDelete->{
                    ConfirmDialog(this@ScheduleDetailActivity)
                            .setContent(getString(R.string.request_delete_tournament))
                            .setOnConfirm {
                                deleteTournament()
                            }.show()
                }
                R.id.actionEdit-> {
                    if (!mIsStageEdit) {
                        mBtPlayNewGround.text = getString(R.string.done)
                        mIconUploadImage.visibility = View.VISIBLE
                        mIsStageEdit = true
                        mListMemberAdapter.hideOrshowMark()
                    }
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTournament(){
        mModel?.let {safeModel->
            showLoading()
            registerResponse(ApiService.getInstance().golfService.deleteTournament(safeModel.tournament.id),
                    object : HandleResponse<Any>{
                        override fun onSuccess(result: Any) {
                            hideLoading()
                            this@ScheduleDetailActivity.showMessage(getString(R.string.delete_success),
                                    DialogInterface.OnCancelListener { onBackPressed() })
                        }

                    })
        }
    }

    override fun initView() {
        mIdTOURNAMENT = intent.getIntExtra(Contains.EXTRA_ID_TOURNAMENT,0)


        val viewRoot = findViewById<ViewGroup>(R.id.viewRoot)
        mSelectCourseView = SelectCourseView(this)
        viewRoot.addView(mSelectCourseView)

        mListMemberAdapter = ListMemberAdapter(this,false,object : ListMemberAdapter.MemberClickListener{
            override fun onRemoveItem(id: Int,index:Int) {
                showLoading()
                registerResponse(ApiService.getInstance().golfService.removeFriendTournament(mIdTOURNAMENT,id),
                        object : HandleResponse<Tournament>{
                            override fun onSuccess(result: Tournament) {
                                hideLoading()
                                mListMemberAdapter.removeItem(index)
                            }

                        })
            }

        })
        val listMember = findViewById<RecyclerView>(R.id.listMember)
        listMember.layoutManager = LinearLayoutManager(this)
        listMember.adapter = mListMemberAdapter

        mSpaceAddFriend = findViewById(R.id.spaceAddFriend)

        mTxtMakePhone = findViewById(R.id.txtMakePhone)
        mAppBarLayout = findViewById(R.id.appBarLayout)

        mTxtAddFriend = findViewById(R.id.txtAddFriend)

        mAvatarImageView = findViewById(R.id.imageView_avatar)
        mAppBarStateChangeListener = object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: STATE) {

            }

            override fun onOffsetChanged(state: STATE, offset: Float) {
                translationView(offset)
            }

        }
        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener)
        val observer = mAppBarLayout.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mAppBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initLayoutPosition()
            }
        })

        val imgCourseLink = intent.getStringExtra(Contains.EXTRA_IMAGE_COURSE)
        mImgCourse = findViewById(R.id.imgCourse)


        Glide.with(this).load(imgCourseLink)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mImgCourse)

        supportPostponeEnterTransition()
        mImgCourse.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mImgCourse.viewTreeObserver.removeOnPreDrawListener(this)
                supportStartPostponedEnterTransition()
                return true
            }
        })

        mIconUploadImage = findViewById(R.id.iconUploadImage)
        mIconUploadImage.visibility = View.GONE
        mTxtName = findViewById(R.id.txtName)
        mTxtName.text = intent.getStringExtra(Contains.EXTRA_NAME_COURSE)

        mTxtDate = findViewById(R.id.txtDate)
        mTxtLocation = findViewById(R.id.txtLocation)

        findViewById<View>(R.id.actionPhone).setOnClickListener {
            mModel?.apply {
                val club = this.tournament.club
                val gmmIntentUri = Uri.parse("google.navigation:q=" + club.latitude
                        + "," + club.longitude + "&avoid=tf" + "(" + club.name + ")")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        mActionAddFriend = findViewById(R.id.actionAddFriend)
        mActionAddFriend.setOnClickListener {
            mModel?.let { safeModel->
                val bundle = Bundle()
                bundle.putString(Contains.EXTRA_BLACK_LIST,getListFriendByString(safeModel.tournament))
                startActivityForResult(AddPeopleChatActivity::class.java, Contains.REQUEST_CODE_ADD_MEMBER_CHAT,bundle)
            }
        }
        mActionAccept = findViewById(R.id.actionAccept)
        mActionReject = findViewById(R.id.actionReject)

        mViewStage = findViewById(R.id.viewStage)
        mViewStage.visibility = View.GONE

        mActionAccept.setOnClickListener {
            isActionAccept()
        }

        mActionReject.setOnClickListener {
            isActionAccept(false)
        }

        mBtPlayNewGround = findViewById(R.id.btPlayNewGround)
        mBtPlayNewGround.setOnClickListener {
            if (!mIsStageEdit) {
                mSelectCourseView.show()
            }else{
                mBtPlayNewGround.text = getString(R.string.play_new_round)
                mIsStageEdit = false
                mListMemberAdapter.hideOrshowMark()
                mIconUploadImage.visibility = View.GONE
            }
        }

        /**
         * Listener
         * */
        mTxtName.setOnClickListener {
            if(mIsStageEdit){
                EditNameGroupDialog(this@ScheduleDetailActivity,getString(R.string.edit_tournament_name),mTxtName.text.toString())
                        .setEvent(object : EventSaveNameGroup{
                            override fun onSave(value: String) {
                                changeNameTournament(value)
                            }

                        }).show()
            }
        }

        mTxtLocation.setOnClickListener {
            if(mIsStageEdit) {
                pickDataLocation()
            }
        }

        mTxtDate.setOnClickListener {
            if(mIsStageEdit){
                mModel?.let { safeModel ->
                    val dateModel = AppUtil.fromISO8601UTCToDate(safeModel.tournament.scheduleDate)
                    showDialogDate(dateModel)
                }
            }
        }

        mIconUploadImage.setOnClickListener {
            if(mIsStageEdit){
                openDialogOptionPickerImage()
            }
        }
    }
    private fun openDialogOptionPickerImage() {
        OptionPickerImageDialog(this).show()
    }

    private fun pickDataLocation() {
        val bundle = Bundle()
        bundle.putBoolean(Contains.EXTRA_FOR_RETURN_DATA,true)
        startActivityForResult(SearchActivity::class.java, Contains.REQUEST_CODE_COURSE,bundle)
    }

    private fun showDialogDate(targetDate:Date){

            mHolderDateTime.time = targetDate
            val datePicker = DatePickerDialog(this, {
                _, year, month, dayOfMonth ->
                mHolderDateTime.set(Calendar.YEAR,year)
                mHolderDateTime.set(Calendar.MONTH,month)
                mHolderDateTime.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                showDialogTime()

            },mHolderDateTime.get(Calendar.YEAR),mHolderDateTime.get(Calendar.MONTH),
                    mHolderDateTime.get(Calendar.DAY_OF_MONTH))

            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis + (24*60*60*1000)
            datePicker.show()
    }

    private fun showDialogTime(){
        TimePickerDialog(this, { _, hourOfDay, minute ->
            mHolderDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
            mHolderDateTime.set(Calendar.MINUTE,minute)

            updateScheduleDate()
        },mHolderDateTime.get(Calendar.HOUR_OF_DAY),mHolderDateTime.get(Calendar.MINUTE),true).show()
    }

    private fun updateScheduleDate(){
        updateInLine("schedule_date",AppUtil.formatDateToIS08601UTC(mHolderDateTime.time),object : CallbackInline{
            override fun callback(result: Tournament) {
                mTxtDate.text = AppUtil.formatDate(mHolderDateTime.time,"dd/MM/yyyy HH:mm")
            }

        })
    }

    private fun updateScheduleLocation(clubId:String){
        updateInLine("club_id",clubId,object : CallbackInline{
            override fun callback(result: Tournament) {
                mTxtLocation.text = result.club.name
            }

        })
    }

    private fun changeNameTournament(value:String){
       updateInLine("name",value,object : CallbackInline{
           override fun callback(result: Tournament) {
               mTxtName.text = result.name
           }

       })
    }


    private fun updateInLine(key:String,value:String,callbackInline:CallbackInline){
        val data = HashMap<String,String>()
        data[key] = value
        showLoading()
        registerResponse(ApiService.getInstance().golfService.updateTournament(mIdTOURNAMENT,data),
                object : HandleResponse<Tournament>{
                    override fun onSuccess(result: Tournament) {
                        callbackInline.callback(result)
                        Toast.makeText(this@ScheduleDetailActivity,"update success!!!",Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }

                })
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == Activity.RESULT_OK) {
//            if (requestCode == Contains.REQUEST_CODE_ADD_MEMBER_CHAT) {
//                data?.let { dataSafe ->
//
//                    val members = dataSafe.getStringExtra(Contains.EXTRA_IS_RETURN)
//                    val arrayMembers = members.split("-")
//
//                    val mFriends = HashMap<String,ArrayList<String>>()
//                    val mListFriendTournament = ArrayList<String>()
//                    for (item in arrayMembers) {
//                        val target = item.split("$")
//                        mListFriendTournament.add(target[0])
//                    }
//                    mFriends["friends"] = mListFriendTournament
//                    callApiUpdateFriend(mFriends)
//                }
//            }
//
//            if (requestCode == Contains.REQUEST_CODE_PHOTO_GALLERY) {
//                if (data != null) {
//                    if (data.data != null) {
//                        var uCrop = UCrop.of(data.data!!,
//                                Uri.fromFile(File(cacheDir, CROPPED_IMAGE_NAME)))
//                        uCrop = advancedConfig(uCrop)
//                        uCrop.start(this@ScheduleDetailActivity)
//                    }
//                }
//            }
//
//
//            if (requestCode == UCrop.REQUEST_CROP) {
//                if (data != null) {
//                    val resultUri = UCrop.getOutput(data)
//                    if (resultUri != null) {
//                        val mFile = File(resultUri.path!!)
//                        uploadImage(mFile)
//                    }
//                }
//            }
//
//            if (requestCode == Contains.REQUEST_CODE_CAMERA) {
//                if (resultCode == Activity.RESULT_OK) {
//                    val mFile = AppUtil.mFile
//                    var uCrop = UCrop.of(Uri.fromFile(mFile),
//                            Uri.fromFile(File(cacheDir, CROPPED_IMAGE_NAME)))
//                    uCrop = advancedConfig(uCrop)
//                    uCrop.start(this@ScheduleDetailActivity)
//                }
//            }
//
//            if(requestCode == Contains.REQUEST_CODE_COURSE){
//                data?.let { dataSafe->
//                    val mClubId = dataSafe.getStringExtra(Contains.EXTRA_ID_COURSE)
//                    updateScheduleLocation(mClubId)
//                }
//            }
//        }
//    }

//    private fun uploadImage(file:File) {
//        val mFile = AppUtil.resizeImageFileWithGlide(this, file)
//        mFile?.let { fileSafe->
//            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), fileSafe)
//
//            val fileImage = MultipartBody.Part.createFormData("photo", fileSafe.name, requestFile)
//
//            ApiService.getInstance().golfService.uploadPhotoTournament(mIdTOURNAMENT,fileImage)
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        Glide.with(this@ScheduleDetailActivity).load(file)
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
//                                .into(mImgCourse)
//                        hideLoading()
//                        Toast.makeText(this@ScheduleDetailActivity,"update photo success!!!",Toast.LENGTH_SHORT).show()
//                    }, { throwable ->
//                        // show back button
//                        hideLoading()
//                        AlertDialogIOS(this, throwable.localizedMessage) { }.show()
//                    })
//        }
//    }

//
//    private fun advancedConfig(uCrop: UCrop): UCrop {
//        val options = UCrop.Options()
//        options.setFreeStyleCropEnabled(true)
//        return uCrop.withOptions(options)
//    }

    private fun callApiUpdateFriend(data: HashMap<String,ArrayList<String>>){
        mModel?.let { safeModel->
            showLoading()
            registerResponse(ApiService.getInstance().golfService.addFriendTournament(safeModel.tournament.id,
                    data),object : HandleResponse<Tournament>{
                override fun onSuccess(result: Tournament) {
                    mListMemberAdapter.setDataList(result.participants)
                    hideLoading()
                }

            })
        }

    }

    private fun getListFriendByString(tournament: Tournament):String{
        val result = StringBuilder()
        val mListFriendTournament = tournament.participants
        val mSize = mListFriendTournament.size
        if(mSize > 0) {
            if (mSize in 1..1) {
                if(!mListFriendTournament[0].stage.equals("deleted",true) &&
                        !mListFriendTournament[0].stage.equals("rejected",true)) {
                    result.append(mListFriendTournament[0].id)
                }
            }else {
                for (index in 0 until mListFriendTournament.size) {
                    if (!mListFriendTournament[index].stage.equals("deleted", true) &&
                            !mListFriendTournament[index].stage.equals("rejected", true)) {

                        if (index < mSize - 1) {
                            result.append("${mListFriendTournament[index].id}-")
                        } else {
                            result.append("${mListFriendTournament[index].id}")
                        }
                    }
                }
            }
        }
        return result.toString()
    }

    private fun isActionAccept(isAccept:Boolean = true){
        mModel?.let { safeModel->
            showLoading()
            val type = if(isAccept) { "accepted"}else{"rejected"}
            registerResponse(ApiService.getInstance().golfService.isRejectAcceptTournament(safeModel.tournament.id,type),
                    object : HandleResponse<Participant>{
                        override fun onSuccess(result: Participant) {
                            hideLoading()
                            mViewStage.visibility = View.GONE
                            mListMemberAdapter.setDataList(result.tournament.participants)
                            this@ScheduleDetailActivity.showMessage("update success!!!",null)
                            if(!isAccept){
                                mBtPlayNewGround.visibility = View.GONE
                            }
                        }

                    })

        }


    }

    private fun initLayoutPosition() {

        val offset = mAppBarStateChangeListener.getCurrentOffset()
        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)

        val avatarPoint = IntArray(2)
        mActionAddFriend.getLocationOnScreen(avatarPoint)
        mAvatarPoint[0] = avatarPoint[0].toFloat() - mActionAddFriend.translationX -
                (expandAvatarSize - newAvatarSize) / 2f

        mAvatarPoint[1] = avatarPoint[1].toFloat() - mActionAddFriend.translationY -
                (expandAvatarSize - newAvatarSize)


        val spacePoint = IntArray(2)
        mSpaceAddFriend.getLocationOnScreen(spacePoint)
        mSpaceAvatarPoint[0] = spacePoint[0].toFloat()
        mSpaceAvatarPoint[1] = spacePoint[1].toFloat()

    }

    private fun translationView(offset:Float){
        mTxtAddFriend.alpha = 1-offset

        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val xAvatarOffset = (mSpaceAvatarPoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset
        val yAvatarOffset = (mSpaceAvatarPoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset

        mActionAddFriend.translationX = xAvatarOffset
        mActionAddFriend.translationY = yAvatarOffset
        mActionAddFriend.requestLayout()

    }

    override fun loadData() {

        registerResponse(ApiService.getInstance().golfService.getTournamentDetail(mIdTOURNAMENT),
                object : HandleResponse<Participant>{
            override fun onSuccess(result: Participant) {
                mModel = result
                mTxtLocation.text = result.tournament.club.name
                mTxtDate.text = AppUtil.fromISO8601UTC(result.tournament.scheduleDate,"dd/MM/yyyy HH:mm")
                mListMemberAdapter.setDataList(result.tournament.participants)

                if(!result.stage.equals("accepted",true)){
                    mViewStage.visibility = View.VISIBLE
                }
                if(result.owner){
                    mActionAddFriend.visibility = View.VISIBLE
                }else{
                    mActionAddFriend.visibility = View.INVISIBLE
                }
                loadDataClubDetail(result.tournament.club.id)
            }

        })
    }

    private fun loadDataClubDetail(clubId:String?){
        registerResponse(ApiService.getInstance().golfService.getClubDetails(clubId), object : HandleResponse<Club>{
            override fun onSuccess(result: Club) {
                mSelectCourseView.setData(result.courses,object :EventOnCourse{
                    override fun onSelect(item: CourseClub) {
                        mSelectCourseView.hide()
                        val bundle = Bundle()
                        bundle.putBoolean(Contains.EXTRA_EXIT_GAME_BATTLE, false)
                        bundle.putString(Contains.EXTRA_NAME_COURSE, item.name)
                        bundle.putString(Contains.EXTRA_ID_CLUB, clubId)
                        bundle.putString(Contains.EXTRA_ID_COURSE, item.id)
                        startActivity(PlayGolfActivityOld::class.java, bundle)
                    }

                })
            }

        })
    }
}