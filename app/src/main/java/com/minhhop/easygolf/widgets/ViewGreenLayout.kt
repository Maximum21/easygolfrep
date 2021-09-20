package com.minhhop.easygolf.widgets

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.utils.AppUtil

class ViewGreenLayout : FrameLayout {

    private lateinit var mPhotoViewGreen:PhotoView
    private lateinit var mTxtNoImage:TextView
    private lateinit var mButtonClose:ImageView

    private lateinit var mContainer:FrameLayout

    private lateinit var mAnimationEnter:Animation
    private lateinit var mAnimationExit:Animation

    private var mValuePadding:Int = 0
    private var mSizeButtonClose:Int = 0

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private fun initView(){

        mAnimationEnter = AnimationUtils.loadAnimation(context,R.anim.slide_dialog_enter)
        mAnimationExit = AnimationUtils.loadAnimation(context,R.anim.slide_dialog_exit)
        mAnimationExit.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        mValuePadding = AppUtil.convertDpToPixel(25,context)
        mSizeButtonClose = AppUtil.convertDpToPixel(25,context)
        val halfPadding = (mValuePadding/2)

        setBackgroundColor(ContextCompat.getColor(context, R.color.color_mask_view_green))
        mContainer = FrameLayout(context)

        mPhotoViewGreen = PhotoView(context)
        mContainer.background =  ContextCompat.getDrawable(context,R.drawable.background_border_white)
        mContainer.addView(mPhotoViewGreen)
        val layoutPhoto = mPhotoViewGreen.layoutParams as MarginLayoutParams
        layoutPhoto.width = ViewGroup.LayoutParams.MATCH_PARENT
        layoutPhoto.height = ViewGroup.LayoutParams.MATCH_PARENT
        mPhotoViewGreen.requestLayout()


        mButtonClose = ImageView(context)
        mButtonClose.adjustViewBounds = true
        mButtonClose.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_close_task))
        mContainer.addView(mButtonClose)
        val layoutBtClose = mButtonClose.layoutParams as LayoutParams
        layoutBtClose.gravity = Gravity.END or Gravity.TOP
        layoutBtClose.width = mSizeButtonClose
        layoutBtClose.height = mSizeButtonClose

        val layoutBtCloseMargin = mButtonClose.layoutParams as MarginLayoutParams
        layoutBtCloseMargin.setMargins(0,mSizeButtonClose/2, mSizeButtonClose/2,0)
        mButtonClose.requestLayout()

        mTxtNoImage = TextView(context)
        mTxtNoImage.text = context.getString(R.string.txt_not_found_image)
        mTxtNoImage.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        mTxtNoImage.setTextSize(TypedValue.COMPLEX_UNIT_SP,17f)

        mContainer.addView(mTxtNoImage)
        val layoutTxtNoImage = mTxtNoImage.layoutParams as LayoutParams
        layoutTxtNoImage.gravity = Gravity.CENTER
        layoutTxtNoImage.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutTxtNoImage.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mTxtNoImage.requestLayout()


        requestLayout()

        mButtonClose.setOnClickListener {
            hidePhotoViewGreen()
        }

        addView(mContainer)

        (mContainer.layoutParams  as MarginLayoutParams).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            this.setMargins(halfPadding,mValuePadding,halfPadding,mValuePadding)

        }
//        mContainer.setPadding(halfPadding,halfPadding,halfPadding,halfPadding)

        setOnClickListener {

        }

        hideYourSelfNotAniamation()
    }

    fun setPhoto(url:String?){
        if(url == null){
            mTxtNoImage.visibility = View.VISIBLE
            mPhotoViewGreen.visibility = View.GONE
        }else {
            mTxtNoImage.visibility = View.GONE
            mPhotoViewGreen.visibility = View.VISIBLE
            Glide.with(context).load(url).into(mPhotoViewGreen)
        }
    }

    fun showPhoto(){
        visibility = View.VISIBLE
        showMask()
    }

    private fun showMask(){
        this.animate().alpha(1f)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        showPhotoViewGreen()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                .start()
    }

    private fun showPhotoViewGreen(){

        this.mContainer.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                .start()

    }


    private fun hideYourSelfNotAniamation(){
        visibility = View.GONE
    }

    private fun hidePhotoViewGreen(){
        this.mContainer.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        hideMask()
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                .start()

    }

    private fun hideMask(){
        this.animate().alpha(0f)
                .setDuration(200)
                .setListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }

                })
                .start()
    }

}