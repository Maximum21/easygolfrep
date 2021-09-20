package com.minhhop.easygolf.widgets.chart

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.easygolf.R
import kotlin.math.roundToInt

class ScoreChartView : FrameLayout {

    private var mAlbatross = 0f
    private var mEagle = 0f
    private var mBirdie = 0f
    private var mPar = 0f
    private var mBogey = 0f
    private var mTwoBogey = 0f
    private var mOthers = 0f
    /**
     * Zone views
     * */
    private lateinit var mTxtPercentAlbatross:TextView
    private lateinit var mTxtPercentEagle:TextView
    private lateinit var mTxtPercentBirdie:TextView
    private lateinit var mTxtPercentPar:TextView
    private lateinit var mTxtPercentBogey:TextView
    private lateinit var mTxtPercent2Bogey:TextView
    private lateinit var mTxtPercentOthers:TextView

    private lateinit var mContainerChart: ConstraintLayout
    constructor(context: Context) : super(context){ init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init()}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}

    private fun init(){
        val viewRoot = LayoutInflater.from(context).inflate(R.layout.view_score_chart,this,false)
        addView(viewRoot)
        mContainerChart = viewRoot.findViewById(R.id.containerChart)
        mTxtPercentAlbatross = viewRoot.findViewById(R.id.txtPercentAlbatross)
        mTxtPercentEagle = viewRoot.findViewById(R.id.txtPercentEagle)
        mTxtPercentBirdie = viewRoot.findViewById(R.id.txtPercentBirdie)
        mTxtPercentPar = viewRoot.findViewById(R.id.txtPercentPar)
        mTxtPercentBogey = viewRoot.findViewById(R.id.txtPercentBogey)
        mTxtPercent2Bogey = viewRoot.findViewById(R.id.txtPercent2Bogey)
        mTxtPercentOthers = viewRoot.findViewById(R.id.txtPercentOthers)
    }


    fun scaleView(listDataLayGolf : List<DataScoreGolf?>?,parsMap:HashMap<Int, Int>) {
        for ((key,value) in parsMap){
            Log.e("WOW","key $key-- value: $value")
        }
        listDataLayGolf?.forEach {
            it?.let { item->
                if(item.score?:0 > 0) {
                    val valueCheck = (item.score?:0) - (parsMap[item.number!!] ?:0)
                    if (valueCheck == 0) {
                        /**
                         * Par
                         * */
                        mPar++
                    } else {
                        if (valueCheck < 0) {
                            if (valueCheck == -1) {
                                /**
                                 * Birdie
                                 * */
                                mBirdie++

                            } else {
                                /**
                                 * Eagle
                                 * */
                                if(valueCheck == -2) {
                                    mEagle++
                                }else{
                                    mAlbatross++
                                }
                            }
                        } else {
                            // > 0
                            if (valueCheck == 1) {
                                /**
                                 * Bogey
                                 * */
                                mBogey++
                            } else {
                                /**
                                 * 2Bogey
                                 * */
                                if(valueCheck == 2) {
                                    mTwoBogey++
                                }else{
                                    mOthers++
                                }
                            }
                        }
                    }
                }
            }
        }


        var total = mAlbatross + mEagle + mBirdie + mPar + mBogey + mTwoBogey + mOthers
        if(total == 0f){
            total = 1f
        }
        val percentAlbatross = ((mAlbatross / total) * 100).roundToInt()
        val percentEagle = ((mEagle / total) * 100).roundToInt()
        val percentBirdie = ((mBirdie / total) * 100).roundToInt()
        val percentPar = ((mPar / total) * 100).roundToInt()
        val percentBogey = ((mBogey / total) * 100).roundToInt()
        val percent2Bogey = ((mTwoBogey / total) * 100).roundToInt()
        val tempOthers = ((mOthers / total) * 100).roundToInt()

        val percentOthers = if(tempOthers > 0){
            100 - percentAlbatross - percentEagle - percentBirdie - percentPar - percentBogey
        }else{
            0
        }
        mTxtPercentAlbatross.text = context.getString(R.string.percent,percentAlbatross.toString())
        mTxtPercentEagle.text = context.getString(R.string.percent,percentEagle.toString())
        mTxtPercentBirdie.text = context.getString(R.string.percent,percentBirdie.toString())
        mTxtPercentPar.text = context.getString(R.string.percent,percentPar.toString())
        mTxtPercentBogey.text = context.getString(R.string.percent,percentBogey.toString())
        mTxtPercent2Bogey.text = context.getString(R.string.percent,percent2Bogey.toString())
        mTxtPercentOthers.text = context.getString(R.string.percent,percentOthers.toString())

        for (i in 0 until mContainerChart.childCount) {
            if (i % 2 == 0) {
                val percent = when (i) {
                    0 -> {
                        mAlbatross / total
                    }
                    2 -> {
                        mEagle / total
                    }
                    4 -> {
                        mBirdie / total
                    }
                    6 -> {
                        mPar / total
                    }
                    8 -> {
                        mBogey / total
                    }
                    10 -> {
                        mTwoBogey / total
                    }
                    12 -> {
                        mOthers / total
                    }
                    else -> {
                        0f
                    }
                }
                val anim = ScaleAnimation(
                        1f, 1f, // Start and end values for the X axis scaling
                        0f, percent, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 1f) // Pivot point of Y scaling
                anim.fillAfter = true // Needed to keep the result of the animation
                anim.duration = 500
                anim.startOffset = 100 * i.toLong()
                mContainerChart.getChildAt(i).startAnimation(anim)
            }
        }
    }
}