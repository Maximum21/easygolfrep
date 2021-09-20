package com.minhhop.easygolf.views.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.feed.Like
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.CommentsAdapter
import com.minhhop.easygolf.adapter.LikedByAdapter
import com.minhhop.easygolf.adapter.MultipleImagesAdapter
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.PostDetailsBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.ConfirmationDialog
import com.minhhop.easygolf.listeners.KeyboardHeightObserver
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedFragment
import com.minhhop.easygolf.presentation.home.newsfeed.PostDetailsViewModel
import com.minhhop.easygolf.utils.KeyboardHeightProvider
import com.minhhop.easygolf.presentation.like.LikesFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.fragment_post_details.comment_count_list_headaer_tv
import kotlinx.android.synthetic.main.fragment_post_details.comment_etv
import kotlinx.android.synthetic.main.fragment_post_details.comment_list_count_group
import kotlinx.android.synthetic.main.fragment_post_details.keyboard
import kotlinx.android.synthetic.main.fragment_post_details.post_detail_options_iv
import kotlinx.android.synthetic.main.fragment_post_details.view.*
import kotlinx.android.synthetic.main.fragment_post_details.viewRoot
import kotlinx.android.synthetic.main.share_post_row_layout.view.*
import org.koin.android.ext.android.inject

class PostDetailsActivity : EasyGolfActivity<PostDetailsViewModel>(), View.OnClickListener,
        CommentsAdapter.CommentEvent, KeyboardHeightObserver, LikedByAdapter.LikedByEvent {
    private var commentID: String = ""
    private var editting = false
    lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private var mediaAdapter: MultipleImagesAdapter? = null
    private var userEntitity: User? = null
    private var newsFeed: NewsFeed? = null
    private lateinit var commentsAdapter: CommentsAdapter
    private var likedByAdapter: LikedByAdapter? = null
    private lateinit var mViewRoot: View
    private var localCommentsList : List<Comment> = ArrayList()
    override val mViewModel: PostDetailsViewModel by inject()

    override fun setLayout(): Int {
        // inside your activity (if you did not enable transitions in your theme)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        // set an exit transition
        window.exitTransition = Explode()


//        EventBus.getDefault().register(this)
        return R.layout.fragment_post_details
    }

    override fun initView() {
        keyboardHeightProvider = KeyboardHeightProvider(this)
        mViewRoot = findViewById(R.id.viewRoot)
        try {
            val addPlayerBundle = EasyGolfNavigation.PostBundle(intent)
            newsFeed = addPlayerBundle?.getResult()
            mViewModel.getUser()?.let { user ->
                if(newsFeed!!.created_by.id== user.id){
                    post_detail_options_iv.visibility = View.VISIBLE
                }else{
                    post_detail_options_iv.visibility = View.GONE
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mViewRoot.post_profile_iv_layout.post_profile_iv.setOnClickListener(this)
        mViewRoot.post_comment_btn.setOnClickListener(this)
        mViewRoot.comments_recylerview_post_detail.layoutManager = LinearLayoutManager(this)
        commentsAdapter = CommentsAdapter(this,this)
        mViewRoot.comments_recylerview_post_detail.adapter = commentsAdapter

        mViewModel.commentsList.observe(this, Observer{
            commentsAdapter.setDataList(it)
            if (it.isNotEmpty()) {
                comment_list_count_group.visibility = View.VISIBLE
                comment_count_list_headaer_tv.text = "(${it.size})"
            }else{
                comment_list_count_group.visibility = View.GONE
                comment_count_list_headaer_tv.text = "(${it.size})"
            }
            localCommentsList = it
        })
        comment_etv.measuredHeight
        mViewModel.newsFeedModel.observe(this, Observer{
            Log.e("testingmosl","${it.post_likes?.size}")
            setLikes(it)
            newsFeed = it
        })
        mViewModel.removeComment.observe(this, Observer{
            for(x in localCommentsList.indices){
                if(localCommentsList[x].id == it){
                    commentsAdapter.removeItemAt(x)
                    break
                }
            }
            if (commentsAdapter.itemCount>0) {
                comment_count_list_headaer_tv.text = "(${commentsAdapter.itemCount})"
            }else{
                comment_list_count_group.visibility = View.GONE
            }

        })
        mViewModel.replaceComment.observe(this, Observer{
            for(x in localCommentsList.indices){
                if(localCommentsList[x].id == it.id){
                    commentsAdapter.replaceItem(it,x)
                    break
                }
            }
        })
        mViewModel.comment.observe(this, Observer{
            commentsAdapter.addItem(it)
            comment_list_count_group.visibility = View.VISIBLE
            comment_count_list_headaer_tv.text = "(${commentsAdapter.itemCount})"
        })

        mViewModel.deletePost.observe(this, Observer {
            val dataIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(PostDetailsBundle::tag.name,0)
            bundle.putString(PostDetailsBundle::postId.name,it)
            dataIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK,dataIntent)
            finish()
//            EasyGolfNavigation.easyGolfHomeDirection(this)
        })
        mViewRoot.back_btn.setOnClickListener(this)
        mViewRoot.post_like_group.setOnClickListener(this)
        mViewRoot.post_detail_options_iv.setOnClickListener(this)
        mViewRoot.post_like_group_list.setOnClickListener(this)
        mViewRoot.post_share_group.setOnClickListener(this)
    }

    override fun loadData() {
        mViewModel.listComments(newsFeed!!.id)
        if(newsFeed!=null){
            newsFeed?.let{
                Log.e("testingmpoiuy","${newsFeed} ${newsFeed?.post_files}")
                post_profile_iv_layout.setData(it,null,1)
                it.shared_post?.let{post ->
                    post_profile_iv_layout1.setData(post,null,1)
                    post_profile_iv_layout1.visibility = View.VISIBLE
                }
                setData(newsFeed!!,mViewRoot,0)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider.setKeyboardHeightObserver(this)
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.back_btn ->{
                finish()
            }
            R.id.post_detail_options_iv ->{
                showPopup(v)
            }
            R.id.post_like_group ->{
                Log.e("testingclick","3")
                mViewModel.likePost(newsFeed!!.id)
            }
            R.id.post_like_group_list ->{
                showLikesFragment()
            }
            R.id.post_profile_iv ->{
                startUserDetailsActivity(newsFeed?.created_by?.profile_id?:"")
            }
            R.id.post_share_group ->{
                EasyGolfNavigation.createPostDirection(this, NewsFeedFragment.POST_DETAILS_REQUESTCODE,
                        2, newsFeed?.id,Gson().toJson(newsFeed),CreatePostActivity::class.java)
            }
            R.id.post_comment_btn -> {
                if (!editting) {
                    if(!TextUtils.isEmpty(viewRoot.comment_etv.text.toString().trim())){
                        mViewModel.addComment(viewRoot.comment_etv.text.toString().trim(),newsFeed!!.id)
                    }else{
                        toast("Comment must not be empty!")
                    }
                    viewRoot.comment_etv.setText("")
                }else{
                    if(!TextUtils.isEmpty(viewRoot.comment_etv.text.toString().trim())){
                        mViewModel.editComment(newsFeed!!.id,commentID,viewRoot.comment_etv.text.toString().trim())
                        viewRoot.comment_etv.setText("")
                        commentID=""
                        editting = false
                    }else{
                        toast("Comment must not be empty!")
                    }
                }
            }
        }
    }

    private fun startUserDetailsActivity(profileId: String) {
        EasyGolfNavigation.startUserDetailsActivity(this,profileId)
    }

    private fun showPopup(itemView: View) {
        //creating a popup menu
        val popup = PopupMenu(this@PostDetailsActivity, itemView)
        //inflating menu from xml resource
        //inflating menu from xml resource
        popup.inflate(R.menu.options_menu)
        //adding click listener
        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    EasyGolfNavigation.createPostDirection(this, NewsFeedFragment.POST_DETAILS_REQUESTCODE,1, newsFeed!!.id,Gson().toJson(newsFeed),CreatePostActivity::class.java)
                }
                R.id.delete -> {
                    ConfirmationDialog(this,"Are you sure you want to delete this post?","Delete","Cancel",
                            DialogInterface.OnCancelListener { },
                            DialogInterface.OnCancelListener { mViewModel.deletePost(newsFeed!!.id)}).show()

                }
            }
            false
        }
        //displaying the popup
        //displaying the popup
        popup.show()
    }
    private fun showLikesFragment() {
        if (newsFeed!=null && newsFeed!!.post_likes!=null && newsFeed!!.post_likes!!.isNotEmpty()) {
            val ft = supportFragmentManager.beginTransaction()
            val fragment = LikesFragment()
            val bundle = Bundle()
            bundle.putString("data",Gson().toJson(newsFeed!!.post_likes))
            fragment.arguments = bundle
            ft.replace(R.id.viewRoot,fragment,"likesfragment").addToBackStack(null).commit()
        }
    }


    private fun setData(newsFeed: NewsFeed, view:View,tag:Int) {
        mViewModel.getUser()?.let {
            userEntitity = it
        }

        if(userEntitity!=null && userEntitity!!.avatar!=null){
            Picasso.get().load(userEntitity!!.avatar).placeholder(R.drawable.placeholder).into(view.post_comment_profile_iv)
        }

        if (tag==0) {
            newsFeed?.friends?.let{
                if(it.isNotEmpty()){
                    var temp = view.new_post_tag_value.text.toString().trim()
                    for(name in it){
                        temp = if (temp.isNotEmpty()) {
                            "$temp, ${name.fullName}"
                        }else{
                            name.fullName!!
                        }
                    }
                    view.post_tags_group.visibility = View.VISIBLE
                    view.new_post_tag_value.text = temp
                }
            }
            setLikes(newsFeed)
        }
    }

    private fun setLikes(newsFeed: NewsFeed){
        if (newsFeed.post_likes!=null && newsFeed.post_likes!!.isNotEmpty()) {
            mViewRoot.like_post_tv.text = resources.getString(R.string.liked_by)
            if(!checkifLiked(newsFeed.post_likes!!)){
                mViewRoot.like_post_iv.setImageResource(R.drawable.ic_icon_like_black)
            }else{
                mViewRoot.like_post_iv.setImageResource(R.drawable.ic_like_filled_black)
            }
            val mLayoutManager = LinearLayoutManager(applicationContext)
            mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            mViewRoot.liked_by_recyclerview.layoutManager = mLayoutManager
            likedByAdapter = LikedByAdapter(this,1,this)
            mViewRoot.liked_by_recyclerview.adapter = likedByAdapter
            likedByAdapter?.setDataList(newsFeed.post_likes!!)
        }else{
            likedByAdapter?.clearAll()
            mViewRoot.like_post_iv.setImageResource(R.drawable.ic_icon_like_black)
            mViewRoot.like_post_tv.text = resources.getString(R.string.like)
        }
    }

    private fun checkifLiked(it: List<Like>): Boolean {
        if (it.isNotEmpty()) {
            for(element in it){
                if(element.user.id == userEntitity!!.id){
                    return true
                }
            }
        }
        return false
    }

    override fun deleteComment(comment: Comment) {
        mViewModel.deleteComment(newsFeed!!.id,comment.id)
    }

    override fun editComment(comment: Comment) {
        editting = true
        commentID = comment.id
        comment_etv.setText(comment.comment)
    }

    override fun onImageClick(profileId: String) {
        startUserDetailsActivity(profileId)
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        Log.e("onKeyboardHeightChanged", "height : " + height + "    ||   orientation : " + orientation);
        if (height != 0 && orientation == 1) {
            Log.e("onKeyboardHeight = 1", "height : $height    ||   orientation : $orientation")
            keyboard.layoutParams.height = height - 180
            keyboard.requestLayout()
        } else if (height != 0 && orientation == 2) {
            Log.e("onKeyboardHeight = 2", "height : $height    ||   orientation : $orientation")
            keyboard.layoutParams.height = height - 540
            keyboard.requestLayout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Log.e("TESTIMGKSLSLS","-----2------$requestCode------$resultCode--")
        if(requestCode == NewsFeedFragment.POST_DETAILS_REQUESTCODE && resultCode == Activity.RESULT_OK && intent != null){
            val postblundle = EasyGolfNavigation.PostBundle(intent)
            val dataIntent = Intent()
            val bundle = Bundle()
            bundle.putInt(PostDetailsBundle::tag.name,1)
            bundle.putString(PostDetailsBundle::newsFeed.name,postblundle!!.newsFeed)
            dataIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK,dataIntent)
            finish()
        }
    }

    override fun onClickListener(profileId: String) {
        Log.e("TESTIMGKSLSLS","-----2------$profileId------$profileId--")
    }
}