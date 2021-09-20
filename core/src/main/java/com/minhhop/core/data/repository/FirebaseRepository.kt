package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.FirebaseDataSource
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.MatchGolf
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.golf.ScorecardModel

class FirebaseRepository(private val firebaseDataSource: FirebaseDataSource) {
    suspend fun getBattleRoundFindFirst(onComplete:(BattleRound?)->Unit,errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.getBattleRoundFindFirst(onComplete, errorResponse)
    fun getBattleRound(roundId: String, userId: String, onComplete: (BattleRound?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.getBattleRound(roundId, userId, onComplete, errorResponse)
    suspend fun removeBattleRound(roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.removeBattleRound(roundId, onComplete, errorResponse)
    fun getBattleRoundWithCourse(courseId: String, userId: String? = null, onComplete: (BattleRound?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.getBattleRoundWithCourse(courseId, userId, onComplete, errorResponse)
    fun getProfileUser(id: String, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.getProfileUser(id, onComplete, errorResponse)
    fun fetchMembersWithIdInBattle(roundId: String,onResult:(List<String>?)->Unit,errorResponse: (ErrorResponse) -> Unit)
            = firebaseDataSource.fetchMembersWithIdInBattle(roundId, onResult, errorResponse)
    fun fetchMembersInBattle(roundId: String, onResult: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.fetchMembersInBattle(roundId, onResult, errorResponse)
    fun fetchDataScoreGolfForMembersInBattle(roundGolf: RoundGolf, onResult:(HashMap<String, ArrayList<MatchGolf?>?>?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseDataSource.fetchDataScoreGolfForMembersInBattle(roundGolf, onResult, errorResponse)
    fun addMembersToBattle(roundId: String, courseId: String, users: List<User>, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.addMembersToBattle(roundId, courseId, users, onComplete, errorResponse)
    fun removeMemberFromBattle(roundId: String, user: User, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.removeMemberFromBattle(roundId, user, onComplete, errorResponse)
    suspend fun postScore(roundId: String, numberHole: String, user: User, data: DataScoreGolf, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.postScore(roundId, numberHole, user, data, onComplete, errorResponse)
    fun getDataScoreAtRound(roundId: String, onComplete:(List<ScorecardModel>?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseDataSource.getDataScoreAtRound(roundId, onComplete, errorResponse)
    fun getScoreUser(roundId: String, numberHole: String, user: User, onComplete: (DataScoreGolf?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.getScoreUser(roundId, numberHole, user, onComplete, errorResponse)
    suspend fun createBattleGame(roundId: String, courseId: String, friends: List<User>?, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.createBattleGame(roundId, courseId, friends, onComplete, errorResponse)
    fun updateHandicapUser(user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.updateHandicapUser(user, onComplete, errorResponse)
    fun updateStatusPendingInBattle(isPending: Boolean, roundId: String, user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.updateStatusPendingInBattle(isPending, roundId, user, onComplete, errorResponse)
    fun changeTeeTypeInBattle(teeType:String,roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseDataSource.changeTeeTypeInBattle(teeType, roundId, onComplete, errorResponse)
    fun isRoundExit(roundId: String,onResult: (Boolean) -> Unit,errorResponse: (ErrorResponse) -> Unit) = firebaseDataSource.isRoundExit(roundId, onResult, errorResponse)
}