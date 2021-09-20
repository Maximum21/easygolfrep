package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import java.io.File

class UploadPhotoScorecardGolf (private val golfRepository: GolfRepository) {
    suspend operator fun invoke(file: File, courseId: String,
                                    teeType: String, date:Long, friends: List<User>?, result: (Any) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.uploadScorecardGolf(file, courseId, teeType, date, friends, result, errorResponse)
}