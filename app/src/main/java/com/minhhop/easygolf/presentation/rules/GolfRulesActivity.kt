package com.minhhop.easygolf.presentation.rules

import android.text.Editable
import android.text.TextWatcher
import android.transition.Explode
import android.util.Log
import android.view.Window
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import kotlinx.android.synthetic.main.activity_golf_rules.*
import org.koin.android.ext.android.inject

class GolfRulesActivity : EasyGolfActivity<GolfRulesViewModel>() {

    private var mRuleGolfAdapter: GolfRuleAdapter? = null
    override val mViewModel: GolfRulesViewModel
            by inject()

    override fun setLayout(): Int {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        window.exitTransition = Explode()


        return R.layout.activity_golf_rules
    }

    override fun initView() {
        back_btn.setOnClickListener {
            finish()
        }
        mViewModel.rulesModel.observe(this, Observer{
            mViewModel.saveRules(it)
            mRuleGolfAdapter?.setDataList(it)
        })

        val layoutManager = LinearLayoutManager(this)
        rules_act_rv.layoutManager = layoutManager
        mRuleGolfAdapter = GolfRuleAdapter(this, rules_act_rv)
        rules_act_rv.adapter = mRuleGolfAdapter
        Log.e("testingmowpss","--yes-")
        toolbarSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mRuleGolfAdapter?.setDataList(mViewModel.getRules(s.toString().trim())?:ArrayList())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    override fun loadData() {
//        var  rulesGolf  : List<GolfRule> = mViewModel.getRules("")?:ArrayList()
//        if(rulesGolf.isNotEmpty()){
//            Log.e("testingmowpss","--no-")
//            mRuleGolfAdapter?.setDataList(rulesGolf)
//        }else{
            mViewModel.getRulesFromServer()
//        }
    }
}