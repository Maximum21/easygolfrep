package com.minhhop.easygolf.presentation.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.GolfUtils
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.framework.models.firebase.UserFirebase

class EasyGolfAvatarView : FrameLayout {
    private lateinit var mCardViewAvatar: MaterialCardView
    private lateinit var mContainerView: FrameLayout
    private lateinit var mTextShortNameUserView: MaterialTextView
    private lateinit var mImgAvatarView: ImageView

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintBubble = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintBubbleWhite = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mStartColor: Int = AppUtils.mColorBezierStart.toColor()
    private var mEndColor: Int = AppUtils.mColorBezierEnd.toColor()

    private var mColorBubbleTop: Int = 0
    private var mColorBubbleBottom: Int = 0

    private var mTextBubbleTop: String? = null
    private var mPaddingDefault = 20

    private var mIsNeedToShowBubble: Boolean = true
    private var mScorecardData: Scorecard? = null

    constructor(context: Context, isBubble: Boolean) : super(context) {
        mIsNeedToShowBubble = isBubble
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {

        mColorBubbleTop = ContextCompat.getColor(context, R.color.colorLinker)
        mColorBubbleBottom = ContextCompat.getColor(context, R.color.colorPrimary)

        mCardViewAvatar = MaterialCardView(context)
        mCardViewAvatar.setCardBackgroundColor(0)
        mCardViewAvatar.elevation = 0f
        mContainerView = FrameLayout(context)
        mTextShortNameUserView = MaterialTextView(context)
        mTextShortNameUserView.setTextColor(ContextCompat.getColor(context, R.color.textColorLight))
        TextViewCompat.setTextAppearance(mTextShortNameUserView, R.style.bold)
        mTextShortNameUserView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.max_limit_small_font_size))
        mTextShortNameUserView.gravity = Gravity.CENTER
        mContainerView.addView(mTextShortNameUserView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        mImgAvatarView = ImageView(context)
        mImgAvatarView.adjustViewBounds = true
        mImgAvatarView.scaleType = ImageView.ScaleType.CENTER_CROP
        mContainerView.addView(mImgAvatarView)

        mCardViewAvatar.addView(mContainerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        addView(mCardViewAvatar)
        mPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mPaintText.apply {
            isAntiAlias = true
            textSize = 10f
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        mPaintBubbleWhite.apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        mPaintBubble.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
        setWillNotDraw(false)
    }


    fun dispatchUserProfileFromFirebase(userId: String?, isShowBubble: Boolean = false, scorecard: Scorecard?) {
        mScorecardData = scorecard
        mIsNeedToShowBubble = isShowBubble
        userId?.let { userIdSafe ->
            try {
                FirebaseDatabase.getInstance().reference.child("users")
                        .child(userIdSafe)
                        .child("profile")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                setUnknown()
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val result = dataSnapshot.getValue(UserFirebase::class.java)
                                    result?.id = userId
                                    mImgAvatarView.visibility = View.VISIBLE
                                    mImgAvatarView.loadImage(result?.avatar) {
                                        mImgAvatarView.visibility = View.GONE
                                        mTextShortNameUserView.visibility = View.VISIBLE
                                        mTextShortNameUserView.text = result?.toUser()?.cutName
                                    }
                                    scorecard?.let { scorecardSafe ->
                                        result?.toUser()?.let { user ->
                                            val handicapUser = GolfUtils.calculateUserHandicapByCourse(scorecardSafe, user)
                                                    ?: user.handicap
                                            mTextBubbleTop = GolfUtils.roundHandicap(
                                                    handicapUser?:0.0
                                            )
                                        }
                                        invalidate()
                                    }

                                }
                            }
                        })
            } catch (e: Exception) {
                setUnknown()
            }
        } ?: setUnknown()
    }

    @SuppressLint("SetTextI18n")
    private fun setUnknown() {
        mImgAvatarView.visibility = View.GONE
        mTextShortNameUserView.visibility = View.VISIBLE
        mTextShortNameUserView.text = "UK"
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(0f, mPaddingDefault * 1f,
                width * 1f - mPaddingDefault, height * 1f, height / 2f, height / 2f, mPaint)
        super.dispatchDraw(canvas)
    }

    override fun draw(c: Canvas?) {
        super.draw(c)
        c?.let { canvas ->
            val boundsTop = Rect()

            mTextBubbleTop?.let { textTop ->
                mPaintBubble.color = mColorBubbleTop

                mPaintText.getTextBounds(textTop, 0, textTop.length, boundsTop)
                val offsetTop = boundsTop.width() / 2
                val radiusBubbleTop = (boundsTop.width().toFloat() / 2) + offsetTop
                canvas.drawCircle(width - (boundsTop.width() / 2f) - offsetTop, (boundsTop.width() / 2f) + offsetTop, (boundsTop.width().toFloat() / 2) + offsetTop * 1.2f, mPaintBubbleWhite)
                canvas.drawCircle(width - (boundsTop.width() / 2f) - offsetTop, (boundsTop.width() / 2f) + offsetTop, (boundsTop.width().toFloat() / 2) + offsetTop, mPaintBubble)
                canvas.drawText(textTop, width - boundsTop.width().toFloat() - offsetTop,
                        (radiusBubbleTop + boundsTop.height() / 2)
                        , mPaintText)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaddingDefault = if (mIsNeedToShowBubble) {
            w / 10
        } else 0
        setPadding(0, mPaddingDefault, mPaddingDefault, 0)

        setTextSizeForWidth(mPaintText, w / 3f, "888")
        (mCardViewAvatar.layoutParams as? MarginLayoutParams)?.marginEnd = 5
        mCardViewAvatar.radius = (w / 2f) - (mPaddingDefault / 2)
        mPaint.shader = LinearGradient(0f, h / 2f, w.toFloat(), h / 2f, mEndColor,
                mStartColor, Shader.TileMode.CLAMP)
    }

    private fun setTextSizeForWidth(paint: Paint, desiredWidth: Float, text: String) {
        val testTextSize = 10f
        paint.textSize = testTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val desiredTextSize = testTextSize * desiredWidth / bounds.width()
        paint.textSize = desiredTextSize
    }
}