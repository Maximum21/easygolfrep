package com.minhhop.easygolf.presentation.round


import android.transition.Explode
import android.view.Window
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.profile.Round
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.EndGameBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_round_history.*
import org.koin.android.ext.android.inject

class RoundHistoryActivity : EasyGolfActivity<RoundHistoryViewModel>(), RoundHistoryAdapter.RoundHistoryEvent {

    private var historyAdapter: RoundHistoryAdapter? = null
    override val mViewModel: RoundHistoryViewModel
            by inject()

    override fun setLayout(): Int {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = Explode()
        return R.layout.activity_round_history
    }

    override fun initView() {
        historyAdapter = RoundHistoryAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)
        rv_round_history.layoutManager = layoutManager

        historyAdapter?.registerLoadMore(layoutManager, rv_round_history) {
            if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                mViewModel.getRoundHistory()
            }
        }
        rv_round_history.adapter = historyAdapter
        mViewModel.roundHistory.observe(this, Observer {
            hideMask()
            if (it.items.isNotEmpty()) {
                if (mStatusLoading == StatusLoading.REFRESH_LOAD
                        || mStatusLoading == StatusLoading.FIRST_LOAD) {
                    historyAdapter?.setDataList(it.items)
                    hideRefreshLoading()
                } else {
                    historyAdapter?.addListItem(it.items)
                }
                if (it.paginator.start <= -1) {
                    historyAdapter?.onReachEnd()
                }
            } else {
                hideRefreshLoading()
                historyAdapter?.onReachEnd()
                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    historyAdapter?.clearAll()
                    historyAdapter?.onReachEnd()
                }
            }

            if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
            hideRefreshLoading()
        })
    }

    override fun loadData() {
        mViewModel.getRoundHistory()
    }

    override fun onClickListener(data: Round, adapterPosition: Int) {
        EasyGolfNavigation.endGameGolfDirection(this, EndGameBundle(data.id,data.tee,null,PlayGolfBundle.TypeGame.BATTLE_GAME))
    }
}