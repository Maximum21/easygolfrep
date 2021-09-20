package com.minhhop.core.domain.golf

import com.minhhop.core.domain.Paginator

data class ClubPager(
        val paginator: Paginator,
        val items: List<Club>
)