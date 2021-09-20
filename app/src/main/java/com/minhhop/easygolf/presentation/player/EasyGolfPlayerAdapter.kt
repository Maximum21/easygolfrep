package com.minhhop.easygolf.presentation.player

import android.content.Context
import android.util.Log
import android.view.View
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.GolfUtils
import kotlinx.android.synthetic.main.item_easygolf_player.view.*

class EasyGolfPlayerAdapter(context: Context, private val currentUserId: String? = null) : EasyGolfRecyclerViewAdapter<User?>(context) {

    private var mPlayerViewDeleteHolder: PlayerView? = null
    private var mCurrentScorecard: Scorecard? = null

    /**
     * @param @mUserScore is data user, it can be a score local convert to or from firebase
     * */
    private var mUserDataScore: HashMap<String, DataScoreGolf?>? = null
    private var mEasyGolfPlayerAdapterListener: EasyGolfPlayerAdapterListener? = null
    private var mCurrentPar: Int? = null
    private var mIsShowButtonAddPlayer = true
    private var mIsShowScorePlayer = false
    private var mIsEnableDelete = true

    override fun setLayout(viewType: Int): Int = R.layout.item_easygolf_player

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfPlayerViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfPlayerViewHolder)?.bindData(position)
    }

    fun addListener(easyGolfPlayerAdapterListener: EasyGolfPlayerAdapterListener) {
        mEasyGolfPlayerAdapterListener = easyGolfPlayerAdapterListener
    }

    override fun getRealCount(): Int = if (mIsShowButtonAddPlayer) mDataList.size - 1 else mDataList.size

    fun setDataScore(data: HashMap<String, DataScoreGolf?>?, currentPar: Int?) {
        mUserDataScore = data
        mCurrentPar = currentPar

        notifyDataSetChanged()
    }

    override fun addListItem(itemList: List<User?>) {
        removeAddPlayer(itemList.size)
        super.addListItem(itemList)
    }

    override fun addItem(item: User?) {
        if(mDataList.find { item?.id == it?.id } == null){
            removeAddPlayer()
            super.addItem(item)
        }
    }

    fun enableDelete(isCanDelete:Boolean){
        mIsEnableDelete = isCanDelete
        notifyDataSetChanged()
    }

    private fun removeAddPlayer(size:Int = 1){
        if (itemCount + size > 4) {
            removeItemAt(0)
            mIsShowButtonAddPlayer = false
        }
    }

    fun removeItem(userId:String){
        mDataList.forEachIndexed{ index, player->
            if(player?.id == userId){
                cancelShakeView()
                removeItemAt(index)
                return
            }
        }
    }

    fun removeItemNotEnableCancel(userId:String){
        mDataList.forEachIndexed{ index, player->
            if(player?.id == userId){
                mPlayerViewDeleteHolder?.enableDelete(false)
                mPlayerViewDeleteHolder = null
                removeItemAt(index)
                return
            }
        }
    }

    override fun removeItemAt(position: Int) {
        super.removeItemAt(position)
        if (itemCount < 4 && !mIsShowButtonAddPlayer) {
            mIsShowButtonAddPlayer = true
            addItemAt(null, 0)
        }
    }

    fun showScoreForPLayer(isShow: Boolean = true) {
        mIsShowScorePlayer = isShow
        notifyDataSetChanged()
    }

    fun getListUserExit(): List<User> {
        val results = ArrayList<User>()
        mDataList.map {
            if (it != null && it.id != currentUserId) results.add(it)
        }
        return results
    }

    fun updateScorecard(scorecard: Scorecard) {
        this.mCurrentScorecard = scorecard
        notifyDataSetChanged()
    }

    fun cancelShakeView() {
        mPlayerViewDeleteHolder?.enableDelete(false)
        mPlayerViewDeleteHolder = null
        mEasyGolfPlayerAdapterListener?.onDeletePlayerEnable(null)
    }

    inner class EasyGolfPlayerViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {

        fun bindData(position: Int) {
            val model = getItem(position)
            itemView.playerView.setCanDelete(model?.id != currentUserId && mIsEnableDelete)
            model?.let { user ->
                itemView.playerView.setIsPlayer(true, mIsShowScorePlayer)
                mUserDataScore?.also { userScore ->
                    itemView.playerView.setScoreBottomSheet(userScore[user.id]?.score, mCurrentPar)
                }
                itemView.playerView.setData(
                        user,
                        mCurrentScorecard?.let { scorecard ->
                            GolfUtils.calculateUserHandicapByCourse(scorecard, user)
                        }
                )

            } ?: itemView.playerView.setIsPlayer(false, mIsShowScorePlayer)
            itemView.playerView.addPlayerViewListener(object : PlayerView.PlayerViewListener {
                override fun onDeleteView() {
                    mEasyGolfPlayerAdapterListener?.onDeletePlayerEnable(model)
                    removeItemAt(adapterPosition)
                    mPlayerViewDeleteHolder?.enableDelete(false)
                    mPlayerViewDeleteHolder = null

                }

                override fun onShakeView() {
                    mPlayerViewDeleteHolder?.enableDelete(false)
                    mPlayerViewDeleteHolder = itemView.playerView
                    mEasyGolfPlayerAdapterListener?.onDeletePlayerEnable(null)
                }

                override fun onAddPlayer() {
                    mEasyGolfPlayerAdapterListener?.onAddPlayer()
                }

                override fun onSelected() {
                    mEasyGolfPlayerAdapterListener?.onPlayerClick(model)
                }
            })
        }
    }

    interface EasyGolfPlayerAdapterListener {
        fun onAddPlayer()
        fun onDeletePlayerEnable(user: User?)
        fun onPlayerClick(user: User?)
    }
}