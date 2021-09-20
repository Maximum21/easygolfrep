package com.minhhop.easygolf.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.presentation.golf.component.score.picker.ScoreListAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.listeners.EventSelectMoreScore
import com.minhhop.easygolf.framework.common.Contains

class WozListScoreMoreActivity : WozBaseActivity() {
    override fun loadData() {

    }

    override fun setLayoutView(): Int {
        return R.layout.activity_woz_list_score
    }

    override fun initView() {
        val par = intent.getIntExtra(Contains.EXTRA_PAR, 1)
        val adapterScore = ScoreListAdapter(this,par) { score ->
            val intent = Intent()
            val bundle = Bundle()
            bundle.putInt(Contains.EXTRA_PAR, score)
            intent.putExtras(bundle)

            setResult(Contains.REQUEST_SCORE_MORE, intent)
            overridePendingTransition(R.anim.show_alpha, R.anim.hide_alpha)
            onBackPressed()
        }

//                , par, object : EventSelectMoreScore {
//            override fun onSelected(score: Int) {
//                val intent = Intent()
//                val bundle = Bundle()
//                bundle.putInt(Contains.EXTRA_PAR, score)
//                intent.putExtras(bundle)
//
//                setResult(Contains.REQUEST_SCORE_MORE, intent)
//                overridePendingTransition(R.anim.show_alpha, R.anim.hide_alpha)
//                onBackPressed()
//            }
//
//        })
        val listData = ArrayList<Int>()
        for (i in 1..99) {
            listData.add(i)
        }
        adapterScore.addListItem(listData)

        findViewById<RecyclerView>(R.id.listScore).apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterScore
        }


        findViewById<View>(R.id.btClose).setOnClickListener {
            onBackPressed()
        }
    }

}