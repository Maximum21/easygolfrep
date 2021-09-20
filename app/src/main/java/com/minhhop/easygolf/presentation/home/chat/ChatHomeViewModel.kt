package com.minhhop.easygolf.presentation.home.chat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.MessageBundle
import com.minhhop.easygolf.framework.common.FirebaseManager
import kotlinx.coroutines.launch

class ChatHomeViewModel(private val interactors: Interactors) : EasyGolfViewModel() {

    var onInsertChannelLive = MutableLiveData<Int>()
    var createGroupLive = MutableLiveData<List<String>>()
    var navigationMessageLive = MutableLiveData<MessageBundle>()

    private var observerChannelMessage = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            mChannelMessengerAdapter?.let { adapter ->
                val toItem = adapter.getItem(toPosition)
                val fromItem = adapter.getItem(fromPosition)

                mListChannelIsReadyAdd[toItem.id!!] = toPosition
                mListChannelIsReadyAdd[fromItem.id!!] = fromPosition
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            onInsertChannelLive.postValue(positionStart)
            /**
             * push event to view
             * */
            mChannelMessengerAdapter?.let { adapter ->
                val channelMessenger = adapter.getItem(positionStart)
                mListChannelIsReadyAdd[channelMessenger.id!!] = positionStart
                if (!channelMessenger.group) {
                    val idMember = getIdMember(channelMessenger.members)
                    idMember?.let { member ->
                        getInfoUserChat(member, channelMessenger.id!!)
                    }
                } else {
                    val listMember = channelMessenger.getMember(mCurrentUser)
                    if (listMember.size > 1) {
                        getInfoGroupChat(listMember[0].user_id, listMember[1].user_id, channelMessenger.id!!)
                    } else {
                        getInfoGroupChat(listMember[0].user_id, listMember[0].user_id, channelMessenger.id!!)
                    }
                }
            }
        }
    }

    fun getIdMember(listMember: HashMap<String, MemberChat>): String? {
        for ((_, value) in listMember) {
            if (value.user_id != mCurrentUser) {
                return value.user_id
            }
        }
        return null
    }

    val mCurrentUser = "1"

    var mChannelMessengerAdapter: ChannelMessengerAdapter? = null

    private val mPathProfileFirebase = ArrayList<DatabaseReference>()
    private var mListenerProfile = ArrayList<ValueEventListener>()

    private var mListChannelIsReadyAdd = HashMap<String, Int>()

    private fun getInfoUserChat(id: String, channelId: String) {
        FirebaseManager.getProfileUser(id) { firebaseUser ->
            val index = mListChannelIsReadyAdd[channelId]
            index?.let { _index ->
                mChannelMessengerAdapter?.updateProfile(firebaseUser._id,
                        firebaseUser.first_name, firebaseUser.last_name,
                        firebaseUser.avatar, _index)
            }
        }
        /**
         * Register user is online
         * */
        val path = FirebaseManager.pathProfile(id)

        val listener = FirebaseManager.registerListenerProfile(path) { firebaseUser ->
            val index = mListChannelIsReadyAdd[channelId]
            index?.let { _index ->
                mChannelMessengerAdapter?.updateStatus(firebaseUser.online, channelId, _index)
            }
        }
        mPathProfileFirebase.add(path)
        mListenerProfile.add(listener)
    }

    private fun getInfoGroupChat(idTop: String, idBottom: String, channelId: String) {
        val index = mListChannelIsReadyAdd[channelId]
        index?.let { _index ->
            var iconAvatar: String
            FirebaseManager.getProfileUser(idTop) { userTop ->
                iconAvatar = userTop.avatar
                if (idTop != idBottom) {
                    FirebaseManager.getProfileUser(idBottom) { userBottom ->
                        iconAvatar = "$iconAvatar$${userBottom.avatar}"

                        mChannelMessengerAdapter?.updateProfile(userBottom._id,
                                userBottom.first_name, userBottom.last_name,
                                iconAvatar, _index)
                    }
                } else {
                    mChannelMessengerAdapter?.updateProfile(userTop._id,
                            userTop.first_name, userTop.last_name,
                            iconAvatar, _index)
                }
            }
        }
    }

    fun detectChannel(stringMembers: String) {
        val arrayMembers = stringMembers.split("-")

        val result = ArrayList<String>()
        for (item in arrayMembers) {
            val target = item.split("$")
            result.add(target[0])
        }
        if (arrayMembers.size > 1) {
            createGroupLive.postValue(result)

        } else {
            if (result.isNotEmpty()) {
                FirebaseManager.didWeChatBefore(mCurrentUser, result[0]) { channelId ->
                    if (channelId != null) {
                        //true
                        navigationMessageLive.postValue(MessageBundle(channelId, false))
                    } else {
                        //false
                        val channelChat = FirebaseManager.createChannel(result, mCurrentUser)
                        navigationMessageLive.postValue(MessageBundle(channelChat.id!!, false))
                    }
                }
            }
        }
    }

    fun startMessageForGroup(title: String, userReceivers: List<String>) {
        val channelChat = FirebaseManager.createChannel(userReceivers, mCurrentUser, title)
        navigationMessageLive.postValue(MessageBundle(channelChat.id!!, channelChat.group))
    }

    fun goingToOnline() {
        FirebaseManager.goingToOnline(mCurrentUser)
    }

    fun createAdapterChannelMessage(context: Context, event: ChannelMessengerAdapter.EventChannelMessenger): ChannelMessengerAdapter {
        mChannelMessengerAdapter = ChannelMessengerAdapter(context, mCurrentUser, event, FirebaseManager.createOptionChannelMessage(mCurrentUser))
        mChannelMessengerAdapter?.registerAdapterDataObserver(observerChannelMessage)
        return mChannelMessengerAdapter!!
    }

    fun startListeningChannel() {
        if(mChannelMessengerAdapter == null){
            interactors.getProfileInLocal{
                mCommonErrorLive.postValue(it)
            }?.let { user->

            }?:mScope.launch {
                interactors.getProfileUser({

                },{
                    mCommonErrorLive.postValue(it)
                })
            }
        }
        mChannelMessengerAdapter?.startListening()
    }

    fun stopListeningChannel() {
        mPathProfileFirebase.forEachIndexed { index, item ->
            mListenerProfile[index].apply {
                item.removeEventListener(this)
            }
        }
        mChannelMessengerAdapter?.stopListening()
    }
}