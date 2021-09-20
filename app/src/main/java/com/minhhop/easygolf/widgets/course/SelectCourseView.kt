package com.minhhop.easygolf.widgets.course

import android.animation.Animator
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.SelectCourseAdapter
import com.minhhop.easygolf.framework.models.CourseClub


class SelectCourseView :RelativeLayout {

    private lateinit var mSelectCourseAdapter: SelectCourseAdapter
    private var mEventOnCourse: EventOnCourse? = null

    private lateinit var mLayoutContent:LinearLayout

    constructor(context: Context?) : super(context){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private fun initView(){

        setOnClickListener {
            hideAnimation()
        }

        setBackgroundColor(ContextCompat.getColor(context, R.color.background_select_course))
        mSelectCourseAdapter = SelectCourseAdapter(context)

        mLayoutContent = LinearLayout(context)
        mLayoutContent.orientation = LinearLayout.VERTICAL
        addView(mLayoutContent)
        val layoutParamsContent = mLayoutContent.layoutParams as LayoutParams?
        layoutParamsContent?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            addRule(CENTER_VERTICAL)
            addRule(CENTER_HORIZONTAL)
            val valuePadding = context.resources.getDimension(R.dimen.d_20)
            setMargins(valuePadding.toInt(),0,valuePadding.toInt(),valuePadding.toInt())
            requestLayout()
        }

        mLayoutContent.setOnClickListener {  }
        /**
         * Start Zone main data
         * */

        val layoutData = LinearLayout(context)
        layoutData.orientation = LinearLayout.VERTICAL
        layoutData.background = context.getDrawable(R.drawable.bg_border_white)
        mLayoutContent.addView(layoutData)

        val layoutParamsData = layoutData.layoutParams as LinearLayout.LayoutParams?
        layoutParamsData?.apply {
            this.width = ViewGroup.LayoutParams.MATCH_PARENT
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            val valuePadding = context.resources.getDimension(R.dimen.d_5).toInt()
            setPadding(valuePadding,valuePadding,valuePadding,valuePadding)
            requestLayout()
        }


        val title = TextView(context)
        title.isAllCaps = true
        title.text = context.getString(R.string.select_course)
        title.setTypeface(title.typeface,Typeface.BOLD)
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.font_small))
        title.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        title.setTextColor(ContextCompat.getColor(context,R.color.color_text_title_select_course))
        layoutData.addView(title)

        val layoutParamsTitle = title.layoutParams as LinearLayout.LayoutParams?
        layoutParamsTitle?.apply {
            this.width = ViewGroup.LayoutParams.MATCH_PARENT
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            topMargin = context.resources.getDimension(R.dimen.d_15).toInt()
            requestLayout()
        }


        val listData = RecyclerView(context)
        layoutData.addView(listData)

        listData.layoutManager = LinearLayoutManager(context)
        listData.adapter = mSelectCourseAdapter

        val layoutParamsListData = listData.layoutParams as LinearLayout.LayoutParams?
        layoutParamsListData?.apply {
            this.width = ViewGroup.LayoutParams.MATCH_PARENT
            this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            topMargin = context.resources.getDimension(R.dimen.d_5).toInt()
            bottomMargin = context.resources.getDimension(R.dimen.d_10).toInt()
            requestLayout()
        }

        /**
         * End Zone main data
         * */


        val btCancel = ImageView(context)
        btCancel.setImageResource(R.drawable.ic_icon_close_task)

        mLayoutContent.addView(btCancel)
        val layoutParamsButton = btCancel.layoutParams as LinearLayout.LayoutParams?
        layoutParamsButton?.apply {
            topMargin = context.resources.getDimension(R.dimen.d_5).toInt()
            marginEnd =  context.resources.getDimension(R.dimen.d_5).toInt()
            this.width = ViewGroup.LayoutParams.WRAP_CONTENT
            this.gravity = Gravity.END + Gravity.TOP
            this.height = context.resources.getDimension(R.dimen.d_25).toInt()
            requestLayout()
        }

        btCancel.setOnClickListener {
            hideAnimation()
        }

        hide()
    }


    fun show(){
        visibility = View.VISIBLE
        mLayoutContent.animate()
                .translationY(0f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(300)
                .start()


        animate()
                .alpha(1f)
                .setDuration(150)
                .setInterpolator(DecelerateInterpolator())
                .setListener(object :Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                })
                .start()
    }

    fun hide(){
        visibility = View.INVISIBLE
        alpha = 0f
        mLayoutContent.translationY = 1000f
    }

    private fun hideAnimation(){

        mLayoutContent.animate()
                .translationY(1000f)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(200)
                .start()
        animate()
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .setListener(object :Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}

                })
                .start()
    }

    fun setData(listData:List<CourseClub>,eventOnCourse: EventOnCourse){
        mEventOnCourse = eventOnCourse
        mSelectCourseAdapter.setEvent(eventOnCourse)
        mSelectCourseAdapter.setDataList(listData)
    }
}