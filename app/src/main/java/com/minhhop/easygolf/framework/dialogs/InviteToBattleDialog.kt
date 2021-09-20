package com.minhhop.easygolf.framework.dialogs

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseDialog
import com.minhhop.easygolf.framework.models.common.Battle
import com.minhhop.easygolf.utils.AppUtil

class InviteToBattleDialog(private val mActivity:Activity, private val mIdUser:String,
                           private val mCurrentBattle: Battle, private val mEventInvite: EventInvite)
    : BaseDialog(mActivity) {
    override fun initView() {
        findViewById<View>(R.id.btAccept).setOnClickListener(this)
        findViewById<View>(R.id.btRefuse).setOnClickListener(this)
    }

    override fun config(window: Window?) {
        super.config(window)
        window?.let { _window->
            val wlp = _window.attributes
            wlp.gravity = Gravity.CENTER
            _window.setLayout((AppUtil.getWidthScreen(mActivity) * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            _window.attributes = wlp
        }

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when(id){
                R.id.btAccept->{
                    mEventInvite.onAccept()
                    this@InviteToBattleDialog.dismiss()
                }
                R.id.btRefuse->{
                    removeDataBattle()
                    this@InviteToBattleDialog.dismiss()
                }
            }
        }
    }

    private fun removeDataBattle(){
        FirebaseDatabase.getInstance().getReference("users")
                .child(mIdUser)
                .child("battles")
                .orderByChild("club_id").equalTo(mCurrentBattle.club_id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.e("WOW","")
                        for (item in dataSnapshot.children){
                            item.ref.setValue(null)
                        }
                    }

                })

        FirebaseDatabase.getInstance().getReference("rounds")
                .child(mCurrentBattle.round_id)
                .child("members")
                .orderByChild("id").equalTo(mIdUser)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (item in dataSnapshot.children){
                            item.ref.setValue(null)
                        }
                    }

                })
    }

    override fun resContentView(): Int = R.layout.dialog_invite_to_battle

    interface EventInvite{
        fun onAccept()
    }
}