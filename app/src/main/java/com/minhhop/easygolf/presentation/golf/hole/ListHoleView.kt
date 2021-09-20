package com.minhhop.easygolf.presentation.golf.hole

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.core.domain.golf.Hole
import com.minhhop.easygolf.R

class ListHoleView : LinearLayout {

    private var mOnClickHole: ((Hole) -> Unit?)? = null
    private var mListHoleAdapter: ListHoleAdapter? = null

    constructor(context: Context?) : super(context) { initView() }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val paddingStartEnd = context.resources.getDimension(R.dimen.normal_padding).toInt()
        setPadding(paddingStartEnd, 0, paddingStartEnd, 0)
        orientation = VERTICAL

        val listHole = RecyclerView(context)
        addView(listHole)

        val layoutParams = listHole.layoutParams
        layoutParams?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        listHole.requestLayout()

        mListHoleAdapter = ListHoleAdapter(context) { hole ->
            collapse()
            mOnClickHole?.invoke(hole)
        }
        listHole.apply {
            layoutManager = GridLayoutManager(context, 6)
            adapter = mListHoleAdapter
            background = ContextCompat.getDrawable(context, R.drawable.background_border_white)
        }

        collapse()
    }

    fun updateIndexHole(index:Int){
        mListHoleAdapter?.updateIndexSelect(index)
    }

    fun setOnClickHole(onClickHole: (Hole) -> Unit) {
        mOnClickHole = onClickHole
    }

    fun expand() {
        if (visibility != View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
            visibility = View.VISIBLE
            startAnimation(animation)
        }
    }

    fun collapse() {
        if(visibility == View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {}
            })
            startAnimation(animation)
        }
    }

    fun setDataHole(list: List<Hole>) {
        mListHoleAdapter?.setDataList(list)
    }
}
