package com.minhhop.easygolf.views.activities

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ContactSearchAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.ChatService
import com.minhhop.easygolf.services.DatabaseService

class ContactSearchActivity : WozBaseActivity(), ContactSearchAdapter.ContactSerchClick {

    /**
     * Views in layout
     * */
    private lateinit var mListFriend:RecyclerView
    private lateinit var mContactSearchAdapter:ContactSearchAdapter
    private lateinit var mEditSearch: EditText
    /**
     * Value calculator
     * */
    private lateinit var mDatabaseChannel: DatabaseReference
    private lateinit var mDatabaseMe: DatabaseReference
    private lateinit var mCurrentUserEntity: UserEntity

    internal val handler = Handler()
    private val mListDataUser = ArrayList<UserEntity>()

    override fun setLayoutView(): Int = R.layout.activity_contact_search

    override fun initView() {
        mCurrentUserEntity = DatabaseService.getInstance().currentUserEntity!!
        mDatabaseChannel = FirebaseDatabase.getInstance().getReference("channels")
        mDatabaseMe = FirebaseDatabase.getInstance().getReference("users").child(mCurrentUserEntity.id)

        mListFriend = findViewById(R.id.listFriend)
        mListFriend.layoutManager = LinearLayoutManager(this)
        mContactSearchAdapter = ContactSearchAdapter(this,this)
        mListFriend.adapter = mContactSearchAdapter
        mEditSearch = findViewById(R.id.editSearch)

        watcherEditSearch()
    }

    override fun loadData() {
        showLoading()
        registerResponse(ApiService.getInstance().generalService.listFriend(),object : HandleResponse<List<UserEntity>>{
            override fun onSuccess(result: List<UserEntity>) {
                mListDataUser.addAll(result)
                mContactSearchAdapter.setDataList(mListDataUser)
                hideLoading()
            }
        })
    }


    private fun watcherEditSearch(){
        mEditSearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val term = s.toString()
                if (term.isNotEmpty()) {
                    handler.removeMessages(0)
                    handler.postDelayed({
                        val listTemp = mListDataUser.filter {
                            val keyword = mEditSearch.text.toString()
                            if (keyword.isEmpty()) {
                                true
                            } else {
                                val query = mEditSearch.text.toString()
                                val fullName = it.fullName
                                fullName.contains(query, true) || it.phoneNumber?.contains(query,true)?:false
                            }
                        }

                        mContactSearchAdapter.setDataList(listTemp)

                    }, 350)
                } else {
                    mContactSearchAdapter.setDataList(mListDataUser)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    override fun getTitleToolBar(): String? {
        return getString(R.string.search_contact)
    }


    override fun onClickMe(userEntity: UserEntity) {
        areWeChatBefore(userEntity)
    }

    private fun areWeChatBefore(userEntity: UserEntity){
        mDatabaseMe.child("chats").orderByChild("receiver_id").equalTo(userEntity.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
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
                .addListenerForSingleValueEvent(object : ValueEventListener {
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
        ChatService.getInstance().startMessage(item,mCurrentUserEntity.id,this)
    }
}