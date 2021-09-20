package com.minhhop.easygolf.framework.dialogs

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.firebase.database.FirebaseDatabase
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.utils.AppUtil

class ConfirmDataDialog(private val mActivity: Activity,private val mIdClub:String,private val mIdRoundBattle:String,
                        private val mListener: ListenerConfirmData?) : BaseDialog(mActivity) {

    private lateinit var mProfileUserEntity: UserEntity

    override fun initView() {

        mProfileUserEntity = DatabaseService.getInstance().currentUserEntity!!

        findViewById<View>(R.id.btAccept).setOnClickListener(this)
        findViewById<View>(R.id.btRefuse).setOnClickListener(this)
    }

    override fun resContentView(): Int = R.layout.dialog_confirm_data

    override fun config(window: Window?) {
        super.config(window)
        window?.let { _window->
            val wlp = _window.attributes
            wlp.gravity = Gravity.CENTER
            _window.setLayout((AppUtil.getWidthScreen(mActivity) * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            _window.attributes = wlp
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.let { _v ->
            when (_v.id) {
                R.id.btAccept->{
                    val listDataScore = DatabaseService.getInstance().getDataPlayGolf(mIdClub)
                    for (item in listDataScore){
                        updateScoreToBattle(item)
                    }
                    mListener?.onConfirm()
                    dismiss()
                }
                R.id.btRefuse->{
                    mListener?.onConfirm()
                    dismiss()
                }
            }
        }
    }

    private fun updateScoreToBattle(data: DataPlayGolf){
        if(data.mValueScore > 0 || data.mValueFairwayHit > -1 || data.mValueGreenInRegulation > -1 || data.mValuePutt > -1) {
            val mDbReferenceScore = FirebaseDatabase.getInstance().getReference("rounds")
                    .child(mIdRoundBattle).child("holes")
                    .child((data.numberHole) .toString()).child(mProfileUserEntity.id)
            val dataScore = HashMap<String, Int>()
            if(data.mValueScore > 0) {
                dataScore["score"] = data.mValueScore
            }
            if (data.mValueFairwayHit >= 0)
                dataScore["fairway_hit"] = data.mValueFairwayHit
            if (data.mValueGreenInRegulation >= 0)
                dataScore["green"] = data.mValueGreenInRegulation
            if (data.mValuePutt >= 0)
                dataScore["putts"] = data.mValuePutt
            mDbReferenceScore.setValue(dataScore)
        }
    }

    interface ListenerConfirmData{
        fun onConfirm()
    }



}