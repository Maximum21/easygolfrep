package com.minhhop.core.domain.golf


data class RoundGolf(
        /**
         *@param @id is null when start explore
         * */
        val id: String?,
        val club_name: String?,
        val course_name: String?,
        var course_id: String?,
        val date: String?,
        val status: String?,
        val tee: String?,
        val unit: String?,
        val course: Course?,
        val holes: List<Hole>?,
        val math: Hole?,
        val hdc: Double,
        val hcp: Double,
        val differential: Double,
        val is_host: Boolean?,
        val stage: String?,
        val matches: List<MatchGolf>?,
        val friends: List<FriendGolf>?,
        /**
         * @param @teeType is support get teeType in local
         */
        var teeType: String?
) {
    enum class Stage {
        INCLUDED,
        NOT_INCLUDED,
        PENDING
    }

    fun isFinish() = this.status?.equals("Finished", true) == true
    fun getStage() : Stage {
        return when (this.stage) {
            Stage.INCLUDED.name -> {
                Stage.INCLUDED
            }
            Stage.NOT_INCLUDED.name -> {
                Stage.NOT_INCLUDED
            }
            Stage.PENDING.name -> {
                Stage.PENDING
            }
            else->{
                Stage.PENDING
            }
        }
    }
}