package com.minhhop.easygolf.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.provider.ContactsContract
import android.util.Log
import com.minhhop.core.domain.Contact
import com.minhhop.easygolf.framework.models.SyncContact
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SyncContactScheduleJob: JobService() {

   companion object{
       private const val TAG = "SyncContactScheduleJob"
   }

    override fun onStopJob(params: JobParameters?): Boolean = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.e("WOW","SyncContactScheduleJob")
        ApiService.getInstance().generalService.syncContact(SyncContact(getAllContact()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ result ->
                    Log.d(TAG,result.message)
                },{ throwable ->
                    Log.e(TAG,throwable.localizedMessage)
                })
        return true
    }

    private fun getAllContact():ArrayList<Contact>{
        val listContact = ArrayList<Contact>()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                null,null,null)

        cursor?.apply {
            while (moveToNext()){
                val avatar = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                val fullName = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var firstName = ""
                val temp = fullName.split(" ")
                if(temp.isNotEmpty()){
                    firstName = temp[0]
                }
                val lastName = fullName.replace(firstName,"")

                val phone = this.getString(this.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val newContact = Contact(avatar, firstName, lastName, phone, null)
                listContact.add(newContact)
            }
        }
        cursor?.close()
        return listContact
    }
}