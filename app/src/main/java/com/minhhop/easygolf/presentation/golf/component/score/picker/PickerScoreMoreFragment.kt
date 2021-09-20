package com.minhhop.easygolf.presentation.golf.component.score.picker

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.presentation.golf.play.PlayGolfViewModel
import kotlinx.android.synthetic.main.activity_woz_list_score.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PickerScoreMoreFragment : EasyGolfBottomSheetFragment<PlayGolfViewModel>() {
    override val mViewModel: PlayGolfViewModel by sharedViewModel()

    private var mPar:Int? = null
    private var mPickerScoreMoreListener:PickerScoreMoreListener? = null
    override fun setLayout(): Int = R.layout.activity_woz_list_score

    override fun initView(viewRoot: View) {
        context?.let { context ->
            val adapterScore = ScoreListAdapter(context,mPar){ score->
                mPickerScoreMoreListener?.onSelected(score)
                dismiss()
            }
            val listData = ArrayList<Int>()
            for (i in 1..99) {
                listData.add(i)
            }
            adapterScore.addListItem(listData)
            viewRoot.listScore.layoutManager = GridLayoutManager(context, 5)
            viewRoot.listScore.adapter = adapterScore
        }

    }

    fun addListener(par:Int?,listener:PickerScoreMoreListener):PickerScoreMoreFragment{
        mPar = par
        mPickerScoreMoreListener = listener
        return this
    }
    override fun loadData() {

    }

    override fun tag(): String = "PickerScoreMoreFragment"

    interface PickerScoreMoreListener{
        fun onSelected(score:Int)
    }
}