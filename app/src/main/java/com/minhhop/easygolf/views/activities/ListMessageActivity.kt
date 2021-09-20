package com.minhhop.easygolf.views.activities

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.WozBaseActivity
import com.minhhop.easygolf.presentation.group.chat.ChatFragment
import com.minhhop.easygolf.presentation.group.contact.ContactFragment

class ListMessageActivity : WozBaseActivity() {
    enum class TAB {
        Chat,
        Contact
    }

    companion object {
        const val TRANSITION_START = "transition_message"
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }

    /**
     * Start zone values
     * */
    private var mRevealX: Int = 0
    private var mRevealY: Int = 0

    private var mTabSelected = TAB.Chat
    private lateinit var mActive: Fragment
    private lateinit var mFm: FragmentManager
    /**
     * Start zone views
     * */
    private lateinit var mViewRoot: View

    private var mChatFragment = ChatFragment()
    private var mContactFragment = ContactFragment()

    private lateinit var mViewTabChat: View
    private lateinit var mViewTabContact: View

    override fun setLayoutView(): Int = R.layout.activity_list_message

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewRoot = findViewById(R.id.viewRoot)
        if(savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)){

            mViewRoot.visibility = View.INVISIBLE
            mRevealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X,0)
            mRevealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y,0)

            val viewTreeObserver = mViewRoot.viewTreeObserver
            if(viewTreeObserver.isAlive){
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        revealActivity()
                        mViewRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                })
            }


        }else {
            mViewRoot.visibility = View.VISIBLE
        }
    }

    private fun revealActivity(){
        val finalRadius = Math.max(mViewRoot.width,mViewRoot.height) * 1.1f
        /**
         * create the animator for this view (the start radius is zero)
         * */
        val circularReveal = ViewAnimationUtils.createCircularReveal(mViewRoot,mRevealX,mRevealY,0f,finalRadius)
        circularReveal.duration = 400
        circularReveal.interpolator = AccelerateInterpolator()
        mViewRoot.visibility = View.VISIBLE

        circularReveal.start()
    }

    private fun unRevealActivity(){
        val finalRadius = Math.max(mViewRoot.width,mViewRoot.height) * 1.1f
        val circularReveal = ViewAnimationUtils.createCircularReveal(mViewRoot,mRevealX,mRevealY,finalRadius,0f)
        circularReveal.duration = 400
        circularReveal.interpolator = AccelerateInterpolator()
        circularReveal.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                mViewRoot.visibility = View.INVISIBLE
                finish()
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
        })
        circularReveal.start()
    }

    override fun onBackPressed() {
        unRevealActivity()
    }

    override fun initView() {
        mViewTabChat = findViewById(R.id.tabChat)
        mViewTabChat.setOnClickListener(this)

        mViewTabContact = findViewById(R.id.tabContact)
        mViewTabContact.setOnClickListener(this)


        mFm = supportFragmentManager
        mActive = mChatFragment
        mFm.beginTransaction().add(R.id.tabContent, mChatFragment).commit()
        mFm.beginTransaction().add(R.id.tabContent, mContactFragment).hide(mContactFragment).commit()
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when(id){
                R.id.tabChat->{
                    if(mTabSelected != TAB.Chat){
                        mFm.beginTransaction().hide(mActive).show(mChatFragment).commit()
                        mActive = mChatFragment
                        mTabSelected = TAB.Chat
                        initBackgroundTab()
                    }
                }
                R.id.tabContact->{
                    if(mTabSelected != TAB.Contact){
                        mFm.beginTransaction().hide(mActive).show(mContactFragment).commit()
                        mActive = mContactFragment
                        mTabSelected = TAB.Contact

                        initBackgroundTab()
                    }
                }
            }
        }
    }

    private fun initBackgroundTab() {
        when (mTabSelected) {
            TAB.Chat -> {
                mViewTabChat.background = ContextCompat.getDrawable(this,R.drawable.background_tab_courses_selected)
                mViewTabContact.background = ContextCompat.getDrawable(this,R.drawable.background_tab_plan)
            }
            TAB.Contact -> {
                mViewTabChat.background = ContextCompat.getDrawable(this,R.drawable.background_tab_courses)
                mViewTabContact.background = ContextCompat.getDrawable(this,R.drawable.background_tab_plan_selected)
            }
        }
    }

    override fun loadData() {

    }
}