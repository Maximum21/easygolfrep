package com.minhhop.easygolf.presentation.feedback

import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import kotlinx.android.synthetic.main.activity_feedback.*
import org.koin.android.ext.android.inject

class FeedbackActivity : EasyGolfActivity<FeedbackViewModel>() {

    override val mViewModel: FeedbackViewModel
            by inject()

    override fun setLayout(): Int = R.layout.activity_feedback


    override fun initView() {
        mViewModel.feedbackCallbackLive.observe(this, Observer {
            hideMask()
            MaterialAlertDialogBuilder(this)
                    .setMessage(it.message)
                    .setNegativeButton(getString(android.R.string.ok)) { _, _ -> finish() }
                    .show()
        })
        btSubject.setOnClickListener { v ->
            showPopup(v)
        }
        btSendFeedback.setOnClickListener {
            if (!TextUtils.isEmpty(textMessage.text)) {
                viewMask()
                mViewModel.sendFeedback(btSubject.text?.toString(), textMessage.text?.toString())
            }
        }
        viewClose.setOnClickListener { finish() }
    }

    override fun loadData() {}

    private fun showPopup(v: View?) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            var value = getString(R.string.general_question)
            when (item.itemId) {
                R.id.actionGeneral -> value = getString(R.string.general_question)
                R.id.actionSuggestion -> value = getString(R.string.suggestion)
                R.id.actionRecentPost -> value = getString(R.string.recent_post)
                R.id.actionIncorrectHole -> value = getString(R.string.incorrect_hole_details)
                R.id.actionIncorrectCourse -> value = getString(R.string.incorrect_course_details)
            }
            (v as? MaterialTextView)?.text = value
            true
        }
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.menu_feedback, popup.menu)
        popup.show()
    }


}