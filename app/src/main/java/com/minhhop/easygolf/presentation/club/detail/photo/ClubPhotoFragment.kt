package com.minhhop.easygolf.presentation.club.detail.photo

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minhhop.core.domain.golf.ClubPhoto
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailActivity
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailViewModel
import kotlinx.android.synthetic.main.fragment_course_photo.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ClubPhotoFragment : EasyGolfFragment<EasyGolfClubDetailViewModel>(),ClubPhotoAdapter.ClubPhotoAdapterListener{

    companion object{
        const val TAG = "ClubPhotoFragment"
    }

    private val mSpanCountPhoto = 4
    private var mClubPhotoAdapter:ClubPhotoAdapter? = null

    override val mViewModel by sharedViewModel<EasyGolfClubDetailViewModel>()

    override fun setLayout(): Int = R.layout.fragment_course_photo

    override fun initView(viewRoot: View) {
        context?.let { context ->
            initListPhoto(context)
        }
    }

    override fun loadData() {
        mViewModel.clubPhotosLiveData.observe(viewLifecycleOwner, Observer {
            mClubPhotoAdapter?.setDataList(it)
        })
        mViewModel.positionDeleteLiveData.observe(viewLifecycleOwner, Observer {
            if(it >= 0 && it < mClubPhotoAdapter?.itemCount?: 0) {
                mClubPhotoAdapter?.removeItemAt(it)
            }
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })
        mViewModel.newPhotoInsertLiveData.observe(viewLifecycleOwner, Observer {
            mClubPhotoAdapter?.addListItemAt(it,0)
            (activity as? EasyGolfClubDetailActivity)?.hideMask()
        })
    }

    private fun initListPhoto(context: Context){
        viewRoot.listClubPhotos.layoutManager = GridLayoutManager(context,mSpanCountPhoto)
        mClubPhotoAdapter = ClubPhotoAdapter(context,mViewModel.getProfileUserInLocal(),mSpanCountPhoto)
        mClubPhotoAdapter?.addListener(this)
        viewRoot.listClubPhotos.adapter = mClubPhotoAdapter
    }

    override fun onDelete(photo: ClubPhoto, position: Int) {
        mViewModel.clubDataLive.value?.let {  club ->
            context?.let { context->
                MaterialAlertDialogBuilder(context)
                        .setTitle(getString(R.string.delete))
                        //TODO change string here
                        .setMessage(getString(R.string.delete_conversation))
                        .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                            (activity as? EasyGolfClubDetailActivity)?.viewMask()
                            mViewModel.deleteClubPhoto(club.id,photo.id,position)
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

            }
        }
    }

    override fun onClick(photo: ClubPhoto, position: Int) {
        //TODO @quipham
    }

    override fun uploadPhoto() {}
}