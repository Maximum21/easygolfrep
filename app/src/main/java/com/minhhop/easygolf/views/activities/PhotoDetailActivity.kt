package com.minhhop.easygolf.views.activities

import android.graphics.drawable.Drawable
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.framework.common.Contains

class PhotoDetailActivity : WozBaseActivity() {
   private lateinit var mDetailImage:PhotoView
    override fun setLayoutView(): Int = R.layout.activity_photo_detail

    override fun initView() {
        mDetailImage = findViewById(R.id.imageDetail)
        val url = intent.getStringExtra(Contains.EXTRA_PHOTO_URL_KEY)
        mDetailImage.transitionName = Contains.TRANSITION_PHOTO_NAME

        supportStartPostponedEnterTransition()
        mDetailImage.load(url){
            supportStartPostponedEnterTransition()
        }
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
        super.onBackPressed()
    }

    override fun loadData() {
    }

    fun ImageView.load(url:String,onLoadingFinished:()-> Unit = {}){
        val listener = object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                      isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                         dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                onLoadingFinished()
                return false
            }

        }

        Glide.with(this).load(url)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .dontTransform()
                .into(this)
    }
}