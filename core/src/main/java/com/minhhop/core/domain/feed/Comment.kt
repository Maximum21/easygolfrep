package com.minhhop.core.domain.feed

import com.minhhop.core.domain.User

data class Comment (var last_updated:String,
                    var deleted:Boolean,
                    var post: NewsFeed,
                    var reply_to:String,
                    var date_created:String,
                    var comment:String,
                    var created_by_id:String,
                    var id:String,
                    var created_by: User)