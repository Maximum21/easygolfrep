package com.minhhop.easygolf.presentation.club

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.extension.loadImage
import kotlinx.android.synthetic.main.item_course_normal.view.*
import kotlin.math.roundToInt

class ClubAdapter(context: Context, private val isLarge:Boolean = false, private val courseEvent: CourseEvent) : EasyGolfRecyclerViewAdapter<Club>(context) {
    companion object{
        private const val TYPE_ITEM_NORMAL = 0x0003
        private const val TYPE_ITEM_LARGE = 0x0004
    }
    private val mNormalMargin = context.resources.getDimension(R.dimen.normal_margin).toInt()
    override fun setLayout(viewType: Int): Int{
        return if(viewType == TYPE_ITEM_NORMAL){
            R.layout.item_course_normal
        }else{
            R.layout.item_course_large
        }
    }

    override fun setLayoutLoadMore(): Int = R.layout.home_bottom_load_more

    override fun customViewType(position: Int): Int {
        return if(!isLarge) TYPE_ITEM_NORMAL else TYPE_ITEM_LARGE
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView:View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.viewRoot.setOnClickListener {
                courseEvent.onClickListener(getItem(adapterPosition))
            }
        }

        fun bindData(position: Int){
            val model = getItem(position)
            itemView.txtNameCourse.text = model.name
            itemView.textLocation?.text = model.address
            itemView.imgCourse.loadImage(model.image)
            itemView.viewRating.setRatting(model.rating.roundToInt())
            if(customViewType(position) == TYPE_ITEM_LARGE) {
                (itemView.viewRoot.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = if (position >= mDataList.size - 1) {
                    mNormalMargin
                } else 0
            }
        }
    }

    interface CourseEvent{
        fun onClickListener(club: Club)
        fun searchClubs()
    }
}