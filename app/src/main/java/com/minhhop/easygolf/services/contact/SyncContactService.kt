package com.minhhop.easygolf.services.contact

import android.app.Service
import android.content.Intent
import android.os.*
import android.provider.ContactsContract
import androidx.core.content.ContextCompat.startForegroundService


class SyncContactService : Service() {
    private lateinit var mServiceLooper:Looper
    private lateinit var mServiceHandle: ServiceHandler


    override fun onCreate() {
        super.onCreate()
        val handlerThread = HandlerThread("ServiceStartArguments",Process.THREAD_PRIORITY_FOREGROUND)
        handlerThread.start()

        mServiceLooper = handlerThread.looper
        mServiceHandle = ServiceHandler(mServiceLooper)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val msg = mServiceHandle.obtainMessage()
        msg.arg1 = startId
        mServiceHandle.sendMessage(msg)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
//            // To run your service even after your app is closed
            val intent = Intent(applicationContext,SyncContactService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(this,intent)
            } else {
                startService(intent)
            }


        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun startContactObserver(){
        try {
            applicationContext.contentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI,
                    false,MyContentObserver(applicationContext,Handler()))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message?) {
            try {
                startContactObserver()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}