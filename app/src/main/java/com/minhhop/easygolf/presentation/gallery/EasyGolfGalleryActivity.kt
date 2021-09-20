package com.minhhop.easygolf.presentation.gallery

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.minhhop.core.domain.GalleryMedia
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.GalleryBundle
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_easy_golf_gallery.*
import org.koin.android.ext.android.inject
import java.io.File

class EasyGolfGalleryActivity : EasyGolfActivity<EasyGolfGalleryViewModel>() {

    override val mViewModel: EasyGolfGalleryViewModel
            by inject()
    private var mEasyGolfGalleryAdapter: EasyGolfGalleryAdapter? = null
    private lateinit var mGalleryBundle: GalleryBundle
    private var mUrlFilePicture: String? = null
    private var mIsGetPictureFromGallery: Boolean = false

    override fun setLayout(): Int = R.layout.activity_easy_golf_gallery

    override fun initView() {
        EasyGolfNavigation.getGalleryBundle(intent)?.let {
            mGalleryBundle = it
        } ?: finish()

        mEasyGolfGalleryAdapter = EasyGolfGalleryAdapter(this, mGalleryBundle.maxPhotoSelect)

        val layoutGallery = GridLayoutManager(this, 3)
        listGallery.layoutManager = layoutGallery
        listGallery.adapter = mEasyGolfGalleryAdapter

        mEasyGolfGalleryAdapter?.registerLoadMore(layoutGallery, listGallery) {
            if (mStatusLoading == StatusLoading.FINISH_LOAD) {
                mViewModel.getExternalGallery(this)
            }
        }

        textCounterSelected.text = getString(R.string.done_format, "0")
        textCounterSelected.setOnClickListener {
            val dataIntent = Intent()
            val bundle = Bundle()
            bundle.putString(GalleryBundle::result.name, GalleryBundle.toData(mEasyGolfGalleryAdapter?.getResult()))
            bundle.putInt(GalleryBundle::maxPhotoSelect.name, mGalleryBundle.maxPhotoSelect)
            dataIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK, dataIntent)
            finish()
        }

        mEasyGolfGalleryAdapter?.addListener(object : EasyGolfGalleryAdapter.EasyGolfGalleryListener {
            override fun onOpenCamera() {
                dispatchTakePictureCamera()
            }

            override fun onSelect(counter: Int) {
                textCounterSelected.text = getString(R.string.done_format, counter.toString())
            }

            override fun onFinish(result: List<GalleryMedia>) {
                val dataIntent = Intent()
                val bundle = Bundle()
                bundle.putString(GalleryBundle::result.name, GalleryBundle.toData(result))
                bundle.putInt(GalleryBundle::maxPhotoSelect.name, mGalleryBundle.maxPhotoSelect)
                dataIntent.putExtras(bundle)
                setResult(Activity.RESULT_OK, dataIntent)
                finish()
            }

            override fun onLimitSelect(limit: Int) {
                toast(getString(R.string.limit_get_gallery, limit))
            }
        })
    }

    private fun dispatchTakePictureCamera() {
        try {
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                mViewModel.createImageFileHolder(it)?.let { photoFile ->
                    mUrlFilePicture = photoFile.absolutePath
                    EasyGolfNavigation.dispatchTakePictureCameraIntent(this, photoFile)
                }
            }
        } catch (e: Exception) {
            showCommonMessage(e.localizedMessage)
        }
    }

    private fun galleryAddPic(currentPhotoPath: String) {
        val resolver = contentResolver
        val file = File(currentPhotoPath)
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
            resolver.openOutputStream(uri).use {
                it?.write(file.inputStream().readBytes())
            }
            mIsGetPictureFromGallery = true
        }

//        mUrlFilePicture?.let { urlFilePicture ->
//            mViewModel.deleteFileImage(urlFilePicture)
//        }
        onRefreshData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Contains.REQUEST_CODE_CAMERA -> {
                mUrlFilePicture?.let { urlFilePicture ->
                    if (resultCode == Activity.RESULT_OK) {
                        galleryAddPic(urlFilePicture)
                    } else {
                        mViewModel.deleteFileImage(urlFilePicture)
                    }
                }
            }
        }
    }

    override fun loadData() {
        /**
         * view camera icon
         * */
        mEasyGolfGalleryAdapter?.clearAll()
        mEasyGolfGalleryAdapter?.openReachEnd()
        mEasyGolfGalleryAdapter?.addItem(null)

        mViewModel.listGalleryLiveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                if (mStatusLoading == StatusLoading.REFRESH_LOAD || mStatusLoading == StatusLoading.FIRST_LOAD) {
                    mEasyGolfGalleryAdapter?.addListItem(it)
                    if (mIsGetPictureFromGallery) {
                        mEasyGolfGalleryAdapter?.autoSelectPicFromCamera()
                        mIsGetPictureFromGallery = false
                    }
                    hideRefreshLoading()
                } else {
                    mEasyGolfGalleryAdapter?.addListItem(it)
                }
            } else {
                mEasyGolfGalleryAdapter?.onReachEnd()
                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    mEasyGolfGalleryAdapter?.onReachEnd()
                }
            }

            if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
        })

        EasyGolfNavigation.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Contains.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION, this) {
            mViewModel.getExternalGallery(this)
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        mViewModel.clearCursor()

        mEasyGolfGalleryAdapter?.clearAll()
        mEasyGolfGalleryAdapter?.openReachEnd()
        mEasyGolfGalleryAdapter?.addItem(null)
        EasyGolfNavigation.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Contains.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION, this) {
            mViewModel.getExternalGallery(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Contains.REQUEST_CODE_EXTERNAL_STORAGE_PERMISSION -> {
                if (permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    mViewModel.getExternalGallery(this)
                } else {
                    mEasyGolfGalleryAdapter?.onReachEnd()
                }
            }
            Contains.REQUEST_CODE_CAMERA_PERMISSION -> {
                if (permissions.contains(Manifest.permission.CAMERA)) {
                    dispatchTakePictureCamera()
                }
            }
        }
    }
}