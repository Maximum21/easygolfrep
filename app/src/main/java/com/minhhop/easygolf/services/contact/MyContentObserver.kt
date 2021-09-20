package com.minhhop.easygolf.services.contact

import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import com.minhhop.core.domain.Contact
import com.minhhop.easygolf.framework.models.SyncContact
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.services.DatabaseService
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyContentObserver(private val context: Context,handler: Handler) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        if (!selfChange) {

            try {
                if (ActivityCompat.checkSelfPermission(context,
                                READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    val cr = context.contentResolver
                    val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null,
                            null, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP +" ASC")
                    Log.e("WOW","cursor.count: ${cursor?.count}")


                    if (cursor != null && cursor.count > 0) {
                            //moving cursor to last position
                            //to get last element added
                            cursor.moveToLast()
                            var contactName: String?
                            var contactNumber: String?
                            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))

                            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf<String>(id),
                                        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " ASC")
                                if (pCur != null) {
                                    while (pCur.moveToNext()) {
                                        contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                        if (contactNumber != null && contactNumber.isNotEmpty()) {
                                            contactNumber = contactNumber.replace(" ", "")
                                        }
                                        contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                                        val lastTimeUpdateTamp = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))

                                        contactNumber?.let {
                                            val newContact = Contact(null, contactName, null, it, null, false)
                                            callApiToSyncNewContact(newContact, lastTimeUpdateTamp)

                                        }

                                    }
                                    pCur.close()
                                }
                            }

                        cursor.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }else{
            Log.e("WOW","else selfChange on chane wozpi.com")
        }
    }

    private fun callApiToSyncNewContact(contact: Contact, lastTime:String){
        try {
            ApiService.getInstance().generalService.syncContact(SyncContact(arrayListOf(contact)))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        DatabaseService.getInstance().addCountRecordContact(lastTime)
                    },{ throwable ->
                        throwable.printStackTrace()
                    })
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}