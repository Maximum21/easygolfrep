package com.minhhop.core.usercase.file

import com.minhhop.core.data.repository.FileHelperRepository
import com.minhhop.core.domain.ErrorResponse
import java.io.ByteArrayOutputStream
import java.io.File

class WriteBytesToFile(private val fileHelperRepository: FileHelperRepository) {
    operator fun invoke(data: ByteArrayOutputStream, file: File, errorResponse: (ErrorResponse) -> Unit)
            = fileHelperRepository.writeBytesToFile(data, file, errorResponse)
}