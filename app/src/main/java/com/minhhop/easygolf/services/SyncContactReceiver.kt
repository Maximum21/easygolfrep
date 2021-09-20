package com.minhhop.easygolf.services

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class SyncContactReceiver: BroadcastReceiver() {
    private val TAG = "WOW"
    override fun onReceive(context: Context?, intent: Intent) {
        Log.e("WOW","SyncContactReceiver")
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> Log.e(TAG, "screen is on...")
            Intent.ACTION_SCREEN_OFF -> Log.e(TAG, "screen is off...")
            Intent.ACTION_USER_PRESENT -> Log.e(TAG, "screen is unlock...")
        }

        if (intent.action == Intent.ACTION_SCREEN_OFF) {

            context?.let { _context ->
                val serviceComponent = ComponentName(_context, SyncContactScheduleJob::class.java)
                val builder = JobInfo.Builder(0, serviceComponent)
//               builder.setMinimumLatency(3000)
                builder.setMinimumLatency(2_000L)
                builder.setOverrideDeadline(4_000L)
                        .setRequiresCharging(false)
                val jobScheduler = _context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler?
                jobScheduler?.apply {
                    this.schedule(builder.build())
                }

            }
        }

    }
}