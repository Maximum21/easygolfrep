package com.minhhop.core.usercase.firebase

import com.minhhop.core.data.repository.FirebaseRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.firebase.BattleRound

class GetBattleRoundWithCourseInFirebase (private val firebaseRepository: FirebaseRepository){
    operator fun invoke(courseId: String, userId:String? = null, onComplete:(BattleRound?)->Unit, errorResponse: (ErrorResponse) -> Unit)
            = firebaseRepository.getBattleRoundWithCourse(courseId, userId, onComplete, errorResponse)
}