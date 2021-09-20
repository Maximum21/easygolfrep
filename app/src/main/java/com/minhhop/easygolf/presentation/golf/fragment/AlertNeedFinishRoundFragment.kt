package com.minhhop.easygolf.presentation.golf.fragment

import android.content.res.ColorStateList
import android.view.View
import androidx.core.widget.TextViewCompat
import com.minhhop.core.domain.golf.Course
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.presentation.home.EasyGolfHomeViewModel
import kotlinx.android.synthetic.main.fragment_alert_need_finish_round.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AlertNeedFinishRoundFragment : EasyGolfBottomSheetFragment<EasyGolfHomeViewModel>() {

    override val mViewModel
            by sharedViewModel<EasyGolfHomeViewModel>()

    private var mStopRegisterJob = false
    override fun setLayout(): Int = R.layout.fragment_alert_need_finish_round


    override fun initView(viewRoot: View) {
        viewMask()
        mViewModel.needShowAlertFinishRoundLiveData.value?.let { roundGolf ->
            textTitleFinishRound.text = getString(R.string.title_alert_finish_round,
                    mViewModel.getProfileUserInLocal()?.first_name)
            setData(roundGolf)
        } ?: collapse()

        btContinue.setOnClickListener {
            mViewModel.needShowAlertFinishRoundLiveData.value?.let { roundGolf ->
                context?.let { context ->
                    mStopRegisterJob = true
                    collapse {
                        roundGolf.course?.let { course ->
                            roundGolf.id?.let { roundGolfId->
                                mViewModel.updateStatusPendingInBattleForUser(roundGolfId)
                            }
                            EasyGolfNavigation.playGolfDirection(context,
                                    PlayGolfBundle(mViewModel.getTypeGame(),
                                            roundGolf.id,
                                            course.id,
                                            roundGolf.teeType ?: TeeUtils.DEFAULT_TEE_TYPE,
                                            PlayGolfBundle.toScorecard(course.scorecard))
                            )
                        }
                    }
                }
            }
        }

        btClose.setOnClickListener {
            mStopRegisterJob = false
            collapse()
        }
    }

    override fun stateHidden() {
        super.stateHidden()
        if (!mStopRegisterJob) {
            mViewModel.needShowAlertFinishRoundLiveData.value?.id?.let { roundGolfId ->
                mViewModel.setJobToCheckRound(roundGolfId)
            }
        }
    }

    private fun viewMask() {
        viewMask.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE
        viewMask.startBurnViewAnimation()
    }

    private fun hideMask() {
        mainLayout.visibility = View.VISIBLE
        viewMask.visibility = View.GONE
        viewMask.stopBurnViewAnimation()
    }

    override fun canCanceledOnTouchOutside(): Boolean = false

    private var mCourse: Course? = null
    private fun setData(roundGolf: RoundGolf) {
        textDescriptionFinishRound.text = getString(R.string.description_alert_finish_round, roundGolf.club_name)
        textNameCourse.text = getString(R.string.format_name_course, roundGolf.club_name, roundGolf.course_name)
        TextViewCompat.setCompoundDrawableTintList(textTeeView,
                ColorStateList.valueOf(TeeUtils.getColorByType(roundGolf.teeType)))

        if (!roundGolf.teeType.isNullOrEmpty()) {
            textTeeView.text = getString(R.string.tee_name, roundGolf.teeType
                    ?: getString(R.string.unknown_name))
        } else {
            textTeeView.visibility = View.GONE
        }

        context?.let { context ->
            if (!roundGolf.date.isNullOrEmpty()) {
                textDateStart.text = AppUtils.convertISOToDate(roundGolf.date, context)
            } else {
                textDateStart.visibility = View.GONE
            }

        }
        roundGolf.course?.let { course ->
            imgCourse.loadImage(course.image)
            mCourse = course
        }
        hideMask()
    }


    override fun loadData() {

    }

    override fun tag(): String = "AlertNeedFinishRoundFragment"
}