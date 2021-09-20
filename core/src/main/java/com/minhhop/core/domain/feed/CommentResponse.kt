package com.minhhop.core.domain.feed

data class CommentResponse (var total : Int,
                            var data : ArrayList<Comment>? = null)