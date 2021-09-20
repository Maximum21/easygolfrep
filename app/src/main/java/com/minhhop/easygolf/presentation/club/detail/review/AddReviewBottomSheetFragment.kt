package com.minhhop.easygolf.presentation.club.detail.review

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minhhop.easygolf.R
import kotlinx.android.synthetic.main.dialog_bottom_sheet_add_review.view.*


class AddReviewBottomSheetFragment : BottomSheetDialogFragment() {
    companion object{
        const val TAG = "AddReviewBottomSheetFragment"
    }
    private var mAddReviewBottomSheetFragmentListener:AddReviewBottomSheetFragmentListener? = null
    private var mRate:Int = 1
    private var mContentDefault:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.dialog_bottom_sheet_add_review,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewRating.setRate(mRate)
        view.editContent.setText(mContentDefault)
        view.editContent.setSelection(mContentDefault?.length?:0)
        view.btClose.setOnClickListener {
            dismiss()
        }
        view.btSubmit.setOnClickListener {
            mAddReviewBottomSheetFragmentListener?.onSubmit(view.viewRating.getRate(),view.editContent.text?.toString())
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { setupBottomSheet(it) }
        if (dialog is BottomSheetDialog) {
            dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundColor(Color.TRANSPARENT)
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = STATE_EXPANDED
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        (dialogInterface as? BottomSheetDialog)?.let{ bottomSheetDialog->
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet)
                    ?: return
            bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    fun addListener(addReviewBottomSheetFragmentListener:AddReviewBottomSheetFragmentListener):AddReviewBottomSheetFragment{
        mAddReviewBottomSheetFragmentListener = addReviewBottomSheetFragmentListener
        return this
    }

    fun setStartRate(rate:Int,contentDefault:String?): AddReviewBottomSheetFragment{
        mRate = rate
        mContentDefault = contentDefault
        return this
    }

    interface AddReviewBottomSheetFragmentListener{
        fun onSubmit(rate:Int,content:String?)
    }
}