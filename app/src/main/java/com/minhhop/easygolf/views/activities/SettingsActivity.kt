package com.minhhop.easygolf.views.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.transition.Explode
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.framework.dialogs.OptionPickerImageDialog
import com.minhhop.easygolf.presentation.account.AccountSettingsActivity
import com.minhhop.easygolf.presentation.home.EasyGolfHomeViewModel
import com.minhhop.easygolf.presentation.notification.NotificationActivity
import com.minhhop.easygolf.presentation.password.ChangePasswordActivity
import com.minhhop.easygolf.presentation.policy.PolicyTermsActivity
import com.minhhop.easygolf.presentation.requestcourse.RequestCourseActivity
import com.minhhop.easygolf.presentation.round.RoundHistoryActivity
import com.minhhop.easygolf.presentation.rules.GolfRulesActivity
import kotlinx.android.synthetic.main.activity_settings_menu.*
import org.koin.android.ext.android.inject
import java.io.File

class SettingsActivity : EasyGolfActivity<EasyGolfHomeViewModel>(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private var darkMode: Boolean = false
    private lateinit var mViewRoot: View
    private var themeTag = 0

    private val mMaxPhotoGetFromGallery = 5
    private var mUrlFilePicture: String? = null
    private var mListPhotoGallery = ArrayList<File>()
    private var uriList: ArrayList<Uri> = ArrayList()
    override val mViewModel: EasyGolfHomeViewModel
            by inject()

    override fun setLayout(): Int {
        // inside your activity (if you did not enable transitions in your theme)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        // set an exit transition
        window.exitTransition = Explode()


//        EventBus.getDefault().register(this)
        return R.layout.activity_settings_menu
    }

    override fun initView() {
//        mViewRoot = findViewById(R.id.viewRoot)
        back_btn.setOnClickListener(this)
        settings_act_req_course_group.setOnClickListener(this)
        settings_act_round_history_group.setOnClickListener(this)
        settings_act_about_us_group.setOnClickListener(this)
        settings_act_account_settings_group.setOnClickListener(this)
        settings_act_change_password_group.setOnClickListener(this)
        settings_act_golf_rules_group.setOnClickListener(this)
        settings_act_mode_change_group.setOnClickListener(this)
        settings_act_notification_group.setOnClickListener(this)
        settings_act_privacy_policy_group.setOnClickListener(this)
        settings_act_mode_switch.setOnCheckedChangeListener(this)
        settings_act_upload_history_group.setOnClickListener(this)

        mViewModel.newPhotoInsertLiveData.observe(this, Observer {
            Toast.makeText(this,getString(R.string.hostory_uploaded_success), Toast.LENGTH_LONG).show()
        })
    }

    override fun loadData() {

        mViewModel.getProfileUserInLocal()?.let { user->
            golfer_name.text = user.fullName
            settings_act_user_profile_iv.loadImage(user.avatar,R.drawable.ic_icon_user_default)
        }
//        mViewModel?.getThemeTag()?.let{
//            themeTag = it
//            settings_act_mode_switch.isChecked = themeTag != 0
//        }
    }

    private fun uploadPhoto() {
        OptionPickerImageDialog(this)
                .addListener(object : OptionPickerImageDialog.OptionPickerImageListener {
                    override fun takePhoto() {
                        dispatchTakePictureCamera()
                    }

                    override fun getFromGallery() {
                        EasyGolfNavigation.pickPhotoGalleryDirection(this@SettingsActivity, Contains.REQUEST_CODE_PHOTO_GALLERY, mMaxPhotoGetFromGallery)
                    }
                })
                .show()
    }
    private fun dispatchTakePictureCamera() {
        try {
            Contains.fileDirClubPhoto(this)?.let {
                mViewModel.createImageFileHolder(it)?.let { photoFile ->
                    mUrlFilePicture = photoFile.absolutePath
                    EasyGolfNavigation.dispatchTakePictureCameraIntent(this@SettingsActivity, photoFile)
                }
            }
        } catch (e: Exception) {
            showCommonMessage(e.localizedMessage)
        }
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_btn ->{
                finish()
            }
            R.id.settings_act_about_us_group ->{
                startActivity(Intent(this,AboutActivity::class.java))
            }
            R.id.settings_act_upload_history_group ->{
                uploadPhoto()
            }
            R.id.settings_act_account_settings_group ->{
                startActivity(Intent(this,AccountSettingsActivity::class.java))
            }
            R.id.settings_act_change_password_group ->{
                startActivity(Intent(this,ChangePasswordActivity::class.java))
            }
            R.id.settings_act_golf_rules_group ->{
                startActivity(Intent(this,GolfRulesActivity::class.java))
            }
//            R.id.settings_act_mode_switch ->{
//                if(themeTag==1){
//                    themeTag=0
//                    setTheme(R.style.ThemeAppDark)
//                }else{
//                    themeTag=1
//                    setTheme(R.style.AppTheme)
//                }
//                mViewModel.setThemeTag(themeTag)
//            }
            R.id.settings_act_mode_change_group ->{
//                if(settings_act_mode_switch.isSelected){
//                    settings_act_mode_switch.isSelected = false
//                    darkMode = false;
//                }else{
//                    settings_act_mode_switch.isSelected = true
//                    darkMode = true;
//                }
            }
            R.id.settings_act_notification_group ->{
                startActivity(Intent(this,NotificationActivity::class.java))
            }
            R.id.settings_act_privacy_policy_group ->{
                startActivity(Intent(this,PolicyTermsActivity::class.java))
            }
            R.id.settings_act_req_course_group ->{
                startActivity(Intent(this,RequestCourseActivity::class.java))
            }
            R.id.settings_act_round_history_group ->{
                startActivity(Intent(this,RoundHistoryActivity::class.java))
            }

        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        if(!isChecked){
//            themeTag=1
//            AppUtils.mColorPrimaryStart = ColorRGBHolder(131, 193, 37)
//            AppUtils.mColorPrimaryEnd = ColorRGBHolder(37, 123, 17)
//
//            AppUtils.mColorBezierStart = ColorRGBHolder(84, 175, 71)
//            AppUtils.mColorBezierEnd = ColorRGBHolder(102, 179, 47)
//        }else{
//            themeTag=0
//            AppUtils.mColorPrimaryStart= ColorRGBHolder(0,0,0)
//            AppUtils.mColorPrimaryEnd= ColorRGBHolder(0,0,0)
//            AppUtils.mColorBezierEnd= ColorRGBHolder(0,0,0)
//            AppUtils.mColorBezierStart= ColorRGBHolder(0,0,0)
//        }
//        mViewModel.setThemeTag(themeTag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Contains.REQUEST_CODE_PHOTO_GALLERY -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    EasyGolfNavigation.getGalleryBundle(data)?.getResult()?.let { galleryMedias ->
                        if (galleryMedias.isNotEmpty()) {
                            try {

                                galleryMedias.forEach { media ->
                                    this.contentResolver?.openInputStream(Uri.parse(media.path))?.let { inputStream ->
                                        Contains.fileDirClubPhoto(this)?.let { fileSafe->
                                            mViewModel.createImageFileHolder(fileSafe,
                                                    inputStream)?.let { fileResult ->
                                                mUrlFilePicture = fileResult.absolutePath
                                                mViewModel.resizeFileImage(fileResult.absolutePath)
                                                mListPhotoGallery.add(fileResult)
                                            }
                                        }

                                    }
                                }
                                viewMask()
                                mViewModel.uploadPhotoFileForClub(mListPhotoGallery)

                            } catch (e: Exception) {
                                showCommonMessage(e.localizedMessage)
                            }
                        }
                    }
                }
            }
            Contains.REQUEST_CODE_CAMERA -> {
                mUrlFilePicture?.let { urlFilePicture ->
                    if (resultCode == Activity.RESULT_OK) {
                        viewMask()
                        mViewModel.resizeFileImage(urlFilePicture)
                            mViewModel.uploadPhotoFileForClub(listOf(File(urlFilePicture)))
                    } else {
                        mViewModel.deleteFileImage(urlFilePicture)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Contains.REQUEST_CODE_CAMERA_PERMISSION && permissions.contains(Manifest.permission.CAMERA)) {
            dispatchTakePictureCamera()
        }
    }

}