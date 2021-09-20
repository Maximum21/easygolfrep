package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.feed.CommentResponse
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.golf.Club

class PostDetailsRepository (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(id: String, comment :String, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.addComment(id,comment,result, errorResponse)

    suspend operator fun invoke(id: String, result: (CommentResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.listComment(id,result, errorResponse)

    suspend operator fun invoke(id: String,type: Int,type2: String,result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.deletePost(id,type,type2,result, errorResponse)

    suspend operator fun invoke(result: (ArrayList<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.getFriends(result, errorResponse)

    suspend operator fun invoke(postDescription: String, files: ArrayList<String>, club: Club?, friends:List<User>, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.createPost(postDescription,files,club,friends,result, errorResponse)

    suspend operator fun invoke(postDescription: String, files: ArrayList<String>, club: Club?, friends:List<User>,id:String, tag:Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.updatePost(postDescription,files,club,friends,id,tag,result, errorResponse)

    suspend operator fun invoke(postId: String, commentId :String, type:Int, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.deleteComment(postId,commentId,type,result, errorResponse)

    suspend operator fun invoke(postId: String, commentId: String, comment: String, type:Int, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.editComment(postId,commentId,comment,type,result, errorResponse)

}