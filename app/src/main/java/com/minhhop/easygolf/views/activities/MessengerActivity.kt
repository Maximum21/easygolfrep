package com.minhhop.easygolf.views.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.MemberChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.WozMessengerAdapter
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.framework.models.LastMessengerModel
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.utils.AppUtil.pickImageGallery
import com.minhhop.easygolf.framework.common.Contains
import com.vanniktech.emoji.EmojiPopup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

class MessengerActivity : WozBaseActivity(),WozMessengerAdapter.ListenItemMessage {


    enum class StateButtonSender {
        SEND_TEXT,
        ANIMATION_LOADING,
        SEND_IMAGE
    }
    companion object {
        private const val NUMBER_MESSAGE_IN_PAGE = 20
    }

    /**
     * Start for views
     * */
    private lateinit var mViewRoot: View
    private lateinit var mInputChat: AppCompatEditText
    private lateinit var mEmojiPopup: EmojiPopup
    private lateinit var mMessageRecyclerView: RecyclerView
    private lateinit var mBtSend: AppCompatImageView
    private lateinit var mLayoutUnread: View

    private lateinit var mTxtNameChat: TextView
    private lateinit var mTxtStatus: TextView
    private lateinit var mTxtUnreadMessage: TextView
    private lateinit var mAnimationShowUnread: Animation
    private lateinit var mAnimationHideUnread: Animation
    private lateinit var mAnimationUpdateUnread: Animation

    private var mIsShowUnread = false
    private var mLeaveGroup = false
    /**
     * End for views
     * */

    private lateinit var mChannelChatId: String

    private var mStateButtonSender = StateButtonSender.SEND_IMAGE

    private lateinit var mWozMessengerAdapter: WozMessengerAdapter
    private lateinit var mTargetLayoutManager: LinearLayoutManager
    private lateinit var mCurrentUserEntity: UserEntity
    private lateinit var mIdUser: String

    private var mIsGoingToGroupChat = false

    private var mStorage = FirebaseStorage.getInstance("gs://easygolf-9872e.appspot.com")
    private var mStorageRef = mStorage.reference

    private var mAnimationDrawableSendText: AnimatedVectorDrawableCompat? = null
    private var mAnimationDrawableSendImage: AnimatedVectorDrawableCompat? = null

    /**
     * @param {true when user going to typing --- false when user finish typing}
     * */
    private var mIsTyping = false

    /**
     * Timer when stop typing
     * */
    private var mTimer: Timer? = null
    /**
     * Value for firebase
     * */
    private lateinit var mLastKeyMessage: String

    private var mIdCurrentUserInChannel:String? = null
    private var mFirebaseRefMembers: DatabaseReference? = null
    private var mListenerTyping: ChildEventListener? = null
    private var mListenerUnread: ValueEventListener? = null

    private lateinit var mFirebaseDatabaseReferenceChannel: DatabaseReference
    private lateinit var mFirebaseDatabaseReferenceMessage: DatabaseReference

    override fun setLayoutView(): Int {
        EventBus.getDefault().register(this)
        return R.layout.activity_messenger
    }

    override fun onDestroy() {
        mListenerTyping?.let {
            mFirebaseRefMembers?.removeEventListener(it)
        }

        mListenerUnread?.let {
            mFirebaseRefMembers?.removeEventListener(it)
        }

        if(!mLeaveGroup) {
            mIdCurrentUserInChannel?.apply {
                mFirebaseRefMembers?.child(this)?.child("active")?.setValue(false)
                mFirebaseRefMembers?.child(this)?.child("typing")?.setValue(false)
            }
        }
        super.onDestroy()
    }

    override fun initView() {
        mStatusLoading = STATUS.FIRST_LOAD
        mCurrentUserEntity = DatabaseService.getInstance().currentUserEntity!!
        mIdUser = StringBuilder().append(mCurrentUserEntity.id).toString()

        val messageBundle = EasyGolfNavigation1.messageBundle(intent)

        mChannelChatId = messageBundle.channelId
        mIsGoingToGroupChat = messageBundle.group

        Log.e("WOW","channel id in message: ${mChannelChatId}")
        /**
         * Start init for firebase
         * */
        mFirebaseDatabaseReferenceChannel = FirebaseDatabase.getInstance().getReference("channels")
                .child(mChannelChatId)

        mFirebaseDatabaseReferenceMessage = FirebaseDatabase.getInstance().getReference("messages")
                .child(mChannelChatId)

        mFirebaseRefMembers = FirebaseDatabase.getInstance().getReference("channels").child(mChannelChatId)
                .child("members")



        mFirebaseDatabaseReferenceChannel.child("last_message")
                .child("need_push").onDisconnect().setValue(false)


        mTxtNameChat = findViewById(R.id.txtName)
        mTxtStatus = findViewById(R.id.txtStatus)
        if (mIsGoingToGroupChat) {
            handlerNameGroupChat()
            mTxtStatus.visibility = View.GONE
        } else {
            mTxtStatus.visibility = View.VISIBLE
            mFirebaseRefMembers?.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(dataSnapMembers: DataSnapshot) {
                    for (item in dataSnapMembers.children) {
                        val member = item.getValue(MemberChat::class.java)
                        member?.apply {
                           if(user_id != mIdUser){
                               getInfoUserChat(user_id)
                           }
                        }
                    }
                }

            })
        }

        mAnimationShowUnread = AnimationUtils.loadAnimation(this, R.anim.show_unread_message)
        mAnimationHideUnread = AnimationUtils.loadAnimation(this, R.anim.hide_unread_message)
        mAnimationUpdateUnread = AnimationUtils.loadAnimation(this, R.anim.update_unread_message)

        mAnimationShowUnread.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                mIsShowUnread = true
            }

            override fun onAnimationStart(animation: Animation?) {
                mLayoutUnread.visibility = View.VISIBLE
            }

        })

        mAnimationHideUnread.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                mIsShowUnread = false
                mLayoutUnread.visibility = View.INVISIBLE
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })

        /**
         * Start find view by id
         * */
        mViewRoot = findViewById(R.id.viewRoot)
        mBtSend = findViewById(R.id.btSend)

        findViewById<View>(R.id.btCamera).setOnClickListener(this)
        mBtSend.setOnClickListener(this)
        mInputChat = findViewById(R.id.inputChat)
        mLayoutUnread = findViewById(R.id.layoutUnread)
        mEmojiPopup = EmojiPopup.Builder.fromRootView(mViewRoot).build(mInputChat)
        mTxtUnreadMessage = findViewById(R.id.txtUnreadMessage)
        findViewById<View>(R.id.emojiIcon).setOnClickListener(this)
        findViewById<View>(R.id.actionMore).setOnClickListener(this)

        mMessageRecyclerView = findViewById(R.id.messageRecyclerView)
        mMessageRecyclerView.setHasFixedSize(true)
        mTargetLayoutManager = LinearLayoutManager(this)
        mTargetLayoutManager.stackFromEnd = true
        mMessageRecyclerView.layoutManager = mTargetLayoutManager

        mLayoutUnread.setOnClickListener {
            mMessageRecyclerView.smoothScrollToPosition(mWozMessengerAdapter.itemCount)
        }

        /**
         * End find view by id
         * */



        /**
         * Start get last message
         * */
        mFirebaseDatabaseReferenceMessage.orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (itemChild in dataSnapshot.children) {
                            itemChild.key?.let { key ->
                                mLastKeyMessage = key
                                initEndStackMessage()
                            }
                            break
                        }
                    }
                })

        /**
         * Start active for yourself in this channel
         * */
        mFirebaseRefMembers?.orderByChild("user_id")?.equalTo(mIdUser)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (item in dataSnapshot.children) {
                            val meInChannel = item.getValue(MemberChat::class.java)
                            meInChannel?.apply {
                                this.active = true
                                this.unread_count = 0
                                dataSnapshot.ref.child(item.key!!).setValue(this)

                                mIdCurrentUserInChannel = item.key!!
                            }
                        }
                    }

                })
    }


    private fun getInfoUserChat(id: String) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(id).child("profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(ProfileUser::class.java)
                        user?.apply {
                            mTxtNameChat.text = this.getFullName()

                        }
                    }

                })

        /**
         * Register user is online
         * */
        FirebaseDatabase.getInstance().getReference("users")
                .child(id).child("profile")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(ProfileUser::class.java)
                        user?.apply {
                            val statusValue = if(this.online) {"Online"} else {"Offline"}
                            mTxtStatus.text = statusValue
                        }
                    }

                })
    }

    /**
     * Item message click
     * */
    override fun onClickItem(model: EasyGolfMessenger, v: View) {
        when(EasyGolfMessenger.getType(model.type)){
            EasyGolfMessenger.TypeMessage.TEXT->{

            }
            EasyGolfMessenger.TypeMessage.IMAGE->{
                goToDetailPhoto(model.body,v)
            }
            else -> {}
        }
    }

    private fun goToDetailPhoto(url:String,imageView:View){
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                imageView, Contains.TRANSITION_PHOTO_NAME).toBundle()
        Intent(this,PhotoDetailActivity::class.java)
                .putExtra(Contains.EXTRA_PHOTO_URL_KEY,url)
                .let {
                    startActivity(it,option)
                }
    }

    fun initEndStackMessage() {
        mWozMessengerAdapter = WozMessengerAdapter(this, mIdUser,mChannelChatId,this)
        mMessageRecyclerView.adapter = mWozMessengerAdapter
        mWozMessengerAdapter.registerLoadMore(mTargetLayoutManager, mMessageRecyclerView) {
            if(mStatusLoading != STATUS.LOAD_MORE) {
                mStatusLoading = STATUS.LOAD_MORE
                loadMoreHistoryMessage()
            }
        }

        mFirebaseDatabaseReferenceMessage.orderByKey().startAt(mLastKeyMessage)
                .addChildEventListener(object : ChildEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        val itemMess = dataSnapshot.getValue(EasyGolfMessenger::class.java)
                        itemMess?.let { model ->
                            model.id = dataSnapshot.key
                            mWozMessengerAdapter.insertNewMessage(model)

                            val messengerCount = mWozMessengerAdapter.itemCount
                            val lastVisiblePosition = mTargetLayoutManager.findLastCompletelyVisibleItemPosition()
                            /**
                             * @param {lastVisiblePosition == -1} when first data
                             * @param {(positionStart > (messengerCount - 1) } when data load finish in last
                             * @param {lastVisiblePosition == (positionStart - 1)}  when data load finish in last
                             * */
                            if (lastVisiblePosition == -1  ||
                                    lastVisiblePosition == messengerCount - 2) {
                                mMessageRecyclerView.scrollToPosition(mWozMessengerAdapter.itemCount - 1)
                            } else {
                                /**
                                 * Show unread
                                 * */
                                if (lastVisiblePosition < messengerCount - 1 && model.sender_id != mIdUser
                                        && mStatusLoading != STATUS.FIRST_LOAD)
                                    receiveUnreadMessage(model)
                            }

                            if(mStatusLoading == STATUS.FIRST_LOAD){
                                mStatusLoading = STATUS.LOAD_MORE
                            }
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }

                })

        /**
         * End init for firebase
         * */

        mAnimationDrawableSendText = AnimatedVectorDrawableCompat.create(this,R.drawable.sender_text_anim)
        mAnimationDrawableSendText?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback(){
            override fun onAnimationEnd(drawable: Drawable?) {
                mStateButtonSender = StateButtonSender.SEND_TEXT
                super.onAnimationEnd(drawable)

            }
        })
        mAnimationDrawableSendImage = AnimatedVectorDrawableCompat.create(this,R.drawable.sender_image_anim)
        mAnimationDrawableSendImage?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback(){
            override fun onAnimationEnd(drawable: Drawable?) {
                mStateButtonSender = StateButtonSender.SEND_IMAGE
                super.onAnimationEnd(drawable)

            }
        })
//
        mInputChat.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                mTimer = Timer()
                mTimer?.schedule(object : TimerTask(){
                    override fun run() {
                        finishTyping()
                    }

                },1000)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mTimer?.apply {
                    cancel()
                }
                if(mInputChat.text.toString().trim().isNotEmpty()) {
                    if(before < count) {
                        goingToTyping()
                    }
                    animationButtonSender(true)
                }else{
                    animationButtonSender(false)
                }

            }
        })



        mListenerTyping = object : ChildEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {}
                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        val itemUser = dataSnapshot.getValue(MemberChat::class.java)
                        itemUser?.apply {
                            if(this.user_id != mIdUser) {
                                if (mWozMessengerAdapter.itemCount > 0) {
                                    if (typing) {
                                        val messengerCount = mWozMessengerAdapter.itemCount
                                        val lastVisiblePosition = mTargetLayoutManager.findLastCompletelyVisibleItemPosition()
                                        mWozMessengerAdapter.addTyping(this.user_id)
                                        if (lastVisiblePosition == -1 ||
                                                lastVisiblePosition == messengerCount - 1) {
                                            mMessageRecyclerView.scrollToPosition(messengerCount)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        val itemUser = dataSnapshot.getValue(MemberChat::class.java)
                        itemUser?.apply {
                            if(this.user_id != mIdUser) {
                                mWozMessengerAdapter.removeTyping(this.user_id)
                            }
                        }

                    }

                }

        /**
         * Register event members typing
         * */
        mFirebaseRefMembers?.orderByChild("typing")?.equalTo(true)?.addChildEventListener(mListenerTyping!!)

        mListenerUnread = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataUnread: DataSnapshot) {
                mWozMessengerAdapter.bindUnread(dataUnread.value == null)
            }

        }
        /**
         * Register event members unread
         * */
        mFirebaseRefMembers?.orderByChild("unread_count")?.startAt(1.0)?.addValueEventListener(mListenerUnread!!)


        mMessageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if(mLayoutUnread.visibility == View.VISIBLE){
                        mLayoutUnread.startAnimation(mAnimationHideUnread)
                    }
                }
            }
        })
//
//        registerAdapterMessenger()

        /**
         * Start handle load more
         * */
//        mMessageRecyclerView.addOnScrollListener(object : EndlessRecyclerEndStackScrollListener(mTargetLayoutManager){
//            override fun onLoadMore() {
//                mViewLoadMore.visibility = View.VISIBLE
//                loadMoreHistoryMessage()
//            }
//        })
        /**
         * End handle load more
         * */
    }

    private fun loadMoreHistoryMessage() {
        mFirebaseDatabaseReferenceMessage.orderByKey().endAt(mLastKeyMessage)
                .limitToLast(NUMBER_MESSAGE_IN_PAGE).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(dataSnap: DataSnapshot) {
                        Log.e("WOW", "onDataChange: $dataSnap")
                        val listHistory = ArrayList<EasyGolfMessenger>()
                        var setKey = false
                        val total = dataSnap.childrenCount - 1
                        for ((index, item) in dataSnap.children.withIndex()) {
                            if (index < total) {
                                if (!setKey) {
                                    mLastKeyMessage = item.key!!
                                    setKey = true
                                }
                                val itemMessage = item.getValue(EasyGolfMessenger::class.java)
                                itemMessage?.apply {
                                    this.id = item.key
                                    listHistory.add(this)

                                }
                            }
                        }
                        mWozMessengerAdapter.addListHistory(listHistory)
                        if(dataSnap.childrenCount < NUMBER_MESSAGE_IN_PAGE){
                            mWozMessengerAdapter.onReachEnd()
                        }

                        mStatusLoading = STATUS.FINISH
                    }

                })
    }

    private fun handlerNameGroupChat() {
        FirebaseDatabase.getInstance().getReference("channels").child(mChannelChatId)
                .child("name").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(snapChat: DataSnapshot) {
                        val nameGroup = snapChat.getValue(String::class.java)
                        nameGroup?.apply {
                            mTxtNameChat.text = this
                        }
                    }

                })

    }


    override fun loadData() {

    }

    private fun receiveUnreadMessage(model: EasyGolfMessenger) {
        if (!mIsShowUnread) {
            mLayoutUnread.startAnimation(mAnimationShowUnread)
        } else {
            mLayoutUnread.startAnimation(mAnimationUpdateUnread)
        }
        mTxtUnreadMessage.text = model.body
    }

    private fun animationButtonSender(sender: Boolean) {
        if (sender) {
            if (mStateButtonSender != StateButtonSender.SEND_TEXT) {
                mAnimationDrawableSendText?.apply {
                    if (!isRunning) {
                        mBtSend.setImageDrawable(this)
                        mAnimationDrawableSendImage?.stop()
                        start()
                        mStateButtonSender = StateButtonSender.ANIMATION_LOADING
                    }
                }
            }
        } else {
            if (mStateButtonSender != StateButtonSender.SEND_IMAGE) {
                mAnimationDrawableSendImage?.apply {
                    if (!isRunning) {
                        mBtSend.setImageDrawable(this)
                        mAnimationDrawableSendText?.stop()
                        start()
                        mStateButtonSender = StateButtonSender.ANIMATION_LOADING
                    }
                }
            }
        }
    }

    /**
     * update updated_at for members unread
     * */

    private fun unreadForMembers() {
        mFirebaseRefMembers
                ?.orderByChild("active")?.equalTo(false)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (item in dataSnapshot.children) {
                            val member = item.getValue(MemberChat::class.java)
                            member?.apply {
                                val unreadCount = this.unread_count + 1
                                Log.e("WOW","unread: ${unreadCount}")
                                mFirebaseDatabaseReferenceChannel.child("last_message")
                                        .child("need_push").setValue(false)
                                mFirebaseRefMembers?.child(item.key!!)?.child("unread_count")?.setValue(unreadCount)
                            }

                        }
                    }

                })


    }

    private fun unlockMemberHidden(){
        mFirebaseRefMembers?.orderByChild("hidden")
                ?.equalTo(true)?.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataMembers: DataSnapshot) {
                        for (snapHidden in dataMembers.children){
                            snapHidden.key?.let { safeKey->
                                dataMembers.ref.child(safeKey).child("hidden").setValue(false)
                            }

                        }
                        Log.e("WOW",dataMembers.toString())
                    }

                })
    }

    /**
     * update updated_at for members
     * */

    private fun updateAtForMembers(message: EasyGolfMessenger) {
        mFirebaseRefMembers?.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dataMembers: DataSnapshot) {
                for (item in dataMembers.children) {
                    val member = item.getValue(MemberChat::class.java)
                    member?.apply {
                        FirebaseDatabase.getInstance().getReference("users").child(user_id)
                                .child("chats").child(mChannelChatId).child("updated_at")
                                .setValue(message.time * -1)
                    }

                }
            }

        })
    }

    private fun updateLastMessageToChannel(message: EasyGolfMessenger, key: String?) {
        key?.apply {
            message.id = this
        }
        mFirebaseDatabaseReferenceChannel.child("last_message").setValue(LastMessengerModel(message))
    }

    private fun goingToTyping() {
        if (!mIsTyping) {
            mIsTyping = true
            mFirebaseRefMembers
                    ?.orderByChild("user_id")?.equalTo(mIdUser)
                    ?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (item in dataSnapshot.children) {
                                mFirebaseDatabaseReferenceChannel.child("last_message")
                                        .child("need_push").setValue(false)
                                dataSnapshot.ref.child(item.key!!).child("typing").setValue(true)
                            }
                        }

                    })

        }
    }

    private fun finishTyping() {
        if (mIsTyping) {
            mIsTyping = false
            mFirebaseRefMembers
                    ?.orderByChild("user_id")?.equalTo(mIdUser)
                    ?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (item in dataSnapshot.children) {
                                mFirebaseDatabaseReferenceChannel.child("last_message")
                                        .child("need_push").setValue(false)
                                dataSnapshot.ref.child(item.key!!).child("typing")
                                        .setValue(false)
                            }
                        }

                    })

        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when (id) {
                R.id.actionMore -> {
                    val bundle = Bundle()
                    bundle.putString(Contains.EXTRA_CHANEL_CHAT,mChannelChatId)
                    bundle.putBoolean(Contains.EXTRA_IS_GROUP_CHAT,mIsGoingToGroupChat)
                    startActivity(SettingMessageActivity::class.java,bundle)
                }
                R.id.btCamera -> {
                    checkPermissionToGetImage(true)
//                    OptionPickerImageDialog(this@MessengerActivity).show()
                }

                R.id.btSend -> {
                    if (mStateButtonSender == StateButtonSender.SEND_TEXT && !mInputChat.text.isNullOrEmpty()) {
                        sendMessage("text", mInputChat.text.toString().trim(), Date().time)
                    } else {
                        if (mStateButtonSender == StateButtonSender.SEND_IMAGE) {
                            checkPermissionToGetImage(false)
                        }
                    }

                    mMessageRecyclerView.smoothScrollToPosition(mWozMessengerAdapter.itemCount)
                }

                R.id.emojiIcon -> {
                    mEmojiPopup.toggle()
                }
            }
        }
    }

    /**
     * Send message to channel
     * @param {type: starting - text - image - add - leave}
     * */
    private fun sendMessage(type: String, body: String, currentTime: Long, key: String? = null,
                            listener: DatabaseReference.CompletionListener? = null) {
        var db = mFirebaseDatabaseReferenceMessage

        db = if (key == null) {
            db.push()
        } else {
            db.child(key)
        }

        val itemMessage = EasyGolfMessenger()
        itemMessage.sender_id = mCurrentUserEntity.id
        itemMessage.time = currentTime
        itemMessage.type = type
        itemMessage.body = body
        db.setValue(itemMessage, listener)
        mInputChat.text = null

        unreadForMembers()
        updateAtForMembers(itemMessage)
        updateLastMessageToChannel(itemMessage, db.key)
        unlockMemberHidden()
    }

    private fun removeMessage(key: String,
                              listener: DatabaseReference.CompletionListener? = null) {
        val db = mFirebaseDatabaseReferenceMessage.child(key)

        db.setValue(null, listener)
        mInputChat.text = null
    }


    private fun checkPermissionToGetImage(isCamera: Boolean) {
//    TODO @quipham    Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//                .withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                        if (report.isAnyPermissionPermanentlyDenied) {
//                            val dataExplanation = getString(R.string.explanation,
//                                    report.deniedPermissionResponses[0]
//                                            .permissionName)
//
//                            Snackbar.make(mViewRoot, dataExplanation, Snackbar.LENGTH_INDEFINITE)
//                                    .setAction("ok") {
//                                        val intent = Intent()
//                                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                        val uri = Uri.fromParts("package", packageName, null)
//                                        intent.data = uri
//                                        startActivity(intent)
//                                    }.show()
//                        }
//
//                        if (report.areAllPermissionsGranted()) {
//                            if (isCamera) {
//                                AppUtil.dispatchTakePictureIntent(this@MessengerActivity)
//                            } else {
//                                pickImageGallery(this@MessengerActivity)
//                            }
//                        }
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
//                        token.continuePermissionRequest()
//                    }
//
//                }).onSameThread().check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Contains.REQUEST_CODE_PHOTO_GALLERY) {
                intent?.apply {
                    data?.let { _uri ->

                        val currentTime = Date().time
                        sendMessage("image", "", currentTime, null,
                                DatabaseReference.CompletionListener { error, databaseReference ->
                                    if (error == null) {
                                        val key = databaseReference.key!!
                                        val storageReference = mStorageRef
                                                .child("chats")
                                                .child(mCurrentUserEntity.id)
                                                .child(createNameImage())

                                        putImageInStorage(storageReference, _uri, key, currentTime)
                                    }
                                })
                    }
                }
            }

            if (requestCode == Contains.REQUEST_CODE_CAMERA) {
                val uriFileCamera = Uri.fromFile(AppUtil.mFile)

                val currentTime = Date().time
                sendMessage("image", "", currentTime, null,
                        DatabaseReference.CompletionListener { error, databaseReference ->
                            if (error == null) {
                                val key = databaseReference.key!!
                                val storageReference = mStorageRef
                                        .child("chats")
                                        .child(mCurrentUserEntity.id)
                                        .child(createNameImage())

                                putImageInStorage(storageReference, uriFileCamera, key, currentTime)
                            }
                        })
            }
        }
    }

    private fun createNameImage(): String {
        return "${AppUtil.getRandomString(5)}_${Date().time}.jpg"
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String, currentTime: Long) {
        storageReference.putFile(uri).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                task.result?.apply {
                    metadata?.reference?.downloadUrl?.addOnCompleteListener(this@MessengerActivity
                    ) { uriTask ->
                        Log.e("WOW", "uri img upload : ${uriTask.result}")
                        sendMessage("image", uriTask.result.toString(), currentTime, key)
                    }
                }
            } else {
                Toast.makeText(this@MessengerActivity, "upload image is fail", Toast.LENGTH_SHORT).show()
                removeMessage(key)
                /**
                 * Remove message image if necessary
                 * */
            }
        }
    }

    override fun isHideSoftKeyBoardTouchOutSide(): Boolean {
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun leaveGroupChat(e: MessageEvent) {
        mLeaveGroup = true
        finish()
    }
}