package com.minhhop.easygolf.framework.models

import com.minhhop.core.domain.Contact

class ContactRetrofit {
    private val friends = ArrayList<ContactEntry>()
    fun removeOrAdd(contact: Contact){
        if(contact.selected){
            friends.add(ContactEntry(contact.firstName,contact.lastName,contact.phone_number))
        }else{
            for (i in 0 until friends.size){
                if(friends[i].phoneNumber == contact.phone_number){
                    friends.removeAt(i)
                    break
                }
            }
        }
    }

    fun getSizeFriend() = friends.size
}