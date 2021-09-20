package com.minhhop.easygolf.widgets.course

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.presentation.golf.component.tee.SegmentTeeAdapter
import com.minhhop.easygolf.framework.models.Tee
import com.minhhop.easygolf.presentation.golf.component.tee.OnTeeListener

class ChangTeeView : RelativeLayout, OnTeeListener {

    private var mSelectTee:Tee? = null
    private var mPositionSelectTee = -1

    override fun onClickTee(tee: com.minhhop.core.domain.golf.Tee?, position: Int) {
//        mSelectTee = tee
        mPositionSelectTee = position
    }

    private lateinit var mLayoutContent: View
    private lateinit var mSegmentTeeAdapter: SegmentTeeAdapter
    private var mOnTeeListener: OnTeeListener? = null

    constructor(context: Context?) : super(context){ initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView(){
        visibility = View.GONE
        mLayoutContent = LayoutInflater.from(context).inflate(R.layout.change_tee_bottom_sheet_fragment,this)
        val valuePadding = context.resources.getDimension(R.dimen.d_15).toInt()
        val valuePaddingTop = context.resources.getDimension(R.dimen.d_100).toInt()
        setPadding(valuePadding,valuePaddingTop, valuePadding, valuePadding)
//        setBackgroundColor(ContextCompat.getColor(context,R.color.color_mask))
        setOnClickListener {  }

        mSegmentTeeAdapter = SegmentTeeAdapter(context, this)
        val listTee = findViewById<RecyclerView>(R.id.listTee)
        listTee.layoutManager = GridLayoutManager(context,5)
        listTee.adapter = mSegmentTeeAdapter


        findViewById<View>(R.id.btSave).setOnClickListener {
//            mOnTeeListener?.onClickTee(mSelectTee,mPositionSelectTee)
            mSelectTee = null
            mPositionSelectTee = -1
            hide()
        }
    }

    fun setData(tees:List<Tee>,indexSelected:Int){
//        mSegmentTeeAdapter.setDataList(tees)
        mSegmentTeeAdapter.setIndexSelected(indexSelected)
    }

    fun setEvent(onSelectTee: OnTeeListener){
        mOnTeeListener = onSelectTee
    }

    fun hide(){
        mLayoutContent.animate()
                .translationY(1000f)
                .setDuration(200)
                .setListener(object :Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                       visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
                .start()


    }

    fun show(){
        visibility = View.VISIBLE
//        mLayoutContent.translationY = -1000f
        mLayoutContent.animate()
                .translationY(0f)
                .setDuration(200)
                .setListener(object :Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
                .start()
    }
}