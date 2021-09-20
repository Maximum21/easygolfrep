package com.minhhop.core.domain.notifications

import com.minhhop.core.domain.Paginator

data class NotificationPager (
    val paginator: Paginator,
    val data: List<GolfNotification>
)