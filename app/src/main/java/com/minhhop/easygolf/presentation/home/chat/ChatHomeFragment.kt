package com.minhhop.easygolf.presentation.home.chat

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.firebase.ChannelChat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.bundle.MessageBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation1
import com.minhhop.easygolf.framework.dialogs.EditNameGroupDialog
import com.minhhop.easygolf.listeners.EventSaveNameGroup
import kotlinx.android.synthetic.main.fragment_chat_home.view.*
import org.koin.android.ext.android.inject

class ChatHomeFragment : EasyGolfFragment<ChatHomeViewModel>(), ChannelMessengerAdapter.EventChannelMessenger {
    override val mViewModel: ChatHomeViewModel by inject()

    private var mChannelMessengerLayout: LinearLayoutManager? = null
    private var mChannelMessengerAdapter: ChannelMessengerAdapter? = null

    override fun setLayout(): Int = R.layout.fragment_chat_home

    override fun initView(viewRoot: View) {
        context?.let { context ->
            mChannelMessengerLayout = LinearLayoutManager(context)
            mChannelMessengerAdapter = mViewModel.createAdapterChannelMessage(context, this)

            viewRoot.listMessage.layoutManager = mChannelMessengerLayout
            viewRoot.listMessage.adapter = mChannelMessengerAdapter
            viewRoot.nestedScrollChatView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
                if ((scrollY - oldScrollY) > 0) {
                    viewRoot.buttonCreateChannel.hide()
                } else {
                    viewRoot.buttonCreateChannel.show()
                }
            }

            mViewModel.onInsertChannelLive.observe(this, Observer {
                if (mChannelMessengerLayout?.findFirstCompletelyVisibleItemPosition() == 0) {
                    // Its at top
                    viewRoot.listMessage.smoothScrollToPosition(0)
                }
            })

            mViewModel.createGroupLive.observe(this, Observer {
                activity?.let { _activity ->
                    EditNameGroupDialog(_activity, getString(R.string.create_group_name))
                            .setEvent(object : EventSaveNameGroup {
                                override fun onSave(value: String) {
                                    mViewModel.startMessageForGroup(value, it)
                                }
                            })
                            .show()
                }
            })
        }

        mViewModel.navigationMessageLive.observe(this, Observer {
            context?.apply {
                EasyGolfNavigation1.messageDirection(this, it)
            }

        })
    }

    override fun loadData() {

    }

    override fun onClickChannel(item: ChannelChat) {
        context?.apply {
            EasyGolfNavigation1.messageDirection(this, MessageBundle(item.id!!, item.group))
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.startListeningChannel()
    }

    override fun onDestroy() {
        mViewModel.stopListeningChannel()
        super.onDestroy()
    }
}