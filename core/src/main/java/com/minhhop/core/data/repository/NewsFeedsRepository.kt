package com.minhhop.core.data.repository

import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.feed.NewsFeedResponse

class NewsFeedsRepository(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(type: String,start : Int,tag:Int,result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getPosts(type,start,tag,result, errorResponse)
    suspend operator fun invoke(id: String,type:Int,result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.likePost(id,type,result, errorResponse)
}