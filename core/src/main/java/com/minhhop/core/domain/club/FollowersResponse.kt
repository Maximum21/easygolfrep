package com.minhhop.core.domain.club

import com.minhhop.core.domain.Paginator
import com.minhhop.core.domain.profile.People

class FollowersResponse (
        var paginator: Paginator,
        var data : List<People> = ArrayList()
)