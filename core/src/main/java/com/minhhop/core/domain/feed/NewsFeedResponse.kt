package com.minhhop.core.domain.feed

import com.minhhop.core.domain.Paginator

data class NewsFeedResponse(
        val paginator: Paginator,
        var data: List<NewsFeed>)