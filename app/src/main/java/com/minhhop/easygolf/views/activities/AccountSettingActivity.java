package com.minhhop.easygolf.views.activities;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.minhhop.easygolf.R;
import com.minhhop.easygolf.base.WozBaseActivity;
import com.minhhop.easygolf.core.Utils;
import com.minhhop.easygolf.framework.models.entity.UserEntity;
import com.minhhop.easygolf.services.ApiService;
import com.minhhop.easygolf.services.DatabaseService;
import com.minhhop.easygolf.utils.AppUtil;
import com.minhhop.easygolf.widgets.appbar.AppBarStateChangeListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AccountSettingActivity extends WozBaseActivity {

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

    private ImageView mImgAvatar;


    private RadioButton mRadioFemale;
    private RadioButton mRadioMale;

    private TextView mValueNameCountry;
    private ImageView mImgFlag;
    private TextView mTxtFirstName;
    private TextView mTxtLastName;
    private TextView mTxtEmail;
    private TextView mTxtCellphone;
    private TextView mTxtBirthday;
    private SwitchCompat mSwitchPhoneNotification;
    private SwitchCompat mSwitchEmailNotification;

    private AppBarStateChangeListener mAppBarStateChangeListener;
    private TextView mTxtBack;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_account_setting;
    }

    @Override
    protected void initView() {

        mSpaceName = findViewById(R.id.spaceName);

        mSpaceAvatar = findViewById(R.id.spaceAvatar);
        mLayoutAvatar = findViewById(R.id.layoutAvatar);
        mTxtBack = findViewById(R.id.txtBack);
        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar_layout);
        mImgAvatar = findViewById(R.id.imgAvatar);
        mTxtFullName = findViewById(R.id.txtFullName);
        mTxtFirstName = findViewById(R.id.txtFirstName);
        mTxtLastName = findViewById(R.id.txtLastName);
        mTxtEmail = findViewById(R.id.txtEmail);
        mTxtCellphone = findViewById(R.id.txtCellPhone);
        mTxtBirthday = findViewById(R.id.txtBirthday);
        mValueNameCountry = findViewById(R.id.valueNameCountry);
        mImgFlag = findViewById(R.id.imgFlag);
        findViewById(R.id.actionEdit).setOnClickListener(this);
        mSwitchPhoneNotification = findViewById(R.id.switchPhoneNotification);
        mSwitchEmailNotification = findViewById(R.id.switchEmailNotification);


        mRadioFemale = findViewById(R.id.radioFemale);
        mRadioMale = findViewById(R.id.radioMale);
        mRadioFemale.setOnCheckedChangeListener((buttonView, isChecked) -> mRadioMale.setChecked(!isChecked));
        mRadioMale.setOnCheckedChangeListener((buttonView, isChecked) -> mRadioFemale.setChecked(!isChecked));

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
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.actionEdit){
            startActivity(AccountSettingEditActivity.class);
        }
    }

    @Override
    protected void loadData() {
        showLoading();
        UserEntity currentUserEntity = DatabaseService.Companion.getInstance().getCurrentUserEntity();
        if(currentUserEntity != null) {
            ApiService.Companion.getInstance().getUserService().profile()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        setValueUser(result);
                        DatabaseService.Companion.getInstance().saveUser(result, this::hideLoading);

                    }, throwable -> {
                        // show back button
                        hideLoading();
                        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    });

        }else {
            hideLoading();
            setValueUser(currentUserEntity);
        }
    }


    private void setValueUser(@NotNull UserEntity userEntity){
        if(!TextUtils.isEmpty(userEntity.getAvatar())) {
            Picasso.get().load(userEntity.getAvatar())
                    .placeholder(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(mImgAvatar);
        }else {
            Picasso.get().load(R.drawable.ic_icon_user_default).placeholder(R.drawable.ic_icon_user_default)
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
            }
        }


        mRadioFemale.setChecked(true);

        mTxtFullName.setText(userEntity.getFullName());

        mTxtFirstName.setText(userEntity.getFirstName());
        mTxtLastName.setText(userEntity.getLastName());
        mTxtEmail.setText(userEntity.getEmail());

        AppUtil.setFlagDrawableAssets(this, userEntity.getCountryEntity().getIso(),mImgFlag);
        mValueNameCountry.setText(getString(R.string.format_result_name_country,
                String.valueOf(userEntity.getCountryEntity().getPhoneCode()), userEntity.getCountryEntity().getNiceName()));
        mTxtCellphone.setText(userEntity.getPhoneNumber());
        if (!TextUtils.isEmpty(userEntity.getBirthday())) {
            mTxtBirthday.setText(AppUtil.fromISO8601UTC(userEntity.getBirthday(), "MMM - dd - yyyy"));
        }
        mSwitchPhoneNotification.setChecked(userEntity.isPhoneNotification());
        mSwitchEmailNotification.setChecked(userEntity.isEmailNotification());
    }
}
