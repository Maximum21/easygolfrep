package com.minhhop.easygolf.views.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.OptionPickerImageDialog
import com.minhhop.easygolf.framework.models.FriendTournament
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.framework.models.TournamentEntry
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.widgets.FriendsCalendarView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ScheduleCreateActivity : WozBaseActivity() {
    companion object{
        private const val CROPPED_IMAGE_NAME = "CROPPED_IMAGE_NAME"
    }

    private lateinit var mPickerDate:TextView
    private lateinit var mPickerTime:TextView
    private lateinit var mIconUploadImage:ImageView
    private lateinit var mImgBannerSchedule:ImageView
    private lateinit var mPopupLocation:TextView
    private lateinit var mViewAddFriends:FriendsCalendarView
    private lateinit var mInputNameEvent:EditText

    private var mListFriendTournament = ArrayList<FriendTournament>()
    private var mCalendarDate:Calendar = Calendar.getInstance()
    private var mCalendarTime:Calendar = Calendar.getInstance()
    private var mFile: File? = null

    private var mDate:String? = null
    private var mTime:String? = null
    private var mClubId:String? = null
    private val mFriends = ArrayList<String>()

    private val mTargetCalendar = Calendar.getInstance()

    override fun setLayoutView(): Int = R.layout.activity_create_schedule

    override fun initView() {

        mIconUploadImage = findViewById(R.id.iconUploadImage)
        mImgBannerSchedule = findViewById(R.id.imgBannerSchedule)
        mInputNameEvent = findViewById(R.id.inputNameEvent)
        findViewById<View>(R.id.iconUploadImage).setOnClickListener {
            openDialogOptionPickerImage()
        }

        mPopupLocation = findViewById(R.id.popupLocation)
        mPopupLocation.setOnClickListener {
            pickDataLocation()
        }

        mPickerDate = findViewById(R.id.pickerDate)
        mPickerDate.setOnClickListener {
            showDialogDate()
        }

        mPickerTime = findViewById(R.id.pickerTime)
        mPickerTime.setOnClickListener {
            showDialogTime()
        }

        mViewAddFriends = findViewById(R.id.viewFriends)
        findViewById<View>(R.id.actionAddFriend).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Contains.EXTRA_BLACK_LIST,getListFriendByString())
            startActivityForResult(AddPeopleChatActivity::class.java, Contains.REQUEST_CODE_ADD_MEMBER_CHAT,bundle)
        }


        findViewById<View>(R.id.btAddEvent).setOnClickListener {
            checkValueCreateSchedule()
        }
    }

    private fun checkValueCreateSchedule(){
        if(mInputNameEvent.text.isNotEmpty()){
            if(mClubId != null){
                if(mDate != null){
                    if(mTime != null){
                        isPlayingThisClub()
                    }else{
                        showMessage(getString(R.string.error_event_time),null)
                    }
                }else{
                    showMessage(getString(R.string.error_event_date),null)
                }
            }else{
                showMessage(getString(R.string.error_event_location),null)
            }
        }else{
            showMessage(getString(R.string.error_event_name),null)
        }
    }

    private fun isPlayingThisClub() {
        val idUser = DatabaseService.getInstance().currentUserEntity!!.id
        FirebaseDatabase.getInstance().getReference("users").child(idUser)
                .child("battles").orderByChild("club_id").equalTo(mClubId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(dataBattle: DataSnapshot) {
                        if(dataBattle.value != null) {
                            showMessage(getString(R.string.club_is_playing_tournament),null)
                        }else{
                            callApiToCreateSchedule()
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {}

                })
    }

    private fun callApiToCreateSchedule(){
        showLoading()
        val target = TournamentEntry(mInputNameEvent.text.toString(),null,AppUtil.formatDateToIS08601UTC(mTargetCalendar.time),
                mFriends,mClubId!!)
        registerResponse(ApiService.getInstance().golfService.createTournament(target),object : HandleResponse<Tournament>{
            override fun onSuccess(result: Tournament) {


                if(mFile != null) {
                    uploadImage(result.id)
                }else {
                    showMessage("create success!!!", DialogInterface.OnCancelListener {
                        finish()
                    })
                    hideLoading()
                }
            }

        })
    }

    private fun getListFriendByString():String{
        val result = StringBuilder()
        val mSize = mListFriendTournament.size
        if(mSize > 0) {
            if (mSize in 1..1) {
                result.append(mListFriendTournament[0].id)
            }
            for (index in 0 until mListFriendTournament.size) {
                if (index < mSize - 1) {
                    result.append("${mListFriendTournament[index].id}-")
                } else {
                    result.append("${mListFriendTournament[index].id}")
                }
            }
        }
        return result.toString()
    }

    private fun pickDataLocation() {
        val bundle = Bundle()
        bundle.putBoolean(Contains.EXTRA_FOR_RETURN_DATA,true)
        startActivityForResult(SearchActivity::class.java, Contains.REQUEST_CODE_COURSE,bundle)
    }

    private fun showDialogTime() {
        val timeDialog = TimePickerDialog(this,{
            _, hourOfDay, minute ->
            mCalendarTime.set(mCalendarTime.get(Calendar.YEAR),mCalendarTime.get(Calendar.MONTH),mCalendarTime.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,minute)
            mPickerTime.text = AppUtil.formatDate(mCalendarTime.time, "HH:mm")

            mTargetCalendar.set(mTargetCalendar.get(Calendar.YEAR),mTargetCalendar.get(Calendar.MONTH),
                    mTargetCalendar.get(Calendar.DAY_OF_MONTH),hourOfDay,minute)
            mTime = mPickerTime.text.toString()
        },mCalendarTime.get(Calendar.HOUR_OF_DAY),mCalendarTime.get(Calendar.MINUTE),true)


        timeDialog.show()
    }

    private fun showDialogDate() {
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            mCalendarDate.set(year, month, dayOfMonth)

            mTargetCalendar.set(year,month,dayOfMonth)
            mPickerDate.text = AppUtil.formatDate(mCalendarDate.time, "dd - MM - yyyy")
            mDate = mPickerDate.text.toString()
        }, mCalendarDate.get(Calendar.YEAR), mCalendarDate.get(Calendar.MONTH), mCalendarDate.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() + (24*60*60*1000)
        datePickerDialog.show()
    }

    override fun loadData() {

    }

    private fun openDialogOptionPickerImage() {
        OptionPickerImageDialog(this).show()
    }

//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == Contains.REQUEST_CODE_PHOTO_GALLERY) {
//                if (data != null) {
//                    if (data.data != null) {
//                        var uCrop = UCrop.of(data.data!!,
//                                Uri.fromFile(File(cacheDir, CROPPED_IMAGE_NAME)))
//                        uCrop = advancedConfig(uCrop)
//                        uCrop.start(this@ScheduleCreateActivity)
//                    }
//
//                }
//            }
//
//
//            if (requestCode == UCrop.REQUEST_CROP) {
//                if (data != null) {
//                    val resultUri = UCrop.getOutput(data)
//                    if (resultUri != null) {
//                        mFile = File(resultUri.path!!)
//                        showImageToView()
//                    }
//                }
//            }
//
//            if (requestCode == Contains.REQUEST_CODE_CAMERA) {
//                if (resultCode == Activity.RESULT_OK) {
//                    mFile = AppUtil.mFile
//                    var uCrop = UCrop.of(Uri.fromFile(mFile),
//                            Uri.fromFile(File(cacheDir, CROPPED_IMAGE_NAME)))
//                    uCrop = advancedConfig(uCrop)
//                    uCrop.start(this@ScheduleCreateActivity)
//                }
//            }
//
//            if(requestCode == Contains.REQUEST_CODE_COURSE){
//                data?.let { dataSafe->
//                    mClubId = dataSafe.getStringExtra(Contains.EXTRA_ID_COURSE)
//                    mPopupLocation.text = dataSafe.getStringExtra(Contains.EXTRA_NAME_COURSE)
//                }
//            }
//
//            if(requestCode == Contains.REQUEST_CODE_ADD_MEMBER_CHAT){
//                data?.let { dataSafe ->
//
//                    val members = dataSafe.getStringExtra(Contains.EXTRA_IS_RETURN)
//                    val arrayMembers = members.split("-")
//
//                    for (item in arrayMembers) {
//                        val target = item.split("$")
//                        mFriends.add(target[0])
//                        mListFriendTournament.add(FriendTournament(target[0].toInt(),"pending",null,target[1]))
//                    }
//                   mViewAddFriends.setData(mListFriendTournament)
//                }
//            }
//        }
//    }

    private fun showImageToView(){
        mFile?.let { fileSafe ->
            Glide.with(this).load(fileSafe)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(mImgBannerSchedule)
        }
    }

    private fun uploadImage(id:Int) {
//        mFile = AppUtil.resizeImageFileWithGlide(this, mFile)
        mFile?.let { fileSafe->
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), fileSafe)

            val fileImage = MultipartBody.Part.createFormData("photo", fileSafe.name, requestFile)

            ApiService.getInstance().golfService.uploadPhotoTournament(id,fileImage)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ _ ->
                        showMessage("create success!!!", DialogInterface.OnCancelListener {
                            finish()
                        })
                        hideLoading()

                    }, { throwable ->
                        // show back button
                        hideLoading()
                        AlertDialogIOS(this, throwable.localizedMessage) { }.show()
                    })
        }
    }

//    private fun advancedConfig(uCrop: UCrop): UCrop {
//        val options = UCrop.Options()
//        options.setFreeStyleCropEnabled(true)
//        return uCrop.withOptions(options)
//    }

    override fun getTitleToolBar(): String? {
        return getString(R.string.back)
    }
}