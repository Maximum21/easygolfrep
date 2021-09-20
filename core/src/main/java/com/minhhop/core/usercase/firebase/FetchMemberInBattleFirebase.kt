package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User

class FetchMemberInBattleFirebase (private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String, onResult:(User?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.fetchMembersInBattle(roundId, onResult, errorResponse)
}