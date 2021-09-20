package com.minhhop.easygolf.presentation.group.contact
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FriendChatAdapter
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.listeners.EventContact
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.ChatService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.views.activities.ContactActivity

class ContactFragment : WozBaseFragment() {
    private lateinit var mDatabaseChannel: DatabaseReference
    private lateinit var mDatabaseMe: DatabaseReference

    private lateinit var mFriendChatAdapter:FriendChatAdapter
    private lateinit var mCurrentUserEntity: UserEntity

    override fun initView(viewRoot: View) {
        mCurrentUserEntity = DatabaseService.getInstance().currentUserEntity!!

        mDatabaseChannel = FirebaseDatabase.getInstance().getReference("channels")
        mDatabaseMe = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUserEntity.id)


        mFriendChatAdapter = FriendChatAdapter(context, object : EventContact{
            override fun onClick(userEntity: UserEntity) {
                areWeChatBefore(userEntity)
            }
        })

        val listFriend:RecyclerView = viewRoot.findViewById(R.id.listFriend)
        listFriend.layoutManager = LinearLayoutManager(context)
        listFriend.adapter = mFriendChatAdapter


        viewRoot.findViewById<View>(R.id.actionAddFriend).setOnClickListener {
            startActivity(ContactActivity::class.java)
        }

    }

    private fun areWeChatBefore(userEntity: UserEntity){
        mDatabaseMe.child("chats").orderByChild("receiver_id").equalTo(userEntity.id)
                .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataChannel: DataSnapshot) {
                if(dataChannel.value != null) {
                    for (item in dataChannel.children){
                        Log.e("WOW","key: ${item.key}")
                        initChat(item.key!!)
                        break
                    }
                }else{
                    ChatService.getInstance().createChannel(listOf(userEntity.id),mCurrentUserEntity.id)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun initChat(idChannel:String){
        FirebaseDatabase.getInstance().getReference("channels").child(idChannel)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapChannel: DataSnapshot) {
                        Log.e("WOW","dataSnapChannel: ${dataSnapChannel}")
                        val itemChannel = dataSnapChannel.getValue(ChannelChat::class.java)
                        itemChannel?.apply {
                            this.id = dataSnapChannel.key!!
                            startMessage(this)
                        }
                    }

                })
    }

    private fun startMessage(item: ChannelChat){
        context?.let { cleanContext->
            ChatService.getInstance().startMessage(item,mCurrentUserEntity.id,cleanContext)
        }

    }

    override fun loadData() {
        registerResponse(ApiService.getInstance().generalService.listFriend(), object : HandleResponse<List<UserEntity>>{
            override fun onSuccess(result: List<UserEntity>) {
                mFriendChatAdapter.setDataList(result)
                hideLayoutRefresh()
            }
        })
    }

    override fun setLayout(): Int = R.layout.fragment_contact
}