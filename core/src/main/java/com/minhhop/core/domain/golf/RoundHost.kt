package com.minhhop.core.domain.golf

data class RoundHost(
        val handicap:Boolean = true,
        val matches:List<MatchGolf>,
        val friends:HashMap<String,ArrayList<MatchGolf?>?>? = null
)