package com.minhhop.core.domain.firebase

data class BattleRound(
        var id: String? = null,
        var course_id: String? = null,
        @field:JvmField
        val is_host: Boolean? = false,
        @field:JvmField
        val is_pending: Boolean? = false,
        var round_id: String? = null,
        var tee_type:String? = null
)