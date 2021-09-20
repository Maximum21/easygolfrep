package com.minhhop.core.domain.feed

import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Club
import com.minhhop.core.domain.golf.Course

data class NewsFeed (
        var courses:List<Course>,
        var post_files:List<PostImage>?,
        var last_updated:String,
        var comments:Int,
        var date_created:String,
        var created_by:User,
        var friends:List<User>,
        var content:String,
        var shares:Int,
        var deleted:Boolean,
        var shared_post: NewsFeed?,
        var post_likes:List<Like>?,
        var id:String,
        var club: Club,
        var likes:Int,
        var liked:Boolean=false,
        var shared:Boolean=false,
        var image_url:String,
        var url:String,
        var created_by_id:String,
        var short_desc:String
)