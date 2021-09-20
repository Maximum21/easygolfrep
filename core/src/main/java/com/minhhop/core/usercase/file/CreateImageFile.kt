package com.minhhop.core.usercase.file

import com.minhhop.core.data.repository.FileHelperRepository
import java.io.File
import java.io.IOException

class CreateImageFile(private val fileHelperRepository: FileHelperRepository) {
    @Throws(IOException::class)
    operator fun invoke(externalFilesDir:File) : File? = fileHelperRepository.createImageFile(externalFilesDir)
}