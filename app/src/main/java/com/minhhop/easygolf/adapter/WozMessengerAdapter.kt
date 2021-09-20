package com.minhhop.easygolf.adapter

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.minhhop.core.domain.firebase.EasyGolfMessenger
import com.minhhop.core.domain.firebase.EasyGolfMessenger.TypeMessage.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.listeners.EndlessRecyclerEndStackScrollListener
import com.minhhop.easygolf.listeners.EventLoadMore
import com.minhhop.easygolf.utils.AppUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WozMessengerAdapter(context: Context, private val mIdCurrentUser: String,
                          private val mChannelChatId:String,private val mListener: ListenItemMessage) :
        BaseRecyclerViewAdapter<EasyGolfMessenger>(context) {

    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder {
        return WozMessengerHolder(viewRoot!!)
    }

    enum class WhereItem {
        ALONE,
        TOP,
        CENTER,
        BOTTOM
    }


    companion object {
        private const val STARTING_TYPE = 0x0002
        private const val SENDER_TYPING_TYPE = 0x0003
        private const val SENDER_TEXT_TYPE = 0x0004
        private const val SENDER_IMAGE_TYPE = 0x0005

        private const val RECEIVE_TYPING_TYPE = 0x0006
        private const val RECEIVE_TEXT_TYPE = 0x0007
        private const val RECEIVE_IMAGE_TYPE = 0x0008

        private const val CHANGE_NAME_GROUP_TYPE = 0x0009
        private const val ADD_MEMBER_GROUP_TYPE = 0x0010
        private const val LEAVE_MEMBER_GROUP_TYPE = 0x0011
    }
    private var mUnread = true

    private var mListTyping = ArrayList<String>()
    private val mListProfileUser = HashMap<String, ProfileUser>()
    private var mEndlessRecyclerEndStackScrollListener: EndlessRecyclerEndStackScrollListener? = null

    private var mMarginTopSameMassage:Int = 0
    private var mMarginTopAnotherMassage:Int = 0

    init {
        mMarginTopSameMassage = AppUtil.dp2px(3f, context).toInt()
        mMarginTopAnotherMassage = AppUtil.dp2px(10f, context).toInt()
    }

    override fun setLayout(viewType: Int): Int {
        return when (viewType) {
            STARTING_TYPE -> {
                R.layout.item_messenger_starting
            }
            SENDER_TYPING_TYPE -> {
                R.layout.item_messenger_typing_sender
            }
            RECEIVE_TYPING_TYPE -> {
                R.layout.item_messenger_typing_sender
            }
            SENDER_TEXT_TYPE -> {
                R.layout.item_messenger_text_sender
            }
            RECEIVE_TEXT_TYPE -> {
                R.layout.item_messenger_text_recieve
            }
            SENDER_IMAGE_TYPE -> {
                R.layout.item_messenger_image_sender
            }
            RECEIVE_IMAGE_TYPE -> {
                R.layout.item_messenger_image_receive
            }
            CHANGE_NAME_GROUP_TYPE->{
                R.layout.item_messenger_change_name_group
            }
            ADD_MEMBER_GROUP_TYPE->{
                R.layout.item_messenger_change_name_group
            }
            LEAVE_MEMBER_GROUP_TYPE->{
                R.layout.item_messenger_change_name_group
            }
            else -> {
                R.layout.item_messenger_starting
            }
        }
    }

    fun addTyping(id:String) {
        mListTyping.add(0,id)
        notifyItemInserted(itemCount - 1)
        notifyItemChanged(itemCount - 2)

        val layout = mLayoutManager as LinearLayoutManager
        Log.e("WOW","first visible: ${layout.findFirstVisibleItemPosition()}")

    }

    fun removeTyping(id:String) {
        for (index in 0 until mListTyping.size){
            if(mListTyping[index] == id){
                mListTyping.removeAt(index)
                notifyItemRemoved(itemCount - index)
                break
            }
        }


    }

    override fun getCustomItemViewType(position: Int): Int {
        if (position >= itemCount - mListTyping.size) {
            return RECEIVE_TYPING_TYPE
        }

        val itemMessage = getItem(position)

        when (EasyGolfMessenger.getType(itemMessage.type)) {
            STARTING -> {
                return STARTING_TYPE
            }
            TEXT -> {
                return if (itemMessage.sender_id == mIdCurrentUser) {
                    SENDER_TEXT_TYPE
                } else {
                    RECEIVE_TEXT_TYPE
                }
            }
            IMAGE -> {
                return if (itemMessage.sender_id == mIdCurrentUser) {
                    SENDER_IMAGE_TYPE
                } else {
                    RECEIVE_IMAGE_TYPE
                }
            }
            ADD -> {
                return ADD_MEMBER_GROUP_TYPE
            }
            LEAVE -> {
                return LEAVE_MEMBER_GROUP_TYPE
            }
            NAME -> {
                return CHANGE_NAME_GROUP_TYPE
            }
        }
    }

    private fun getItem(position: Int): EasyGolfMessenger = mDataList[position - 1]

    fun insertNewMessage(model: EasyGolfMessenger){
        mDataList.add(model)
        notifyItemInserted(itemCount - mListTyping.size)
        notifyItemChanged(itemCount - 2 - mListTyping.size)
    }

    fun bindUnread(isUnread:Boolean){
        this.mUnread = isUnread
        notifyItemChanged(mDataList.size)
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(holder is WozMessengerHolder) {
            val coreHolder = holder as WozMessengerHolder?
            coreHolder?.let { wozHolder ->
                wozHolder.destroyDeque()

                if (position >= itemCount - mListTyping.size) {
                    val idSenderTyping = mListTyping[itemCount - 1 - position ]
                    if(idSenderTyping != mIdCurrentUser){
                        wozHolder.initProfileMember(idSenderTyping)
                    }
                    wozHolder.mLayoutRoot?.let { root ->
                        if (position > 1) {
                            val params = root.layoutParams as ViewGroup.MarginLayoutParams

                            if(position >= itemCount - 1){
                                params.bottomMargin = mMarginTopAnotherMassage
                            }
                            root.requestLayout()
                        }
                    }
                    return
                }

                val model = getItem(position)
                val type = EasyGolfMessenger.getType(model.type)
                when (type) {
                    STARTING -> {
                    }
                    ADD -> {
                        wozHolder.initMemberAddGroup("")
                        Log.e("WOW","body add : ${model.body}")
                        wozHolder.initProfileMember(model.body)
                        wozHolder.showTime(model.time)
                    }
                    LEAVE -> {
                        wozHolder.initMemberLeftGroup(model.body)
                        wozHolder.initProfileMember(model.sender_id)
                        wozHolder.showTime(model.time)
                    }
                    IMAGE -> {
                        wozHolder.registerDequePhoto(model)
                        wozHolder.initViewImageMessage(model)
                    }
                    TEXT -> {
                        wozHolder.initViewTextMessage(position,model)
                    }
                    NAME -> {
                        wozHolder.initChangeNameGroup(model.body)
                        wozHolder.initProfileMember(model.sender_id)
                        wozHolder.showTime(model.time)
                    }
                }

                if(type != ADD || type != LEAVE || type != NAME) {
                    if (model.sender_id == mIdCurrentUser && position >= mDataList.size) {
                        wozHolder.mIconMark?.let { mark ->
                            val draw = if (mUnread) {
                                ContextCompat.getDrawable(context, R.drawable.ic_icon_send_mark)
                            } else {
                                null
                            }
                            mark.setImageDrawable(draw)
                            mark.visibility = View.VISIBLE
                        }
                    } else {
                        wozHolder.mIconMark?.visibility = View.GONE

                        wozHolder.initProfileMember(model.sender_id)
                    }
                }


                wozHolder.mLayoutRoot?.let { root ->
                    val whereIsIt = wozHolder.whereIsMyItem(position, model)
                    val params = root.layoutParams as ViewGroup.MarginLayoutParams

                    if (position >= itemCount - 1 && (whereIsIt == WhereItem.TOP ||
                                            whereIsIt == WhereItem.ALONE)) {
                        params.bottomMargin = mMarginTopAnotherMassage
                    }else{
                        params.bottomMargin = mMarginTopSameMassage
                    }

                    root.requestLayout()
                }
            }
        }

    }

    override fun registerLoadMore(layoutManager: RecyclerView.LayoutManager, recyclerView: RecyclerView,
                                  eventLoadMore: EventLoadMore) {
        this.isRegisterLoadMore = true
        this.mLayoutManager = layoutManager
        this.mEventLoadMore = eventLoadMore

        mEndlessRecyclerEndStackScrollListener = object : EndlessRecyclerEndStackScrollListener(mLayoutManager){
            override fun onLoadMore() {
                if (!mIsReachEnd) {
                    mEventLoadMore.onLoadMore()
                }
            }
        }

        recyclerView.addOnScrollListener(mEndlessRecyclerEndStackScrollListener!!)
    }

    fun addListHistory(listData:List<EasyGolfMessenger>){
        for (index in listData.size - 1 downTo 0 step 1) {
            mDataList.add(0,listData[index])
        }
        notifyItemChanged(1)
        notifyItemRangeInserted(0,listData.size)
    }

    override fun getItemCount(): Int {
        return mDataList.size + 1 + mListTyping.size
    }

    override fun onReachEnd() {
        mIsReachEnd = true
        notifyItemChanged(0)
    }

    override fun getBottomItemPosition(): Int = 0

    inner class WozMessengerHolder(itemView:View) : CoreRecyclerViewHolder(itemView) {
        private var mDBFirebase: DatabaseReference? = null
        private var mListenerFirebase:ValueEventListener? = null

        private var mDBFirebaseDeque: DatabaseReference? = null
        private var mListenerFirebaseDeque:ValueEventListener? = null

        val mIconMark: ImageView? = itemView.findViewById(R.id.iconMark)
        private val mLoadingImage: View? = itemView.findViewById(R.id.loadingImage)
        val mLayoutRoot: View? = itemView.findViewById(R.id.layoutRoot)
        private val mTxtMessage: TextView? = itemView.findViewById(R.id.txtMessage)
        val mImgPhoto: ImageView? = itemView.findViewById(R.id.imgPhoto)
        private val mTxtDate: TextView? = itemView.findViewById(R.id.txtDate)
        val mImgAvatar: ImageView? = itemView.findViewById(R.id.imgAvatar)
        private val mLayoutMessage: View? = itemView.findViewById(R.id.layoutMessage)
        private val mTxtNameMember: TextView? = itemView.findViewById(R.id.txtNameMember)

        init {
            itemView.setOnClickListener {
                if(adapterPosition - 1 < mDataList.size) {
                    val model = mDataList[adapterPosition - 1]
                    if (model.body.isNotEmpty()) {
                        mListener.onClickItem(model, it)
                    }
                }
            }
        }

        fun initViewImageMessage(model: EasyGolfMessenger) {
            showTime(model.time)
            mImgPhoto?.let {  photo ->
                if (model.body.isEmpty()) {
                    mLoadingImage?.visibility = View.VISIBLE
                    photo.visibility = View.GONE
                } else {
                    mLoadingImage?.visibility = View.GONE
                    photo.visibility = View.VISIBLE
                    Glide.with(context)
                            .asDrawable()
                            .load(model.body)
                            .into(photo)
                }
            }
        }

        fun showTime(inTime: Long) {
            mTxtDate?.visibility = View.VISIBLE
            val targetDate = Date(inTime)

            mTxtDate?.text = if (DateUtils.isToday(inTime)) {
                AppUtil.formatDate(targetDate, "hh:mm")
            } else {
                AppUtil.formatDate(targetDate, "MMM dd yyyy")
            }

        }

        private fun hideTime() {
            mTxtDate?.visibility = View.GONE
        }

        fun initChangeNameGroup(newName:String){
            mTxtMessage?.apply {
                text = context.getString(R.string.view_change_name_group,newName)
            }
        }

        fun initMemberAddGroup(newName:String){
            mTxtMessage?.apply {
                text = context.getString(R.string.member_add_to_group,newName)
            }
        }

        fun initMemberLeftGroup(newName:String){
            mTxtMessage?.apply {
                text = context.getString(R.string.member_leave_to_group,newName)
            }
        }

        fun initViewTextMessage(position: Int, model: EasyGolfMessenger) {
            itemView.setOnLongClickListener { true }
            /**
             * When item chat in center list chat
             * */
            val idResBg = when (whereIsMyItem(position, model)) {

                WhereItem.ALONE -> {
                    showTime(model.time)
                    showAvatar()
                    if (model.sender_id == mIdCurrentUser) {
                        R.drawable.bg_messenger_sender_center
                    } else {
                        R.drawable.bg_messenger_receive_center
                    }
                }
                WhereItem.TOP -> {
                    hideTime()
                    hideAvatar()
                    if (model.sender_id == mIdCurrentUser) {
                        R.drawable.bg_messenger_sender_top
                    } else {
                        R.drawable.bg_messenger_receive_top
                    }
                }
                WhereItem.CENTER -> {
                    hideTime()
                    hideAvatar()
                    if (model.sender_id == mIdCurrentUser) {
                        R.drawable.bg_messenger_sender_center
                    } else {
                        R.drawable.bg_messenger_receive_center
                    }
                }
                WhereItem.BOTTOM -> {
                    showTime(model.time)
                    showAvatar()
                    if (model.sender_id == mIdCurrentUser) {
                        R.drawable.bg_messenger_sender_bottom
                    } else {
                        R.drawable.bg_messenger_receive_bottom
                    }
                }
            }
            mLayoutMessage?.background = ContextCompat.getDrawable(context, idResBg)
            mTxtMessage?.text = model.body

        }

        fun initProfileMember(idSender: String){
            if (mListProfileUser.containsKey(idSender)){
                mImgAvatar?.let { avatar->
                    mListProfileUser[idSender]?.let { data->
                        Glide.with(context)
                                .load(data.avatar)
                                .error(R.drawable.ic_icon_user_default)
                                .circleCrop()
                                .into(avatar)

                        mTxtNameMember?.apply {
                            text = data.getShortName()
                        }
                    }
                }

            }else{
                mDBFirebase?.apply {
                    mListenerFirebase?.let { listener->
                        this.removeEventListener(listener)
                    }
                }
                mDBFirebase = FirebaseDatabase.getInstance().getReference("users")
                        .child(idSender).child("profile")
                mListenerFirebase = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(ProfileUser::class.java)
                        user?.apply {
                            mListProfileUser[idSender] = this

                            mImgAvatar?.let { avatar->
                                Glide.with(context)
                                        .load(this.avatar)
                                        .error(R.drawable.ic_icon_user_default)
                                        .circleCrop()
                                        .into(avatar)
                            }

                            mTxtNameMember?.let {nameMember->
                                nameMember.text = getShortName()
                            }

                        }
                    }

                }
                mDBFirebase?.addListenerForSingleValueEvent(mListenerFirebase!!)
            }
        }

        fun destroyDeque(){
            mDBFirebaseDeque?.apply {
                this.removeEventListener(mListenerFirebaseDeque!!)
            }
        }

        fun registerDequePhoto(model: EasyGolfMessenger){
            if(model.body.isEmpty()){
                mDBFirebaseDeque = FirebaseDatabase.getInstance().getReference("messages").child(mChannelChatId)
                        .child(model.id!!).child("body")

                mListenerFirebaseDeque = object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(item: DataSnapshot) {
                        val itemBody = item.getValue(String::class.java)
                        itemBody?.apply {
                            if(this.isNotEmpty()) {
                                model.body = this
                                mDBFirebaseDeque?.removeEventListener(mListenerFirebaseDeque!!)
                                notifyItemChanged(adapterPosition)
                            }
                        }
                    }
                }
                mDBFirebaseDeque?.addValueEventListener(mListenerFirebaseDeque!!)
            }
        }

        private fun showAvatar() {
            mImgAvatar?.visibility = View.VISIBLE
        }

        private fun hideAvatar() {
            mImgAvatar?.visibility = View.INVISIBLE
        }

        fun whereIsMyItem(position: Int, model: EasyGolfMessenger): WhereItem {
            /**
             * find type item chat
             * */
            if (mDataList.size <= 1){
                return  WhereItem.CENTER
            }
            if(position <= 1){
                val itemAfter = getItem(position + 1)
                return if (model.sender_id != itemAfter.sender_id){
                     WhereItem.TOP
                }else{
                    WhereItem.CENTER
                }
            }

            val itemBefore = getItem(position - 1)
            return if (position + 1 < itemCount - mListTyping.size) {
                val itemAfter = getItem(position + 1)
                if ((model.sender_id != itemBefore.sender_id || model.type != itemBefore.type) &&
                        (model.sender_id != itemAfter.sender_id || model.type != itemAfter.type)) {
                    WhereItem.ALONE
                } else {
                    if (model.sender_id != itemBefore.sender_id || model.type != itemBefore.type) {
                       WhereItem.TOP
                    } else {
                        if (model.sender_id != itemAfter.sender_id || model.type != itemAfter.type) {
                            WhereItem.BOTTOM
                        } else {
                            WhereItem.CENTER
                        }
                    }
                }
            } else {
                if (model.sender_id != itemBefore.sender_id || model.type != itemBefore.type) {
                    WhereItem.ALONE
                } else {
                    WhereItem.BOTTOM
                }
            }
        }
    }

    interface ListenItemMessage{
        fun onClickItem(model: EasyGolfMessenger, v:View)
    }
}