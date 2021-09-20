package com.minhhop.easygolf.framework.dialogs

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.CoreDialog
import com.minhhop.easygolf.listeners.EventMoreScore
import com.minhhop.easygolf.listeners.WozPickerPointSelected
import com.minhhop.easygolf.listeners.WozSelected
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.widgets.WozFairwayGreen
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView
import com.minhhop.easygolf.widgets.WozPutt
import com.squareup.picasso.Picasso

class ScoreGolfDialog(context: Context,
                      val hole:Int, val par:Int, private val sl:Int,
                      val fullName:String? = null,
                      private val avatarPlayer:String? = null) : CoreDialog(context), WozPickerPointSelected{

    override fun onSelected(score: Int, resWhite: Int, res: Int) {
        mScore = score
        mResWhiteId = resWhite
        mResId = res
    }

    override fun moreScore() {
        mEventMoreScore?.callMe()
    }

    private var mScore = 0
    private var mFairway = 0
    private var mGreen = 0
    private var mPutt = 0

    private var mResWhiteId = 0
    private var mResId = 0

    private var mEasyGolfPickerScoreView: EasyGolfPickerScoreView? = null

    private var mTxtHole:TextView? = null
    private var mTxtPar:TextView? = null
    private var mTxtSL:TextView? = null
    private var mWozFairway:WozFairwayGreen? = null
    private var mWozGreen:WozFairwayGreen? = null
    private var mWozPutt:WozPutt? = null
    private lateinit var mImgAvatar:ImageView
    private lateinit var mTxtName:TextView

    private var mEventMoreScore: EventMoreScore? = null

    override fun initView() {
        mEasyGolfPickerScoreView = findViewById(R.id.wozScore)
        findViewById<Button>(R.id.btSubmit).setOnClickListener(this)
        mEasyGolfPickerScoreView?.apply {
//            setEventMoreScore(this@ScoreGolfDialog)
//            onStart(par,mScore,mScore > 0)
        }

        mImgAvatar = findViewById(R.id.imgAvatar)
        mTxtName = findViewById(R.id.txtName)

        if(fullName.isNullOrEmpty()) {
            DatabaseService.getInstance().currentUserEntity?.apply {
                Picasso.get().load(avatar)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgAvatar)
                mTxtName.text = fullName
            }
        }else{
            if(avatarPlayer.isNullOrEmpty()){
                Picasso.get().load(R.drawable.ic_icon_user_default)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgAvatar)
            }else{
                Picasso.get().load(avatarPlayer)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImgAvatar)
            }

            mTxtName.text = fullName
        }

        mWozFairway = findViewById(R.id.wozFairway)
        mWozGreen = findViewById(R.id.wozGreen)
        mWozPutt = findViewById(R.id.wozPutt)
        mTxtHole = findViewById(R.id.txtHole)
        mTxtPar = findViewById(R.id.txtPar)
        mTxtSL = findViewById(R.id.txtSL)

        mTxtHole!!.text = hole.toString()
        mTxtPar!!.text = par.toString()
        mTxtSL!!.text = sl.toString()

        mWozFairway?.apply {
            setValueWhenStart(mFairway)
//            setEvent(object : WozSelected{
//                override fun onSelected(value: Int) {
//                    mFairway = value
//                }
//
//            })
        }

        mWozGreen?.apply {
            setValueWhenStart(mGreen)
//            setEvent(object : WozSelected{
//                override fun onSelected(value: Int) {
//                    mGreen = value
//                }
//
//            })
        }

        mWozPutt?.apply {
            setValuePuttStart(mPutt)
//            setEvent(object : WozSelected{
//                override fun onSelected(value: Int) {
//                    mPutt = value
//                }
//
//            })
        }
    }


    override fun resContentView(): Int {
        return R.layout.dialog_score_golf
    }

    fun setResWhite(value : Int):ScoreGolfDialog{
        this.mResWhiteId = value
        return this
    }

    fun setRes(value : Int):ScoreGolfDialog{
        this.mResId = value
        return this
    }

    fun setScoreWhenStart(value : Int):ScoreGolfDialog{
        this.mScore = value
        return this
    }

    fun updateScore(score: Int){
        mEasyGolfPickerScoreView?.apply {
            mScore = score
//            onStart(par,mScore,mScore > 0)
        }
    }

    fun setFairwayWhenStart(value : Int):ScoreGolfDialog{
        this.mFairway = value
        return this
    }

    fun setGreenWhenStart(value : Int):ScoreGolfDialog{
        this.mGreen = value
        return this
    }

    fun setPuttWhenStart(value : Int):ScoreGolfDialog{
        this.mPutt = value
        return this
    }

    fun setEventScore(eventMoreScore: EventMoreScore):ScoreGolfDialog{
        this.mEventMoreScore = eventMoreScore
        return this
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            if(id == R.id.btSubmit){
                mEventMoreScore?.saveMe(mScore,mFairway,mGreen,mPutt,mResWhiteId,mResId)
                dismiss()
            }
        }
    }

    override fun handlerActionClose() {
        mEventMoreScore?.close()
    }
}