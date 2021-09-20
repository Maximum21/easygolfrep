package com.minhhop.core.domain.profile

import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.golf.RoundGolf
import jdk.internal.dynalink.beans.StaticClass

data class PeopleResponse (
        val profile: People,
        val feed:ArrayList<NewsFeed>?,
        val round:Round?,
        val statistic:ArrayList<Statistic>?,
        val stage:Stage?,
        var followers:PeopleObject?
)