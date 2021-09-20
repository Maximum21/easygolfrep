package com.minhhop.easygolf.presentation.player.add

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.User
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.extension.loadImage
import kotlinx.android.synthetic.main.item_add_player_view.view.*

class AddPlayerAdapter(context: Context,private val mLimitSelect:Int = -1) : EasyGolfRecyclerViewAdapter<User>(context) {
    private val mListUserIdSelected = HashMap<Int, User>()
    override fun setLayout(viewType: Int): Int = R.layout.item_add_player_view

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = AddPlayerViewHolder(viewRoot)

    override fun setDataList(itemList: List<User>) {
        mListUserIdSelected.clear()
        super.setDataList(itemList)

    }

    private var mAddPlayerAdapterListener: AddPlayerAdapterListener? = null
    fun addListener(addPlayerAdapterListener: AddPlayerAdapterListener) {
        this.mAddPlayerAdapterListener = addPlayerAdapterListener
    }

    fun getResult() = mListUserIdSelected.map { it.value }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? AddPlayerViewHolder)?.bindData(position)
    }

    inner class AddPlayerViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {
        init {
            itemView.viewRoot.setOnClickListener {
                val model = getItem(adapterPosition)
                if (!mListUserIdSelected.containsKey(adapterPosition)) {
                    if ((mLimitSelect > 0 && (mListUserIdSelected.size < mLimitSelect)) || mLimitSelect < 0) {
                        mListUserIdSelected[adapterPosition] = model
                    } else {
                        Toast.makeText(context,
                                context.getString(R.string.limit_add_player, mLimitSelect),
                                Toast.LENGTH_LONG).show()
                    }
                } else {
                    mListUserIdSelected.remove(adapterPosition)
                }
                mAddPlayerAdapterListener?.onChange(mListUserIdSelected.size)
                notifyItemChanged(adapterPosition)
            }
        }
        fun bindData(position: Int) {
            val model = getItem(position)
            itemView.imgAvatar.loadImage(model.avatar, R.drawable.ic_icon_user_default)
            itemView.textName.text = model.fullName
            itemView.textHandicap.text = context.getString(R.string.handicap_format_view, model.handicap.toString())
            val iconCheck = if (mListUserIdSelected.containsKey(position)) R.drawable.ic_icon_check_user else R.drawable.ic_icon_uncheck_user
            itemView.imgChecker.setImageDrawable(ContextCompat.getDrawable(context, iconCheck))
        }

    }

    interface AddPlayerAdapterListener {
        fun onChange(counter: Int)
    }
}
