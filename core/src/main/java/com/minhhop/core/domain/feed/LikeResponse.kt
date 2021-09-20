package com.minhhop.core.domain.feed

import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Course

data class LikeResponse (
        val courses:List<Course>,
        val last_updated:String,
        val comments:String,
        val image_url:String,
        val date_created:String,
        val post_files:String,
        val created_by: User,
        val friends:List<User>,
        val url:String,
        val content:String,
        val shares:Int,
        val deleted:Boolean,
        val shared_post:String,
        val created_by_id:String,
        val short_desc:String,
         val id:String,
         val likes:Int,
         val post_likes:List<Like>
         )