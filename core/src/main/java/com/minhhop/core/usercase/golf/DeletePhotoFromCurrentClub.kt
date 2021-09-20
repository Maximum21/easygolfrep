package com.minhhop.core.usercase.golf

import com.minhhop.core.data.repository.GolfRepository
import com.minhhop.core.domain.ErrorResponse

class DeletePhotoFromCurrentClub(private val golfRepository: GolfRepository) {
    suspend operator fun invoke(clubId: String,photoId:String,position:Int,
                                    result: (Int) -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfRepository.deletePhotoFromClub(clubId, photoId, position, result, errorResponse)
}