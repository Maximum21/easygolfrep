package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.MatchGolf
import com.minhhop.core.domain.golf.RoundGolf

class FetchDataScoreGolfMembersInBattleFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundGolf: RoundGolf, onResult:(HashMap<String, ArrayList<MatchGolf?>?>?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.fetchDataScoreGolfForMembersInBattle(roundGolf, onResult, errorResponse)
}