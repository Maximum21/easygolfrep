package com.minhhop.easygolf.framework.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import java.util.*

abstract class EasyGolfRecyclerViewAdapter<T>(protected val context: Context) : RecyclerView.Adapter<EasyGolfRecyclerViewHolder>(){

    companion object{
        private const val TYPE_PROGRESS_LOAD_MORE = 0x0001
        const val TYPE_ITEM = 0x0002
    }
    private var mIsRegisterLoadMore:Boolean = false
    var mDataList = ArrayList<T>()

    private var mEndlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener? = null
    private var mIsReachEnd:Boolean = false

    protected abstract fun setLayout(viewType: Int): Int

    protected abstract fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EasyGolfRecyclerViewHolder {
        return if (viewType == TYPE_PROGRESS_LOAD_MORE) {
            val viewRoot = LayoutInflater.from(context)
                    .inflate(setLayoutLoadMore(), parent, false)
            LoadMoreViewHolder(viewRoot)
        }else {
            val viewRoot = LayoutInflater.from(context).inflate(setLayout(viewType), parent, false)
            setViewHolder(viewRoot,viewType)
        }
    }

    open fun setLayoutLoadMore() = R.layout.easygolf_bottom_load_more

    override fun getItemViewType(position: Int): Int = if (position == getBottomItemPosition() && mIsRegisterLoadMore) {
        TYPE_PROGRESS_LOAD_MORE
    }else customViewType(position)

    open fun customViewType(position: Int):Int = TYPE_ITEM

    override fun getItemCount(): Int = if (mIsRegisterLoadMore) calTotal() + 1 else calTotal()

    open fun calTotal() = mDataList.size

    open fun getRealCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        (holder as? EasyGolfRecyclerViewAdapter<*>.LoadMoreViewHolder)?.mLayoutProgress?.visibility =
                if (mIsReachEnd) View.GONE else View.VISIBLE
    }


    fun registerLoadMore(
            layoutManager: RecyclerView.LayoutManager,
            recyclerView: RecyclerView, loadMoreCallback: ()->Unit) {

        mIsRegisterLoadMore = true
        mIsReachEnd = false

        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    when (getItemViewType(position)) {
                        TYPE_PROGRESS_LOAD_MORE -> return layoutManager.spanCount
                        TYPE_ITEM -> return 1
                    }
                    return 0
                }
            }
        }

        this.mEndlessRecyclerViewScrollListener =
                object : EndlessRecyclerViewScrollListener(layoutManager) {
                    override fun onLoadMore() {
                        if(!mIsReachEnd){
                            loadMoreCallback()
                        }
                    }
                }

        recyclerView.addOnScrollListener(this.mEndlessRecyclerViewScrollListener!!)
    }

    fun isReachEnd() = mIsReachEnd
    fun onReachEnd() {
        mIsReachEnd = true
        notifyItemChanged(getBottomItemPosition())
    }

    fun openReachEnd() {
        mEndlessRecyclerViewScrollListener?.resetState()
        mIsReachEnd = false
        notifyItemChanged(getBottomItemPosition())
    }

    private fun getBottomItemPosition(): Int {
        return itemCount - 1
    }
    open fun addListItem(itemList: List<T>) {
        val start = itemCount
        mDataList.addAll(itemList)
        this.notifyItemRangeInserted(start,itemList.size)
    }

    open fun addItem(item: T) {
        mDataList.add(item)
        this.notifyItemInserted(itemCount)
    }

    open fun addItemAt(item: T,position: Int) {
        mDataList.add(position,item)
        this.notifyItemInserted(position)
    }

    open fun removeItemAt(position: Int) {
        this.notifyItemRemoved(position)
        mDataList.removeAt(position)
    }

    open fun replaceItem(item: T,position: Int) {
        mDataList[position] = item
        this.notifyItemChanged(position)
    }

    fun getItem(position: Int) = mDataList[position]

    fun clearAll(){
        mDataList.clear()
        notifyDataSetChanged()
    }

    open fun setDataList(itemList: List<T>) {
        val clone = ArrayList(itemList)
        mDataList.clear()
        mDataList.addAll(clone)
        this.notifyDataSetChanged()
    }

    fun getData() = mDataList

    inner class LoadMoreViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){
        val mLayoutProgress: View = itemView.findViewById(R.id.layoutProgress)
    }
}