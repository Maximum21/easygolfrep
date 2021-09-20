package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.golf.ClubPhoto
import java.io.File

class UploadPhotoForClub(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String,  files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.uploadPhotoForClub(clubId, files, result, errorResponse)
    suspend operator fun invoke(files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.uploadHistoryPhotoForClub(files, result, errorResponse)
}