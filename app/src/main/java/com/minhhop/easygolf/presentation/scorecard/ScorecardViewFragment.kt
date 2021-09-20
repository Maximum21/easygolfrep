package com.minhhop.easygolf.presentation.scorecard

import android.content.res.Configuration
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.minhhop.core.domain.golf.Hole
import com.minhhop.core.domain.golf.ScorecardModel
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import kotlinx.android.synthetic.main.fragment_scorecard_view.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

open class ScorecardViewFragment : EasyGolfFragment<EndGameViewModel>() {
    companion object {
        const val TAG = "ScorecardViewFragment"
    }

    override val mViewModel: EndGameViewModel
            by sharedViewModel()

    override fun setLayout(): Int = R.layout.fragment_scorecard_view

    override fun onConfigurationChanged(newConfig: Configuration) {
       onOrientationChange()
        super.onConfigurationChanged(newConfig)
        mViewModel.listDataScoreGolfLiveData.value?.let {
            mViewModel.getProfileUserInLocal()?.let { user ->
                mViewModel.roundGolfLiveData.value?.holes?.let { holes ->
                    setDataScorecard(holes,it,user.id)
                }
            }
        }
    }

    override fun initView(viewRoot: View) {
        mViewModel.listDataScoreGolfLiveData.observe(viewLifecycleOwner, Observer {
            Log.e("WOW","list score model: ${it?.size}")
            mViewModel.getProfileUserInLocal()?.let { user ->
                mViewModel.roundGolfLiveData.value?.holes?.let { holes ->
                    setDataScorecard(holes,it,user.id)
                }
            }
        })
    }

    open fun setDataScorecard(holes:List<Hole>,dataScore:List<ScorecardModel>?,userId:String?){
        getCurrentBaseTee(holes, dataScore, userId)
    }

    open fun onOrientationChange(){
        viewRoot.scorecardView.clearAllView()
    }

    override fun loadData() {

    }

    private fun getCurrentBaseTee(holes:List<Hole>,dataScore:List<ScorecardModel>?,userId:String?){
        mViewModel.mEndGameBundle?.getScorecards()?.let { dataScorecard ->
            TeeUtils.getTeeByType(dataScorecard, mViewModel.mEndGameBundle?.teeType)?.let { currentBaseTee ->
                viewRoot.scorecardView.initTable(holes, dataScore, userId,currentBaseTee)

                (activity as? EasyGolfActivity<*>)?.hideMask()
            } ?: viewRoot.scorecardView.initTable(holes, dataScore, userId)
        }
    }
}