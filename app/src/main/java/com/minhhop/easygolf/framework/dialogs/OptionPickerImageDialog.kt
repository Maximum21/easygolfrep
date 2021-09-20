package com.minhhop.easygolf.framework.dialogs

import android.content.Context
import android.content.Intent
import android.view.View
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfDialog
import kotlinx.android.synthetic.main.dialog_option.*

class OptionPickerImageDialog(context: Context) : EasyGolfDialog(context) {

    private var mOptionPickerImageListener:OptionPickerImageListener? = null

    override fun initView() {
        btCamera.setOnClickListener(this)
        btGallery.setOnClickListener(this)
    }

    fun addListener(optionPickerImageListener:OptionPickerImageListener):OptionPickerImageDialog{
        this.mOptionPickerImageListener = optionPickerImageListener
        return this
    }
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btCamera -> {
                mOptionPickerImageListener?.takePhoto()
                dismiss()
            }
            R.id.btGallery -> {
                mOptionPickerImageListener?.getFromGallery()
                dismiss()
            }
        }
    }

    private fun checkPermissionToGetImage(isCamera: Boolean) {
        if(isCamera){

        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            galleryIntent.type = "image/*"
//            activity.startActivityForResult(galleryIntent, Contains.REQUEST_CODE_PHOTO_GALLERY)
        }
        dismiss()
//        ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        Dexter.withActivity(mActivity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//                .withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                        if (report.isAnyPermissionPermanentlyDenied) {
//                            val dataExplanation = context.getString(R.string.explanation,
//                                    report.deniedPermissionResponses[0]
//                                            .permissionName)
//                            Snackbar.make(mViewRoot!!, dataExplanation, Snackbar.LENGTH_INDEFINITE)
//                                    .setAction("ok") { v: View? ->
//                                        val intent = Intent()
//                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                        val uri = Uri.fromParts("package", context.packageName, null)
//                                        intent.data = uri
//                                        context.startActivity(intent)
//                                    }.show()
//                        }
//                        if (report.areAllPermissionsGranted()) {
//                            if (isCamera) {
//                                AppUtil.dispatchTakePictureIntent(mActivity)
//                            } else {
//                                AppUtil.pickImageGallery(mActivity)
//                            }
//                        }
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
//                        token.continuePermissionRequest()
//                    }
//                }).onSameThread().check()
    }

    override fun resContentView(): Int {
        return R.layout.dialog_option
    }

    override fun cancelTouchOutside(): Boolean {
        return true
    }

    interface OptionPickerImageListener{
        fun takePhoto()
        fun getFromGallery()
    }
}