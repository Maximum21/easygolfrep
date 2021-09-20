package com.minhhop.easygolf.presentation.home.chat

import android.content.Context
import android.graphics.Typeface
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.EasyGolfMessenger.TypeMessage.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.utils.AppUtil
import kotlinx.android.synthetic.main.item_channel_chat_single.view.*
import java.util.*
import kotlin.collections.HashMap

class ChannelMessengerAdapter(private val mContext: Context, private val mIdUser: String, private val mEvent: EventChannelMessenger?,
                              options: FirebaseRecyclerOptions<ChannelChat>)
    : FirebaseRecyclerAdapter<ChannelChat, ChannelMessengerAdapter.ChannelMessengerHolder>(options) {

    companion object {
        private const val TYPE_CHAT_SINGLE = 0x0001
        private const val TYPE_CHAT_GROUP = 0x0002
        private const val TYPE_CHAT_HIDDEN = 0x0003
    }

    private val mListProfileUser = HashMap<String, ProfileUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelMessengerHolder {
        val layout = if (viewType == TYPE_CHAT_HIDDEN) {
            R.layout.item_channel_chat_hidden
        } else {
            if (viewType == TYPE_CHAT_SINGLE)
                R.layout.item_channel_chat_single else R.layout.item_channel_chat_group
        }
        val viewRoot = LayoutInflater.from(mContext).inflate(layout, parent, false)
        return ChannelMessengerHolder(viewRoot)
    }

    override fun getItemViewType(position: Int): Int {
        val itemChannel = getItem(position)

        return if (itemChannel.getMy(mIdUser).hidden) {
            TYPE_CHAT_HIDDEN
        } else {
            if (itemChannel.group) TYPE_CHAT_GROUP else TYPE_CHAT_SINGLE
        }
    }

    override fun onBindViewHolder(holder: ChannelMessengerHolder, position: Int, model: ChannelChat) {
        holder.bindData(position)
        val targetHolder = holder as ChannelMessengerHolder?
        targetHolder?.apply {
            if (model.getMy(mIdUser).hidden) {
                this.mViewRoot.visibility = View.GONE
                return
            }
            this.mViewRoot.visibility = View.VISIBLE
            if (!model.group) {
                if (mListProfileUser.containsKey(model.id)) {
                    val profileCurrent = mListProfileUser[model.id]!!
                    Glide.with(mContext).load(profileCurrent.avatar)
                            .error(R.drawable.ic_icon_user_default)
                            .circleCrop()
                            .into(this.mImageAvatar!!)
                    mTxtName?.text = profileCurrent.getFullName()
                    /**
                     * set status is online for channel
                     * */
                    holder.mStatusOnline?.let { online ->
                        online.visibility = if (profileCurrent.online) View.VISIBLE else View.GONE
                    }
                }
            } else {
                mTxtName?.text = model.name!!
                if (mListProfileUser.containsKey(model.id)) {
                    val listIcon = mListProfileUser[model.id]!!.avatar.split("$")
                    if (listIcon.size >= 2) {
                        this.mImageAvatarTop!!.visibility = View.VISIBLE
                        this.mImageAvatarBottom!!.visibility = View.VISIBLE
                        this.mImageAvatar?.visibility = View.GONE

                        Glide.with(mContext).load(listIcon[0])
                                .circleCrop()
                                .error(R.drawable.ic_icon_user_default)
                                .into(this.mImageAvatarTop)
                        Glide.with(mContext).load(listIcon[1])
                                .circleCrop()
                                .error(R.drawable.ic_icon_user_default)
                                .into(this.mImageAvatarBottom)
                    } else {
                        this.mImageAvatarTop!!.visibility = View.INVISIBLE
                        this.mImageAvatarBottom!!.visibility = View.INVISIBLE
                        this.mImageAvatar?.visibility = View.VISIBLE

                        Glide.with(mContext).load(listIcon[0])
                                .circleCrop()
                                .into(this.mImageAvatar!!)
                    }
                }

            }

            when (EasyGolfMessenger.getType(model.last_message.type)) {
                TEXT -> {
                    mLastMessage?.text = model.last_message.body
                }
                STARTING -> {
                    mLastMessage?.text = mContext.getString(R.string.create_chat)
                }
                IMAGE -> {
                    mLastMessage?.text = if (model.last_message.sender_id == mIdUser) {
                        mContext.getString(R.string.view_you_sent_photo)
                    } else {
                        mContext.getString(R.string.view_group_sent_photo)
                    }
                }
                ADD -> {
                    mLastMessage?.text = mContext.getString(R.string.someone_added_to_group)
                }
                LEAVE -> {
                    mLastMessage?.text = mContext.getString(R.string.someone_leave_to_group)
                }
                NAME -> {
                    mLastMessage?.text = mContext.getString(R.string.view_change_name_group, model.last_message.body)
                }
            }


            val date = Date(model.last_message.time)
            if (DateUtils.isToday(model.last_message.time)) {
                mTxtTime?.text = AppUtil.formatDate(date, "hh:mm")
            } else {
                mTxtTime?.text = AppUtil.formatDate(date, "MMM dd")
            }

            for ((_, memberChat) in model.members) {
                if (memberChat.user_id == mIdUser) {
                    memberChat.apply {
                        if (unread_count <= 0) {
                            mTxtUnreadCount?.visibility = View.GONE
                            mLastMessage?.typeface = Typeface.SANS_SERIF
                            mLastMessage?.setTextColor(ContextCompat.getColor(mContext, R.color.color_read_message))
                            mTxtName?.setTextColor(ContextCompat.getColor(mContext, R.color.textColorDark))
                        } else {
                            mTxtUnreadCount?.text = if (unread_count > 9) "+9" else unread_count.toString()
                            mTxtUnreadCount?.visibility = View.VISIBLE
                            mLastMessage?.typeface = Typeface.DEFAULT_BOLD
                            mLastMessage?.setTextColor(ContextCompat.getColor(mContext, R.color.color_unread_message))
                            mTxtName?.setTextColor(ContextCompat.getColor(mContext, R.color.color_unread_message))
                        }
                    }
                    break
                }
            }
        }
    }

    fun updateProfile(_id: String, firstName: String, lastName: String, avatar: String, index: Int) {
        val item = getItem(index)
        mListProfileUser[item.id!!] = ProfileUser(_id, firstName, lastName, avatar)
        notifyItemChanged(index)
    }

    fun updateStatus(online: Boolean, idChannel: String, index: Int) {
        mListProfileUser[idChannel]?.let { item ->
            item.online = online
            notifyItemChanged(index)
        }
    }

    inner class ChannelMessengerHolder(itemView: View) : CoreRecyclerViewHolder(itemView) {
        val mViewRoot: View = itemView.findViewById(R.id.viewRoot)
        val mImageAvatar: ImageView? = itemView.findViewById(R.id.imgAvatar)

        val mStatusOnline: View? = itemView.findViewById(R.id.statusOnline)

        val mImageAvatarTop: ImageView? = itemView.findViewById(R.id.imgAvatarTop)
        val mImageAvatarBottom: ImageView? = itemView.findViewById(R.id.imgAvatarBottom)
        val mTxtName: TextView? = itemView.findViewById(R.id.txtName)
        val mLastMessage: TextView? = itemView.findViewById(R.id.lastMessage)
        val mTxtUnreadCount: TextView? = itemView.findViewById(R.id.txtUnreadCount)
        val mTxtTime: TextView? = itemView.findViewById(R.id.txtTime)

        init {
            itemView.setOnClickListener {
                mEvent?.onClickChannel(getItem(adapterPosition))
            }
        }

        fun bindData(position: Int) {
            itemView.viewAssistance.visibility = if (position >= itemCount - 1) {
                View.VISIBLE
            } else View.GONE
        }
    }

    interface EventChannelMessenger {
        fun onClickChannel(item: ChannelChat)
    }
}