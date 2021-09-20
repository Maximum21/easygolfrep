package com.minhhop.easygolf.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ParticipantAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.framework.dialogs.ConfirmDialog
import com.minhhop.easygolf.framework.dialogs.EditNameGroupDialog
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.listeners.EventSaveNameGroup
import com.minhhop.easygolf.services.ChatService
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.framework.common.Contains
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class SettingMessageActivity : WozBaseActivity() {

    private var mUrlAvatarTop:String? = null
    private var mUrlAvatarBottom:String? = null
    private var mIsGroup:Boolean = false
    private var mCountDownAnotherPlayer = 0

    private lateinit var mIdUser:String
    private lateinit var mIdChannel:String

    private var mUserReceiver:ProfileUser? = null
    private lateinit var mParticipantAdapter:ParticipantAdapter
    private lateinit var mListParticipant:RecyclerView

    private lateinit var mTxtParticipant:TextView
    private lateinit var mTxtName:TextView
    /**
     * Image for single chat
     * */
    private lateinit var mImgAvatar:ImageView

    /**
     * Image for group chat
     * */
    private lateinit var mContentAvatarGroup:View
    private lateinit var mImgAvatarTop:ImageView
    private lateinit var mImgAvatarBottom:ImageView
    private lateinit var mNumberElseMember:TextView

    /**
     * Firebase
     * */
    private lateinit var mDbChannel:DatabaseReference

    private lateinit var mDbNameChannel:DatabaseReference
    private lateinit var mDbMembersChannel:DatabaseReference
    private var mListenNameChannel:ValueEventListener? = null
    private var mListenMembers:ChildEventListener? = null

    override fun setLayoutView(): Int = R.layout.activity_more_message

    override fun onDestroy() {
        super.onDestroy()
        mListenNameChannel?.let { event->
            mDbNameChannel.removeEventListener(event)
        }
        mListenMembers?.let { event->
            mDbMembersChannel.removeEventListener(event)
        }
    }

    override fun initView() {
        mIdUser = DatabaseService.getInstance().currentUserEntity!!.id
        mIdChannel = intent.getStringExtra(Contains.EXTRA_CHANEL_CHAT)

        mDbChannel = FirebaseDatabase.getInstance().getReference("channels").child(mIdChannel)
        mDbNameChannel = mDbChannel.child("name")
        mDbMembersChannel = mDbChannel.child("members")

        mParticipantAdapter = ParticipantAdapter(this)
        mListParticipant = findViewById(R.id.listParticipant)
        mListParticipant.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mListParticipant.adapter = mParticipantAdapter

        mTxtParticipant = findViewById(R.id.txtParticipant)
        mTxtName = findViewById(R.id.txtName)
        mImgAvatar = findViewById(R.id.imgAvatar)

        findViewById<View>(R.id.actionAddParticipant).setOnClickListener(this)

        mContentAvatarGroup = findViewById(R.id.contentAvatarGroup)
        mImgAvatarTop = findViewById(R.id.imgAvatarTop)
        mImgAvatarBottom = findViewById(R.id.imgAvatarBottom)
        mNumberElseMember = findViewById(R.id.numberElseMember)
        mNumberElseMember.visibility = View.GONE

        mIsGroup = intent.getBooleanExtra(Contains.EXTRA_IS_GROUP_CHAT,false)

        findViewById<View>(R.id.actionDeleteConversation).setOnClickListener(this)
        if(mIsGroup){
            mImgAvatar.visibility = View.GONE
            findViewById<View>(R.id.actionLeaveGroup).setOnClickListener(this)
        }else{
            mTxtParticipant.visibility = View.GONE
            mContentAvatarGroup.visibility = View.GONE
            findViewById<View>(R.id.iconEditNameGroup).visibility = View.GONE
            findViewById<View>(R.id.actionLeaveGroup).visibility = View.GONE
        }

        mDbChannel.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataChannel: DataSnapshot) {
                val channel = dataChannel.getValue(ChannelChat::class.java)
                channel?.let {
                    if(it.group){
                        getNameGroup()
                        getListParticipants()
                        editNameGroup()
                    }else{
                        for ((_,value) in it.members){
                            if(value.user_id != mIdUser){
                                getNameReceiver(value.user_id)
                                break
                            }
                        }
                    }

                }
            }
        })
    }

    private fun editNameGroup(){
        findViewById<View>(R.id.editNameGroup).setOnClickListener {
            EditNameGroupDialog(this,getString(R.string.edit_group_name),mTxtName.text.toString())
                    .setNameButton(getString(R.string.save))
                    .setEvent(object : EventSaveNameGroup{
                        override fun onSave(value: String) {
                            mDbNameChannel.setValue(value) { error, ref ->
                                setMessageChangeNameGroup(value)
                            }
                        }

                    }).show()
        }
    }

    private fun setMessageChangeNameGroup(value:String){
        val messNameGroup = EasyGolfMessenger()
        messNameGroup.type = "name"
        messNameGroup.sender_id = mIdUser
        messNameGroup.time = Date().time
        messNameGroup.body = value

       val key = FirebaseDatabase.getInstance().getReference("messages")
                .child(mIdChannel).push()
        key.setValue(messNameGroup)

        messNameGroup.id = key.key
        updateLastMessage(messNameGroup)
    }

    private fun setMessageAddMemberGroup(idMemberAdded:String){
        val messNameGroup = EasyGolfMessenger()
        messNameGroup.type = "add"
        messNameGroup.sender_id = idMemberAdded
        messNameGroup.time = Date().time
        messNameGroup.body = ""

        val key = FirebaseDatabase.getInstance().getReference("messages")
                .child(mIdChannel).push()
        key.setValue(messNameGroup)

        messNameGroup.id = key.key
        updateLastMessage(messNameGroup)
    }

    private fun setMessageLeftGroup(idMemberLeft:String){
        val messNameGroup = EasyGolfMessenger()
        messNameGroup.type = "leave"
        messNameGroup.sender_id = idMemberLeft
        messNameGroup.time = Date().time
        messNameGroup.body = ""

        val key = FirebaseDatabase.getInstance().getReference("messages")
                .child(mIdChannel).push()
        key.setValue(messNameGroup)

        messNameGroup.id = key.key
        updateLastMessage(messNameGroup)
    }

    private fun updateLastMessage(lastMessage: EasyGolfMessenger){
        mDbChannel.child("last_message").setValue(lastMessage)
        unreadForMembers()
        updateAtForMembers(lastMessage)
    }

    private fun unreadForMembers() {
        mDbMembersChannel
                .orderByChild("active").equalTo(false)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (item in dataSnapshot.children) {
                            val member = item.getValue(MemberChat::class.java)
                            member?.apply {
                                val unreadCount = this.unread_count + 1
                                Log.e("WOW","unread: ${unreadCount}")
                                mDbMembersChannel.child(item.key!!).child("unread_count").setValue(unreadCount)
                            }

                        }
                    }

                })
    }

    /**
     * update updated_at for members
     * */

    private fun updateAtForMembers(message: EasyGolfMessenger) {
        mDbMembersChannel.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataMembers: DataSnapshot) {
                for (item in dataMembers.children) {
                    val member = item.getValue(MemberChat::class.java)
                    member?.apply {
                        FirebaseDatabase.getInstance().getReference("users").child(user_id)
                                .child("chats").child(mIdChannel).child("updated_at")
                                .setValue(message.time * -1)
                    }

                }
            }

        })
    }

    private fun getNameReceiver(idUser:String){
        FirebaseDatabase.getInstance().getReference("users")
                .child(idUser).child("profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataProfile: DataSnapshot) {
                        val unsafeProfile = dataProfile.getValue(ProfileUser::class.java)
                        unsafeProfile?.let { profile ->
                            mUserReceiver = profile
                            mUserReceiver?._id = idUser
                            mTxtName.text = profile.getFullName()
                            Glide.with(this@SettingMessageActivity)
                                    .load(profile.avatar)
                                    .error(R.drawable.ic_icon_user_default)
                                    .circleCrop()
                                    .into(mImgAvatar)
                        }.apply {

                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {}
                })
    }

    private fun getNameGroup(){
        mListenNameChannel = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataName: DataSnapshot) {
                val valueName = dataName.getValue(String::class.java)
                valueName?.let {
                    mTxtName.text = it
                }
            }

        }

        mDbNameChannel.addValueEventListener(mListenNameChannel!!)
    }


    private fun getListParticipants(){
        mListenMembers = object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(member: DataSnapshot, p1: String?) {
                val unsafeMember = member.getValue(MemberChat::class.java)
                unsafeMember?.let { safeMember->
                    Log.e("WOW","safeMember.user_id: ${safeMember.user_id}")
//                    if(safeMember.user_id != mIdUser)
                    findProfileForUser(safeMember.user_id)
                }
            }
            override fun onChildRemoved(member: DataSnapshot) {
                val unsafeMember = member.getValue(MemberChat::class.java)
                unsafeMember?.let { safeMember->
                    mParticipantAdapter.removeItem(safeMember.user_id)
                    mTxtParticipant.text = getString(R.string.members_chat,(mParticipantAdapter.itemCount).toString())

                }
            }

        }

        mDbMembersChannel.addChildEventListener(mListenMembers!!)
    }

    private fun findProfileForUser(user_id:String){
        FirebaseDatabase.getInstance().getReference("users").child(user_id)
                .child("profile").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataProfile: DataSnapshot) {
                        val unsafeProfile = dataProfile.getValue(ProfileUser::class.java)
                        unsafeProfile?.apply {
                            this._id = user_id
                            this.also {
                                mParticipantAdapter.addItem(this)
                                showNumberAnotherPlayer()
                                mTxtParticipant.text = getString(R.string.members_chat,(mParticipantAdapter.itemCount).toString())
                                if(mUrlAvatarTop == null){
                                    mUrlAvatarTop = it.avatar
                                    Glide.with(this@SettingMessageActivity)
                                            .load(it.avatar)
                                            .error(R.drawable.ic_icon_user_default)
                                            .circleCrop()
                                            .into(mImgAvatarTop)
                                }else{
                                    if(mUrlAvatarBottom == null){
                                        mUrlAvatarBottom = it.avatar
                                        Glide.with(this@SettingMessageActivity)
                                                .load(it.avatar)
                                                .circleCrop()
                                                .error(R.drawable.ic_icon_user_default)
                                                .into(mImgAvatarBottom)
                                    }
                                }
                            }
                        }
                    }
                })
    }

    private fun showNumberAnotherPlayer(){
        mCountDownAnotherPlayer++
        if(mCountDownAnotherPlayer > 2){
            mNumberElseMember.visibility = View.VISIBLE
        }else{
            mNumberElseMember.visibility = View.GONE
        }
        mNumberElseMember.text = "+${mCountDownAnotherPlayer - 2}"
    }

    override fun loadData() {

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.let{ viewClick ->
            when(viewClick.id){
                R.id.actionAddParticipant->{
                    val bundle = Bundle()
                    if (mIsGroup) {
                        bundle.putString(Contains.EXTRA_BLACK_LIST, mParticipantAdapter.getListCurrentMembers())
                    }else{
                        bundle.putString(Contains.EXTRA_BLACK_LIST, mUserReceiver?._id)
                    }
                    startActivityForResult(AddPeopleChatActivity::class.java, Contains.REQUEST_CODE_ADD_MEMBER_CHAT,bundle)
                }
                R.id.actionDeleteConversation->{
                    ConfirmDialog(this).setContent(getString(R.string.delete_conversation_content)).setOnConfirm {
                        mDbMembersChannel.orderByChild("user_id").equalTo(mIdUser)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {}
                                    override fun onDataChange(data: DataSnapshot) {
                                        for (item in data.children) {
                                            item.ref.child("hidden").setValue(true)
                                            EventBus.getDefault().post( MessageEvent())
                                            finish()
                                        }
                                    }

                                })
                    }.show()
                }
                R.id.actionLeaveGroup->{
                    ConfirmDialog(this).setContent(getString(R.string.confirm_leave_group)).setOnConfirm {
                        mDbMembersChannel.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}

                            override fun onDataChange(dataShot: DataSnapshot) {
                                if(dataShot.childrenCount > 1){
                                    leaveGroup()
                                }else{
                                    deleteGroup()
                                }
                            }

                        })

                    }.show()
                }
                else -> {}
            }

        }
    }

    private fun deleteGroup(){
        FirebaseDatabase.getInstance().getReference("users")
                .child(mIdUser).child("chats").child(mIdChannel).setValue(null)

        /**
         * delete channel
         * */
        mDbChannel.setValue(null)

        /**
         * delete message
         * */
        FirebaseDatabase.getInstance().getReference("messages").child(mIdChannel).setValue(null)

        EventBus.getDefault().post( MessageEvent())
        finish()
    }

    private fun leaveGroup(){
        mDbMembersChannel.orderByChild("user_id").equalTo(mIdUser)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(data: DataSnapshot) {
                        for (item in data.children) {
                            item.ref.setValue(null) { _, _ ->
                                setMessageLeftGroup(mIdUser)
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(mIdUser).child("chats").child(mIdChannel).setValue(null)
                                EventBus.getDefault().post( MessageEvent())
                                finish()

                            }

                        }
                    }

                })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let { _data ->
            if(resultCode == Activity.RESULT_OK && requestCode == Contains.REQUEST_CODE_ADD_MEMBER_CHAT){
                val members = _data.getStringExtra(Contains.EXTRA_IS_RETURN)
                val arrayMembers = members.split("-")

                val result = ArrayList<String>()
                for (item in arrayMembers){
                    val target = item.split("$")
                    result.add(target[0])
                }

                if(!mIsGroup) {
                    mUserReceiver?.let {
                        result.add(it._id)
                    }

                    if (result.size > 1) {
                        EditNameGroupDialog(this, getString(R.string.create_group_name))
                                .setEvent(object : EventSaveNameGroup {
                                    override fun onSave(value: String) {
                                        ChatService.getInstance().startMessage(ChatService.getInstance()
                                                .createChannel(result, mIdUser, value),mIdUser,this@SettingMessageActivity)
                                    }
                                })
                                .show()
                    } else {
                        ChatService.getInstance().startMessage(ChatService.getInstance().createChannel(result, mIdUser),
                                mIdUser,this)
                    }
                }else{

                    /**
                     * Add member to this group
                     * */
                    val holderMembers = mDbChannel.child("members")
                    for (item in result){
                        mDbMembersChannel.orderByChild("user_id")
                                .equalTo(item).addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError) {
                                    }

                                    override fun onDataChange(dataMember: DataSnapshot) {
                                        if(dataMember.value == null){
                                            val memberChat = MemberChat()
                                            memberChat.user_id = item
                                            memberChat.unread_count = 1
                                            holderMembers.push().setValue(memberChat)
                                            setMessageAddMemberGroup(item)
                                        }
                                    }

                                })

                    }

                }
            }
        }

    }

    override fun setIconToolbar(): Int {
        return R.drawable.ic_icon_back_button_black
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        finish()
    }
}