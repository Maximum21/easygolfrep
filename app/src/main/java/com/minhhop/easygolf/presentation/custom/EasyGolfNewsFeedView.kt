package com.minhhop.easygolf.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.share_post_row_layout.view.*

class EasyGolfNewsFeedView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(context, attrs, defStyleAttr) {

    var buttonTapped: (() -> Unit)? = null
    lateinit var view: View

    init {
        view = LayoutInflater.from(context).inflate(R.layout.share_post_row_layout, this, false)
        val set = ConstraintSet()
        addView(view)

        set.clone(this)
        set.match(view, this)
    }

    fun setData(newsFeed: NewsFeed, user: String?, tag: Int) {

        if(newsFeed?.created_by?.avatar!=null){
            Picasso.get().load(newsFeed.created_by.avatar).placeholder(R.drawable.placeholder).into(view.post_profile_iv)
        }else{
            view.post_profile_iv.setImageResource(R.drawable.placeholder)
        }
        if (newsFeed.content != null) {
            view.post_desription_tv.text = newsFeed.content
        } else {
            view.post_desription_tv.text = ""
        }
        if (newsFeed.created_by.fullName != null) {
            view.player_name_tv.text = newsFeed.created_by.fullName
        } else {
            view.player_name_tv.text = ""
        }
        if (newsFeed.date_created != null) view.post_publish_time_tv.text = AppUtils.generatePublishTime(newsFeed.date_created.toLong())
        if (newsFeed.club != null && newsFeed.club.name != null) {
            view.post_course_iv.visibility = View.VISIBLE
            view.player_type_tv.text = newsFeed.club.name
        } else {
            view.post_course_iv.visibility = View.GONE
            view.player_type_tv.text = ""
        }
        if (newsFeed.shared_post == null) {
            if (newsFeed.post_files != null && newsFeed.post_files!!.isNotEmpty()) {
                if (newsFeed.post_files!!.isNotEmpty()) {
                    when (newsFeed.post_files!!.size) {
                        1 -> {
                            view.img1.visibility = View.VISIBLE
                            view.img2.visibility = View.GONE
                            view.img3.visibility = View.GONE
                            view.img4.visibility = View.GONE
                            setImageLayoutDimensions(view.img1, null, Utils.setLayoutHeight(400.0))
                            Picasso.get().load(newsFeed.post_files!![0].url).fit().centerCrop().into(view.img1)
                        }
                        2 -> {
                            view.img1.visibility = View.VISIBLE
                            view.img2.visibility = View.VISIBLE
                            view.img3.visibility = View.GONE
                            view.img4.visibility = View.GONE
                            setImageLayoutDimensions(view.img1, null, Utils.setLayoutHeight(400.0))
                            Picasso.get().load(newsFeed.post_files!![0].url).fit().centerCrop().into(view.img1)
                            setImageLayoutDimensions(view.img2, null, Utils.setLayoutHeight(400.0))
                            Picasso.get().load(newsFeed.post_files!![1].url).fit().centerCrop().into(view.img2)
                        }
                        3 -> {
                            view.img1.visibility = View.VISIBLE
                            view.img2.visibility = View.VISIBLE
                            view.img3.visibility = View.GONE
                            view.img4.visibility = View.VISIBLE
                            setImageLayoutDimensions(view.img1, null, Utils.setLayoutHeight(400.0))
                            Picasso.get().load(newsFeed.post_files!![0].url).fit().centerCrop().into(view.img1)
                            setImageLayoutDimensions(view.img2, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![1].url).fit().centerCrop().into(view.img2)
                            setImageLayoutDimensions(view.img4, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![2].url).fit().centerCrop().into(view.img4)
                        }
                        else -> {
                            view.img1.visibility = View.VISIBLE
                            view.img2.visibility = View.VISIBLE
                            view.img3.visibility = View.VISIBLE
                            view.img4.visibility = View.VISIBLE
                            setImageLayoutDimensions(view.img1, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![0].url).fit().centerCrop().centerCrop().into(view.img1)
                            setImageLayoutDimensions(view.img2, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![1].url).fit().centerCrop().into(view.img2)
                            setImageLayoutDimensions(view.img3, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![2].url).fit().centerCrop().into(view.img3)
                            setImageLayoutDimensions(view.img4, null, Utils.setLayoutHeight(200.0))
                            Picasso.get().load(newsFeed.post_files!![3].url).fit().centerCrop().into(view.img4)
                            if (newsFeed.post_files != null && newsFeed.post_files!!.size > 4) {
                                view.extra_images_tv.visibility = View.VISIBLE
                                view.extra_images_tv.text = "+${(newsFeed.post_files!!.size - 3).toString()}"
                            } else {
                                view.extra_images_tv.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            } else {
                view.img1.visibility = View.GONE
                view.img2.visibility = View.GONE
                view.img3.visibility = View.GONE
                view.img4.visibility = View.GONE
            }
        } else {
            view.img1.visibility = View.GONE
            view.img2.visibility = View.GONE
            view.img3.visibility = View.GONE
            view.img4.visibility = View.GONE
        }
        when (tag) {
            0 -> {
                view.post_like_group_new.visibility = View.VISIBLE
                view.post_comment_group_new.visibility = View.VISIBLE
                view.post_share_group_new.visibility = View.VISIBLE
//                var test = false
//                user?.let{
//                    if (newsFeed.post_likes!=null && newsFeed.post_likes!!.isNotEmpty()) {
//                        for(element in newsFeed.post_likes!!){
//                            if(element!=null && element.user!=null && element.user.id == it){
//                                test = true
//                                break
//                            }
//                        }
//                    }
//                }
//                setLikes(test)
//                if (newsFeed.comments > 0) {
//                    view.comment_post_tv_new.text = context.getString(R.string.comment) + " (${newsFeed.comments})"
//                }
//                if (newsFeed.shares > 0) {
//                    view.share_post_tv_new.text = context.getString(R.string.share) + " (${newsFeed.shares})"
//                }
//                if (newsFeed.likes > 0) {
//                    view.like_post_tv.text = context.getString(R.string.like) + " (${newsFeed.likes})"
//               }
            }
        }
    }

    fun setLikes(test: Boolean) {
        if (!test) {
            view.like_post_iv_new.setImageResource(R.drawable.ic_icon_like_black)
            view.like_post_tv_new.setTextColor(ContextCompat.getColor(context, R.color.textColorGray))
        } else {
            view.like_post_iv_new.setImageResource(R.drawable.ic_like_filled_black)
            view.like_post_tv_new.setTextColor(ContextCompat.getColor(context, R.color.textColorGray))
        }
    }

    private fun setImageLayoutDimensions(img: ImageView, layoutWidth: Int?, layoutHeight: Int) {
        if (layoutWidth != null) {
            img.layoutParams.width = layoutWidth
        }
        img.layoutParams.height = layoutHeight
        img.requestLayout()
    }

    fun ConstraintSet.match(view: View, parentView: View) {
        this.connect(view.id, ConstraintSet.TOP, parentView.id, ConstraintSet.TOP)
        this.connect(view.id, ConstraintSet.START, parentView.id, ConstraintSet.START)
        this.connect(view.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
        this.connect(view.id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM)
    }
}