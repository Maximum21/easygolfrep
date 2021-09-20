package com.minhhop.easygolf.presentation.notification

import android.app.Notification
import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.notifications.GolfNotification
import com.minhhop.core.domain.notifications.NotificationPager
import com.minhhop.core.domain.round.RoundHistoryPager

class NotificationRepository (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(start:Int,text:String, result: (NotificationPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getNotifications(start,text,result, errorResponse)
    suspend operator fun invoke(id:String, tag:Int,result: (GolfNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.notifyNotification(id,tag,result, errorResponse)

}