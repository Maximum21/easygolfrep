package com.minhhop.easygolf.presentation.account

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.AccountUpdateBundle
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.custom.EasyGolfToolbar
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import java.util.*

class AccountSettingsActivity : EasyGolfActivity<AccountSettingsViewModel>(), View.OnClickListener {

    private var user: User? = null
    override val mViewModel: AccountSettingsViewModel
            by inject()

    private val EXPAND_AVATAR_SIZE_DP = 80f
    private val COLLAPSED_AVATAR_SIZE_DP = 40f

    private lateinit var mLayoutAvatar: View
    private lateinit var mSpaceAvatar: Space
    private val mAvatarPoint = FloatArray(2)
    private val mSpacePoint = FloatArray(2)


    private lateinit var mSpaceName: Space
    private val mNamePoint = FloatArray(2)
    private val mSpaceNamePoint = FloatArray(2)
    private lateinit var mTxtFullName: TextView

    private lateinit var mImgAvatar: ImageView


    private lateinit var mRadioFemale: RadioButton
    private lateinit var mRadioMale: RadioButton

    private lateinit var mValueNameCountry: TextView
    private lateinit var mImgFlag: ImageView
    private lateinit var editFirstName: TextView
    private lateinit var editLastName: TextView
    private lateinit var mTxtEmail: TextView
    private lateinit var mTxtCellphone: TextView
    private lateinit var mTxtBirthday: TextView
    private lateinit var mSwitchPhoneNotification: SwitchCompat
    private lateinit var mSwitchEmailNotification: SwitchCompat

    private var mAppBarStateChangeListener: AppBarStateChangeListener? = null
    private lateinit var mTxtBack: EasyGolfToolbar
    
    override fun setLayout(): Int {
        return R.layout.activity_account_setting
    }

    override fun initView() {
        mSpaceName = findViewById(R.id.spaceName)

        mSpaceAvatar = findViewById(R.id.spaceAvatar)
        mLayoutAvatar = findViewById(R.id.layoutAvatar)
        mTxtBack = findViewById(R.id.toolbarBack)
        val mAppBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        mImgAvatar = findViewById(R.id.imgAvatar)
        mTxtFullName = findViewById(R.id.txtFullName)
        editFirstName = findViewById(R.id.editFirstName)
        editLastName = findViewById(R.id.editLastName)
        mTxtEmail = findViewById(R.id.editEmail)
        mTxtCellphone = findViewById(R.id.editPhone)
        mTxtBirthday = findViewById(R.id.txtBirthday)
        mValueNameCountry = findViewById(R.id.valueNameCountry)
        mImgFlag = findViewById(R.id.imgFlag)
        findViewById<View>(R.id.actionEdit).setOnClickListener(this)
        mSwitchPhoneNotification = findViewById(R.id.switchPhoneNotification)
        mSwitchEmailNotification = findViewById(R.id.switchEmailNotification)


        mRadioFemale = findViewById(R.id.radioFemale)
        mRadioMale = findViewById(R.id.radioMale)
        mRadioFemale.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean -> mRadioMale.isChecked = !isChecked }
        mRadioMale.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean -> mRadioFemale.isChecked = !isChecked }

        mAppBarStateChangeListener = object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: STATE) {}
            override fun onOffsetChanged(state: STATE, offset: Float) {
                mTxtBack.alpha = 1 - offset
                Log.e("WOW", "offset: $offset")
                translationView(offset)
            }
        }

        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener)


        val observer = mAppBarLayout.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mAppBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                initLayoutPosition()
            }
        })
        mViewModel.userProfile.observe(this, Observer {
            user = it
            setValueUser(it)
        })
    }

    override fun loadData() {
        viewMask()
        val currentUserEntity = mViewModel.getUser()
        user = currentUserEntity
        if (currentUserEntity == null) {
            mViewModel.getUserProfile()
        } else {
            currentUserEntity?.let{
                setValueUser(currentUserEntity)
            }
            hideMask()
        }
    }

    private fun translationView(offset: Float) {
        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val xAvatarOffset = (mSpacePoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset
        val yAvatarOffset = (mSpacePoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset
        mLayoutAvatar.layoutParams.width = Math.round(newAvatarSize)
        mLayoutAvatar.layoutParams.height = Math.round(newAvatarSize)
        mLayoutAvatar.translationX = xAvatarOffset
        mLayoutAvatar.translationY = yAvatarOffset
        mLayoutAvatar.requestLayout()
        val xNameOffset = (mSpaceNamePoint[0] - mNamePoint[0]) * offset
        val yNameOffset = (mSpaceNamePoint[1] - mNamePoint[1]) * offset
        mTxtFullName.translationX = xNameOffset
        mTxtFullName.translationY = yNameOffset
    }

    private fun initLayoutPosition() {
        val offset = mAppBarStateChangeListener!!.getCurrentOffset()
        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val avatarPoint = IntArray(2)
        mLayoutAvatar.getLocationOnScreen(avatarPoint)
        mAvatarPoint[0] = avatarPoint[0] - mLayoutAvatar.translationX - (expandAvatarSize - newAvatarSize) / 2f
        mAvatarPoint[1] = avatarPoint[1] - mLayoutAvatar.translationY -
                (expandAvatarSize - newAvatarSize)
        val spacePoint = IntArray(2)
        mSpaceAvatar.getLocationOnScreen(spacePoint)
        mSpacePoint[0] = spacePoint[0].toFloat()
        mSpacePoint[1] = spacePoint[1].toFloat()
        val namePoint = IntArray(2)
        mTxtFullName.getLocationOnScreen(namePoint)
        mNamePoint[0] = namePoint[0] - mTxtFullName.translationX - (expandAvatarSize - newAvatarSize) / 2f
        mNamePoint[1] = namePoint[1] - mTxtFullName.translationY -
                (expandAvatarSize - newAvatarSize)
        val spaceNamePoint = IntArray(2)
        mSpaceName.getLocationOnScreen(spaceNamePoint)
        mSpaceNamePoint[0] = spaceNamePoint[0].toFloat()
        mSpaceNamePoint[1] = spaceNamePoint[1].toFloat()
    }
    
    override fun onClick(v: View?) {
        if (v!!.id == R.id.actionEdit) {
            EasyGolfNavigation.accountUpdateDirection(this, AccountUpdateBundle(Gson().toJson(user),true))
//            startActivity(Intent(this,AccountSettingEditActivity::class.java))
        }
    }

    private fun setValueUser(userEntity: User) {
        if (!TextUtils.isEmpty(userEntity.avatar)) {
            Picasso.get().load(userEntity.avatar)
                    .placeholder(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar)
        } else {
            Picasso.get().load(R.drawable.ic_icon_user_default).placeholder(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar)
        }
        if (userEntity.gender == null) {
            mRadioMale.isChecked = true
            mRadioFemale.isChecked = false
        } else {
            if (userEntity.gender.equals("Male", ignoreCase = true)) {
                mRadioMale.isChecked = true
                mRadioFemale.isChecked = false
            } else {
                mRadioMale.isChecked = false
            }
        }
        mRadioFemale.isChecked = true
        mTxtFullName.text = userEntity.fullName
        editFirstName.text = userEntity.first_name
        editLastName.text = userEntity.last_name
        mTxtEmail.text = userEntity.email
        userEntity?.country?.let{
            it.iso?.let{iso->
                AppUtil.setFlagDrawableAssets(this, iso, mImgFlag)
            }
            mValueNameCountry.text = getString(R.string.format_result_name_country, it.phone_code.toString(), it.nice_name)
        }
        mTxtCellphone.text = userEntity.phone_number
        if (!TextUtils.isEmpty(userEntity.birthday)) {
            mTxtBirthday.text = AppUtil.fromISO8601UTC(userEntity.birthday, "MMM - dd - yyyy")
        }
        mSwitchPhoneNotification.isChecked = userEntity.phone_notification!!
        mSwitchEmailNotification.isChecked = userEntity.email_notification!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Contains.UPDATE_REQEUST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let{
                user = Gson().fromJson(it.getStringExtra("data"), User::class.java)
                user?.let{userData->
                    setValueUser(userData)
                }
            }
        }
    }
}