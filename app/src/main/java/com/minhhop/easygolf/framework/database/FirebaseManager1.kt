package com.minhhop.easygolf.framework.database

import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.*
import com.minhhop.core.domain.User
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import java.util.*
import kotlin.collections.HashMap

class FirebaseManager1 {

    private fun pathRootUser(userId:String) = FirebaseDatabase.getInstance().reference.child("users").child(userId)
    fun pathProfile(userId: String) = pathRootUser(userId).child("profile")
    private fun pathRootChannel() = FirebaseDatabase.getInstance().getReference("channels")
    private fun pathChannelInUserOrderBy(userId:String) = pathRootUser(userId).child("chats").orderByChild("updated_at")

    private fun pathChatUser(userId: String) = pathRootUser(userId).child("chats")
    private fun pathMessage(id:String) = FirebaseDatabase.getInstance().getReference("messages")
            .child(id)

    private fun pathRootRound(roundId:String) = FirebaseDatabase.getInstance().getReference("rounds").child(roundId)
    private fun pathHole(roundId:String,numberHole:String,userId:String) =
            pathRootRound(roundId).child("holes").child(numberHole).child(userId)


    fun uploadProfileUser(user: User, onComplete:()->Unit){
        pathProfile(user.id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null){
                    val profileUser = UserFirebase(user.first_name,user.last_name,user.avatar,false)
                    pathProfile(user.id).setValue(profileUser)
                            .addOnCompleteListener {
                                onComplete()
                            }
                }else{
                    onComplete()
                }
            }
        })
    }

    fun createOptionChannelMessage(user: User):FirebaseRecyclerOptions<ChannelChat>{

        val parser = SnapshotParser { dataSnapshot ->
            Log.e("WOW","START SnapshotParser")
            Log.e("WOW",dataSnapshot.toString())
            Log.e("WOW","END SnapshotParser")
            val channelChat = dataSnapshot.getValue(ChannelChat::class.java)
            channelChat?.id = dataSnapshot.key!!
            channelChat ?: ChannelChat()
        }
        return FirebaseRecyclerOptions.Builder<ChannelChat>()
                .setIndexedQuery(pathChannelInUserOrderBy(user.id),
                        pathRootChannel(), parser).build()
    }

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

    fun registerListenerProfile(pathProfile: DatabaseReference,callback:(firebaseUser:FirebaseUser)->Unit): ValueEventListener{
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

    fun updateScoreToBattle(userId:String,currentHole:Hole?,idRoundBattle:String,data: DataPlayGolf){
        currentHole?.let { hole->
            val dataScore = HashMap<String,Int>()
            dataScore["score"] = data.mValueScore
            if(data.mValueFairwayHit >= 0)
                dataScore["fairway_hit"] = data.mValueFairwayHit
            if(data.mValueGreenInRegulation >= 0)
                dataScore["green"] = data.mValueGreenInRegulation
            if(data.mValuePutt >= 0)
                dataScore["putts"] = data.mValuePutt
            pathHole(idRoundBattle,hole.number.toString(),userId).setValue(dataScore)
        }
    }
}