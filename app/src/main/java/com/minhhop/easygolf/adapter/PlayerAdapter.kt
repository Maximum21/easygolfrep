package com.minhhop.easygolf.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.common.BattleScorePlayer
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.listeners.OnAddPlayerListener
import com.squareup.picasso.Picasso

class PlayerAdapter(context: Context, private var mIdRoundBattle:String? = null,
                    private val isHost : Boolean  = false,
                    val mOnAddPlayer:OnAddPlayerListener) : BaseRecyclerViewAdapter<ProfileUser>(context){

    companion object{
        private const val TYPE_USER = 0x0003
        private const val TYPE_ADD_PLAYER = 0x0004
    }

    private var isRemoveUser = false
    private var mPar:Int = 0

    private var mDataReferenceScore:DatabaseReference? = null
    private var mEventScore:ChildEventListener? = null

    private var mDataScoreMember:HashMap<String, BattleScorePlayer> = HashMap()

    override fun setLayout(viewType: Int): Int{
        return if(isHost) {
            when (viewType) {
                TYPE_USER -> R.layout.item_add_player
                else -> R.layout.item_add_easygolf_player
            }
        }else{
            R.layout.item_add_player
        }
    }

    fun setIdRoundBattle(idRound:String){
        mIdRoundBattle = idRound
    }


    override fun addItem(item: ProfileUser?) {
        item?.let {
            super.addItem(item)
            mDataScoreMember[it._id] = BattleScorePlayer()
        }

    }

    fun removePlayer(item: ProfileUser){
        for (index in 0 until mDataList.size){
            if(mDataList[index]._id == item._id){

                mDataList.removeAt(index)
                notifyDataSetChanged()
                break
            }
        }
    }

    fun getListFriendByString():String{
        val result = StringBuilder()
        for (item in mDataList){
            if(result.count() > 0) {
                result.append("-")
            }
            result.append(item._id)
        }
        return result.toString()
    }
    fun setParHole(par:Int){
        mPar = par
        notifyDataSetChanged()
    }

    fun setNumberHole(number:Int){
        mIdRoundBattle?.let { _idRound->
            mDataScoreMember.clear()
            notifyDataSetChanged()
            mDataReferenceScore?.apply {
                removeEventListener(mEventScore!!)
            }

            mDataReferenceScore = FirebaseDatabase.getInstance().getReference("rounds").child(_idRound)
                    .child("holes").child(number.toString())

            mEventScore = object : ChildEventListener{
                override fun onCancelled(dataSnapShot: DatabaseError) {

                }

                override fun onChildMoved(dataSnapShot: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(dataSnapShot: DataSnapshot, p1: String?) {
                    val dataScore = dataSnapShot.getValue(BattleScorePlayer::class.java)
                    dataScore?.apply {
                        mDataScoreMember[dataSnapShot.key!!] = this
                        notifyDataSetChanged()
                    }
                }

                override fun onChildAdded(dataSnapShot: DataSnapshot, p1: String?) {
                    val dataScore = dataSnapShot.getValue(BattleScorePlayer::class.java)
                    dataScore?.apply {
                        mDataScoreMember[dataSnapShot.key!!] = this
                        notifyDataSetChanged()
                    }

                }

                override fun onChildRemoved(dataSnapShot: DataSnapshot) {
                }

            }

            mDataReferenceScore?.addChildEventListener(mEventScore!!)
        }
    }

    fun hideOrOpenRemove(){
        isRemoveUser = false
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.let {
            if(it is PlayerHolder){
                val data = mDataList[position]
                it.apply {  }
                val score = mDataScoreMember[data._id]
                if(score != null) {
                    if(score.score != 0) {
                        it.mValueScore.text = score.score.toString()
                        if(getResWhite(score.score) != 0) {
                            it.mValueScore.background = context.getDrawable(getResWhite(score.score))
                        }else{
                            it.mValueScore.setBackgroundResource(0)
                        }
                    }else{
                        it.mValueScore.text = ""
                        it.mValueScore.background = context.getDrawable(R.drawable.ic_icon_score_blue)
                    }
                }else{
                    it.mValueScore.text = ""
                    it.mValueScore.background = context.getDrawable(R.drawable.ic_icon_score_blue)
                }

                it.mTxtName.text = data.getShortName()
                if(!TextUtils.isEmpty(data.avatar)) {
                    Picasso.get().load(data.avatar)
                            .placeholder(R.drawable.ic_icon_user_default)
                            .into(it.mImgUser)
                }else{
                    Picasso.get().load(R.drawable.ic_icon_user_default)
                            .placeholder(R.drawable.ic_icon_user_default)
                            .into(it.mImgUser)
                }

                if(isRemoveUser){
                    it.mActionButtonRemoveUser.visibility = View.VISIBLE
                }else{
                    it.mActionButtonRemoveUser.visibility = View.GONE
                }
            }
        }
    }

    private fun getResWhite(score: Int):Int{
        return when(val index = mPar - score){
            0->{
                0
            }
            1->{
                R.drawable.circle_single_point
            }
            2->{
                R.drawable.circle_double_point
            }
            -1->{
                R.drawable.rectangle_single_point
            }
            -6->{
                R.drawable.rectangle_single_red_point
            }
            else->{
                if(index > 0){
                    R.drawable.circle_double_point
                }else{
                    R.drawable.rectangle_double_point
                }
            }

        }
    }

    override fun getCustomItemViewType(position: Int): Int {
        return if(isHost) {
            if(mDataList.size < 3) {
                when (position) {
                    itemCount - 1 -> TYPE_ADD_PLAYER
                    else -> TYPE_USER
                }
            }else{
                TYPE_USER
            }
        }else{
            TYPE_USER
        }
    }

    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder {
        return when(viewType){
           TYPE_USER -> PlayerHolder(viewRoot)
            else -> PlayerAddHolder(viewRoot)
        }
    }

    override fun getItemCount(): Int {
        return if(isHost) {
            if(mDataList.size < 3){
                super.getItemCount() + 1
            }else {
                super.getItemCount()
            }
        }else{
            super.getItemCount()
        }
    }

    private inner class PlayerHolder(itemView:View?) : CoreRecyclerViewHolder(itemView){
        var mImgUser:ImageView = itemView!!.findViewById(R.id.imgUser)
        var mTxtName:TextView = itemView!!.findViewById(R.id.txtName)
        var mValueScore:TextView = itemView!!.findViewById(R.id.valueScore)
        var mActionButtonRemoveUser:ImageView = itemView!!.findViewById(R.id.actionButtonRemoveUser)


        init {
            itemView?.let { viewRoot->
                viewRoot.setOnClickListener {

                    val player = mDataList[adapterPosition]

                    if(!isRemoveUser) {
                        val data = if (mDataScoreMember[player._id] != null) {
                            mDataScoreMember[player._id]!!
                        } else {
                            BattleScorePlayer()
                        }
                        mOnAddPlayer.onClickPlayer(player, data)
                    }else{
                        mOnAddPlayer.removeUser(mDataList[adapterPosition])
                    }

                }

                viewRoot.setOnLongClickListener {
                    if (isHost) {
                        isRemoveUser = true
                        notifyDataSetChanged()
                        mOnAddPlayer.showButtonCanncel()
                    }
                    true
                }

            }
        }
    }

    private inner class PlayerAddHolder(itemView:View?) : CoreRecyclerViewHolder(itemView){
        init {
            itemView?.let { viewRoot->
                viewRoot.findViewById<View>(R.id.buttonAdd).setOnClickListener {
                    mOnAddPlayer.onAdd()

                }
            }
        }
    }
}