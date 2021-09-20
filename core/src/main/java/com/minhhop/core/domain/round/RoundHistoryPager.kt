package com.minhhop.core.domain.round

import com.minhhop.core.domain.Paginator
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.profile.Round

data class RoundHistoryPager(
        val paginator: Paginator,
        val items: List<Round>
)