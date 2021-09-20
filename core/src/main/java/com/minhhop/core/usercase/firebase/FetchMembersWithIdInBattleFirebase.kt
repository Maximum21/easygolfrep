package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse

class FetchMembersWithIdInBattleFirebase(private val firebaseRepository: FirebaseRepository) {
    operator fun invoke(roundId: String,onResult:(List<String>?)->Unit,errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.fetchMembersWithIdInBattle(roundId, onResult, errorResponse)
}