package com.minhhop.easygolf.presentation.golf.component.score

import android.view.View
import androidx.lifecycle.Observer
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.Hole
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView
import com.minhhop.easygolf.presentation.golf.component.score.picker.PickerScoreMoreFragment
import com.minhhop.easygolf.presentation.golf.play.PlayGolfViewModel
import kotlinx.android.synthetic.main.fragment_enter_scorecard_bottom_sheet.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.*

class EnterScoreBottomSheetFragment : EasyGolfBottomSheetFragment<PlayGolfViewModel>(), EasyGolfPickerScoreView.EasyGolfPickerScoreListener {

    override val mViewModel: PlayGolfViewModel by sharedViewModel()
    private var mRoundId: String? = null
    private var mHole: Hole? = null
    private var mUser: User? = null
    private var mOnEnterScoreCallback: OnEnterScoreCallback? = null
    override fun setLayout(): Int = R.layout.fragment_enter_scorecard_bottom_sheet

    override fun initView(viewRoot: View) {
        viewRoot.btCancel.setOnClickListener {
            collapse()
        }
        mUser?.let { user ->
            setInfoUser(user)
        } ?: mViewModel.getProfileUser()?.let { user ->
            setInfoUser(user)
        }
        viewRoot.easyGolfPickerScoreView.setEventMoreScore(this)

        //get score from local
        mRoundId?.let { roundId ->
            mHole?.let { hole ->
                mUser?.let { user ->
                    mViewModel.getScoreForUserInBattle(roundId, hole, user) { dataScoreGolf ->
                        updateDataScore(dataScoreGolf)
                    }
                } ?: mViewModel.getScoreGolfAtHoleInLocal(roundId, hole.hole_id)
            }
        }
        mViewModel.updateScoreLiveData.observe(viewLifecycleOwner, Observer { dataScoreGolfMap ->
            mViewModel.getProfileUser()?.let { user ->
                updateDataScore(dataScoreGolfMap?.get(user.id))
            }
        })
        /**
         * start init data
         * */
        viewRoot.btSave.setOnClickListener {
            mRoundId?.let { roundId ->
                mHole?.let { hole ->
                    mViewModel.insertOrUpdateDataScoreGolf(roundId, hole, DataScoreGolf(
                            Calendar.getInstance().timeInMillis.toString(),
                            viewRoot.easyGolfPickerScoreView.getScore(),
                            viewRoot.wozFairway.getValue(),
                            viewRoot.wozGreen.getValue(),
                            viewRoot.wozPutt.getValue(),
                            roundId,
                            hole.number
                    ), mUser)
                    collapse()
                    mOnEnterScoreCallback?.onSave()
                }
            }
        }
    }

    private fun updateDataScore(dataScoreGolf: DataScoreGolf?) {
        viewRoot.easyGolfPickerScoreView.setPar(mHole?.par, dataScoreGolf?.score)
        viewRoot.wozFairway.setValueWhenStart(dataScoreGolf?.fairwayHit)
        viewRoot.wozGreen.setValueWhenStart(dataScoreGolf?.greenInRegulation)
        viewRoot.wozPutt.setValuePuttStart(dataScoreGolf?.putts)
    }

    private fun setInfoUser(user: User) {
        viewRoot.txtName.text = user.fullName
        viewRoot.imgAvatar.loadImage(user.avatar, R.drawable.ic_icon_user_default)
        viewRoot.txtHole.text = mHole?.number?.toString()
        viewRoot.txtPar.text = mHole?.par?.toString()
        viewRoot.txtSL.text = mHole?.index?.toString()
    }

    fun addCallback(onEnterScoreCallback: OnEnterScoreCallback): EnterScoreBottomSheetFragment {
        this.mOnEnterScoreCallback = onEnterScoreCallback
        return this
    }

    fun setInfoHole(roundId: String, hole: Hole, user: User? = null): EnterScoreBottomSheetFragment {
        mRoundId = roundId
        mHole = hole
        mUser = user
        return this
    }

    override fun loadData() {

    }

    override fun tag(): String = "EnterScorecardBottomSheetFragment"

    override fun onClickMore() {
        mHole?.let { hole ->
            PickerScoreMoreFragment()
                    .addListener(hole.par, object : PickerScoreMoreFragment.PickerScoreMoreListener {
                        override fun onSelected(score: Int) {
                            viewRoot.easyGolfPickerScoreView.updateValue(score)
                        }
                    })
                    .show(childFragmentManager)
        }
    }

    interface OnEnterScoreCallback {
        fun onSave()
    }
}