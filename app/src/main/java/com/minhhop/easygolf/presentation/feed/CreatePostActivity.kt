package com.minhhop.easygolf.presentation.feed

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.minhhop.core.domain.User
import com.minhhop.core.domain.feed.CustomImageObject
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.AddPlayerBundle
import com.minhhop.easygolf.framework.bundle.SearchCourseBundle
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.Contains.REQUEST_CODE_CAMERA
import com.minhhop.easygolf.framework.common.Contains.REQUEST_CODE_PHOTO_GALLERY
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.club.detail.overview.ClubOverviewFragment
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedFragment
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.views.activities.PostDetailsActivity
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_create_post.view.*
import org.koin.android.ext.android.inject
import kotlin.collections.ArrayList

class CreatePostActivity : EasyGolfActivity<CreateNewPostViewModel>(), View.OnClickListener, CreatePostImagesAdapter.NewsFeedEvent {
    private var tag: Int = -1
    private var newsFeed: NewsFeed? = null
    private var imagesAdapter: CreatePostImagesAdapter? = null
    private var clubsList: List<Club> = ArrayList()
    private var selectedClub: Club? = null
    private lateinit var mViewRoot: View
    private var uriList: ArrayList<String> = ArrayList()
    private var finalImages: ArrayList<CustomImageObject> = ArrayList()
    private var filesList: ArrayList<Uri> = ArrayList()
    private var friendsList: List<User> = ArrayList()
    override val mViewModel: CreateNewPostViewModel
            by inject()

    companion object {
        const val REQUEST_CODE_COURSE = 1234
    }


    override fun setLayout(): Int {
        // inside your activity (if you did not enable transitions in your theme)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        // set an exit transition
        window.exitTransition = Explode()


//        EventBus.getDefault().register(this)
        return R.layout.activity_create_post
    }

    override fun initView() {
        mViewRoot = findViewById(R.id.viewRoot)
        publish_post_tv.setOnClickListener(this)
        cancel_post_tv.setOnClickListener(this)
        photo_gallery_group.setOnClickListener(this)
        photo_camera_group.setOnClickListener(this)
        new_post_tag_friends_group.setOnClickListener(this)
        new_post_tag_courses_group.setOnClickListener(this)
        new_post_tag_share_group.setOnClickListener(this)
        post_courses_group.setOnClickListener(this)
        post_tags_group.setOnClickListener(this)

        images_list_rv.layoutManager = GridLayoutManager(this, 4)
        imagesAdapter = CreatePostImagesAdapter(this, this)
        images_list_rv.adapter = imagesAdapter

        mViewModel.clubNearbyPagerLive.observe(this, Observer {
            clubsList = it.items
        })
        mViewModel.imageDelete.observe(this, Observer {
            finalImages.removeAt(it.toInt())
            imagesAdapter?.removeItemAt(it.toInt())
        })
        mViewModel.responseCreated.observe(this, Observer {
            EasyGolfNavigation.addPostDirection(this, NewsFeedFragment.POST_DETAILS_REQUESTCODE, -1, it.id, Gson().toJson(it), PostDetailsActivity::class.java)
            hideMask()
            finish()
        })
        mViewModel.updatedPost.observe(this, Observer {
            EasyGolfNavigation.addPostDirection(this, NewsFeedFragment.POST_DETAILS_REQUESTCODE, -1, it.id, Gson().toJson(it), PostDetailsActivity::class.java)
            hideMask()
            finish()
        })

        try {
            val bundle = EasyGolfNavigation.PostBundle(intent)
            newsFeed = bundle?.getResult()
            tag = bundle?.tag ?: -1
            when (tag) {
                1 -> {
                    setData()
                }
                2->{
                    share_post_extra_group.visibility = View.GONE
                    create_post_newsfeed_view.visibility = View.VISIBLE
                    newsFeed?.let{
                        create_post_newsfeed_view.setData(it,null,1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setData() {
        if (newsFeed != null) {
            if (newsFeed!!.content != null && newsFeed!!.content.isNotEmpty()) {
                post_description_etv.setText(newsFeed!!.content)
            }
            if (newsFeed!!.post_files != null && newsFeed!!.post_files!!.isNotEmpty()) {
                for (temp in newsFeed!!.post_files!!) {
                    finalImages.add(CustomImageObject("", temp))
                }
                setImagesUri()
            }
            if (newsFeed!!.friends != null && newsFeed!!.friends!!.isNotEmpty()) {
                friendsList = newsFeed!!.friends
                setFriends()
            }
            if (newsFeed!!.club != null) {
                selectedClub = newsFeed!!.club
                setClub()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cancel_post_tv -> {
                finish()
            }
            R.id.publish_post_tv -> {
                viewMask()
                when (tag) {
                    1 -> {
                        mViewModel.updatePost(mViewRoot.post_description_etv.text.toString().trim(), uriList, selectedClub, friendsList, newsFeed!!.id,1)
                    }
                    2->{
                        mViewModel.updatePost(mViewRoot.post_description_etv.text.toString().trim(), uriList, selectedClub, friendsList, newsFeed!!.id,2)
                    }
                    else -> {
                        mViewModel.createPost(mViewRoot.post_description_etv.text.toString().trim(), uriList, selectedClub, friendsList)
                    }
                }
            }
            R.id.photo_gallery_group -> {
                checkPermissionToGetImage(false)
            }
            R.id.photo_camera_group -> {
                checkPermissionToGetImage(true)
            }
            R.id.new_post_tag_friends_group -> {
                EasyGolfNavigation.addPlayerDirection(this, ClubOverviewFragment.REQUEST_CODE, -1, AddPlayerBundle.toBlackList(friendsList), "Add Friends")
            }
            R.id.new_post_tag_courses_group -> {
                var temp: String = ""
                if (selectedClub != null) {
                    temp = SearchCourseBundle.toBlackList(selectedClub!!)!!
                }
                EasyGolfNavigation.addCourseDirection(this, REQUEST_CODE_COURSE, -1, temp, "Search Course")
            }
            R.id.new_post_tag_share_group -> {

            }

        }
    }

    private fun checkPermissionToGetImage(isCamera: Boolean) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            val dataExplanation = getString(R.string.explanation,
                                    report.deniedPermissionResponses[0]
                                            .permissionName)

                            Snackbar.make(mViewRoot, dataExplanation, Snackbar.LENGTH_INDEFINITE)
                                    .setAction("ok") {
                                        val intent = Intent()
                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                        val uri = Uri.fromParts("package", packageName, null)
                                        intent.data = uri
                                        startActivity(intent)
                                    }.show()
                        }

                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                AppUtil.dispatchTakePictureIntent(this@CreatePostActivity)
                            } else {
                                AppUtils.pickMultipleImageGallery(this@CreatePostActivity)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }

                }).onSameThread().check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO_GALLERY) {
                intent?.apply {
                    if (clipData != null && clipData!!.itemCount > 0) {
                        var count = clipData!!.itemCount
                        for (i in 0 until count) {
                            var imageUri: Uri = clipData!!.getItemAt(i).uri
                            uriList.add(AppUtils.getPathFromURI(imageUri, this@CreatePostActivity))
                            finalImages.add(CustomImageObject(AppUtils.getPathFromURI(imageUri, this@CreatePostActivity), null))
                        }
                        setImagesUri()
                    } else if (data != null) {
                        var imagePath = data!!
                        uriList.add(AppUtils.getPathFromURI(imagePath, this@CreatePostActivity))
                        finalImages.add(CustomImageObject(AppUtils.getPathFromURI(imagePath, this@CreatePostActivity), null))
                        Log.e("imagePath", AppUtils.getPathFromURI(imagePath, this@CreatePostActivity));
//                        uriList.add(imagePath)
                        setImagesUri()
                    }
                }
            }

            if (requestCode == REQUEST_CODE_CAMERA) {
                val uriFileCamera = Uri.fromFile(AppUtil.mFile)
                uriList.add(uriFileCamera.path!!)
                finalImages.add(CustomImageObject(uriFileCamera.path!!, null))
                Log.e("imagePath", "${uriFileCamera.path}");
                setImagesUri()
            }
        }

        if (requestCode == ClubOverviewFragment.REQUEST_CODE && resultCode == Activity.RESULT_OK && intent != null) {
            val addPlayerBundle = EasyGolfNavigation.addPlayerBundle(intent)
            addPlayerBundle?.getResult()?.let { users ->
                Log.e("testofriends", "${users.size}")
                friendsList = users
                setFriends()
            }
        } else if (requestCode == REQUEST_CODE_COURSE && resultCode == Activity.RESULT_OK && intent != null) {
            val selectedCourseBundle = EasyGolfNavigation.SearchCourseBundle(intent)
            selectedCourseBundle?.getResult()?.let { users ->
                Log.e("testofriends", "${users}")
                selectedClub = users
                setClub()
            }
        }
    }

    private fun setClub() {
        viewRoot.post_courses_group.visibility = View.VISIBLE
        viewRoot.player_type_tv.text = selectedClub!!.name
    }

    private fun setFriends() {
        var temp = ""
        for (name in friendsList) {
            if (temp.isNotEmpty()) {
                temp = "$temp, ${name.fullName}"
            } else {
                temp = name.fullName!!
            }
        }
        viewRoot.post_tags_group.visibility = View.VISIBLE
        viewRoot.new_post_tag_value.text = temp
    }

    private fun setImagesUri() {
        imagesAdapter!!.setDataList(finalImages)
    }

    private fun setDimentions(img: ImageView, layoutHeight: Int) {
        img.layoutParams.height = layoutHeight
        img.requestLayout()
    }

    override fun onClickListener(positiom: Int) {
        uriList.removeAt(positiom)
    }

    override fun deleteImage(customImageObject: CustomImageObject, adapterPosition: Int) {
        Log.e("tesiopoiiis", "delete image is called ${customImageObject.url!!.id} and $adapterPosition and ${newsFeed!!.id}")
//        finalImages.removeAt(adapterPosition)
        mViewModel.removeImage(newsFeed!!.id, customImageObject.url!!.id, adapterPosition)
    }

    override fun loadData() {

    }
}