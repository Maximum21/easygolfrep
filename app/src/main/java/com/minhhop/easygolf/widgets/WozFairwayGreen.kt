package com.minhhop.easygolf.widgets

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R

class WozFairwayGreen : LinearLayout,View.OnClickListener{

    enum class TYPE{
        FAIRWAY,
        GREEN_IN_REGULATION
    }
    private var mIsFinishShowView = false
    private var mType = TYPE.FAIRWAY
    private val mSizeItemFairway = 2
    private val mSizeItemGreenInRegulation = 1

    override fun onClick(v: View?) {
        v?.let {view->
            if(view.id == mBigBrother.id){
                mBigBrother.tag = null
                resetToggle()
            }else{
                 try {
                    view.tag.toString().toInt()


                }catch (e:Exception){
                    e.printStackTrace()
                     null
                }?.let { value->
                     toggleChild(value)
                     mBigBrother.tag = value
                 }
            }
        }
    }

    private var mListViewItem = ArrayList<View>()
    private var mListPositionItem = ArrayList<PointF>()
    private lateinit var mBigBrother:WozItemFairwayGreen

    constructor(context: Context?) : super(context){
        initData(null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initData(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initData(attrs)
    }

    private fun initData(attrs: AttributeSet?){

        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        var valueTitle:String? = null

        attrs?.apply {
            val typedArray = context.obtainStyledAttributes(this,R.styleable.WozFairwayGreen)
            valueTitle = typedArray.getString(R.styleable.WozFairwayGreen_title)
            when(typedArray.getInt(R.styleable.WozFairwayGreen_type,0)){
                0->{
                    mType = TYPE.FAIRWAY
                }
                1->{
                    mType = TYPE.GREEN_IN_REGULATION
                }
            }
            typedArray.recycle()
        }


        addView(createTitle(valueTitle))
        val containerList = createContainerRight()

        val v = createPicker()
        containerList.addView(v)
        v.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        v.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        v.gravity = Gravity.END
        v.requestLayout()

        mBigBrother = WozItemFairwayGreen(context)
        containerList.addView(mBigBrother)

        mBigBrother.apply {
            this.id = View.generateViewId()
            setOnClickListener(this@WozFairwayGreen)
            setIcon(getResIdFairway(0,mType))
            visibility = View.INVISIBLE

            val paramsBigBrother = layoutParams as RelativeLayout.LayoutParams
            paramsBigBrother.width = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
            paramsBigBrother.height = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
            paramsBigBrother.addRule(RelativeLayout.ALIGN_PARENT_END)
            requestLayout()
        }
    }

    private fun createContainerRight(): RelativeLayout{
        val container = RelativeLayout(context)
        addView(container)
        container.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        container.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        container.requestLayout()
        return container
    }

    private fun createTitle(nameTitle:String?):TextView{
        val title = TextView(context)
        title.text = nameTitle
        title.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        return title
    }

    private fun createPicker():LinearLayout{
        val holderPicker = LinearLayout(context)
        holderPicker.orientation = HORIZONTAL

        val length = when(mType){
            TYPE.FAIRWAY->{
                mSizeItemFairway
            }
            TYPE.GREEN_IN_REGULATION->{
                mSizeItemGreenInRegulation
            }
        }

        for (i in 0..length){
           val itemView = WozItemFairwayGreen(context)
            itemView.tag = i
            itemView.setIcon(getResIdFairway( i,mType))
            itemView.setOnClickListener(this)
            holderPicker.addView(itemView)
            val params:LayoutParams? = itemView.layoutParams as LayoutParams
            params?.apply {
                width = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
                height = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
                marginStart =  resources.getDimension(R.dimen.d_10).toInt()
            }
            itemView.requestLayout()

            mListViewItem.add(itemView)
        }

        return holderPicker
    }

    fun getValue():Int?{
        return try {
            (mBigBrother.tag?.toString()?.toInt())
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    private fun toggleChild(index:Int, isAnimation:Boolean = true){
        mBigBrother.visibility = View.VISIBLE
        for (i in 0 until mListViewItem.size) {
            if(isAnimation) {
                mListViewItem[i].animate()
                        .alpha(0f)
                        .x(mBigBrother.x)
                        .y(mBigBrother.y)
                        .setDuration(200)
                        .start()
            }else{
                mListViewItem[i].alpha = 0f
                mListViewItem[i].x = mBigBrother.x
                mListViewItem[i].y = mBigBrother.y
            }
        }

        mBigBrother.setIcon(getResIdFairway(index,mType))
        mBigBrother.registerBigBrother()
        mBigBrother.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
    }

    fun setValueWhenStart(value:Int?){
        if(mIsFinishShowView){
            value?.let { clearValue->
                toggleChild(clearValue,true)
            }?:resetToggle()
        }else{
            val observer = viewTreeObserver
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    for (i in 0 until mListViewItem.size) {
                        val childView = mListViewItem[i]
                        mListPositionItem.add(PointF(childView.x, childView.y))
                    }
                    mIsFinishShowView = true
                    value?.let { clearValue->
                        toggleChild(clearValue,false)
                    }?:resetToggle()
                }
            })

        }

        mBigBrother.tag = value
    }

    private fun resetToggle(){
        mBigBrother.visibility = View.INVISIBLE
        mBigBrother.alpha = 0f
        mBigBrother.tag = null
        for (i in 0 until mListViewItem.size) {
            val childView = mListViewItem[i]
            childView.visibility = View.VISIBLE
            childView.animate()
                    .alpha(1f)
                    .x(mListPositionItem[i].x)
                    .y(mListPositionItem[i].y)
                    .setDuration(200)
                    .start()
        }
    }

    companion object{
        @DrawableRes
        fun getResIdFairway(index: Int,type : TYPE): Int{
            if(type == TYPE.FAIRWAY) {
                return when (index) {
                    0 -> {
                        R.drawable.ic_icon_miss_left_fairway_big_brother
                    }
                    1 -> {
                        R.drawable.ic_icon_correct_big_brother
                    }
                    2 -> {
                        R.drawable.ic_icon_miss_right_fairway_big_brother
                    }
                    else -> {
                        0
                    }
                }
            }else{
                return when (index) {
                    0 -> {
                        R.drawable.ic_icon_no_correct_big_brother
                    }
                    1 -> {
                        R.drawable.ic_icon_correct_big_brother
                    }
                    else -> {
                        0
                    }
                }
            }
        }
    }
}
