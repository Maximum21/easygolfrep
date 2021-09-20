package com.minhhop.easygolf.presentation.group.chat
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.framework.bundle.MessageBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.presentation.home.chat.ChannelMessengerAdapter
import com.minhhop.easygolf.framework.common.Contains
import org.koin.android.ext.android.inject

class ChatFragment : WozBaseFragment(), ChannelMessengerAdapter.EventChannelMessenger {

    private val chatViewModel: ChatViewModel by inject()

    private lateinit var mChannelMessengerLayout: LinearLayoutManager

    private lateinit var mListMessage: RecyclerView

    override fun setLayout(): Int = R.layout.fragment_chat

    override fun loadData() {
        chatViewModel.goingToOnline()
    }
    /**
     * handle channel click it @param {item} return about channel click
     * */
    override fun onClickChannel(item: ChannelChat) {
        context?.apply {
            EasyGolfNavigation1.messageDirection(this, MessageBundle(item.id!!,item.group))
        }
    }

    override fun initView(viewRoot: View) {

//        mListMessage = viewRoot.findViewById(R.id.listMessage)
//        mChannelMessengerLayout = LinearLayoutManager(context)
//        mListMessage.layoutManager = mChannelMessengerLayout
//
////        mListMessage.adapter = chatViewModel.createAdapterChannelMessage(context!!,this)
//
//        chatViewModel.onInsertChannelLive.observe(this, Observer {
//            if(mChannelMessengerLayout.findFirstCompletelyVisibleItemPosition() == 0){
//                // Its at top
//                mListMessage.smoothScrollToPosition(0)
//            }
//        })
//
//        chatViewModel.createGroupLive.observe(this, Observer {
//            activity?.let {_activity->
//                EditNameGroupDialog(_activity, getString(R.string.create_group_name))
//                        .setEvent(object : EventSaveNameGroup {
//                            override fun onSave(value: String) {
//                              chatViewModel.startMessageForGroup(value,it)
//                            }
//                        })
//                        .show()
//            }
//        })
//
//        chatViewModel.navigationMessageLive.observe(this, Observer {
//            context?.apply {
//                EasyGolfNavigation1.messageDirection(this, it)
//            }
//
//        })
//        /**
//         * Action create channel chat
//         * */
//        viewRoot.findViewById<View>(R.id.actionCreateChannel).setOnClickListener {
//            startActivityForResult(AddPeopleChatActivity::class.java,Contains.REQUEST_CODE_ADD_MEMBER_CHAT)
//        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let { _data ->
            if(resultCode == Activity.RESULT_OK && requestCode == Contains.REQUEST_CODE_ADD_MEMBER_CHAT && context != null){
                val members = _data.getStringExtra(Contains.EXTRA_IS_RETURN)
                chatViewModel.detectChannel(members)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chatViewModel.startListeningChannel()
    }

    override fun onDestroy() {
        chatViewModel.cancelAllRequest()
        chatViewModel.stopListeningChannel()
        super.onDestroy()
    }
}



