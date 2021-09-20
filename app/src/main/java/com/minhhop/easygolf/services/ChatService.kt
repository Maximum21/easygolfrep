package com.minhhop.easygolf.services

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.framework.bundle.MessageBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.framework.models.ProfileChat
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.framework.models.UserFirebase
import com.minhhop.easygolf.listeners.ChatServiceCallback
import java.util.*
import kotlin.collections.HashMap

class ChatService {

    private var mDBFireBase = FirebaseDatabase.getInstance()

    companion object {
        private var mInstance:ChatService? = null

        fun getInstance():ChatService{
            return if(mInstance != null){
                mInstance!!
            }else{
                mInstance = ChatService()
                mInstance!!
            }
        }
    }

    fun initUser(userEntity: UserEntity, callback:ChatServiceCallback){
        mDBFireBase.reference.child("users").child(userEntity.id).child("profile").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                Log.e("WOW","p0.value: ${p0.value}")
               if (p0.value == null){

                   val profileUser = UserFirebase(userEntity.firstName,userEntity.lastName,userEntity.avatar,false)
                    val gson = Gson()
                   Log.e("WOW","data init userEntity: ${gson.toJson(profileUser)}")
                   mDBFireBase.getReference("users").child(userEntity.id)
                           .child("profile").setValue(profileUser)
                           .addOnCompleteListener {
                               callback.callback()
                           }
               }else{
                   callback.callback()
               }
            }
        })
    }

    /***
     * @param userReceivers is array list member chat in this channel
     * */
     fun createChannel(userReceivers:List<String>,currentUser:String,name:String? = null): ChannelChat {
        val mDatabaseChannel = FirebaseDatabase.getInstance().getReference("channels")
        /***
         * create channel
         * */
        val holderChannel = mDatabaseChannel.push()
        val newChannel = ChannelChat()

        val fbMessage = FirebaseDatabase.getInstance().getReference("messages")
                .child(holderChannel.key!!).push()

        val createNewMassage = EasyGolfMessenger()
        createNewMassage.body = "create channel"
        createNewMassage.time = Date().time
        createNewMassage.sender_id = currentUser
        createNewMassage.type = "starting"

        createNewMassage.id = fbMessage.key!!
        newChannel.last_message = createNewMassage
        val members = HashMap<String, MemberChat>()
        val holderMembers = mDatabaseChannel.child(holderChannel.key!!).child("members")

        val memberMe = MemberChat()
        memberMe.unread_count = 1
        memberMe.active = false
        memberMe.user_id = currentUser
        members[holderMembers.push().key!!] = memberMe

        for (id in userReceivers) {
            val memberReceiver = MemberChat()
            memberReceiver.unread_count = 1
            memberReceiver.active = false
            memberReceiver.user_id = id
            members[holderMembers.push().key!!] = memberReceiver
        }

        newChannel.members = members
        if(userReceivers.size > 1 && !name.isNullOrEmpty()){
            newChannel.group = true
            newChannel.name = name
        }

        holderChannel.setValue(newChannel)

        /**
         * Create message
         * */
        createNewMassage.id = null
        fbMessage.setValue(createNewMassage)

        /**
         * add channel for all members
         * */
        val updatedAt = createNewMassage.time * -1
        val userReceiver = if(userReceivers.size > 1) null else { userReceivers[0]}

        val profileChat = ProfileChat(userReceiver,updatedAt)

        FirebaseDatabase.getInstance().getReference("users").child(currentUser)
                .child("chats").child(holderChannel.key!!).setValue(profileChat)

        for (id in userReceivers) {
            profileChat.receiver_id = if(userReceivers.size > 1) null else { currentUser}

            FirebaseDatabase.getInstance().getReference("users").child(id)
                    .child("chats").child(holderChannel.key!!).setValue(profileChat)
        }


        newChannel.id = holderChannel.key!!
        return newChannel
    }

    fun startMessage(item: ChannelChat, mCurrentUser:String, context: Context){
        EasyGolfNavigation1.messageDirection(context, MessageBundle(item.id!!,item.group))
    }
}