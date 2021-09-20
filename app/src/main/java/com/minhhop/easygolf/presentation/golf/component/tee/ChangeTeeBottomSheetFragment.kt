package com.minhhop.easygolf.presentation.golf.component.tee

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.minhhop.core.domain.golf.Tee
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.presentation.golf.play.PlayGolfViewModel
import kotlinx.android.synthetic.main.change_tee_bottom_sheet_fragment.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ChangeTeeBottomSheetFragment : EasyGolfBottomSheetFragment<PlayGolfViewModel>(), OnTeeListener {

    private var mSegmentTeeAdapter: SegmentTeeAdapter? = null
    override val mViewModel: PlayGolfViewModel
            by sharedViewModel()

    override fun setLayout(): Int = R.layout.change_tee_bottom_sheet_fragment

    override fun initView(viewRoot: View) {
        /**
         * init views
         * */
        context?.let { context ->
            mSegmentTeeAdapter = SegmentTeeAdapter(context, this)
        }

        mViewModel.mCurrentHoleLiveData.value?.tees?.let { tees ->
            viewRoot.listTee.layoutManager = GridLayoutManager(context, if (tees.size < 5) tees.size else 5)
            viewRoot.listTee.adapter = mSegmentTeeAdapter
            var indexTee = -1

            tees.forEachIndexed { index, tee ->
                if (tee.type == mViewModel.mPlayGolfBundle?.teeType) {
                    indexTee = index
                    return@forEachIndexed
                }
            }
            mSegmentTeeAdapter?.setIndexSelected(indexTee)
            mSegmentTeeAdapter?.setDataList(tees)
        }



        viewRoot.btSave.setOnClickListener {
            mSegmentTeeAdapter?.getSelectedTee()?.let{ tee->
                mViewModel.onChangeTee(tee)
                collapse()
            }

        }
    }

    override fun loadData() {

    }

    override fun tag(): String = "ChangeTeeBottomSheetFragment"
    override fun onClickTee(tee: Tee?, position: Int) {

    }
}