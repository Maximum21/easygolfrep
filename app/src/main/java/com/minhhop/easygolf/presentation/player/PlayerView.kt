package com.minhhop.easygolf.presentation.player

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.GolfUtils
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView

class PlayerView : RelativeLayout {

    private var mAnimationShake: Animation? = null
    private lateinit var mMaskPlayer: MaterialCardView
    private lateinit var mImgAvatar: ImageView
    private lateinit var mTextNamePlayer: MaterialTextView

    private var mIconDelete: ImageView? = null
    private var mTextHandicap: MaterialTextView? = null

    private var mPlayerViewListener: PlayerViewListener? = null
    private var mIsPlayerView: Boolean = false
    private var mIsCanDelete: Boolean = true
    private var mIsEnableDelete: Boolean = false


    /**
     * show score at bottom
     * */
    private var mLayoutScoreSheet: FrameLayout? = null
    private var mTextScoreSheet: MaterialTextView? = null
    private var mImgScoreSheet: ImageView? = null

    private var mSmallMargin: Int = 0
    private var mSizeMaskPlayer: Int = 0

    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        mAnimationShake = AnimationUtils.loadAnimation(context, R.anim.shake_view)
        gravity = Gravity.CENTER
        mSmallMargin = context.resources.getDimension(R.dimen.small_margin).toInt()
        mSizeMaskPlayer = context.resources.getDimension(R.dimen.size_player).toInt()
        mMaskPlayer = MaterialCardView(context)
        mMaskPlayer.radius = mSizeMaskPlayer / 2f
        mMaskPlayer.elevation = 0f
        mMaskPlayer.id = View.generateViewId()
        mImgAvatar = ImageView(context)
        mImgAvatar.adjustViewBounds = true
        mImgAvatar.scaleType = ImageView.ScaleType.CENTER_CROP
        mMaskPlayer.addView(mImgAvatar)
        addView(mMaskPlayer, mSizeMaskPlayer, mSizeMaskPlayer)
        (mMaskPlayer.layoutParams as? LayoutParams)?.addRule(CENTER_HORIZONTAL)

        mTextNamePlayer = MaterialTextView(context)
        mTextNamePlayer.id = View.generateViewId()
        mTextNamePlayer.setTextColor(ContextCompat.getColor(context, R.color.textColorDark))
        TextViewCompat.setTextAppearance(mTextNamePlayer, R.style.normal)
        mTextNamePlayer.text = context.getString(R.string.app_name)
        mTextNamePlayer.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.small_font_size))
        addView(mTextNamePlayer)
        (mTextNamePlayer.layoutParams as? LayoutParams)?.let { nameLayout ->
            nameLayout.topMargin = mSmallMargin
            nameLayout.addRule(BELOW, mMaskPlayer.id)
            nameLayout.addRule(CENTER_HORIZONTAL)
        }
        if (mTextHandicap == null) {
            onAddHandicapView()
            enableDelete(false)
        }
        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PlayerView)
            mIsPlayerView = typedArray.getBoolean(R.styleable.PlayerView_isPlayer, false)
            setIsPlayer(mIsPlayerView)
            typedArray.recycle()
        }

        this.setOnClickListener {
            if (!mIsPlayerView) {
                mPlayerViewListener?.onAddPlayer()
            } else {
                if (mIsEnableDelete) {
                    mPlayerViewListener?.onDeleteView()
                }else{
                    mPlayerViewListener?.onSelected()
                }
            }
        }
        this.setOnLongClickListener {
            if (mIsPlayerView && mIsCanDelete) {
                enableDelete(true)
            }
            true
        }
    }

    fun setScoreBottomSheet(score: Int? = null,par: Int? = null) {
        setScore(score,par)
    }

    private fun createScoreBottomSheet() {
        if (mLayoutScoreSheet == null) {
            val sizeParentLayout = (mSizeMaskPlayer * 0.7).toInt()
            mLayoutScoreSheet = FrameLayout(context)

            mTextScoreSheet = MaterialTextView(context)
            TextViewCompat.setTextAppearance(mTextScoreSheet!!, R.style.bold)
            mTextScoreSheet?.gravity = Gravity.CENTER
            mTextScoreSheet?.setTextColor(ContextCompat.getColor(context, R.color.textColorDark))
            mTextScoreSheet?.setBackgroundResource(R.drawable.rectangle_double_point)
            mTextScoreSheet?.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_font_size))
            mLayoutScoreSheet?.addView(mTextScoreSheet)
            (mTextScoreSheet?.layoutParams as? FrameLayout.LayoutParams)?.gravity = Gravity.CENTER
            mTextScoreSheet?.visibility = View.GONE

            mImgScoreSheet = ImageView(context)
            mImgScoreSheet?.adjustViewBounds = true
            mImgScoreSheet?.setImageResource(R.drawable.ic_icon_score_blue)
            mLayoutScoreSheet?.addView(mImgScoreSheet)
            (mImgScoreSheet?.layoutParams as? FrameLayout.LayoutParams)?.setMargins(
                    mSmallMargin,
                    mSmallMargin / 2,
                    mSmallMargin,
                    mSmallMargin / 2
            )

            addView(mLayoutScoreSheet, sizeParentLayout, sizeParentLayout)
            (mLayoutScoreSheet?.layoutParams as? LayoutParams)?.let { nameLayout ->
                nameLayout.topMargin = mSmallMargin * 2
                nameLayout.addRule(BELOW, mTextNamePlayer.id)
                nameLayout.addRule(CENTER_HORIZONTAL)
            }
        } else {
            mLayoutScoreSheet?.visibility = View.VISIBLE
        }
    }

    private fun setScore(score:Int?,par:Int?){
        if(score != null){
            mTextScoreSheet?.text = score.toString()
            par?.let { parClear->
                mTextScoreSheet?.setBackgroundResource(
                        EasyGolfPickerScoreView.getResResource(score,parClear)
                )
            }
            mTextScoreSheet?.visibility = View.VISIBLE
            mImgScoreSheet?.visibility = View.GONE
        }else{
            mTextScoreSheet?.visibility = View.GONE
            mImgScoreSheet?.visibility = View.VISIBLE
        }
    }

    fun enableDelete(enable: Boolean) {
        if (enable) {
            mAnimationShake?.let { animation ->
                this.startAnimation(animation)
            }
            mPlayerViewListener?.onShakeView()
            mIconDelete?.visibility = View.VISIBLE
            mTextHandicap?.visibility = View.INVISIBLE
        } else {
            this.animation?.cancel()
            mIconDelete?.visibility = View.INVISIBLE
            mTextHandicap?.visibility = if(mLayoutScoreSheet == null) View.VISIBLE else View.INVISIBLE
        }
        mIsEnableDelete = enable
    }

    fun setCanDelete(canDelete: Boolean) {
        mIsCanDelete = canDelete
    }

    fun addPlayerViewListener(playerViewListener: PlayerViewListener?) {
        mPlayerViewListener = playerViewListener
    }

    fun setIsPlayer(isPlayer: Boolean,isShowScore: Boolean = false) {
        mIsPlayerView = isPlayer
        if (!mIsPlayerView) {
            //ic_icon_add_user_black
            mImgAvatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_icon_add_user_black))
            mTextNamePlayer.setTextColor(ContextCompat.getColor(context, R.color.colorLinker))
            mTextNamePlayer.gravity = Gravity.CENTER
            mTextNamePlayer.text = context.getString(R.string.add_player)
            mTextHandicap?.visibility = View.GONE

            if (isShowScore){
                createScoreBottomSheet()
                mLayoutScoreSheet?.visibility = View.INVISIBLE
            }else{
                mLayoutScoreSheet?.visibility = View.GONE
            }

        } else {
            mImgAvatar.setImageDrawable(null)
            mTextNamePlayer.setTextColor(ContextCompat.getColor(context, R.color.colorTextDark))
            if (isShowScore){
                mTextHandicap?.visibility = View.GONE
                createScoreBottomSheet()
            }else{
                mTextHandicap?.visibility = View.VISIBLE
                mLayoutScoreSheet?.visibility = View.GONE
            }
        }
    }

    private fun onAddHandicapView() {
        mTextHandicap = MaterialTextView(context)
        mIconDelete = ImageView(context)
        val strokeBubblePlayer = context.resources.getDimension(R.dimen.stroke_bubble_player).toInt() * 2
        mTextHandicap?.let { textHandicap ->
            textHandicap.gravity = Gravity.CENTER
            textHandicap.translationZ = Float.MAX_VALUE
            textHandicap.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            TextViewCompat.setTextAppearance(textHandicap, R.style.normal)
            textHandicap.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.x_limit_small_font_size))
            textHandicap.setPadding(strokeBubblePlayer, strokeBubblePlayer, strokeBubblePlayer, strokeBubblePlayer)
            textHandicap.background = ContextCompat.getDrawable(context, R.drawable.background_bubble_player_view)
            addView(textHandicap)
            (textHandicap.layoutParams as? LayoutParams)?.addRule(END_OF, mMaskPlayer.id)
        }

        mIconDelete?.let { iconDelete ->
            iconDelete.translationZ = Float.MAX_VALUE
            iconDelete.adjustViewBounds = true
            iconDelete.setPadding(strokeBubblePlayer, strokeBubblePlayer, strokeBubblePlayer, strokeBubblePlayer)
            iconDelete.background = ContextCompat.getDrawable(context, R.drawable.background_bubble_delete_player_view)
            iconDelete.scaleType = ImageView.ScaleType.CENTER_CROP
            iconDelete.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_icon_close))
            addView(iconDelete)
            (iconDelete.layoutParams as? LayoutParams)?.addRule(END_OF, mMaskPlayer.id)
        }
    }

    fun setData(user: User, handicap: Double? = null) {
        mImgAvatar.loadImage(user.avatar, R.drawable.ic_icon_user_default)
        mTextNamePlayer.text = user.shortName
        mTextHandicap?.let { textHandicap ->
            textHandicap.text = GolfUtils.roundHandicap(handicap ?: (user.handicap?:0.0))
            textHandicap.measure(0, 0)
            val sizeHandicapView = textHandicap.measuredWidth.coerceAtLeast(textHandicap.measuredHeight)
            (textHandicap.layoutParams as? LayoutParams)?.let { nameLayout ->
                nameLayout.width = sizeHandicapView
                nameLayout.height = sizeHandicapView
            }
            mIconDelete?.measure(0, 0)
            (mIconDelete?.layoutParams as? LayoutParams)?.let { deleteLayout ->
                deleteLayout.width = sizeHandicapView
                deleteLayout.height = sizeHandicapView
            }
        }
    }

    interface PlayerViewListener {
        fun onDeleteView()
        fun onShakeView()
        fun onAddPlayer()
        fun onSelected()
    }
}