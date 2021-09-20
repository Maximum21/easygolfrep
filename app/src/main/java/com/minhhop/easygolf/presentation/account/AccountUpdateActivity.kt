package com.minhhop.easygolf.presentation.account

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS
import com.minhhop.easygolf.framework.dialogs.OptionPickerImageDialog
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import com.minhhop.easygolf.presentation.custom.EasyGolfToolbar
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_update.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.math.roundToInt

class AccountUpdateActivity : EasyGolfActivity<AccountUpdateViewModel>(), View.OnClickListener {

    companion object {
        private const val EXPAND_AVATAR_SIZE_DP = 80f
        private const val COLLAPSED_AVATAR_SIZE_DP = 40f
    }

    override val mViewModel: AccountUpdateViewModel
            by inject()

    private var mCalendar: Calendar? = null
    private var mCountry: Country? = null

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
    override fun setLayout(): Int = R.layout.activity_account_update

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
        mSwitchPhoneNotification = findViewById(R.id.switchPhoneNotification)
        mSwitchEmailNotification = findViewById(R.id.switchEmailNotification)


        mRadioFemale = findViewById(R.id.radioFemale)
        mRadioMale = findViewById(R.id.radioMale)
        mRadioFemale.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean -> mRadioMale.isChecked = !isChecked }
        mRadioMale.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean -> mRadioFemale.isChecked = !isChecked }
        mCalendar = Calendar.getInstance()

        mAppBarStateChangeListener = object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: STATE) {}
            override fun onOffsetChanged(state: STATE, offset: Float) {
                mTxtBack.alpha = 1 - offset
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
        mViewModel.userLiveData.observe(this, Observer {
            mViewModel.updateUserProfileInLocal(it)
        })
        mViewModel.userProfileLiveData.observe(this, Observer {
            setValueUser(it)
        })
        btRegister.setOnClickListener(this)
        pickerCountry.setOnClickListener(this)
        btPickerBirthday.setOnClickListener(this)
        mImgAvatar.setOnClickListener(this)
        mViewModel.currentCountryLiveData.observe(this, Observer {
            mCountry = it
            AppUtil.setFlagDrawableAssets(this, it.iso, imgFlag)
            valueNameCountry.text = getString(R.string.format_result_name_country, it.nice_name, it.phone_code.toString())
        })
    }

    override fun commonError() {
        super.commonError()
        btRegister.setStatus(EasyGolfButton.Status.IDLE)
    }

    override fun loadData() {
        mViewModel.getUserProfile()
    }

    private fun translationView(offset: Float) {
        val newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this)
        val expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this)
        val xAvatarOffset = (mSpacePoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset
        val yAvatarOffset = (mSpacePoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset
        mLayoutAvatar.layoutParams.width = newAvatarSize.roundToInt()
        mLayoutAvatar.layoutParams.height = newAvatarSize.roundToInt()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Contains.REQUEST_CODE_COUNTRY && resultCode == Activity.RESULT_OK) {
            EasyGolfNavigation.countryBundle(data)?.toCountry()?.let { countrySelected ->
                mViewModel.setCountry(countrySelected)
            }
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
        userEntity.country?.let {
            AppUtil.setFlagDrawableAssets(this, it.iso, mImgFlag)
            mValueNameCountry.text = getString(R.string.format_result_name_country, it.phone_code.toString(), it.nice_name)
        }
        mTxtCellphone.text = userEntity.phone_number
        if (!TextUtils.isEmpty(userEntity.birthday)) {
            mTxtBirthday.text = AppUtil.fromISO8601UTC(userEntity.birthday, "MMM - dd - yyyy")
        }
        mSwitchPhoneNotification.isChecked = userEntity.phone_notification ?: false
        mSwitchEmailNotification.isChecked = userEntity.email_notification ?: false
    }

    private fun actionUpdate(firstName: String, lastName: String, email: String?,
                             countryCode: String, phoneNumber: String?, birthday: String?,
                             gender: String?, phoneNotification: Boolean, emailNotification: Boolean) {
        viewMask()
        mViewModel.updateProfileUser(firstName, lastName, email, countryCode, phoneNumber, birthday, gender, phoneNotification, emailNotification)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iconLogo -> {
                OptionPickerImageDialog(this).show()
            }
            R.id.btPickerBirthday -> {
                DatePickerDialog(this,
                        OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                            mCalendar!![year, month] = dayOfMonth
                            txtBirthday.text = AppUtil.formatDate(mCalendar!!.time, "MM - dd - yyyy")
                        }, mCalendar!![Calendar.YEAR], mCalendar!![Calendar.MONTH], mCalendar!![Calendar.DAY_OF_MONTH]).show()
            }
            R.id.pickerCountry -> {
                EasyGolfNavigation.countryDirection(this)
            }
            R.id.btRegister -> {
                mCountry?.iso?.let { countryIso ->
                    if (AppUtil.isValidEmail(editEmail.text.toString().trim { it <= ' ' })) {
                        val gender = if (radioMale.isChecked) "Male" else "Female"
                        actionUpdate(
                                editFirstName.text.toString(),
                                editLastName.text.toString(),
                                editEmail.text.toString().trim { it <= ' ' },
                                countryIso,
                                editPhone.text.toString(),
                                if (mCalendar == null) null else AppUtil.formatDateToIS08601UTC(mCalendar!!.time),
                                gender, switchPhoneNotification.isChecked, switchEmailNotification.isChecked

                        )
                    } else {
                        AlertDialogIOS(this, getString(R.string.error_email), null).show()
                    }
                }
            }

        }
    }
}