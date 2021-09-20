package com.minhhop.easygolf.presentation.dialog

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.presentation.dialog.select.CommonSelectSheetViewModel
import kotlinx.android.synthetic.main.dialog_common_select_bototm_sheet.view.*

class CommonSelectBottomSheetDialog : EasyGolfBottomSheetFragment<CommonSelectSheetViewModel>() {
    private val mListSectionViews = ArrayList<String>()
    private var mMarginSectionView: Int = 0
    override val mViewModel: CommonSelectSheetViewModel? = null

    override fun setLayout(): Int = R.layout.dialog_common_select_bototm_sheet

    override fun initView(viewRoot: View) {
        mMarginSectionView = context?.resources?.getDimension(R.dimen.large_padding)?.toInt() ?: 0
        viewRoot.containerView.removeAllViews()
        mListSectionViews.forEachIndexed { index, titleSection ->
            context?.let { context ->
                val sectionView = MaterialTextView(context)
                sectionView.text = titleSection
                sectionView.gravity = Gravity.CENTER
                sectionView.setTextColor(ContextCompat.getColor(context, R.color.colorLinker))
                TextViewCompat.setTextAppearance(sectionView, R.style.normal)
                sectionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.x_normal_font_size))

                viewRoot.containerView.addView(sectionView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                (sectionView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = mMarginSectionView
                sectionView.setOnClickListener {
                    collapse{
                        mSectionCallback?.onSelectSectionView(index)
                    }
                }
            }

        }
    }

    fun addSectionView(title: Array<String>): CommonSelectBottomSheetDialog {
        mListSectionViews.addAll(title)
        return this
    }

    override fun loadData() {

    }

    override fun tag(): String = "CommonSelectBottomSheetDialog"

    private var mSectionCallback: SectionCallback? = null
    fun addCallback(sectionCallback: SectionCallback): CommonSelectBottomSheetDialog {
        mSectionCallback = sectionCallback
        return this
    }

    interface SectionCallback {
        fun onSelectSectionView(position: Int)
    }
}