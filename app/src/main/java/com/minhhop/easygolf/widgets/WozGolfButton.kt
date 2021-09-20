package com.minhhop.easygolf.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.dialogs.ScoreGolfDialog
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import com.minhhop.easygolf.listeners.EventMoreScore
import com.minhhop.easygolf.listeners.EventWozButtonGolf

class WozGolfButton : FrameLayout , View.OnClickListener{

    private var mPar = 0
    private var mHole = 0
    private var mSL = 0

    private var mDataPlayGolf = DataPlayGolf()

    private var mContainerScore:View
    private var mValueScore:TextView
    private var mEmptyScore:ImageView
    private var mEveWozButtonGolf:EventWozButtonGolf? = null

    private var mWozDialog:ScoreGolfDialog? = null

    private var mIsFinishDialog = false

    override fun onClick(v: View?) {
        if(!mIsFinishDialog) {
            mIsFinishDialog = true
            showDialogScore()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.woz_golf_button,this)
        setOnClickListener(this)
        mContainerScore = findViewById(R.id.containerScore)
        mValueScore = findViewById(R.id.valueScore)
        mEmptyScore = findViewById(R.id.emptyScore)
    }

    fun setEvent(event:EventWozButtonGolf){
        mEveWozButtonGolf = event
    }

    fun initData(hole:Int,par:Int,sl:Int){
        this.mHole = hole
        this.mPar = par
        this.mSL = sl
    }

    fun setDataPlayGolf(value: DataPlayGolf){
        mDataPlayGolf = value

        if(mDataPlayGolf.mValueScore != 0){
            mEmptyScore.visibility = View.GONE
            mContainerScore.visibility = View.VISIBLE
            mValueScore.text = mDataPlayGolf.mValueScore.toString()
            mValueScore.setBackgroundResource(getResWhite(value.mValueScore))
        }else{
            mEmptyScore.visibility = View.VISIBLE
            mContainerScore.visibility = View.GONE
        }
    }

    private fun getResWhite(score: Int):Int{
        return when(val index = mPar - score){
            0->{
                0
            }
            1->{
                R.drawable.circle_single_point_white
            }
            2->{
                R.drawable.circle_double_point_white
            }
            -1->{
                R.drawable.rectangle_single_point_white
            }
            -6->{
                R.drawable.rectangle_single_red_point
            }
            else->{
                if(index > 0){
                    R.drawable.circle_double_point_white
                }else{
                    R.drawable.rectangle_double_point_white
                }
            }

        }
    }

    fun updateScore(score:Int){
        mWozDialog?.updateScore(score)
    }

    fun showDialogScore(){
        mWozDialog = ScoreGolfDialog(context,mHole,mPar,mSL)
                .setEventScore(object : EventMoreScore {
                    override fun close() {
                        mIsFinishDialog = false
                    }

                    override fun saveMe(score: Int, fairway: Int, green: Int, putt: Int,
                                        @DrawableRes resWhite: Int, @DrawableRes res: Int) {

                        mDataPlayGolf.mValueScore = score
                        mDataPlayGolf.mValueFairwayHit = fairway
                        mDataPlayGolf.mValueGreenInRegulation = green
                        mDataPlayGolf.mValuePutt = putt
                        mDataPlayGolf.resId = res
                        mDataPlayGolf.resIdWhite = resWhite

                        if(score > 0){
                            mEmptyScore.visibility = View.GONE
                            mContainerScore.visibility = View.VISIBLE
                            mValueScore.text = score.toString()
                            mValueScore.setBackgroundResource(resWhite)
                        }else{
                            mEmptyScore.visibility = View.VISIBLE
                            mContainerScore.visibility = View.GONE
                        }
                        mEveWozButtonGolf?.onSaveMe(mDataPlayGolf)

                        mIsFinishDialog = false
                    }

                    override fun callMe() {
                        mEveWozButtonGolf?.onMore()
                    }
                })
                .setRes(mDataPlayGolf.resId)
                .setResWhite(mDataPlayGolf.resIdWhite)
                .setFairwayWhenStart(mDataPlayGolf.mValueFairwayHit)
                .setGreenWhenStart(mDataPlayGolf.mValueGreenInRegulation)
                .setPuttWhenStart(mDataPlayGolf.mValuePutt)
                .setScoreWhenStart(mDataPlayGolf.mValueScore)
        mWozDialog?.show()
    }
}