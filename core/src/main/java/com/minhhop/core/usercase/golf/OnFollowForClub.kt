package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.core.domain.feed.NewsFeedResponse
import com.minhhop.core.domain.golf.FollowResponse

class OnFollowForClub (private val golfRepository: GolfRepository){
    suspend operator fun invoke(clubId:String, result: (FollowResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.onFollowClub(clubId, result, errorResponse)
    suspend operator fun invoke(clubId:String,start:Int, result: (FollowersResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.onFollowersClub(clubId, start, result, errorResponse)
    suspend operator fun invoke(clubId:String,start:Int, tag:Int,result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.fetchClubFeeds(clubId, start, result, errorResponse)
}