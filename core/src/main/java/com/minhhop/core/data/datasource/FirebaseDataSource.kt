package com.minhhop.core.data.datasource

import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.MatchGolf
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.golf.ScorecardModel

interface FirebaseDataSource {
    suspend fun getBattleRoundFindFirst(onComplete:(BattleRound?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getBattleRound(roundId:String,userId:String,onComplete:(BattleRound?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    suspend fun removeBattleRound(roundId: String,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
    fun removeMemberFromBattle(roundId: String,user:User,onComplete:()->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getBattleRoundWithCourse(courseId: String,userId:String? = null,onComplete:(BattleRound?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getProfileUser(id:String,onComplete:(User?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun fetchMembersInBattle(roundId: String,onResult:(User?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun fetchMembersWithIdInBattle(roundId: String,onResult:(List<String>?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun fetchDataScoreGolfForMembersInBattle(roundGolf: RoundGolf,onResult:(HashMap<String, ArrayList<MatchGolf?>?>?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun addMembersToBattle(roundId: String,courseId: String,users:List<User>,onComplete:()->Unit,errorResponse: (ErrorResponse) -> Unit)
    suspend fun postScore(roundId: String,numberHole:String,user:User,data:DataScoreGolf,onComplete:()->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getScoreUser(roundId: String,numberHole:String,user:User,onComplete:(DataScoreGolf?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getDataScoreAtRound(roundId: String,onComplete:(List<ScorecardModel>?)->Unit,errorResponse: (ErrorResponse) -> Unit)
    suspend fun createBattleGame(roundId: String,courseId: String,friends: List<User>?,onComplete:()->Unit,errorResponse: (ErrorResponse) -> Unit)
    fun updateHandicapUser(user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun updateStatusPendingInBattle(isPending:Boolean,roundId: String,user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun changeTeeTypeInBattle(teeType:String,roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun isRoundExit(roundId: String,onResult: (Boolean) -> Unit,errorResponse: (ErrorResponse) -> Unit)
}