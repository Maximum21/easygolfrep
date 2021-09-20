package com.minhhop.easygolf.views.activities;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhhop.core.domain.Country;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.core.Utils;
import com.minhhop.easygolf.framework.dialogs.AlertDialogIOS;
import com.minhhop.easygolf.framework.dialogs.OptionPickerImageDialog;
import com.minhhop.easygolf.framework.models.UpdateUser;
import com.minhhop.easygolf.framework.models.entity.UserEntity;
import com.minhhop.easygolf.framework.models.UserFirebase;
import com.minhhop.easygolf.presentation.country.CountryActivity;
import com.minhhop.easygolf.services.ApiService;
import com.minhhop.easygolf.services.DatabaseService;
import com.minhhop.easygolf.utils.AppUtil;
import com.minhhop.easygolf.framework.common.Contains;
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class AccountSettingEditActivity extends WozBaseActivity {
    private static final float EXPAND_AVATAR_SIZE_DP = 80f;
    private static final float COLLAPSED_AVATAR_SIZE_DP = 40f;

    private View mLayoutAvatar;
    private Space mSpaceAvatar;
    private float[] mAvatarPoint = new float[2];
    private float[] mSpacePoint = new float[2];


    private Space mSpaceName;
    private float[] mNamePoint = new float[2];
    private float[] mSpaceNamePoint = new float[2];
    private TextView mTxtFullName;



    private static final String CROPPED_IMAGE_NAME = "CROPPED_IMAGE_NAME";
    private ImageView mImgAvatar;
    private EditText mTxtFirstName;
    private EditText mTxtLastName;
    private EditText mTxtEmail;
    private EditText mTxtCellphone;
    private TextView mTxtBirthday;


    private RadioButton mRadioFemale;
    private RadioButton mRadioMale;

    private File mFile;

    private Calendar mCalendar;
    private String mValueIOS;
    private TextView mValueNameCountry;
    private ImageView mImgFlag;
    private SwitchCompat mSwitchPhoneNotification;
    private SwitchCompat mSwitchEmailNotification;

    private AppBarStateChangeListener mAppBarStateChangeListener;
    private TextView mTxtBack;

    private DatabaseReference mFBProfileUser;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_account_setting_edit;
    }

    @Override
    protected void initView() {
        mSpaceName = findViewById(R.id.spaceName);
        mSpaceAvatar = findViewById(R.id.spaceAvatar);
        mLayoutAvatar = findViewById(R.id.layoutAvatar);
        mTxtBack = findViewById(R.id.txtBack);
        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar_layout);

        mImgAvatar = findViewById(R.id.imgAvatar);
        mImgAvatar.setOnClickListener(this);
        mTxtFullName = findViewById(R.id.txtFullName);
        mTxtFirstName = findViewById(R.id.txtFirstName);
        mTxtLastName = findViewById(R.id.txtLastName);
        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtCellphone = findViewById(R.id.txtCellPhone);
        mTxtBirthday = findViewById(R.id.txtBirthday);
        findViewById(R.id.btPickerBirthday).setOnClickListener(this);
        findViewById(R.id.pickerCountry).setOnClickListener(this);
        mRadioFemale = findViewById(R.id.radioFemale);
        mRadioMale = findViewById(R.id.radioMale);
        findViewById(R.id.actionConfirmEdit).setOnClickListener(this);
        mRadioFemale.setOnCheckedChangeListener((buttonView, isChecked) -> mRadioMale.setChecked(!isChecked));

        mRadioMale.setOnCheckedChangeListener((buttonView, isChecked) -> mRadioFemale.setChecked(!isChecked));

        mValueNameCountry = findViewById(R.id.valueNameCountry);
        Country e = DatabaseService.Companion.getInstance().defaultCountry();
        mValueIOS = e.getIso();
        mValueNameCountry.setText(getString(R.string.format_result_name_country,
                String.valueOf(e.getPhone_code()),e.getNice_name()));


        mImgFlag = findViewById(R.id.imgFlag);
        AppUtil.setFlagDrawableAssets(this,mValueIOS,mImgFlag);
        mSwitchPhoneNotification = findViewById(R.id.switchPhoneNotification);
        mSwitchEmailNotification = findViewById(R.id.switchEmailNotification);

        mAppBarStateChangeListener = new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(@NotNull AppBarLayout appBarLayout, @NotNull STATE state) {

            }

            @Override
            public void onOffsetChanged(@NotNull STATE state, float offset) {
                mTxtBack.setAlpha(1-offset);
                Log.e("WOW","offset: " + offset);
                translationView(offset);
            }
        };

        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener);


        mAppBarLayout.addOnOffsetChangedListener(mAppBarStateChangeListener);


        ViewTreeObserver observer =  mAppBarLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAppBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initLayoutPosition();
            }
        });
    }


    private void translationView(float offset){
        float newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this);
        float expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this);

        float xAvatarOffset = (mSpacePoint[0] - mAvatarPoint[0] - (expandAvatarSize - newAvatarSize) / 2f) * offset;
        float yAvatarOffset = (mSpacePoint[1] - mAvatarPoint[1] - (expandAvatarSize - newAvatarSize)) * offset;

        mLayoutAvatar.getLayoutParams().width = Math.round(newAvatarSize);
        mLayoutAvatar.getLayoutParams().height = Math.round(newAvatarSize);
        mLayoutAvatar.setTranslationX(xAvatarOffset);
        mLayoutAvatar.setTranslationY(yAvatarOffset);
        mLayoutAvatar.requestLayout();



        float xNameOffset = (mSpaceNamePoint[0] - mNamePoint[0] ) * offset;
        float yNameOffset = (mSpaceNamePoint[1] - mNamePoint[1]) * offset;

        mTxtFullName.setTranslationX(xNameOffset);
        mTxtFullName.setTranslationY(yNameOffset);


    }

    private void initLayoutPosition(){
        float offset = mAppBarStateChangeListener.getCurrentOffset();
        float newAvatarSize = Utils.convertDpToPixel(
                EXPAND_AVATAR_SIZE_DP - (EXPAND_AVATAR_SIZE_DP - COLLAPSED_AVATAR_SIZE_DP) * offset,
                this);
        float expandAvatarSize = Utils.convertDpToPixel(EXPAND_AVATAR_SIZE_DP, this);
        int[] avatarPoint = new int[2];
        mLayoutAvatar.getLocationOnScreen(avatarPoint);
        mAvatarPoint[0] = avatarPoint[0]- mLayoutAvatar.getTranslationX() -
                (expandAvatarSize - newAvatarSize) / 2f;

        mAvatarPoint[1] = avatarPoint[1] - mLayoutAvatar.getTranslationY() -
                (expandAvatarSize - newAvatarSize);

        int[] spacePoint = new int[2];
        mSpaceAvatar.getLocationOnScreen(spacePoint);
        mSpacePoint[0] = spacePoint[0];
        mSpacePoint[1] = spacePoint[1];


        int[] namePoint = new int[2];
        mTxtFullName.getLocationOnScreen(namePoint);
        mNamePoint[0] = namePoint[0]- mTxtFullName.getTranslationX()-
                (expandAvatarSize - newAvatarSize) / 2f;

        mNamePoint[1] = namePoint[1] - mTxtFullName.getTranslationY() -
                (expandAvatarSize - newAvatarSize);
        int[] spaceNamePoint = new int[2];
        mSpaceName.getLocationOnScreen(spaceNamePoint);
        mSpaceNamePoint[0] = spaceNamePoint[0];
        mSpaceNamePoint[1] = spaceNamePoint[1];
    }


    @Override
    protected void loadData() {
        UserEntity currentUserEntity = Objects.requireNonNull(DatabaseService.Companion.getInstance().getCurrentUserEntity());
        mFBProfileUser = FirebaseDatabase.getInstance().getReference("users").child(currentUserEntity.getId())
                .child("profile");

        setValueUser(currentUserEntity);
    }


    private void setValueUser(UserEntity userEntity){
        if(!TextUtils.isEmpty(userEntity.getAvatar())) {
            Picasso.get().load(userEntity.getAvatar())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar);
        }else {
            Picasso.get().load(R.drawable.ic_icon_user_default)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar);
        }

        if(userEntity.getGender() == null){
            mRadioMale.setChecked(true);
            mRadioFemale.setChecked(false);
        }else {
            if(userEntity.getGender().equalsIgnoreCase("Male")){
                mRadioMale.setChecked(true);
                mRadioFemale.setChecked(false);
            }else {
                mRadioMale.setChecked(false);
                mRadioFemale.setChecked(true);
            }
        }



        mTxtFullName.setText(userEntity.getFullName());

        mTxtFirstName.setText(userEntity.getFirstName());
        mTxtLastName.setText(userEntity.getLastName());
        mTxtEmail.setText(userEntity.getEmail());
        mTxtCellphone.setText(userEntity.getPhoneNumber());
        mCalendar = Calendar.getInstance();
        if(!TextUtils.isEmpty(userEntity.getBirthday())) {

            mCalendar.setTime(AppUtil.fromISO8601UTCToDate(userEntity.getBirthday()));
            mTxtBirthday.setText(AppUtil.fromISO8601UTC(userEntity.getBirthday(), "MM - dd - yyyy"));
        }
        mSwitchPhoneNotification.setChecked(userEntity.isPhoneNotification());
        mSwitchEmailNotification.setChecked(userEntity.isEmailNotification());
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.imgAvatar:
                new OptionPickerImageDialog(this).show();
                break;

            case R.id.btPickerBirthday:

                new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                    mCalendar.set(year,month,dayOfMonth);
                    mTxtBirthday.setText(AppUtil.formatDate(mCalendar.getTime(),"MM - dd - yyyy"));
                },mCalendar.get(Calendar.YEAR),mCalendar.get(Calendar.MONTH),mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.pickerCountry:
                startActivityForResult(CountryActivity.class, Contains.REQUEST_CODE_COUNTRY);
                break;

            case R.id.actionConfirmEdit:
                if(AppUtil.isValidEmail(mTxtEmail.getText().toString().trim())) {
                    String gender = mRadioMale.isChecked() ? "Male" : "Female";
                    actionUpdate(new UpdateUser(mTxtEmail.getText().toString().trim(), mTxtFirstName.getText().toString(), mTxtLastName.getText().toString(),
                            mCalendar == null ? null : AppUtil.formatDateToIS08601UTC(mCalendar.getTime()), mTxtCellphone.getText().toString(), gender,
                            mValueIOS, mSwitchPhoneNotification.isChecked(), mSwitchEmailNotification.isChecked()));
                }else {
                    new AlertDialogIOS(this, getString(R.string.error_email),null).show();
                }
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            if(requestCode == Contains.REQUEST_CODE_PHOTO_GALLERY){
//                if(data != null) {
//                    if(data.getData() != null) {
//                        UCrop uCrop = UCrop.of(data.getData(),
//                                Uri.fromFile(new File(getCacheDir(), CROPPED_IMAGE_NAME)));
//                        uCrop = advancedConfig(uCrop);
//                        uCrop.start(AccountSettingEditActivity.this);
//                    }
//
//                }
//            }
//        }
//
//        if(requestCode == UCrop.REQUEST_CROP){
//            if(data != null) {
//                Uri resultUri = UCrop.getOutput(data);
//                if(resultUri != null) {
//                    mFile = new File(resultUri.getPath());
//                    uploadAvatar();
//                }
//            }
//        }
//
//        if(requestCode == Contains.REQUEST_CODE_CAMERA){
//            if(resultCode == RESULT_OK) {
//                mFile = AppUtil.mFile;
//                UCrop uCrop = UCrop.of(Uri.fromFile(mFile),
//                        Uri.fromFile(new File(getCacheDir(), CROPPED_IMAGE_NAME)));
//                uCrop = advancedConfig(uCrop);
//                uCrop.start(AccountSettingEditActivity.this);
//            }
//        }
//
//        if(requestCode == Contains.REQUEST_CODE_COUNTRY && data != null){
//            String valueName = data.getStringExtra(Contains.EXTRA_NAME_COUNTRY);
//            mValueIOS = data.getStringExtra(Contains.EXTRA_ISO_COUNTRY);
//            mValueNameCountry.setText(valueName);
//            AppUtil.setFlagDrawableAssets(this,mValueIOS,mImgFlag);
//
//        }
//    }
//
//    private UCrop advancedConfig(@NonNull UCrop uCrop) {
//        UCrop.Options options = new UCrop.Options();
//        options.setFreeStyleCropEnabled(true);
//        return uCrop.withOptions(options);
//    }
    private void uploadAvatar(){
        showLoading();

//        mFile = AppUtil.resizeImageFileWithGlide(this,mFile);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), mFile);

        MultipartBody.Part fileAvatar=
                MultipartBody.Part.createFormData("avatar", mFile.getName(), requestFile);



        registerResponse(ApiService.Companion.getInstance().getUserService().updateAvatar(fileAvatar), result -> {
            if(!TextUtils.isEmpty(result.getAvatar())) {
                Picasso.get().load(result.getAvatar())
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgAvatar);
            }else {
                Picasso.get().load(R.drawable.ic_icon_user_default)
                        .memoryPolicy(MemoryPolicy.NO_CACHE )
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgAvatar);
            }
            UserFirebase updateUser = new UserFirebase(result.getFirstName(),result.getLastName(),result.getAvatar(),true);
            mFBProfileUser.setValue(updateUser);

            hideLoading();
        });
    }


    private void actionUpdate(UpdateUser user){
        showLoading();

        String[] languageArray = Locale.getDefault().toLanguageTag().split("-");
        String language = "en";
        if(languageArray.length > 0){
            language = languageArray[0];
        }
        user.setLanguage(language);

        registerResponse(ApiService.Companion.getInstance().getUserService().updateProfile(user), result -> {
            UserFirebase updateUser = new UserFirebase(result.getFirstName(),result.getLastName(),result.getAvatar(),true);
            mFBProfileUser.setValue(updateUser);
            finish();
        });

    }

}
