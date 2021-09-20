package com.minhhop.easygolf.framework.common

import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.*
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.framework.models.FirebaseUser
import com.minhhop.easygolf.framework.models.ProfileChat
import java.util.*
import kotlin.collections.HashMap

object FirebaseManager{
    private fun pathRootChannel() = FirebaseDatabase.getInstance().getReference("channels")
    private fun pathMessage(id:String) = FirebaseDatabase.getInstance().getReference("messages")
            .child(id)
    private fun pathChatUser(userId: String) = pathRootUser(userId).child("chats")
    private fun pathRootUser(userId:String) = FirebaseDatabase.getInstance().reference.child("users").child(userId)
    private fun pathChannelInUserOrderBy(userId:String) = pathChatUser(userId).orderByChild("updated_at")

    fun pathProfile(userId: String) = pathRootUser(userId).child("profile")


    fun getProfileUser(userId:String,callback:(firebaseUser: FirebaseUser)->Unit){
        pathProfile(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(FirebaseUser::class.java)
                        user?.apply {
                            callback(this)
                        }
                    }

                })
    }

    fun createOptionChannelMessage(userId: String): FirebaseRecyclerOptions<ChannelChat> {

        val parser = SnapshotParser { dataSnapshot ->
            Log.e("WOW","START SnapshotParser")
            Log.e("WOW",dataSnapshot.toString())
            Log.e("WOW","END SnapshotParser")
            val channelChat = dataSnapshot.getValue(ChannelChat::class.java)
            channelChat?.id = dataSnapshot.key!!
            channelChat ?: ChannelChat()
        }
        return FirebaseRecyclerOptions.Builder<ChannelChat>()
                .setIndexedQuery(pathChannelInUserOrderBy(userId),
                        pathRootChannel(), parser).build()
    }

    fun registerListenerProfile(pathProfile: DatabaseReference, callback:(firebaseUser:FirebaseUser)->Unit): ValueEventListener{
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(FirebaseUser::class.java)
                user?.apply {
                    Log.e("WOW","callback")
                    callback(this)
                }
            }
        }

        pathProfile.addValueEventListener(listener)

        return listener
    }

    fun goingToOnline(userId: String){
        pathProfile(userId).let {
            it.child("online").setValue(true)
            it.child("online").onDisconnect().setValue(false)
        }
    }

    /***
     * @param userReceivers is array list member chat in this channel
     * */
    fun createChannel(userReceivers:List<String>,currentUser:String,name:String? = null): ChannelChat {
        val mDatabaseChannel = pathRootChannel()
        /***
         * create channel
         * */
        val holderChannel = mDatabaseChannel.push()
        val newChannel = ChannelChat()

        val fbMessage = pathMessage(holderChannel.key!!).push()

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

        pathChatUser(currentUser).child(holderChannel.key!!).setValue(profileChat)

        for (id in userReceivers) {
            profileChat.receiver_id = if(userReceivers.size > 1) null else { currentUser}
            pathChatUser(currentUser).child(id).setValue(profileChat)
        }

        newChannel.id = holderChannel.key!!
        return newChannel
    }

    fun didWeChatBefore(userId: String,receiverId:String,callback: (channelId: String?) -> Unit){
        pathChatUser(userId).orderByChild("receiver_id").equalTo(receiverId)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(dataChannel: DataSnapshot) {
                        if(dataChannel.value != null) {
                            for (item in dataChannel.children){
                                callback(item.key!!)
                                break
                            }
                        }else{
                            callback(null)
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
    }

}