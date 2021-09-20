package com.minhhop.easygolf.services.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.minhhop.easygolf.services.contact.SyncContactService

class SensorRestarterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.startService(Intent(context, SyncContactService::class.java))
    }
}