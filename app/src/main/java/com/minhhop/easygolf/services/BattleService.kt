package com.minhhop.easygolf.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.models.common.Battle

class BattleService {
    private val mDbUser = FirebaseDatabase.getInstance().getReference("users")

    fun addPlayer(listUser:String,mIdRound:String,mIdClub:String,context: Context,callback: Callback){
        val listUserAdded = ArrayList<String>()
        val arrayUser = listUser.split("-")
        val sizeList = arrayUser.size
        var currentIndex = 0

        var numberFail = 0
        for (item in arrayUser){

            val temp = item.split("$")
            val idPlayer = temp[0]
            val fullName = temp[1]

            val dbRoundMember = mDbUser.child(idPlayer).child("battles")
            val eventMember = object : ValueEventListener{

                override fun onCancelled(snapShot: DatabaseError) {

                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    currentIndex++
                    if(snapShot.value == null){
                        val keyReference = FirebaseDatabase.getInstance().getReference("users")
                                .child(idPlayer).child("battles").push()
                        keyReference.setValue(Battle(mIdRound, mIdClub))

                        val keyReferenceMember =  FirebaseDatabase.getInstance().getReference("rounds")
                                .child(mIdRound).child("members").push()
                        val value = HashMap<String,String>()
                        value["id"] = idPlayer
                        keyReferenceMember.setValue(value)

                        listUserAdded.add(idPlayer)
                        if(sizeList in (numberFail + 1)..currentIndex){
                            callback.onSuccess(listUserAdded)
                        }

                    }else{
                        numberFail ++
                        if(sizeList in (numberFail + 1)..currentIndex){
                            callback.onSuccess(listUserAdded)
                        }

                        Toast.makeText(context,context.getString(R.string.user_cannot_join_the_battle,fullName)
                                ,Toast.LENGTH_SHORT).show()
                    }
                }
            }

            dbRoundMember.orderByChild("club_id").equalTo(mIdClub).addListenerForSingleValueEvent(eventMember)
        }
    }


    fun addMe(idUser:String,mIdRound:String,mIdClub:String){
        val dbAddPlayer = mDbUser.child(idUser)
                .child("battles").push()
        dbAddPlayer.setValue(Battle(mIdRound, mIdClub, true))

        val keyReferenceMember =  FirebaseDatabase.getInstance().getReference("rounds")
                .child(mIdRound).child("members").push()
        val value = HashMap<String,String>()
        value["id"] = idUser
        keyReferenceMember.setValue(value)

    }


    interface Callback{
        fun onSuccess(listUser:List<String>)
    }


}