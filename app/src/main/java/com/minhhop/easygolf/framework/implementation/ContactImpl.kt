package com.minhhop.easygolf.framework.implementation

import android.content.Context
import android.provider.ContactsContract
import com.minhhop.core.data.datasource.ContactDataSource
import com.minhhop.core.domain.Contact
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.easygolf.framework.models.SyncContact
import com.minhhop.easygolf.framework.network.remote.GeneralService
import com.minhhop.easygolf.services.DatabaseService

class ContactImpl(private val context: Context,private val generalService: GeneralService) : ContactDataSource {

    private var mLastTimeNeedUpdate:String = "0"

    override suspend fun sync(contacts: List<Contact>, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {

        val syncContact = SyncContact(contacts)
        if (syncContact.contacts.isNotEmpty()) {
            generalService.syncContacts(syncContact).run({ response ->
                DatabaseService.getInstance().addCountRecordContact(mLastTimeNeedUpdate)
                result(response)
            }, { error ->
                errorResponse(error)
            })
        }
    }

    override fun getContactFromPhone(): List<Contact> {
        val listContact = java.util.ArrayList<Contact>()
        val targetTime = DatabaseService.getInstance().getLastTimeRecordContact()
        mLastTimeNeedUpdate = targetTime.toString()


        val cursor =  context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                getQueryLastTime(), arrayOf(mLastTimeNeedUpdate), ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " ASC")


        if (cursor != null) {
            while (cursor.moveToNext()) {

                val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "")
                val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                mLastTimeNeedUpdate = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))
                val newContact = Contact(null, contactName, null, phone, null)
                listContact.add(newContact)
            }
        }
        cursor?.close()
       return listContact
    }

    private fun getQueryLastTime(): String {
        return "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} > ?"
    }
}