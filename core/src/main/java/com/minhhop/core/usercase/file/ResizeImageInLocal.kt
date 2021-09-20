package com.minhhop.core.usercase.file

import com.minhhop.core.data.repository.FileHelperRepository
import com.minhhop.core.domain.ErrorResponse

class ResizeImageInLocal (private val fileHelperRepository: FileHelperRepository) {
    operator fun invoke(path: String, maxQualityScale:Float, errorResponse: (ErrorResponse) -> Unit) = fileHelperRepository.resizeImage(path, maxQualityScale, errorResponse)
}