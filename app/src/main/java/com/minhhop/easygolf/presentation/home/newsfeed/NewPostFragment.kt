package com.minhhop.easygolf.presentation.home.newsfeed

import android.view.View
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import kotlinx.android.synthetic.main.fragment_new_post.view.*
import org.koin.android.ext.android.inject

class NewPostFragment : EasyGolfFragment<NewPostViewModel>(){

    override val mViewModel: NewPostViewModel by inject()

    override fun setLayout(): Int = R.layout.fragment_new_post

    override fun initView(viewRoot: View) {
        context?.let {
            viewRoot.cancel_post_tv.setOnClickListener {
                fragmentManager!!.popBackStack()
            }
        }
    }

    override fun loadData() {

    }
}