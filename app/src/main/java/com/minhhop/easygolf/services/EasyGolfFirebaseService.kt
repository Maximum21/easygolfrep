package com.minhhop.easygolf.services

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.minhhop.core.domain.firebase.NotificationModel
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.bundle.NotificationBundle


open class EasyGolfFirebaseService : FirebaseMessagingService() {
    companion object {
        const val BROAD_CAST_INTENT = "BROAD_CAST_INTENT"
        private const val NOTIFY_ID = 1002
        private const val CHANEL_ID = "easy_golf_android"
        private const val NAME_CHANEL = "EasyGolf Notification" //TODO @giangle
        private const val CHANEL_DESCRIPTION = "notification for easy golf app " //TODO @giangle
    }

    private var mNotificationBroadCaster: LocalBroadcastManager? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        for (item in remoteMessage.data) {
//            Log.e("WOW", "key: ${item.key}")
//            Log.e("WOW", "value: ${item.value}")
//        }
        val gson = Gson()
        val jsonElement = gson.toJsonTree(remoteMessage.data)
        val dataNotification = gson.fromJson<NotificationModel>(jsonElement, NotificationModel::class.java)
        sendNotification(dataNotification)
    }

    private fun sendNotification(data: NotificationModel) {
        var notifyManager: NotificationManager? = null
        val pendingIntent: PendingIntent

        applicationContext.packageName

        if (notifyManager == null) {
            notifyManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notifyManager.getNotificationChannel(CHANEL_ID)
            if (mChannel == null) {
                mChannel = NotificationChannel(CHANEL_ID, NAME_CHANEL, importance)
                mChannel.description = CHANEL_DESCRIPTION
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                notifyManager.createNotificationChannel(mChannel)
            }
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANEL_ID)
        val notificationBundle = NotificationBundle.extraBundle(data)

//        val classTarget: Class<*> = when (data.type) {
//            Type.CHAT.value -> {
//                bundle.putString(MessageBundle::channelId.name, data.channel_id)
//                bundle.putBoolean(MessageBundle::group.name, data.group)
//                MessengerActivity::class.java
//            }
//            Type.BATTLE.value -> {
//                bundle.putBoolean(PlayGolfBundle1::playWithBattle.name, true)
//                bundle.putString(PlayGolfBundle1::round.name, data.round)
//                bundle.putString(PlayGolfBundle1::clubId.name, data.club_id)
//                bundle.putString(PlayGolfBundle1::courseId.name, data.course_id)
//                PlayGolfActivityOld::class.java
//            }
//            Type.SYNC_CONTACT.value -> {
//                EasyGolfHomeActivity::class.java
//            }
//            Type.FRIEND_INVITATION.value -> {
//                EasyGolfHomeActivity::class.java
//            }
//            else -> {
//                return
//            }
//        }

        if (!isBackgroundRunning(applicationContext)) {
            if (mNotificationBroadCaster == null) {
                mNotificationBroadCaster = LocalBroadcastManager.getInstance(applicationContext)
            }
            val intentBroadcaster = Intent(BROAD_CAST_INTENT)
            notificationBundle?.toBundle()?.let { bundle ->
                bundle.keySet().forEach { key->
                    Log.e("WOW","key new: $key")
                    Log.e("WOW","value new: ${bundle.get(key)}")
                }
                intentBroadcaster.putExtras(bundle)
            }
            mNotificationBroadCaster?.sendBroadcast(intentBroadcaster)
        } else {
            val intent = Intent(this, notificationBundle?.getClassTarget()).apply {
                notificationBundle?.toBundle()?.let { bundle ->
                    putExtras(bundle)
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.action = Intent.ACTION_MAIN

            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            builder.setContentTitle(data.title)  // required
                    .setColor(Color.argb(1, 76, 142, 6))
                    .setSmallIcon(R.drawable.icon_notification) // required
                    .setContentText(data.body)  // required
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(NOTIFY_ID, builder.build())
            }
        }
    }

    private fun isBackgroundRunning(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == context.packageName) {
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("WOW", "token: $p0")
        //TODO call api to update token device
    }
}