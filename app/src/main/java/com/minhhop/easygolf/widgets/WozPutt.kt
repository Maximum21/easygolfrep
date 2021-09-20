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
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R

class WozPutt : LinearLayout,View.OnClickListener{

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
    private lateinit var mBigBrother:WozItemPutt

    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView(){

        orientation = HORIZONTAL
        gravity = Gravity.CENTER


        addView(createTitle())
        val containerList = createContainerRight()

        val v = createPicker()
        containerList.addView(v)
        v.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        v.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        v.gravity = Gravity.END
        v.requestLayout()

        mBigBrother = WozItemPutt(context)
        mBigBrother.registerBigBrother()
        containerList.addView(mBigBrother)

        mBigBrother.apply {
            setOnClickListener(this@WozPutt)
            setValueText("0")
            visibility = View.INVISIBLE
            mBigBrother.id = View.generateViewId()
            val paramsBigBrother = layoutParams as RelativeLayout.LayoutParams
            paramsBigBrother.width = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
            paramsBigBrother.height = resources.getDimension(R.dimen.size_item_fairway_green).toInt()
            paramsBigBrother.addRule(RelativeLayout.ALIGN_PARENT_END)
            requestLayout()
        }
    }

    fun getValue():Int?{
        return try {
            (mBigBrother.tag?.toString()?.toInt())
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    fun setValuePuttStart(value:Int?){
        val observer = viewTreeObserver
        value?.let { clearValue->
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    toggleChild(clearValue,false)
                }
            })
        }?: observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                resetToggle()
            }
        })
        mBigBrother.tag = value
    }


    private fun createContainerRight(): RelativeLayout{
        val container = RelativeLayout(context)
        addView(container)
        container.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        container.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        container.requestLayout()
        return container
    }

    private fun createTitle():TextView{
        val title = TextView(context)
        title.text = resources.getString(R.string.putts)
        title.setTextColor(ContextCompat.getColor(context,R.color.colorBlack))
        return title
    }

    private fun createPicker():LinearLayout{
        val holderPicker = LinearLayout(context)
        holderPicker.orientation = HORIZONTAL

        for (i in 0..4){
           val itemView = WozItemPutt(context)
            itemView.tag = i
            var value = i.toString()
            if(i == 4){
                value += "+"
            }
            itemView.setValueText(value)
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


    private fun toggleChild(index:Int,isAnimation:Boolean = true){
        if(mListPositionItem.size < mListViewItem.size){
            for (i in 0 until mListViewItem.size) {
                mListPositionItem.add(PointF(mListViewItem[i].x, mListViewItem[i].y))
            }
        }
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

        var value = index.toString()
        if(index == 4){
            value += "+"
        }
        mBigBrother.setValueText(value)
        mBigBrother.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

    }

    private fun resetToggle(){
        if(mListPositionItem.size < mListViewItem.size){
            for (i in 0 until mListViewItem.size) {
                mListPositionItem.add(PointF(mListViewItem[i].x, mListViewItem[i].y))
            }
        }
        mBigBrother.visibility = View.INVISIBLE
        mBigBrother.alpha = 0f
        for (i in 0 until mListViewItem.size) {
            mListViewItem[i].visibility = View.VISIBLE
            mListViewItem[i].animate()
                    .alpha(1f)
                    .x(mListPositionItem[i].x)
                    .y(mListPositionItem[i].y)
                    .setDuration(200)
                    .start()
        }
    }
}
